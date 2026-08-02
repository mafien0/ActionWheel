package cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionproviders;

import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ConfigurableRunnable;
import cc.mafien0.actionWheel.core.common.Tr;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import java.util.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MalilibActionProvider implements ConfigurableRunnable, TargetKeybindStorage {
  public static Set<String> awaitingMatch = new HashSet<>();
  private String targetKeyBindingID = "";
  private KeyAction keyActionType = KeyAction.BOTH;

  public MalilibActionProvider(Boolean isReal) {}

  private IHotkey getTargetKeyBinding() {
    for (var kbgroup : InputEventHandler.getKeybindManager().getKeybindCategories()) {
      String prefix = kbgroup.getModName() + "." + kbgroup.getCategory();
      for (IHotkey key : kbgroup.getHotkeys()) {
        if (new IdentifiedHotkey(key, kbgroup).id.equals(targetKeyBindingID)) {
          return key;
        }
      }
    }
    return null;
  }

  @Override
  public Screen getConfiguratorScreen() {
    return new KeybindingActionProvider.KeyBindingEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var ml = new MalilibActionProvider(false);
    ml.keyActionType = this.keyActionType;
    ml.targetKeyBindingID = targetKeyBindingID;
    return ml;
  }

  @Override
  public void run() {
    try {
      var targetKeyBinding = getTargetKeyBinding();
      if (targetKeyBinding == null) return;
      Objects.requireNonNull(((KeybindMulti) targetKeyBinding.getKeybind()).getCallback())
          .onKeyAction(keyActionType, targetKeyBinding.getKeybind());
    } catch (Exception ignored) {
    }
  }

  public List<TargetKeybind> getKeyBindings() {
    List<TargetKeybind> hotkeys = new LinkedList<>();
    for (var kbgroup : InputEventHandler.getKeybindManager().getKeybindCategories()) {
      String prefix = kbgroup.getModName() + "." + kbgroup.getCategory();
      for (IHotkey key : kbgroup.getHotkeys()) {
        hotkeys.add(new IdentifiedHotkey(key, kbgroup));
      }
    }
    return hotkeys;
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
    return Tr.get("actionWheel.keybind.mali.hold." + keyActionType);
  }

  public void nextHoldMode() {
    var values = KeyAction.values();
    keyActionType = values[(keyActionType.ordinal() + 1) % values.length];
  }

  public static boolean singleCategoryInMod(KeybindCategory target) {
    int categoryCount = 0;
    for (var category : InputEventHandler.getKeybindManager().getKeybindCategories()) {
      if (category.getModName().equals(target.getModName())) {
        categoryCount++;
      }
    }
    return categoryCount == 1;
  }

  private static class IdentifiedHotkey implements TargetKeybind {
    private final KeybindCategory category;
    public final String id;
    public final IHotkey hotkey;

    public IdentifiedHotkey(IHotkey hotkey, KeybindCategory category) {
      this.hotkey = hotkey;
      this.category = category;
      String prefix = category.getModName() + "." + category.getCategory();
      id = prefix + "." + hotkey.getName();
    }

    @Override
    public Component getButtonText() {
      String categoryText = "";
      if (!singleCategoryInMod(category)) {
        category.getCategory();
      }
      return Component.literal(String.format("%s: %s", category.getModName(), hotkey.getName()));
    }

    @Override
    public String getId() {
      return id;
    }

    public boolean matches(String search) {
      search = search.toLowerCase();
      return category.getModName().toLowerCase().contains(search)
          || category.getCategory().toLowerCase().contains(search)
          || hotkey.getName().toLowerCase().contains(search);
    }
  }
}
