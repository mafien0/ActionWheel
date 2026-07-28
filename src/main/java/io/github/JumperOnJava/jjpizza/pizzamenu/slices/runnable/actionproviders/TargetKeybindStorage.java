package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import java.util.Collection;
import net.minecraft.text.Text;

public interface TargetKeybindStorage {
  Collection<TargetKeybind> getKeyBindings();

  void setTargetID(String id);

  String getTargetId();

  Text getHoldText();

  void nextHoldMode();
}
