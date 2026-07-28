package io.github.JumperOnJava.jjpizza.pizzamenu;

import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaWidget;
import io.github.JumperOnJava.lavajumper.common.Tr;
import java.util.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PizzaScreen extends Screen {
  private final List<? extends PizzaSlice> slices;
  private final Screen configuratorScreen;
  private final PizzaManager manager;
  public PizzaWidget pizzaWidget;

  // ImmutableSlicesList slices;
  public PizzaScreen(
      List<? extends PizzaSlice> slices, Screen configuratorScreen, PizzaManager manager) {
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

    if (configuratorScreen != null)
      addRenderableWidget(
          new Button.Builder(
                  Tr.get("jjpizza.screen.openconfig"),
                  b -> {
                    Minecraft.getInstance().setScreen(configuratorScreen);
                  })
              .pos(10, 10)
              .size(60, 20)
              .build());
    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
      addRenderableWidget(
          new Button.Builder(
                  Component.literal("Export Translaslation"), b -> Tr.generateTranlationMap())
              .pos(10, 32)
              .width(120)
              .build());
      /*addDrawableChild(new ButtonWidget.Builder(Text.literal("Force load"),b->{
      	//PizzaManager.getManager().actions.clear();
      	manager.actions=manager.load();
      }).position(10,54).width(70).build());*/

    }

    pizzaWidget = new PizzaWidget();
    pizzaWidget.setupSize(
        (int) (Math.min(width, height) / 2f * .8f),
        (int) (Math.min(width, height) / 2f * .8f / 8),
        width / 2,
        height / 2);
    pizzaWidget.setupHitRadius(10000);
    pizzaWidget.setupSlices(slices);
    addRenderableWidget(pizzaWidget);
  }

  private boolean releasedOnce = false;

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (manager.matchesKey(keyCode, scanCode) && !releasedOnce) {
      clickAtMouse();
    }
    if (!releasedOnce) releasedOnce = true;
    return super.keyReleased(keyCode, scanCode, modifiers);
  }

  private void clickAtMouse() {
    var x = minecraft.mouseHandler.xpos() / minecraft.options.guiScale().get();
    var y = minecraft.mouseHandler.ypos() / minecraft.options.guiScale().get();
    this.mouseClicked(x, y, 0);
  }

  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!releasedOnce) return false;
    if (manager.matchesKey(keyCode, scanCode)) {
      clickAtMouse();
      this.onClose();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    renderBackground(context, mouseX, mouseY, delta);
    super.render(context, mouseX, mouseY, delta);
  }
}
