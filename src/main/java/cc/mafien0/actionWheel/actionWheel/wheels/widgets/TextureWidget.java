package cc.mafien0.actionWheel.actionWheel.wheels.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TextureWidget implements Renderable, NarratableEntry, GuiEventListener {
  public Identifier getTexture() {
    return texture;
  }

  public void setTexture(Identifier texture) {
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

  private Identifier texture;
  private int x;
  private int y;
  private int width;
  private int height;

  public TextureWidget(Identifier texture, int x, int y, int width, int height) {
    this.texture = texture;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public void extractRenderState(
      @NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    if (texture == null || texture.getPath().isEmpty()) return;
    context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, width, height);
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public @NonNull NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(@NonNull NarrationElementOutput builder) {}
}
