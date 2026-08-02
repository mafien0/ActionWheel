package cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable;

import static cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry.gap;

import cc.mafien0.actionWheel.actionWheel.wheels.WheelManager;
import cc.mafien0.actionWheel.core.gui.widgets.SubScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

class ActionEditScreen extends Screen {
  final RunnableAction action;

  protected ActionEditScreen(RunnableAction action) {
    super(Component.empty());
    this.action = action;
  }

  @Override
  protected void init() {

    var leftAction =
        new SubScreen(gap / 2, gap / 2 + 20, this.width / 2 - gap / 2, this.height - gap / 2 - 40);
    leftAction.setScreen(action.onLeftClick.getConfiguratorScreen());
    addRenderableWidget(leftAction);

    var leftCycleButton =
        new Button.Builder(
                Component.empty(),
                button -> {
                  action.onLeftClick =
                      WheelManager.actionTypeRegistry
                          .getNextFactoryForType(action.onLeftClick)
                          .apply(true);
                  RunnableScreen.setButtonType(button, action.onLeftClick);
                  leftAction.setScreen(action.onLeftClick.getConfiguratorScreen());
                  action.onLeftClick.setParent(action);
                })
            .size(width / 2 - gap / 2, 20)
            .pos(gap / 2, gap / 2)
            .build();

    RunnableScreen.setButtonType(leftCycleButton, action.onLeftClick);
    leftAction.setScreen(action.onLeftClick.getConfiguratorScreen());

    addRenderableWidget(leftCycleButton);

    var rightAction =
        new SubScreen(
            this.width / 2 + gap / 2,
            gap / 2 + 20,
            this.width / 2 - gap / 2,
            this.height - gap / 2 - 40);
    rightAction.setScreen(action.onLeftClick.getConfiguratorScreen());
    addRenderableWidget(rightAction);

    var rightCycleButton =
        new Button.Builder(
                Component.empty(),
                button -> {
                  action.onRightClick =
                      WheelManager.actionTypeRegistry
                          .getNextFactoryForType(action.onRightClick)
                          .apply(true);
                  RunnableScreen.setButtonType(button, action.onRightClick);
                  rightAction.setScreen(action.onRightClick.getConfiguratorScreen());
                  action.onRightClick.setParent(action);
                })
            .size(width / 2 - gap / 2, 20)
            .pos(width / 2 + gap / 2, gap / 2)
            .build();

    RunnableScreen.setButtonType(rightCycleButton, action.onRightClick);
    rightAction.setScreen(action.onRightClick.getConfiguratorScreen());

    addRenderableWidget(rightCycleButton);
  }
}
