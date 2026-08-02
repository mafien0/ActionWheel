package cc.mafien0.actionWheel.actionWheel.wheels;

import cc.mafien0.actionWheel.actionWheel.datatypes.CircleSlice;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.RunnableAction;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry;
import cc.mafien0.actionWheel.actionWheel.wheels.configurer.EntireWheelConfiguratorScreen;
import cc.mafien0.actionWheel.core.common.FileReadWrite;
import cc.mafien0.actionWheel.core.gui.AskScreen;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WheelManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(WheelManager.class);
  List<RunnableAction> actions = new ArrayList<>();
  public static ActionTypeRegistry actionTypeRegistry = new ActionTypeRegistry();

  public void init() {
    LOGGER.info("WheelManager.init");
    actions = load();
  }

  public void openWheel(Minecraft client) {
    LOGGER.debug("Opening action wheel with {} slices", actions.size());
    client.gui.setScreen(new WheelScreen(actions, getBuilderScreen(), this));
  }

  public AskScreen<List<ConfigurableWheelAction>> getBuilderScreen() {
    return new EntireWheelConfiguratorScreen(new LinkedList<>(actions), this::setSlices, () -> {});
  }

  public AskScreen<List<ConfigurableWheelAction>> getBuilderScreen(
      Consumer<List<ConfigurableWheelAction>> onSuccess, Runnable onFail) {
    return new EntireWheelConfiguratorScreen(
        new LinkedList<>(actions),
        (c) -> {
          this.setSlices(c);
          onSuccess.accept(c);
        },
        onFail);
  }

  public void save() {
    LOGGER.debug("WheelManager.save: {} slices", actions.size());
    try {
      FileReadWrite.write(getConfigFile(), actionTypeRegistry.getGson().toJson(actions));
    } catch (Exception e) {
      LOGGER.error("Failed to save wheel config", e);
    }
  }

  public List<RunnableAction> load() {
    LOGGER.info("WheelManager.load");
    if (readConfig().isEmpty()) {
      LOGGER.info("Config empty, creating defaults");
      actions.add(new RunnableAction("Empty action", CircleSlice.percent(0, .25f), this));
      actions.add(new RunnableAction("Empty action", CircleSlice.percent(.25f, .5f), this));
      actions.add(new RunnableAction("Empty action", CircleSlice.percent(.5f, .75f), this));
      actions.add(new RunnableAction("Empty action", CircleSlice.percent(.75f, 1f), this));
      save();
    }
    List<RunnableAction> l =
        actionTypeRegistry
            .getGson()
            .fromJson(readConfig(), new TypeToken<ArrayList<RunnableAction>>() {}.getType());
    LOGGER.info("Loaded {} slices", l.size());
    l.forEach(s -> s.setManager(this));
    return l;
  }

  private String readConfig() {
    return FileReadWrite.read(getConfigFile());
  }

  protected abstract File getConfigFile();

  private void setSlices(List<ConfigurableWheelAction> configuration) {
    actions.clear();
    configuration.forEach(
        action -> {
          if (action instanceof RunnableAction runnableWheelAction)
            actions.add(runnableWheelAction);
        });
    save();
  }

  public abstract boolean matchesKey(KeyEvent event);
}
