package cc.mafien0.actionWheel.actionWheel.wheels;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.input.KeyEvent;

public class SubWheelManager extends WheelManager {

  public final String id;

  public SubWheelManager(String id) {
    this.id = id;
    init();
  }

  @Override
  protected File getConfigFile() {
    return FabricLoader.getInstance()
      .getConfigDir()
      .resolve("actionWheel/sub/" + id + ".json")
      .toFile();
  }

  @Override
  public boolean matchesKey(KeyEvent event) {
    return false;
  }
}
