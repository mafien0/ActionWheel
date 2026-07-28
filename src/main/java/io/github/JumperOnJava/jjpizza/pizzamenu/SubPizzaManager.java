package io.github.JumperOnJava.jjpizza.pizzamenu;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.input.KeyEvent;

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
  public boolean matchesKey(KeyEvent event) {
    return false;
  }
}
