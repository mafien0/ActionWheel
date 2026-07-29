package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.pizzamenu.PizzaManager;
import io.github.JumperOnJava.lavajumper.gui.widgets.SubScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

class ActionEditScreen extends Screen {
  final RunnableSlice pizzaAction;

  protected ActionEditScreen(RunnableSlice pizzaAction) {
    super(Component.empty());
    this.pizzaAction = pizzaAction;
  }

  @Override
  protected void init() {

    var leftAction =
        new SubScreen(gap / 2, gap / 2 + 20, this.width / 2 - gap / 2, this.height - gap / 2 - 40);
    leftAction.setScreen(pizzaAction.onLeftClick.getConfiguratorScreen());
    addRenderableWidget(leftAction);

    var leftCycleButton =
        new Button.Builder(
                Component.empty(),
                button -> {
                  pizzaAction.onLeftClick =
                      PizzaManager.actionTypeRegistry
                          .getNextFactoryForType(pizzaAction.onLeftClick)
                          .apply(true);
                  RunnableScreen.setButtonType(button, pizzaAction.onLeftClick);
                  leftAction.setScreen(pizzaAction.onLeftClick.getConfiguratorScreen());
                  pizzaAction.onLeftClick.setParent(pizzaAction);
                })
            .size(width / 2 - gap / 2, 20)
            .pos(gap / 2, gap / 2)
            .build();

    RunnableScreen.setButtonType(leftCycleButton, pizzaAction.onLeftClick);
    leftAction.setScreen(pizzaAction.onLeftClick.getConfiguratorScreen());

    addRenderableWidget(leftCycleButton);

    var rightAction =
        new SubScreen(
            this.width / 2 + gap / 2,
            gap / 2 + 20,
            this.width / 2 - gap / 2,
            this.height - gap / 2 - 40);
    rightAction.setScreen(pizzaAction.onLeftClick.getConfiguratorScreen());
    addRenderableWidget(rightAction);

    var rightCycleButton =
        new Button.Builder(
                Component.empty(),
                button -> {
                  pizzaAction.onRightClick =
                      PizzaManager.actionTypeRegistry
                          .getNextFactoryForType(pizzaAction.onRightClick)
                          .apply(true);
                  RunnableScreen.setButtonType(button, pizzaAction.onRightClick);
                  rightAction.setScreen(pizzaAction.onRightClick.getConfiguratorScreen());
                  pizzaAction.onRightClick.setParent(pizzaAction);
                })
            .size(width / 2 - gap / 2, 20)
            .pos(width / 2 + gap / 2, gap / 2)
            .build();

    RunnableScreen.setButtonType(rightCycleButton, pizzaAction.onRightClick);
    rightAction.setScreen(pizzaAction.onRightClick.getConfiguratorScreen());

    addRenderableWidget(rightCycleButton);
  }
}
