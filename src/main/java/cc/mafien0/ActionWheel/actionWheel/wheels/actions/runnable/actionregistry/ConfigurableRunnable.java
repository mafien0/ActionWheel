package cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.actionregistry;

import cc.mafien0.ActionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import net.minecraft.client.gui.screens.Screen;

public interface ConfigurableRunnable extends Runnable {
  /**
   * ignore if you dont need parent slice
   *
   * @param configurableRunnable
   */
  default void setParent(ConfigurableWheelAction configurableRunnable) {}

  Screen getConfiguratorScreen();

  ConfigurableRunnable copy();
}
