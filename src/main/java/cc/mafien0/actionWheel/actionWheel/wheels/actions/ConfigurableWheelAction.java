package cc.mafien0.actionWheel.actionWheel.wheels.actions;

import cc.mafien0.actionWheel.actionWheel.wheels.WheelManager;
import cc.mafien0.actionWheel.actionWheel.wheels.widgets.wheel.WheelAction;
import net.minecraft.client.gui.screens.Screen;

public interface ConfigurableWheelAction extends WheelAction {
  Screen getConfiguratorScreen(Runnable updateCallback);

  WheelManager getManager();

  void setManager(WheelManager manager);

  ConfigurableWheelAction copy();
}
