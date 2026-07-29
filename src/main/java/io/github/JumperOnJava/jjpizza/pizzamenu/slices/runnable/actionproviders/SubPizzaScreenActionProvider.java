package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.pizzamenu.SubPizzaManager;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import io.github.JumperOnJava.lavajumper.common.FileReadWrite;
import io.github.JumperOnJava.lavajumper.common.Tr;
import io.github.JumperOnJava.lavajumper.gui.widgets.ScrollListWidget;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class SubPizzaScreenActionProvider implements ConfigurableRunnable {
  private String id = "";
  private transient SubPizzaManager pizza;

  public SubPizzaScreenActionProvider() {}

  @Override
  public Screen getConfiguratorScreen() {
    return new SubPizzaEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var sp = new SubPizzaScreenActionProvider();
    sp.id = id;
    return sp;
  }

  @Override
  public void run() {
    makeSurePizzaExists();
    pizza.openPizza(Minecraft.getInstance());
  }

  private void makeSurePizzaExists() {
    if (id.isEmpty()) {
      id = "random_name-" + new Random().nextInt(0, 0xFFFF);
    }

    if (pizza == null || !id.equals(pizza.id)) {
      pizza = new SubPizzaManager(id);
    }
  }

  private static class SubPizzaEditScreen extends Screen {
    private final SubPizzaScreenActionProvider target;
    private ScrollListWidget list;

    protected SubPizzaEditScreen(SubPizzaScreenActionProvider target) {
      super(Component.empty());
      this.target = target;
    }

    EditBox nameBox;

    public void init() {

      var button =
          new Button.Builder(Tr.get("jjpizza.subpizza.edit"), this::editSelectedPizza)
              .bounds(width / 2 + gap / 2, gap / 2, width / 2 - gap, 20)
              .build();
      button.active = false;
      addRenderableWidget(button);

      nameBox =
          new EditBox(minecraft.font, gap / 2, gap / 2, width / 2 - gap, 20, Component.empty());
      nameBox.setValue(target.id);
      nameBox.setResponder(
          s -> {
            updatePizzaId(s);
            button.active = !s.isBlank();
          });
      nameBox.setHint(Tr.get("jjpizza.subpizza.placeholder").withColor(CommonColors.GRAY));
      addRenderableWidget(nameBox);

      this.list =
          new ScrollListWidget(
              minecraft, width - gap, height - gap - 2, gap / 2, 22 + gap / 2, 20 + gap / 2);
      addRenderableWidget(list);
      updateList();
    }

    private void updateList() {
      FileReadWrite.write(
          FabricLoader.getInstance().getConfigDir().resolve("jjpizza/sub/hackfile.txt").toFile(),
          "borgir");
      var dir = FabricLoader.getInstance().getConfigDir().resolve("jjpizza/sub").toFile();
      var files = dir.listFiles();
      if (files == null) return;
      list.clearList();
      for (var file : files) {
        if (!file.getName().endsWith(".json")) continue;
        var filename = file.getName().replace(".json", "");
        var entry = new ScrollListWidget.ScrollListEntry();
        entry.addDrawableChild(
            new Button.Builder(
                    Component.literal(filename),
                    (_) -> {
                      target.id = filename;
                      nameBox.setValue(target.id);
                    })
                .bounds(gap / 2, 0, width - 40 - gap / 2, 20)
                .build(),
            false);
        entry.addDrawableChild(
            new Button.Builder(
                    Component.literal("X"),
                    _ -> {
                      //noinspection ResultOfMethodCallIgnored
                      file.delete();
                      updateList();
                    })
                .bounds(width - 40 + gap / 2, 0, 20, 20)
                .build(),
            false);
        list.addEntry(entry);
      }
    }

    private void editSelectedPizza(Button buttonWidget) {
      target.makeSurePizzaExists();
      Screen previousScreen = Minecraft.getInstance().gui.screen();
      var configScreen =
          target.pizza.getBuilderScreen(
              _ -> Minecraft.getInstance().gui.setScreen(previousScreen),
              () -> Minecraft.getInstance().gui.setScreen(previousScreen));
      Minecraft.getInstance().gui.setScreen(configScreen);
    }

    private void updatePizzaId(String s) {
      target.id = s;
    }
  }
}
