package io.github.JumperOnJava.jjpizza.pizzamenu.configurer;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.RunnableSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza.PizzaWidget;
import io.github.JumperOnJava.lavajumper.common.Tr;
import io.github.JumperOnJava.lavajumper.gui.AskScreen;
import io.github.JumperOnJava.lavajumper.gui.widgets.SubScreen;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntirePizzaConfiguratorScreen extends AskScreen<List<ConfigurablePizzaSlice>>
    implements ConfigActionApplier {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntirePizzaConfiguratorScreen.class);
  private final List<EditorPizzaSlice> widgetSlices;
  private final List<PizzaSlice> deleteSlices;
  private final PizzaWidget pizza;
  private final PizzaWidget deletePizza;
  private final LinkedList<ConfigurablePizzaSlice> editSlices;
  private SubScreen subScreen;

  public void rebuildSlices() {
    var l = new LinkedList<EditorPizzaSlice>();
    for (var slice : editSlices) {
      l.add(new EditorPizzaSlice(slice, this));
    }
    this.setWidgetSlices(l);
  }

  public EntirePizzaConfiguratorScreen(
      List<ConfigurablePizzaSlice> input,
      Consumer<List<ConfigurablePizzaSlice>> onSuccess,
      Runnable onFail) {
    super(onSuccess, onFail);
    this.editSlices = new LinkedList<>();
    for (var t : input) {
      editSlices.add(t.copy());
    }
    this.widgetSlices = new LinkedList<>();
    this.deleteSlices = new LinkedList<>();
    pizza = new PizzaWidget();
    deletePizza = new PizzaWidget();
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  @Override
  public void onClose() {
    super.onClose();
  }

  public void setWidgetSlices(List<? extends EditorPizzaSlice> widgetSlices) {
    this.widgetSlices.clear();
    this.widgetSlices.addAll(widgetSlices);
    deleteSlices.clear();
    for (var slice : widgetSlices) {
      // if(slice instanceof ConfiguringPizzaSlice targetSlice)
      {
        var delSlice =
            new PizzaSlice() {
              EditorPizzaSlice target;

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
    deletePizza.setupSlices(deleteSlices);
    pizza.setupSlices(widgetSlices);
  }

  public void init() {
    LOGGER.debug("EntirePizzaConfiguratorScreen.init: {} slices", editSlices.size());
    var radius = (Math.min(width / 4, height / 2) * .8);
    pizza.setupSize((int) radius, (int) (radius / 4), width / 4, height / 2);
    pizza.setupSlices(widgetSlices);
    // deletePizza.setupSize((int) (radius/16*17), (int) radius,width/4,height/2);
    deletePizza.setupSize((int) (radius / 4), (int) radius / 8, width / 4, height / 2);
    deletePizza.setupSlices(deleteSlices);
    addRenderableWidget(pizza);
    addRenderableWidget(deletePizza);
    this.subScreen =
        new SubScreen(width / 2, 0, width / 2, height).setScreen(new Screen(Component.empty()) {});
    addRenderableWidget(subScreen);
    var hwidth = width / 2;
    addRenderableWidget(
        new Button.Builder(Tr.get("jjpizza.edit.save"), _ -> this.success(editSlices))
            .bounds(gap / 2, height - 20 - gap / 2, hwidth / 2 - gap, 20)
            .build());
    addRenderableWidget(
        new Button.Builder(Tr.get("jjpizza.edit.cancel"), _ -> this.fail())
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

  public void splitSlice(ConfigurablePizzaSlice clickedSlice) {
    LOGGER.debug("splitSlice called");
    CircleSlice circleSlice = clickedSlice.getSlice();
    editSlices.remove(clickedSlice);
    editSlices.add(
        new RunnableSlice(
            "Empty action",
            new CircleSlice(circleSlice.startAngle(), circleSlice.getMidAngle()),
            clickedSlice.getManager()));
    editSlices.add(
        new RunnableSlice(
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
  public void removeSlice(ConfigurablePizzaSlice targetAction) {
    editSlices.remove(targetAction);
    if (editSlices.isEmpty()) {
      editSlices.add(
          new RunnableSlice(
              "Empty action", CircleSlice.percent(0.125f, 0.375f), targetAction.getManager()));
    }
    rebuildSlices();
  }
}
