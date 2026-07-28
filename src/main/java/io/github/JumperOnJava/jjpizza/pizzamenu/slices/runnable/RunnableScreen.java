package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.datatypes.Angle;
import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.configurer.TextureListAsk;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import io.github.JumperOnJava.lavajumper.common.Tr;
import io.github.JumperOnJava.lavajumper.gui.AskScreen;
import io.github.JumperOnJava.lavajumper.gui.widgets.SliderWidget;
import io.github.JumperOnJava.lavajumper.gui.widgets.SubScreen;
import java.util.function.Consumer;
import net.minecraft.IdentifierException;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class RunnableScreen extends Screen {
  RunnableSlice pizzaAction;
  Runnable updateCallback;

  public RunnableScreen(RunnableSlice pizzaAction, Runnable updateCallback) {
    super(Component.empty());
    this.pizzaAction = pizzaAction;
    this.updateCallback = updateCallback;
  }

  public void init() {
    initDrawConfig();
    initColorConfig();
    initConfigSubScreens();
  }

  private void initConfigSubScreens() {
    var configScreen = new SubScreen(0, gap * 3 + 20 * 3, width, height - (gap * 3 + 20 * 2));
    configScreen.setScreen(new ActionEditScreen(pizzaAction));
    addRenderableWidget(configScreen);
  }

  private void initColorConfig() {
    var colorTexts =
        java.util.List.of(
            Tr.get("jjpizza.runnable.color.alpha").setStyle(Style.EMPTY.withItalic(true)),
            Tr.get("jjpizza.runnable.color.red").setStyle(Style.EMPTY.withColor(0xFFFF0000)),
            Tr.get("jjpizza.runnable.color.green").setStyle(Style.EMPTY.withColor(0xFF00FF00)),
            Tr.get("jjpizza.runnable.color.blue").setStyle(Style.EMPTY.withColor(0xFF0000FF)));
    for (int i = 0; i < 4; i++) {
      var colorField =
          new SliderWidget(
              gap + width / 4 * i,
              gap * 2 + 20 * 2,
              width / 4 - gap,
              20,
              colorTexts.get(i),
              255d,
              ((pizzaAction.color >> (3 - i) * 8) & 255),
              1);
      var cons =
          new Consumer<Double>() {
            int id;

            @Override
            public void accept(Double d) {
              {
                try {
                  int[] argb = {0, 0, 0, 0};
                  argb[0] = ARGB.alpha(pizzaAction.color);
                  argb[1] = ARGB.red(pizzaAction.color);
                  argb[2] = ARGB.green(pizzaAction.color);
                  argb[3] = ARGB.blue(pizzaAction.color);

                  argb[id] = (int) (double) d;

                  pizzaAction.color = ARGB.color(argb[0], argb[1], argb[2], argb[3]);
                  update();
                } catch (Exception ignored) {

                }
              }
              addRenderableWidget(colorField);
            }
          };
      cons.id = i;
      colorField.setChangedListener(cons);
      addRenderableWidget(colorField);
    }
  }

  private void initDrawConfig() {

    var nameField =
        new EditBox(
            minecraft.font, gap, gap, width / 4 - gap, 16, Component.translatable("name"));
    nameField.setValue(pizzaAction.name);
    nameField.setResponder(
        s -> {
          pizzaAction.name = s;
          update();
        });
    addRenderableWidget(nameField);
    var iconField =
        new EditBox(
            minecraft.font,
            gap + width / 4,
            gap,
            width / 4 * 2 - gap * 2,
            16,
            Component.translatable("name"));
    iconField.setMaxLength(Integer.MAX_VALUE);
    iconField.setValue(pizzaAction.icon.toString());
    iconField.setResponder(
        s -> {
          try {
            pizzaAction.icon = Identifier.parse(s);
          } catch (IdentifierException e) {
            iconField.setTextColor(0xffff7057);
          } finally {
            iconField.setTextColor(0xffe0e0e0);
          }
          update();
        });
    addRenderableWidget(iconField);
    var iconSelectField =
        new Button.Builder(
                Tr.get("jjpizza.runnable.iconselect"),
            _ -> AskScreen.ask(
                new TextureListAsk.Builder()
                    .onSuccess(i -> iconField.setValue(i.toString()))
                    .onFail(() -> {})
                    .build()))
            .pos(width / 4 * 3, gap / 2)
            .size(width / 4, 20)
            .build();
    addRenderableWidget(iconSelectField);

    var startAngleField =
        new SliderWidget(
            gap,
            gap + 20,
            width / 2 - gap,
            20,
            Tr.get("jjpizza.runnable.startangle"),
            360,
            Math.round(pizzaAction.getSlice().startAngle().getDegree()),
            5);
    startAngleField.setChangedListener(
        d -> {
          pizzaAction.setSlice(
              new CircleSlice(
                  Angle.newDegree((float) (double) d), pizzaAction.getSlice().endAngle()));
          update();
        });
    addRenderableWidget(startAngleField);
    var endAngleField =
        new SliderWidget(
            gap + width / 2,
            gap + 20,
            width / 2 - gap,
            20,
            Tr.get("jjpizza.runnable.endangle"),
            360,
            Math.round(pizzaAction.getSlice().endAngle().getDegree()),
            5);
    endAngleField.setChangedListener(
        d -> {
          pizzaAction.setSlice(
              new CircleSlice(
                  pizzaAction.circleSlice.startAngle(), Angle.newDegree((float) (double) d)));
          update();
        });
    addRenderableWidget(endAngleField);
  }

  public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    extractBackground(context, mouseX, mouseY, delta);
    super.extractRenderState(context, mouseX, mouseY, delta);
  }

  private void update() {
    updateCallback.run();
  }

  public static void setButtonType(Button button, ConfigurableRunnable obj) {
    button.setMessage(Tr.get("jjpizza.actions." + obj.getClass().getSimpleName()));
  }
}
