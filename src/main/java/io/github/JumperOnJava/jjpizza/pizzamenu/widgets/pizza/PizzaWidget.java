package io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.phys.Vec2;

/**
 * Pizza menu widget Should be set up by using setupSize and setupSlices methods to work properly.
 */
public class PizzaWidget implements Renderable, GuiEventListener, NarratableEntry {
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

  public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    context.pose().pushMatrix();
    // ActionTextRenderer.sendChatMessag*e("x: ",mouseX," y: ",mouseY);
    var prof = Profiler.get();
    prof.push("Pizza");
    context.pose().translate(x, y, context.pose());
    slices.forEach(
        slice -> {
          slice.render(context, mouseX - x, mouseY - y, delta);
        });
    slices.forEach(
        slice -> {
          slice.renderText(context);
        });
    slices.forEach(
        slice -> {
          slice.renderIcons(context);
        });
    prof.pop();
    context.pose().popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    boolean cond = false;
    for (var slice : new LinkedList<>(slices)) {
      cond = cond || slice.mouseClicked(mouseX - x, mouseY - y, button);
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
  public NarrationPriority narrationPriority() {
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
  public void updateNarration(NarrationElementOutput builder) {}

  public void setupHitRadius(int hitRadius) {
    this.hitRadius = hitRadius;
  }
}
