package cc.mafien0.ActionWheel.actionWheel;

import cc.mafien0.ActionWheel.actionWheel.wheels.MainWheelManager;
import cc.mafien0.ActionWheel.actionWheel.wheels.WheelManager;
import cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry;
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
