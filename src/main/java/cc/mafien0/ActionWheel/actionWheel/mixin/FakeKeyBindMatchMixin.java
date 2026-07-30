package cc.mafien0.ActionWheel.actionWheel.mixin;

import cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.actionproviders.KeybindingActionProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class FakeKeyBindMatchMixin {
  @Shadow @Final private String name;

  @Inject(method = "matches*", at = @At("HEAD"), cancellable = true)
  private void matchIfQueued(final KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    var id = this.name;
    if (KeybindingActionProvider.awaitingMatch.contains(this.name)) {
      cir.setReturnValue(true);
    }
    KeybindingActionProvider.awaitingMatch.remove(id);
  }
}
