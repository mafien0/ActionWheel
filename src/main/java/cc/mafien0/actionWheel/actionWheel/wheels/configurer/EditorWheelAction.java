package cc.mafien0.actionWheel.actionWheel.wheels.configurer;

import cc.mafien0.actionWheel.actionWheel.datatypes.CircleSlice;
import cc.mafien0.actionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import cc.mafien0.actionWheel.actionWheel.wheels.widgets.wheel.WheelAction;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EditorWheelAction implements WheelAction {
  private final ConfigurableWheelAction targetAction;
  private final Consumer<Screen> clickCallback;
  private final Runnable updateCallback;
  private final Consumer<ConfigurableWheelAction> rightClickCallback;
  private final Consumer<ConfigurableWheelAction> removeCallback;

  public EditorWheelAction(ConfigurableWheelAction targetAction, ConfigActionApplier thing) {
    this.targetAction = targetAction;
    this.clickCallback = thing::setSliceConfigScreen;
    this.rightClickCallback = thing::splitSlice;
    this.updateCallback = thing::rebuildSlices;
    this.removeCallback = thing::removeSlice;
  }

  @Override
  public void onLeftClick() {
    clickCallback.accept(targetAction.getConfiguratorScreen(updateCallback));
  }

  @Override
  public void onRightClick() {
    rightClickCallback.accept(targetAction);
  }

  public CircleSlice getSlice() {
    return targetAction.getSlice();
  }

  public void remove() {
    removeCallback.accept(targetAction);
  }

  public int getBackgroundColor() {
    return targetAction.getBackgroundColor();
  }

  @Override
  public Identifier getIconTexture() {
    return targetAction.getIconTexture();
  }

  @Override
  public Component getName() {
    return targetAction.getName();
  }
}
