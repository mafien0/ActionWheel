package io.github.JumperOnJava.jjpizza.pizzamenu.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TextureWidget implements Renderable, NarratableEntry, GuiEventListener {
  public ResourceLocation getTexture() {
    return texture;
  }

  public void setTexture(ResourceLocation texture) {
    this.texture = texture;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  private ResourceLocation texture;
  private int x;
  private int y;
  private int width;
  private int height;

  public TextureWidget(ResourceLocation texture, int x, int y, int width, int height) {
    this.texture = texture;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    context.blit(
        RenderType::guiTexturedOverlay, texture, x, y, 0, 0, width, height, width, height);
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput builder) {}
}
