package io.github.JumperOnJava.lavajumper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.JumperOnJava.lavajumper.gui.AskScreen;

import java.util.Iterator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixins {
  // SnIgnoreRenderAfterOverlay
  @Inject(
      method = "extractRenderState",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/gui/components/Renderable;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
          shift = At.Shift.AFTER
      )
  )
  private void cancelRenderAfterOverlay(
      GuiGraphicsExtractor graphics,
      int mouseX,
      int mouseY,
      float a,
      CallbackInfo ci,
      @Local Iterator<Renderable> iterator,
      @Local(name = "renderable") Renderable renderable
  ) {
    if (renderable instanceof AskScreen.OverlayScreen) {
      while (iterator.hasNext()) {
        iterator.next();
      }
    }
  }}
