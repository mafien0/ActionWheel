package cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionproviders;

import static cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry.gap;

import cc.mafien0.actionWheel.actionWheel.wheels.SubWheelManager;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ConfigurableRunnable;
import cc.mafien0.actionWheel.core.common.FileReadWrite;
import cc.mafien0.actionWheel.core.common.Tr;
import cc.mafien0.actionWheel.core.gui.widgets.ScrollListWidget;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class SubWheelScreenActionProvider implements ConfigurableRunnable {
  private String id = "";
  private transient SubWheelManager wheel;

  public SubWheelScreenActionProvider() {}

  @Override
  public Screen getConfiguratorScreen() {
    return new SubWheelEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var sp = new SubWheelScreenActionProvider();
    sp.id = id;
    return sp;
  }

  @Override
  public void run() {
    makeSureWheelExists();
    wheel.openWheel(Minecraft.getInstance());
  }

  private void makeSureWheelExists() {
    if (id.isEmpty()) {
      id = "random_name-" + new Random().nextInt(0, 0xFFFF);
    }

    if (wheel == null || !id.equals(wheel.id)) {
      wheel = new SubWheelManager(id);
    }
  }

  private static class SubWheelEditScreen extends Screen {
    private final SubWheelScreenActionProvider target;
    private ScrollListWidget list;

    protected SubWheelEditScreen(SubWheelScreenActionProvider target) {
      super(Component.empty());
      this.target = target;
    }

    EditBox nameBox;

    public void init() {

      var button =
          new Button.Builder(Tr.get("actionWheel.subwheel.edit"), this::editSelectedWheel)
              .bounds(width / 2 + gap / 2, gap / 2, width / 2 - gap, 20)
              .build();
      button.active = false;
      addRenderableWidget(button);

      nameBox =
          new EditBox(minecraft.font, gap / 2, gap / 2, width / 2 - gap, 20, Component.empty());
      nameBox.setValue(target.id);
      nameBox.setResponder(
          s -> {
            updateWheelId(s);
            button.active = !s.isBlank();
          });
      nameBox.setHint(Tr.get("actionWheel.subwheel.placeholder").withColor(CommonColors.GRAY));
      addRenderableWidget(nameBox);

      this.list =
          new ScrollListWidget(
              minecraft, width - gap, height - gap - 2, gap / 2, 22 + gap / 2, 20 + gap / 2);
      addRenderableWidget(list);
      updateList();
    }

    private void updateList() {
      FileReadWrite.write(
          FabricLoader.getInstance()
              .getConfigDir()
              .resolve("actionWheel/sub/hackfile.txt")
              .toFile(),
          "borgir");
      var dir = FabricLoader.getInstance().getConfigDir().resolve("actionWheel/sub").toFile();
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

    private void editSelectedWheel(Button buttonWidget) {
      target.makeSureWheelExists();
      Screen previousScreen = Minecraft.getInstance().gui.screen();
      var configScreen =
          target.wheel.getBuilderScreen(
              _ -> Minecraft.getInstance().gui.setScreen(previousScreen),
              () -> Minecraft.getInstance().gui.setScreen(previousScreen));
      Minecraft.getInstance().gui.setScreen(configScreen);
    }

    private void updateWheelId(String s) {
      target.id = s;
    }
  }
}
