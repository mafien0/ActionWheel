package io.github.JumperOnJava.lavajumper.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.JumperOnJava.lavajumper.gui.GuiHelper;
import io.github.JumperOnJava.lavajumper.gui.widgets.ScrollListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** fix for scissors so they work fine with Subscreen. */
@Mixin(GuiGraphics.class)
public abstract class ScissorMixin {
  @Shadow
  public abstract PoseStack pose();

  @ModifyArg(
    method = "enableScissor",
    at =
      @At(
        value = "INVOKE",
        target =
          "Lnet/minecraft/client/gui/GuiGraphics$ScissorStack;push(Lnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/gui/navigation/ScreenRectangle;"),
    index = 0)
  private ScreenRectangle enableScissor(ScreenRectangle rect) {
    var ul = GuiHelper.transformCoords(pose(), rect.left(), rect.top());
    var dr = GuiHelper.transformCoords(pose(), rect.right(), rect.bottom());
    return new ScreenRectangle((int) ul.x, (int) ul.y, (int) (dr.x - ul.x), (int) (dr.y - ul.y));
  }

  @Inject(method = "containsPointInScissor", at = @At("HEAD"), cancellable = true)
  private void ignoreScissorContains(int x, int y, CallbackInfoReturnable<Boolean> cir) {
    if (ScrollListWidget.renderingEntries) {
      cir.setReturnValue(true);
    }
  }
}
