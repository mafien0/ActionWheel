package io.github.JumperOnJava.jjpizza.pizzamenu;

import com.google.gson.reflect.TypeToken;
import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.configurer.EntirePizzaConfiguratorScreen;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.RunnableSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry;
import io.github.JumperOnJava.lavajumper.common.FileReadWrite;
import io.github.JumperOnJava.lavajumper.gui.AskScreen;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PizzaManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(PizzaManager.class);
  List<RunnableSlice> actions = new ArrayList<>();
  public static ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry();

  public void init() {
    LOGGER.info("PizzaManager.init");
    actions = load();
  }

  public void openPizza(Minecraft client) {
    LOGGER.debug("Opening pizza wheel with {} slices", actions.size());
    client.gui.setScreen(new PizzaScreen(actions, getBuilderScreen(), this));
  }

  public AskScreen<List<ConfigurablePizzaSlice>> getBuilderScreen() {
    return new EntirePizzaConfiguratorScreen(new LinkedList<>(actions), this::setSlices, () -> {});
  }

  public AskScreen<List<ConfigurablePizzaSlice>> getBuilderScreen(
      Consumer<List<ConfigurablePizzaSlice>> onSuccess, Runnable onFail) {
    return new EntirePizzaConfiguratorScreen(
        new LinkedList<>(actions),
        (c) -> {
          this.setSlices(c);
          onSuccess.accept(c);
        },
        onFail);
  }

  public void save() {
    LOGGER.debug("PizzaManager.save: {} slices", actions.size());
    try {
      FileReadWrite.write(getConfigFile(), actionTypeRegistry.getGson().toJson(actions));
    } catch (Exception e) {
      LOGGER.error("Failed to save pizza config", e);
    }
  }

  public List<RunnableSlice> load() {
    LOGGER.info("PizzaManager.load");
    if (readConfig().isEmpty()) {
      LOGGER.info("Config empty, creating defaults");
      actions.add(new RunnableSlice("Empty action", CircleSlice.percent(0, .25f), this));
      actions.add(new RunnableSlice("Empty action", CircleSlice.percent(.25f, .5f), this));
      actions.add(new RunnableSlice("Empty action", CircleSlice.percent(.5f, .75f), this));
      actions.add(new RunnableSlice("Empty action", CircleSlice.percent(.75f, 1f), this));
      save();
    }
    List<RunnableSlice> l =
        actionTypeRegistry
            .getGson()
            .fromJson(readConfig(), new TypeToken<ArrayList<RunnableSlice>>() {}.getType());
    LOGGER.info("Loaded {} slices", l.size());
    l.forEach(s -> s.setManager(this));
    return l;
  }

  private String readConfig() {
    return FileReadWrite.read(getConfigFile());
  }

  protected abstract File getConfigFile();

  private void setSlices(List<ConfigurablePizzaSlice> configuration) {
    actions.clear();
    configuration.forEach(
        pizzaSlice -> {
          if (pizzaSlice instanceof RunnableSlice runnablePizzaSlice)
            actions.add(runnablePizzaSlice);
        });
    save();
  }

  public abstract boolean matchesKey(KeyEvent event);
}
