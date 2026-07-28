package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import net.minecraft.network.chat.Component;

public interface TargetKeybind {
  Component getButtonText();

  String getId();

  boolean matches(String search);
}
