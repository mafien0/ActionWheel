package io.github.JumperOnJava.lavajumper.mixin;

import io.github.JumperOnJava.lavajumper.gui.AskScreen;
import io.github.JumperOnJava.lavajumper.gui.widgets.SubScreen;
import java.util.Iterator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Screen.class)
public class ScreenMixins {
  // SnIgnoreRenderAfterOverlay
  @Inject(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              shift = At.Shift.AFTER,
              target =
                  "Lnet/minecraft/client/gui/components/Renderable;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"),
      locals = LocalCapture.CAPTURE_FAILHARD)
  public void cancelRenderAfterOverlay(
      GuiGraphics context,
      int mouseX,
      int mouseY,
      float delta,
      CallbackInfo ci,
      Iterator<Renderable> var5,
      Renderable drawable) {
    if (drawable instanceof AskScreen.OverlayScreen) {
      while (var5.hasNext()) var5.next();
    }
  }

  @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
  public void disableBlur(CallbackInfo ci) {
    if (SubScreen.blurDisabled) {
      ci.cancel();
    }
  }
}
