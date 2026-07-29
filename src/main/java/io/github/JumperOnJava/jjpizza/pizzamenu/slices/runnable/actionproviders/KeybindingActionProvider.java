package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import io.github.JumperOnJava.lavajumper.common.Tr;
import io.github.JumperOnJava.lavajumper.gui.widgets.ScrollListWidget;
import java.util.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class KeybindingActionProvider implements ConfigurableRunnable, TargetKeybindStorage {
  public static Set<String> awaitingMatch = new HashSet<>();
  private String targetKeyBindingID = "";
  private boolean hold = false;

  public KeybindingActionProvider() {}

  private KeyMapping getTargetKeyBinding() {
    for (var kb : Minecraft.getInstance().options.keyMappings) {
      if (kb.getName().equals(targetKeyBindingID)) return kb;
    }
    return null;
  }

  @Override
  public Screen getConfiguratorScreen() {
    return new KeyBindingEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var kb = new KeybindingActionProvider();
    kb.hold = hold;
    if (targetKeyBindingID == null) targetKeyBindingID = "";
    kb.targetKeyBindingID = targetKeyBindingID;
    return kb;
  }

  @Override
  public void run() {
    var targetKeyBinding = getTargetKeyBinding();
    if (targetKeyBinding == null) return;
    if (hold) {
      targetKeyBinding.setDown(!targetKeyBinding.isDown());
    } else {
      awaitingMatch.add(targetKeyBindingID);
      targetKeyBinding.clickCount++;
      var client = Minecraft.getInstance();

      // Dummy key, needed to trigger `KeyMapping.matches`
      // Which is injected with custom matcher
      client.keyboardHandler.keyPress(client.getWindow().handle(), 1, new KeyEvent(-1, -1, -1));
    }
  }

  public List<TargetKeybind> getKeyBindings() {
    var l = Minecraft.getInstance().options.keyMappings;
    List<TargetKeybind> keybinds = new ArrayList<>();
    for (var k : l) {
      keybinds.add(new VanillaKBWrapper(k));
    }
    return keybinds;
  }

  @Override
  public void setTargetID(String id) {
    this.targetKeyBindingID = id;
  }

  @Override
  public String getTargetId() {
    return targetKeyBindingID;
  }

  @Override
  public Component getHoldText() {
    return Tr.get("jjpizza.keybind.hold." + (hold ? "on" : "off"));
  }

  @Override
  public void nextHoldMode() {
    hold = !hold;
    var targetKeyBinding = getTargetKeyBinding();
    if (targetKeyBinding == null) return;
    targetKeyBinding.release();
  }

  private record VanillaKBWrapper(KeyMapping keybind) implements TargetKeybind {

    public Component getButtonText() {
      return keybind
          .getCategory()
          .label()
          .copy()
          .append(" : ")
          .append(Component.translatable(keybind.getName()));
    }

    @Override
    public String getId() {
      return keybind.getName();
    }

    @Override
    public boolean matches(String search) {
      search = search.toLowerCase();
      return keybind.getName().toLowerCase().contains(search)
          || I18n.get(keybind.getName()).toLowerCase().contains(search);
    }
  }

  public static class KeyBindingEditScreen extends Screen {
    private final TargetKeybindStorage target;

    protected KeyBindingEditScreen(TargetKeybindStorage target) {
      super(Component.empty());
      this.target = target;
    }

    public void init() {
      var listWidget = new ScrollListWidget(minecraft, width, height - 24 * 2, 0, 24, 22);
      var searchBox = new EditBox(minecraft.font, 2, 2, width - 4, 20, Component.empty());
      searchBox.setResponder(t -> rebuildList(listWidget, t));
      addRenderableWidget(searchBox);
      addRenderableWidget(listWidget);
      rebuildList(listWidget, "");
      var holdModeButton =
          new Button.Builder(target.getHoldText(), this::holdButton)
              .size(width - ActionTypeRegistry.gap / 2, 20)
              .pos(0, height - 20 - ActionTypeRegistry.gap / 2)
              .build();
      addRenderableWidget(holdModeButton);
    }

    private void rebuildList(ScrollListWidget listWidget, String s) {
      listWidget.clearList();
      listWidget.setScrollAmount(0);
      for (TargetKeybind keybind : target.getKeyBindings()) {
        if (!keybind.matches(s)) continue;
        var listEntry = new ScrollListWidget.ScrollListEntry();
        listWidget.addEntry(listEntry);
        Component buttonText = keybind.getButtonText();
        var activateButton =
            new Button.Builder(
                    buttonText,
                    _ -> {
                      listEntry.setMeActive();
                      target.setTargetID(keybind.getId());
                    })
                .width(width - ActionTypeRegistry.gap)
                .build();
        listEntry.addDrawableChild(activateButton, true);
        if (keybind.getId().equals(target.getTargetId())) listWidget.setSelectedEntry(listEntry);
      }
    }

    private void holdButton(Button buttonWidget) {
      target.nextHoldMode();
      buttonWidget.setMessage(target.getHoldText());
    }
  }
}
