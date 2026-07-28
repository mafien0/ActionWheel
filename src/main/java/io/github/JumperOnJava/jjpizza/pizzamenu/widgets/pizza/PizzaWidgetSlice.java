package io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza;

import static java.lang.Math.*;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.JumperOnJava.jjpizza.datatypes.Angle;
import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec2;

public class PizzaWidgetSlice implements Renderable, GuiEventListener, NarratableEntry {
  public final PizzaSlice pizzaSlice;
  private final CircleSlice circleSlice;
  private final PizzaWidget parent;
  private final HoverManager hoverManager;

  public PizzaWidgetSlice(PizzaSlice slice, PizzaWidget parent) {
    this.pizzaSlice = slice;
    this.circleSlice = pizzaSlice.getSlice();
    this.parent = parent;
    hoverManager = new HoverManager(4);
  }

  public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {

    hoverManager.tickHover(isMouseOver(mouseX, mouseY), delta);

    context.pose().pushMatrix();

    // RenderSystem.enableBlend();
    // RenderSystem.defaultBlendFunc();
    // RenderSystem.disableCull();

    RenderSystem.setShader(CoreShaders.POSITION_COLOR);
    // RenderSystem.setShaderColor(1f,1f,1f,1f);

    var peekMatrix = context.pose().last().pose();

    translateForward(context.pose());
    context.drawSpecial(
        vertexConsumerProvider -> {
          var bufferBuilder = vertexConsumerProvider.getBuffer(RenderType.debugFilledBox());
          float res = (float) (PI / 60);

          if (circleSlice.endAngle.getRadian() != circleSlice.startAngle.getRadian()) {
            for (var a = circleSlice.startAngle.getRadian();
                a
                    < circleSlice.endAngle.getRadian()
                        + (circleSlice.startAngle.getRadian() > circleSlice.endAngle.getRadian()
                            ? (2 * PI)
                            : 0);
                a += res) {
              bufferBuilder
                  .addVertex(
                      peekMatrix,
                      (float) (-cos(a) * parent.radius),
                      (float) (-sin(a) * parent.radius),
                      0f)
                  .setColor(pizzaSlice.getBackgroundColor());
              bufferBuilder
                  .addVertex(
                      peekMatrix,
                      (float) (-cos(a) * parent.innerRadius),
                      (float) (-sin(a) * parent.innerRadius),
                      0f)
                  .setColor(pizzaSlice.getBackgroundColor());
              // var b = a+res;
              // bufferBuilder.vertex(peekMatrix, (float) (-cos(b) * parent.radius), (float)
              // (-sin(b) * parent.radius), 0f).color(pizzaSlice.getBackgroundColor());
              // bufferBuilder.vertex(peekMatrix, (float) (-cos(b) * parent.radius), (float)
              // (-sin(b) * parent.radius), 0f).color(pizzaSlice.getBackgroundColor());
              // bufferBuilder.vertex(peekMatrix, (float) (-cos(b) * parent.innerRadius), (float)
              // (-sin(b) * parent.innerRadius), 0f).color(pizzaSlice.getBackgroundColor());
            }
            bufferBuilder
                .addVertex(
                    peekMatrix,
                    (float) (-cos(circleSlice.endAngle.getRadian()) * parent.radius),
                    (float) (-sin(circleSlice.endAngle.getRadian()) * parent.radius),
                    0f)
                .setColor(pizzaSlice.getBackgroundColor());
            bufferBuilder
                .addVertex(
                    peekMatrix,
                    (float) (-cos(circleSlice.endAngle.getRadian()) * parent.innerRadius),
                    (float) (-sin(circleSlice.endAngle.getRadian()) * parent.innerRadius),
                    0f)
                .setColor(pizzaSlice.getBackgroundColor());
          }
        });

    // RenderSystem.enableCull();
    // RenderSystem.disableBlend();

    context.pose().popPose();
  }

  public void renderIcons(GuiGraphics context) {
    context.pose().pushPose();
    translateForward(context.pose());
    if (pizzaSlice.getIconTexture() == null) {
      context.pose().popPose();
      return;
    }
    context.blit(
        RenderType::guiTexturedOverlay,
        pizzaSlice.getIconTexture(),
        (int) (getRenderPos().x - 16),
        (int) (getRenderPos().y - 16),
        0,
        0,
        32,
        32,
        32,
        32);
    context.pose().popPose();
  }

  public void renderText(GuiGraphics context) {
    context.pose().pushPose();
    translateForward(context.pose());
    context.drawCenteredString(
        Minecraft.getInstance().font,
        pizzaSlice.getName(),
        (int) getRenderPos().x,
        (int) (getRenderPos().y + 18),
        0xFFFFFFFF);
    context.pose().popPose();
  }

  private void translateForward(PoseStack matrixStack) {
    var forward = smoothFunc(hoverManager.getHoverProgress()) * parent.radius / 30;
    matrixStack.translate(
        -cos(circleSlice.getMidAngle().getRadian()) * forward,
        -sin(circleSlice.getMidAngle().getRadian()) * forward,
        0);
  }

  private Vec2 getRenderPos() {
    var mid = pizzaSlice.getSlice().getMidAngle().getRadian();
    return new Vec2((float) -cos(mid), (float) -sin(mid)).scale(parent.radius * 0.75f);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    var cond = isMouseOver(mouseX, mouseY);
    if (cond) {
      switch (button) {
        case 0 -> pizzaSlice.onLeftClick();
        case 1 -> pizzaSlice.onRightClick();
      }
    }
    return cond;
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    var cond = isMouseOver(mouseX, mouseY);
    if (cond) {
      pizzaSlice.onScroll(horizontalAmount, verticalAmount);
    }
    return cond;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    float mouseAngle = (float) Math.atan2(-(mouseY / 2), -(mouseX / 2));
    if (mouseAngle < 0) mouseAngle += PI * 2;

    return circleSlice.inInSlice(Angle.newRadian(mouseAngle))
        && parent.isMouseOverRel(mouseX, mouseY);
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.HOVERED;
  }

  @Override
  public boolean isActive() {
    return NarratableEntry.super.isActive();
  }

  @Override
  public void updateNarration(NarrationElementOutput builder) {
    // builder.put(NarrationPart.TITLE, "slice narration is not implemented");
  }

  private float smoothFunc(float x) {
    return x * x * (3f - 2f * x);
  }
}
