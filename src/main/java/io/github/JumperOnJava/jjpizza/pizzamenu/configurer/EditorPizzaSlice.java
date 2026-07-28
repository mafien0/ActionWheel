package io.github.JumperOnJava.jjpizza.pizzamenu.configurer;

import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaSlice;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EditorPizzaSlice implements PizzaSlice {
  private final ConfigurablePizzaSlice targetAction;
  private final Consumer<Screen> clickCallback;
  private final Runnable updateCallback;
  private final Consumer<ConfigurablePizzaSlice> rightClickCallback;
  private final Consumer<ConfigurablePizzaSlice> removeCallback;

  public EditorPizzaSlice(ConfigurablePizzaSlice targetAction, ConfigActionApplier thing) {
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
