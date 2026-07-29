package io.github.JumperOnJava.lavajumper.gui.widgets;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

/** Scroll list widget for general use. */
public class ScrollListWidget extends ObjectSelectionList<ScrollListWidget.ScrollListEntry> {
  public static boolean renderingEntries;

  public ScrollListWidget(Minecraft client, int width, int height, int x, int y, int itemHeight) {
    super(client, width, height, y, itemHeight);
    setX(x);
    // setRenderBackground(false);
    // setRenderHeader(false,0);
  }

  @Override
  public int getRowWidth() {
    return this.width;
  }

  public void clearList() {
    clearEntries();
  }

  public int addEntry(ScrollListEntry entry) {
    entry.activationConsumer = this::setSelectedEntry;
    entry.isHoveredFunction = this::isMouseOver;
    return super.addEntry(entry);
  }

  protected int getScrollbarPosition() {
    return width - 6;
  }

  @Override
  public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
    return super.mouseClicked(event, doubleClick);
  }

  private ScrollListEntry selectedEntry = new ScrollListEntry();

  public void setSelectedEntry(ScrollListEntry listEntry) {
    selectedEntry.setSelected(false);
    listEntry.setSelected(true);
    selectedEntry = listEntry;
  }

  //
  //    @Override
  //    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
  //        context.enableScissor(left,top,left+width,top+height-1);
  //        super.render(context, mouseX, mouseY, delta);
  //        context.disableScissor();
  //    }

  /**
   * Scroll list entry. Out of box does nothing but using addDrawableChild method you can add
   * widgets for custom behavior.
   */
  public static class ScrollListEntry extends ObjectSelectionList.Entry<ScrollListEntry> {
    private final List<Renderable> drawables = Lists.newArrayList();
    private final List<GuiEventListener> children = Lists.newArrayList();
    private Consumer<ScrollListEntry> activationConsumer;
    private BiFunction<Integer, Integer, Boolean> isHoveredFunction;
    private final List<GuiEventListener> deactivate = Lists.newArrayList();

    @Override
    public @NonNull Component getNarration() {
      return Component.empty();
    }

    int currentX, currentY;

    private void setSelected(boolean selected) {
      for (var d : deactivate) {
        if (d instanceof AbstractButton pw) {
          pw.active = !selected;
        }
      }
    }

    @Override
    public void extractContent(
        @NonNull GuiGraphicsExtractor context,
        int mouseX,
        int mouseY,
        boolean hovered,
        float delta) {
      for (var d : drawables) {
        context.pose().pushMatrix();
        context.pose().translate((float) getX(), (float) getY());
        if (!isHoveredFunction.apply(mouseX, mouseY)) {
          mouseX += 100000;
          mouseY += 100000;
        }
        ScrollListWidget.renderingEntries = true;
        d.extractRenderState(context, mouseX - getX(), mouseY - getY(), delta);
        ScrollListWidget.renderingEntries = false;
        currentX = getX();
        currentY = getY();
        context.pose().popMatrix();
      }
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
      if (!isMouseOver(event.x(), event.y())) return false;
      MouseButtonEvent translated =
          new MouseButtonEvent(event.x() - getX(), event.y() - getY(), event.buttonInfo());
      for (var c : children) {
        c.mouseClicked(translated, doubleClick);
      }
      return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
      return super.isMouseOver(mouseX, mouseY)
          && isHoveredFunction.apply((int) mouseX, (int) mouseY);
    }

    /**
     * adds widget element to this entry.
     *
     * @param drawableElement
     * @param deactivateOnSelect should be this element deactivated when selected. Works only when
     *     widget is instance of PressableWidget
     * @return
     * @param <T>
     */
    public <T extends GuiEventListener & Renderable> T addDrawableChild(
        T drawableElement, boolean deactivateOnSelect) {
      this.drawables.add(drawableElement);
      this.children.add(drawableElement);
      if (deactivateOnSelect) this.deactivate.add(drawableElement);
      return drawableElement;
    }

    public void setMeActive() {
      if (activationConsumer == null) return;
      activationConsumer.accept(this);
    }
  }
}
