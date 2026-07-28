package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import java.util.Collection;
import net.minecraft.network.chat.Component;

public interface TargetKeybindStorage {
  Collection<TargetKeybind> getKeyBindings();

  void setTargetID(String id);

  String getTargetId();

  Component getHoldText();

  void nextHoldMode();
}
