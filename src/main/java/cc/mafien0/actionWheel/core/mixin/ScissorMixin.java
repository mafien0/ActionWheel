package cc.mafien0.actionWheel.core.mixin;

import cc.mafien0.actionWheel.core.gui.widgets.ScrollListWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fix for scissor checks while rendering subscreen widgets. */
@Mixin(GuiGraphicsExtractor.class)
public abstract class ScissorMixin {

  @Inject(method = "containsPointInScissor", at = @At("HEAD"), cancellable = true)
  private void ignoreScissorContains(int x, int y, CallbackInfoReturnable<Boolean> cir) {
    if (ScrollListWidget.renderingEntries) {
      cir.setReturnValue(true);
    }
  }
}
