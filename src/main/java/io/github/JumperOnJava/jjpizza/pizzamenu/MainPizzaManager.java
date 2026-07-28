package io.github.JumperOnJava.jjpizza.pizzamenu;

import io.github.JumperOnJava.lavajumper.common.Binder;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class MainPizzaManager extends PizzaManager {
  public final KeyMapping pizzaKeybind;

  public MainPizzaManager() {
    init();
    pizzaKeybind = Binder.addBind("Open Pizza Menu", -1, this::openPizza);
  }

  @Override
  public boolean matchesKey(KeyEvent event) {
    return pizzaKeybind.matches(event);
  }

  protected File getConfigFile() {
    return FabricLoader.getInstance().getConfigDir().resolve("jjpizza/main.json").toFile();
  }
}
