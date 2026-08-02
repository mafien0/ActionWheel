package cc.mafien0.actionWheel.actionWheel.wheels;

import cc.mafien0.actionWheel.actionWheel.wheels.widgets.wheel.WheelAction;
import cc.mafien0.actionWheel.actionWheel.wheels.widgets.wheel.WheelWidget;
import cc.mafien0.actionWheel.core.common.Tr;
import java.util.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WheelScreen extends Screen {
  private static final Logger LOGGER = LoggerFactory.getLogger(WheelScreen.class);
  private final List<? extends WheelAction> slices;
  private final Screen configuratorScreen;
  private final WheelManager manager;
  public WheelWidget wheelWidget;

  // ImmutableSlicesList slices;
  public WheelScreen(
      List<? extends WheelAction> slices, Screen configuratorScreen, WheelManager manager) {
    super(Component.empty());
    this.manager = manager;
    this.slices = slices;
    this.configuratorScreen = configuratorScreen;
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  public void init() {
    LOGGER.debug(
        "WheelScreen.init - {} slices, configurator={}", slices.size(), configuratorScreen != null);

    if (configuratorScreen != null)
      addRenderableWidget(
          new Button.Builder(
                  Tr.get("actionWheel.screen.openconfig"),
                  b -> Minecraft.getInstance().gui.setScreen(configuratorScreen))
              .pos(10, 10)
              .size(60, 20)
              .build());
    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
      addRenderableWidget(
          new Button.Builder(
                  Component.literal("Export Translation"), b -> Tr.generateTranslationMap())
              .pos(10, 32)
              .width(120)
              .build());
    }

    wheelWidget = new WheelWidget();
    wheelWidget.setupSize(
        (int) (Math.min(width, height) / 2f * .8f),
        (int) (Math.min(width, height) / 2f * .8f / 8),
        width / 2,
        height / 2);
    wheelWidget.setupHitRadius(10000);
    wheelWidget.setupSlices(slices);
    addRenderableWidget(wheelWidget);
  }

  private boolean releasedOnce = false;

  public boolean keyReleased(KeyEvent event) {
    LOGGER.debug("WheelScreen.keyReleased: keysym={}, releasedOnce={}", event.key(), releasedOnce);
    if (manager.matchesKey(event) && !releasedOnce) {
      LOGGER.debug("Key matches, triggering clickAtMouse");
      clickAtMouse();
    }
    if (!releasedOnce) releasedOnce = true;
    return super.keyReleased(event);
  }

  private void clickAtMouse() {
    double x = minecraft.mouseHandler.xpos() / minecraft.options.guiScale().get();
    double y = minecraft.mouseHandler.ypos() / minecraft.options.guiScale().get();
    LOGGER.debug("clickAtMouse: ({}, {})", x, y);

    this.mouseClicked(
        new MouseButtonEvent(
            x, y, new MouseButtonInfo(0, 0) // left mouse button, no modifiers
            ),
        false);
  }

  public boolean keyPressed(@NonNull KeyEvent event) {
    if (!releasedOnce) return false;
    LOGGER.debug("WheelScreen.keyPressed: keysym={}", event.key());
    if (manager.matchesKey(event)) {
      LOGGER.debug("Key matches, triggering clickAtMouse + close");
      clickAtMouse();
      this.onClose();
      return true;
    }
    return super.keyPressed(event);
  }

  @Override
  public void extractRenderState(
      @NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);
  }
}
