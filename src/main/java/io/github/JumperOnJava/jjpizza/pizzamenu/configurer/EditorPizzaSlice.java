package io.github.JumperOnJava.jjpizza.pizzamenu.configurer;

import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaSlice;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EditorPizzaSlice implements PizzaSlice {
  private final ConfigurablePizzaSlice targetAction;
  private final Consumer<Screen> clickCallback;
  private final Runnable updateCallback;
  private final Consumer<ConfigurablePizzaSlice> rightClickCallback;
  private final Consumer<ConfigurablePizzaSlice> removeCallback;

  public EditorPizzaSlice(ConfigurablePizzaSlice targetAction, ConfigActionApplier athing) {
    this.targetAction = targetAction;
    this.clickCallback = athing::setSliceConfigScreen;
    this.rightClickCallback = athing::splitSlice;
    this.updateCallback = athing::rebuildSlices;
    this.removeCallback = athing::removeSlice;
  }

  @Override
  public void onLeftClick() {
    clickCallback.accept(targetAction.getConfiguratorScreen(updateCallback::run));
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
  public ResourceLocation getIconTexture() {
    return targetAction.getIconTexture();
  }

  @Override
  public Component getName() {
    return targetAction.getName();
  }
}
