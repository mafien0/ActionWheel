package io.github.JumperOnJava.jjpizza.pizzamenu;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;

public class SubPizzaManager extends PizzaManager {
  public final String id;

  public SubPizzaManager(String id) {
    this.id = id;
    init();
  }

  @Override
  protected File getConfigFile() {
    return FabricLoader.getInstance()
        .getConfigDir()
        .resolve("jjpizza/sub/" + id + ".json")
        .toFile();
  }

  @Override
  public boolean matchesKey(int keyCode, int scanCode) {
    return false;
  }
}
