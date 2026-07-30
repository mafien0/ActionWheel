package cc.mafien0.ActionWheel.actionWheel.wheels.configurer;

import static cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry.gap;

import cc.mafien0.ActionWheel.actionWheel.datatypes.CircleSlice;
import cc.mafien0.ActionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import cc.mafien0.ActionWheel.actionWheel.wheels.actions.runnable.RunnableAction;
import cc.mafien0.ActionWheel.actionWheel.wheels.widgets.wheel.WheelAction;
import cc.mafien0.ActionWheel.actionWheel.wheels.widgets.wheel.WheelWidget;
import cc.mafien0.ActionWheel.core.common.Tr;
import cc.mafien0.ActionWheel.core.gui.AskScreen;
import cc.mafien0.ActionWheel.core.gui.widgets.SubScreen;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntireWheelConfiguratorScreen extends AskScreen<List<ConfigurableWheelAction>>
    implements ConfigActionApplier {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntireWheelConfiguratorScreen.class);
  private final List<EditorWheelAction> widgetSlices;
  private final List<WheelAction> deleteSlices;
  private final WheelWidget wheel;
  private final WheelWidget deleteWheel;
  private final LinkedList<ConfigurableWheelAction> editSlices;
  private SubScreen subScreen;

  public void rebuildSlices() {
    var l = new LinkedList<EditorWheelAction>();
    for (var slice : editSlices) {
      l.add(new EditorWheelAction(slice, this));
    }
    this.setWidgetSlices(l);
  }

  public EntireWheelConfiguratorScreen(
      List<ConfigurableWheelAction> input,
      Consumer<List<ConfigurableWheelAction>> onSuccess,
      Runnable onFail) {
    super(onSuccess, onFail);
    this.editSlices = new LinkedList<>();
    for (var t : input) {
      editSlices.add(t.copy());
    }
    this.widgetSlices = new LinkedList<>();
    this.deleteSlices = new LinkedList<>();
    wheel = new WheelWidget();
    deleteWheel = new WheelWidget();
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  @Override
  public void onClose() {
    super.onClose();
  }

  public void setWidgetSlices(List<? extends EditorWheelAction> widgetSlices) {
    this.widgetSlices.clear();
    this.widgetSlices.addAll(widgetSlices);
    deleteSlices.clear();
    for (var slice : widgetSlices) {
      // if(slice instanceof ConfiguringWheelAction targetSlice)
      {
        var delSlice =
            new WheelAction() {
              EditorWheelAction target;

              @Override
              public CircleSlice getSlice() {
                return slice.getSlice();
              }

              @Override
              public void onLeftClick() {
                target.remove();
              }

              @Override
              public int getBackgroundColor() {
                return 0xFFFF7057;
              }
            };
        delSlice.target = slice;
        deleteSlices.add(delSlice);
      }
    }
    deleteWheel.setupSlices(deleteSlices);
    wheel.setupSlices(widgetSlices);
  }

  public void init() {
    LOGGER.debug("EntireWheelConfiguratorScreen.init: {} slices", editSlices.size());
    var radius = (Math.min(width / 4, height / 2) * .8);
    wheel.setupSize((int) radius, (int) (radius / 4), width / 4, height / 2);
    wheel.setupSlices(widgetSlices);
    // deleteWheel.setupSize((int) (radius/16*17), (int) radius,width/4,height/2);
    deleteWheel.setupSize((int) (radius / 4), (int) radius / 8, width / 4, height / 2);
    deleteWheel.setupSlices(deleteSlices);
    addRenderableWidget(wheel);
    addRenderableWidget(deleteWheel);
    this.subScreen =
        new SubScreen(width / 2, 0, width / 2, height).setScreen(new Screen(Component.empty()) {});
    addRenderableWidget(subScreen);
    var hwidth = width / 2;
    addRenderableWidget(
        new Button.Builder(Tr.get("actionWheel.edit.save"), _ -> this.success(editSlices))
            .bounds(gap / 2, height - 20 - gap / 2, hwidth / 2 - gap, 20)
            .build());
    addRenderableWidget(
        new Button.Builder(Tr.get("actionWheel.edit.cancel"), _ -> this.fail())
            .bounds(gap / 2 + hwidth / 2, height - 20 - gap / 2, hwidth / 2 - gap, 20)
            .build());
    rebuildSlices();
  }

  public void setSliceConfiguratorScreen(Screen screen) {
    this.subScreen.setScreen(screen);
  }

  // public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
  //   extractBackground(context, mouseX, mouseY, delta);
  //   super.extractRenderState(context, mouseX, mouseY, delta);
  // }

  public void setSliceConfigScreen(Screen screen) {
    this.setSliceConfiguratorScreen(screen);
  }

  public void splitSlice(ConfigurableWheelAction clickedSlice) {
    LOGGER.debug("splitSlice called");
    CircleSlice circleSlice = clickedSlice.getSlice();
    editSlices.remove(clickedSlice);
    editSlices.add(
        new RunnableAction(
            "Empty action",
            new CircleSlice(circleSlice.startAngle(), circleSlice.getMidAngle()),
            clickedSlice.getManager()));
    editSlices.add(
        new RunnableAction(
            "Empty action",
            new CircleSlice(circleSlice.getMidAngle(), circleSlice.endAngle()),
            clickedSlice.getManager()));
    rebuildSlices();
  }

  @Override
  public void initClose() {
    if (this.minecraft.gui.screen() == this) this.minecraft.gui.setScreen(null);
    else super.initClose();
  }

  @Override
  public void removeSlice(ConfigurableWheelAction targetAction) {
    editSlices.remove(targetAction);
    if (editSlices.isEmpty()) {
      editSlices.add(
          new RunnableAction(
              "Empty action", CircleSlice.percent(0.125f, 0.375f), targetAction.getManager()));
    }
    rebuildSlices();
  }
}
