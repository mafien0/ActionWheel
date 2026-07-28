package io.github.JumperOnJava.lavajumper.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Widget for rendering screens in screens. Use init method so set widget dimensions and setScreen
 * method to set/change screen in widget. When screen is not set up or equals null widget will
 * display empty screen with random color and "nullSubScreen" text in center
 */
public class SubScreen implements Renderable, ContainerEventHandler, NarratableEntry, LayoutElement {
  public static boolean blurDisabled = false;
  private Screen screen;
  private int x, y, width, height;

  public SubScreen(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * If screen paremeter equals null widget will display empty screen with random color and
   * "nullSubScreen" text in center
   *
   * @param screen
   */
  public SubScreen setScreen(Screen screen) {
    if (screen == null) screen = new NullSubScreen();
    this.screen = screen;
    screen.init(Minecraft.getInstance(), width, height);
    return this;
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    context.pose().pushPose();
    context.pose().translate(x, y, 0);
    blurDisabled = true;
    screen.render(context, mouseX - x, mouseY - y, delta);
    blurDisabled = false;
    context.pose().popPose();
  }

  @Override
  public void updateNarration(NarrationElementOutput builder) {}

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    screen.mouseMoved(mouseX - x, mouseY - y);
  }

  @Override
  public List<? extends GuiEventListener> children() {
    return screen.children();
  }

  @Override
  public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
    return screen.getChildAt(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return screen.mouseClicked(mouseX - x, mouseY - y, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    return screen.mouseReleased(mouseX - x, mouseY - y, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return screen.mouseDragged(mouseX - x, mouseY - y, button, deltaX, deltaY);
  }

  @Override
  public boolean isDragging() {
    return false;
  }

  @Override
  public void setDragging(boolean dragging) {
    setDragging(dragging);
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    return screen.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return screen.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    return screen.keyReleased(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    return screen.charTyped(chr, modifiers);
  }

  @Nullable
  @Override
  public GuiEventListener getFocused() {
    return screen.getFocused();
  }

  @Override
  public void setFocused(@Nullable GuiEventListener focused) {
    screen.setFocused(focused);
  }

  @Override
  public void setFocused(boolean focused) {
    ContainerEventHandler.super.setFocused(focused);
  }

  @Override
  public boolean isFocused() {
    return ContainerEventHandler.super.isFocused();
  }

  @Nullable
  @Override
  public ComponentPath getCurrentFocusPath() {
    return ContainerEventHandler.super.getCurrentFocusPath();
  }

  @Override
  public ScreenRectangle getRectangle() {
    return ContainerEventHandler.super.getRectangle();
  }

  @Override
  public void setPosition(int x, int y) {
    LayoutElement.super.setPosition(x, y);
  }

  @Nullable
  @Override
  public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
    return ContainerEventHandler.super.nextFocusPath(navigation);
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.x
        && mouseY >= this.y
        && mouseX < (this.x + this.width)
        && mouseY < (this.y + this.height);
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public boolean isActive() {
    return NarratableEntry.super.isActive();
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void visitWidgets(Consumer<AbstractWidget> consumer) {}

  @Override
  public int getTabOrderGroup() {
    return ContainerEventHandler.super.getTabOrderGroup();
  }

  private class NullSubScreen extends Screen {
    public NullSubScreen() {
      super(Component.empty());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      RenderSystem.enableBlend();
      context.fill(
          0,
          0,
          width,
          height,
          (int) (Math.pow(x + width + y + height, 5f) % Integer.MAX_VALUE) & 0x00FFFFFF
              | 0x3F000000);
      context.drawCenteredString(
          Minecraft.getInstance().font,
          "nullSubScreen",
          width / 2,
          height / 2,
          (int) (Math.pow(x + width + y + height, 5f) % Integer.MAX_VALUE) & 0x00FFFFFF
              | 0x3F000000 ^ 0x00FFFFFF);
    }
  }
}
