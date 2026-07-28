package io.github.JumperOnJava.lavajumper.gui.widgets;

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
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * Widget for rendering screens in screens. Use init method so set widget dimensions and setScreen
 * method to set/change screen in widget. When screen is not set up or equals null widget will
 * display empty screen with random color and "nullSubScreen" text in center
 */
public class SubScreen implements Renderable, ContainerEventHandler, NarratableEntry, LayoutElement {
  public static boolean blurDisabled = false;
  private Screen screen;
  private int x;
  private int y;
  private boolean dragging;
  private final int width;
  private final int height;

  public SubScreen(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * If screen parameter equals null widget will display empty screen with random color and
   * "nullSubScreen" text in center
   *
   * @param screen
   */
  public SubScreen setScreen(Screen screen) {
    if (screen == null) screen = new NullSubScreen();
    this.screen = screen;
    screen.init(width, height);
    return this;
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    context.pose().pushMatrix();
    context.pose().translate((float) x, (float) y);
    blurDisabled = true;
    screen.extractRenderState(context, mouseX - x, mouseY - y, delta);
    blurDisabled = false;
    context.pose().popMatrix();
  }

  @Override
  public void updateNarration(@NonNull NarrationElementOutput builder) {}

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    screen.mouseMoved(mouseX - x, mouseY - y);
  }

  @Override
  public @NonNull List<? extends GuiEventListener> children() {
    return screen.children();
  }

  @Override
  public @NonNull Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
    return screen.getChildAt(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
    return screen.mouseClicked(event, doubleClick);
  }

  @Override
  public boolean mouseReleased(@NonNull MouseButtonEvent event) {
    return screen.mouseReleased(event);
  }

  @Override
  public boolean mouseDragged(
      final @NonNull MouseButtonEvent event, final double dx, final double dy) {
    return screen.mouseDragged(event, dx, dy);
  }

  @Override
  public boolean isDragging() {
    return this.dragging;
  }

  @Override
  public void setDragging(boolean dragging) {
    this.dragging = dragging;
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    return screen.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
  }

  @Override
  public boolean keyPressed(@NonNull KeyEvent event) {
    return screen.keyPressed(event);
  }

  @Override
  public boolean keyReleased(@NonNull KeyEvent event) {
    return screen.keyReleased(event);
  }

  @Override
  public boolean charTyped(final @NonNull CharacterEvent event) {
    return screen.charTyped(event);
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
  public @NonNull ScreenRectangle getRectangle() {
    return ContainerEventHandler.super.getRectangle();
  }

  @Override
  public void setPosition(int x, int y) {
    LayoutElement.super.setPosition(x, y);
  }

  @Nullable
  @Override
  public ComponentPath nextFocusPath(@NonNull FocusNavigationEvent navigation) {
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
  public @NonNull NarrationPriority narrationPriority() {
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
  public void visitWidgets(@NonNull Consumer<AbstractWidget> consumer) {}

  @Override
  public int getTabOrderGroup() {
    return ContainerEventHandler.super.getTabOrderGroup();
  }

  private class NullSubScreen extends Screen {
    public NullSubScreen() {
      super(Component.empty());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      super.extractRenderState(context, mouseX, mouseY, delta);
      context.fill(
          0,
          0,
          width,
          height,
          (int) (Math.pow(x + width + y + height, 5f) % Integer.MAX_VALUE) & 0x00FFFFFF
              | 0x3F000000);
      context.centeredText(
          Minecraft.getInstance().font,
          "nullSubScreen",
          width / 2,
          height / 2,
          (int) (Math.pow(x + width + y + height, 5f) % Integer.MAX_VALUE) & 0x00FFFFFF
              | 0x3F000000 ^ 0x00FFFFFF);
    }
  }
}
