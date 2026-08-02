package cc.mafien0.actionWheel.actionWheel;

import cc.mafien0.actionWheel.actionWheel.wheels.MainWheelManager;
import cc.mafien0.actionWheel.actionWheel.wheels.WheelManager;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry;
import net.fabricmc.api.ClientModInitializer;

public class PizzeriaApi implements ClientModInitializer {

  private static WheelManager mainManager;

  public static ActionTypeRegistry getRegistry() {
    if (mainManager == null) mainManager = new MainWheelManager();
    return WheelManager.actionTypeRegistry;
  }

  @Override
  public void onInitializeClient() {
    getRegistry();
  }
}
