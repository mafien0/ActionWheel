package cc.mafien0.ActionWheel.core.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import org.jspecify.annotations.NonNull;

public class GuiHelper {

  public static Vec3 transformCoords(Matrix3x2fStack matrices, Vec3 vec3d) {
    return transformCoords(matrices, vec3d.x, vec3d.y);
  }

  public static Vec3 transformCoords(Matrix3x2fStack matrices, double x, double y) {
    Vector2f result = matrices.transformPosition((float) x, (float) y, new Vector2f());
    return new Vec3(result.x, result.y, 0);
  }

  public static void renderSides(
      GuiGraphicsExtractor context, int x, int y, int width, int height) {
    y -= 1;
    height += 1;
    context.fill(x - 7, y - 7, x + width + 7, y + height + 7, 0xFF000000);
    context.fill(x - 6, y - 6, x + width + 6, y + height + 6, 0xFFC6C6C6);
    context.fill(x - 6, y - 6, x + width + 4, y + height + 4, 0xFFFFFFFF);
    context.fill(x - 4, y - 4, x + width + 6, y + height + 6, 0xFF555555);
    context.fill(x - 4, y - 4, x + width + 4, y + height + 4, 0xFFC6C6C6);
  }

  public static TestScreen TestScreen(int color) {
    return new TestScreen(color);
  }

  public static class TestScreen extends Screen {
    int color;

    public TestScreen(int color) {
      super(Component.empty());
      this.color = color;
    }

    @Override
    public void extractRenderState(
        @NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      super.extractRenderState(context, mouseX, mouseY, delta);
      context.fill(0, 0, width, height, color);
    }
  }
}
