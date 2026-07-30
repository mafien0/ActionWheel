package cc.mafien0.ActionWheel.actionWheel.wheels;

import cc.mafien0.ActionWheel.core.common.Binder;
import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class MainWheelManager extends WheelManager {
  public final KeyMapping wheelKeybind;

  public MainWheelManager() {
    init();
    wheelKeybind = Binder.addBind("Open Action Wheel", -1, this::openWheel);
  }

  @Override
  public boolean matchesKey(KeyEvent event) {
    return wheelKeybind.matches(event);
  }

  protected File getConfigFile() {
    return FabricLoader.getInstance().getConfigDir().resolve("actionWheel/main.json").toFile();
  }
}
