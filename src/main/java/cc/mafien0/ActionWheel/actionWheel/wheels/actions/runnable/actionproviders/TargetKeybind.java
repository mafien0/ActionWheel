package cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.actionproviders;

import net.minecraft.network.chat.Component;

public interface TargetKeybind {
  Component getButtonText();

  String getId();

  boolean matches(String search);
}
