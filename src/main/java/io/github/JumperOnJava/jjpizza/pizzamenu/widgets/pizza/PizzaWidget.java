package io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pizza menu widget Should be set up by using setupSize and setupSlices methods to work properly.
 */
public class PizzaWidget implements Renderable, GuiEventListener, NarratableEntry {
  private static final Logger LOGGER = LoggerFactory.getLogger(PizzaWidget.class);
  private final LinkedList<PizzaWidgetSlice> slices = new LinkedList<>();
  public float radius;
  public float innerRadius;
  public int x, y;
  private float hitRadius;

  /**
   * Creates Pizza widget for slice list
   *
   * @param radius Radius of pizza
   * @param x x coordinate of pizza center
   * @param y y coordinate of pizza center
   */
  public PizzaWidget setupSize(int radius, int innerRadius, int x, int y) {
    this.radius = radius;
    this.hitRadius = radius;
    this.innerRadius = innerRadius;
    this.x = x;
    this.y = y;
    return this;
  }

  public PizzaWidget setupSlices(List<? extends PizzaSlice> slices) {
    this.slices.clear();
    for (var slice : slices) {
      this.slices.add(new PizzaWidgetSlice(slice, this));
    }
    return this;
  }

  public void extractRenderState(
      GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    context.pose().pushMatrix();
    // ActionTextRenderer.sendChatMessag*e("x: ",mouseX," y: ",mouseY);
    var prof = Profiler.get();
    prof.push("Pizza");
    context.pose().translate(x, y);
    slices.forEach(slice -> slice.extractRenderState(context, mouseX - x, mouseY - y, delta));
    slices.forEach(slice -> slice.renderText(context));
    slices.forEach(slice -> slice.renderIcons(context));
    prof.pop();
    context.pose().popMatrix();
  }

  public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean bool) {
    LOGGER.debug("PizzaWidget.mouseClicked: event={}", event);
    boolean cond = false;
    for (var slice : new LinkedList<>(slices)) {
      MouseButtonEvent translated =
          new MouseButtonEvent(event.x() - x, event.y() - y, event.buttonInfo());
      cond = cond || slice.mouseClicked(translated, bool);
    }
    return cond;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
    boolean cond = false;

    for (var slice : new LinkedList<>(slices)) {
      cond = cond || slice.mouseScrolled(mouseX - x, mouseY - y, amountX, amountY);
    }
    return cond;
  }

  @Override
  public @NonNull NarrationPriority narrationPriority() {
    return NarrationPriority.HOVERED;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    var center = new Vec2(x, y);
    var mouse = new Vec2((float) mouseX, (float) mouseY);
    var distanceSq = center.distanceToSqr(mouse);
    return distanceSq < hitRadius * hitRadius && distanceSq > innerRadius * innerRadius;
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  public boolean isMouseOverRel(double mouseX, double mouseY) {
    return isMouseOver(mouseX + x, mouseY + y);
  }

  @Override
  public void updateNarration(@NonNull NarrationElementOutput builder) {}

  public void setupHitRadius(int hitRadius) {
    this.hitRadius = hitRadius;
  }
}
