package cc.mafien0.actionWheel.actionWheel.wheels.widgets.wheel;

import static java.lang.Math.*;

import cc.mafien0.actionWheel.actionWheel.datatypes.Angle;
import cc.mafien0.actionWheel.actionWheel.datatypes.CircleSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WheelWidgetAction implements Renderable, GuiEventListener, NarratableEntry {
  private static final Logger LOGGER = LoggerFactory.getLogger(WheelWidgetAction.class);
  public final WheelAction action;
  private final CircleSlice circleSlice;
  private final WheelWidget parent;
  private final HoverManager hoverManager;

  public WheelWidgetAction(WheelAction slice, WheelWidget parent) {
    this.action = slice;
    this.circleSlice = action.getSlice();
    this.parent = parent;
    hoverManager = new HoverManager(4);
  }

  public void extractRenderState(
      GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    hoverManager.tickHover(isMouseOver(mouseX, mouseY), delta);
    context.pose().pushMatrix();
    translateForward(context.pose());

    int argb = action.getBackgroundColor();
    var arc =
        new SliceRenderState(
            ARGB.red(argb),
            ARGB.green(argb),
            ARGB.blue(argb),
            ARGB.alpha(argb),
            circleSlice,
            parent.radius,
            parent.innerRadius,
            new Matrix3x2f(context.pose()),
            new ScreenRectangle(0, 0, context.guiWidth(), context.guiHeight()),
            new ScreenRectangle(0, 0, context.guiWidth(), context.guiHeight()));
    context.guiRenderState.addGuiElement(arc);

    context.pose().popMatrix();
  }

  private record SliceRenderState(
      int red,
      int green,
      int blue,
      int alpha,
      CircleSlice slice,
      float radius,
      float innerRadius,
      Matrix3x2fc pose,
      ScreenRectangle scissorArea,
      ScreenRectangle bounds)
      implements GuiElementRenderState {
    @Override
    public void buildVertices(@NonNull VertexConsumer consumer) {
      float angleStep = (float) (PI / 120);
      float startAngle = slice.startAngle().getRadian();
      float endAngle = slice.endAngle().getRadian();

      if (endAngle == startAngle) return;

      float wrappedEndAngle = endAngle + (startAngle > endAngle ? (float) (2 * PI) : 0);

      float prevOuterX = (float) (-Math.cos(startAngle) * radius);
      float prevOuterY = (float) (-Math.sin(startAngle) * radius);
      float prevInnerX = (float) (-Math.cos(startAngle) * innerRadius);
      float prevInnerY = (float) (-Math.sin(startAngle) * innerRadius);

      for (float angle = startAngle + angleStep; angle <= wrappedEndAngle; angle += angleStep) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        float currOuterX = -cosAngle * radius;
        float currOuterY = -sinAngle * radius;
        float currInnerX = -cosAngle * innerRadius;
        float currInnerY = -sinAngle * innerRadius;

        consumer
            .addVertexWith2DPose(pose, prevOuterX, prevOuterY)
            .setColor(red, green, blue, alpha);
        consumer
            .addVertexWith2DPose(pose, prevInnerX, prevInnerY)
            .setColor(red, green, blue, alpha);
        consumer
            .addVertexWith2DPose(pose, currInnerX, currInnerY)
            .setColor(red, green, blue, alpha);
        consumer
            .addVertexWith2DPose(pose, currOuterX, currOuterY)
            .setColor(red, green, blue, alpha);

        prevOuterX = currOuterX;
        prevOuterY = currOuterY;
        prevInnerX = currInnerX;
        prevInnerY = currInnerY;
      }
    }

    @Override
    public @NonNull RenderPipeline pipeline() {
      return RenderPipelines.GUI;
    }

    @Override
    public @NonNull TextureSetup textureSetup() {
      return TextureSetup.noTexture();
    }

    @Override
    public ScreenRectangle scissorArea() {
      return scissorArea;
    }

    @Override
    public ScreenRectangle bounds() {
      return bounds;
    }
  }

  public void renderIcons(GuiGraphicsExtractor context) {
    context.pose().pushMatrix();
    translateForward(context.pose());
    if (action.getIconTexture() == null) {
      context.pose().popMatrix();
      return;
    }
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        action.getIconTexture(),
        (int) (getRenderPos().x - 16),
        (int) (getRenderPos().y - 16),
        0,
        0,
        32,
        32,
        32,
        32);
    context.pose().popMatrix();
  }

  public void renderText(GuiGraphicsExtractor context) {
    context.pose().pushMatrix();
    translateForward(context.pose());
    context.centeredText(
        Minecraft.getInstance().font,
        action.getName(),
        (int) getRenderPos().x,
        (int) (getRenderPos().y + 18),
        0xFFFFFFFF);
    context.pose().popMatrix();
  }

  private void translateForward(Matrix3x2fStack matrices) {
    float forward = smoothFunc(hoverManager.getHoverProgress()) * parent.radius / 30f;

    matrices.translate(
        (float) (-cos(circleSlice.getMidAngle().getRadian()) * forward),
        (float) (-sin(circleSlice.getMidAngle().getRadian()) * forward));
  }

  private Vec2 getRenderPos() {
    var mid = action.getSlice().getMidAngle().getRadian();
    return new Vec2((float) -cos(mid), (float) -sin(mid)).scale(parent.radius * 0.75f);
  }

  public boolean mouseClicked(MouseButtonEvent event, boolean bool) {
    boolean cond = isMouseOver(event.x(), event.y());
    LOGGER.debug(
        "WheelWidgetAction.mouseClicked: ({},{}), btn={}, hit={}",
        event.x(),
        event.y(),
        event.button(),
        cond);
    if (cond) {
      switch (event.button()) {
        case 0 -> action.onLeftClick();
        case 1 -> action.onRightClick();
      }
    }
    return cond;
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    var cond = isMouseOver(mouseX, mouseY);
    LOGGER.debug("WheelWidgetAction.mouseScrolled: ({},{}), hit={}", mouseX, mouseY, cond);
    if (cond) {
      action.onScroll(horizontalAmount, verticalAmount);
    }
    return cond;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    float mouseAngle = (float) Math.atan2(-(mouseY / 2), -(mouseX / 2));
    if (mouseAngle < 0) mouseAngle += (float) (PI * 2);

    boolean inSlice = circleSlice.inInSlice(Angle.newRadian(mouseAngle));
    boolean inRadius = parent.isMouseOverRel(mouseX, mouseY);
    LOGGER.debug(
        "WheelWidgetAction.isMouseOver: ({},{}), angle={}, inSlice={}, inRadius={}",
        mouseX,
        mouseY,
        mouseAngle,
        inSlice,
        inRadius);
    return inSlice && inRadius;
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public @NonNull NarrationPriority narrationPriority() {
    return NarrationPriority.HOVERED;
  }

  @Override
  public boolean isActive() {
    return NarratableEntry.super.isActive();
  }

  @Override
  public void updateNarration(@NonNull NarrationElementOutput builder) {
    // builder.put(NarrationPart.TITLE, "slice narration is not implemented");
  }

  private float smoothFunc(float x) {
    return x * x * (3f - 2f * x);
  }
}
