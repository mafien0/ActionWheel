package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable;

import com.google.gson.annotations.Expose;
import io.github.JumperOnJava.jjpizza.datatypes.Angle;
import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.PizzaManager;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders.NullActionProvider;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class RunnableSlice implements ConfigurablePizzaSlice {
  @Expose CircleSlice circleSlice;
  @Expose String name;
  @Expose int color;
  @Expose ConfigurableRunnable onLeftClick = new NullActionProvider(false);
  @Expose ConfigurableRunnable onRightClick = new NullActionProvider(false);
  @Expose ResourceLocation icon = ResourceLocation.parse("textures/item/diamond.png");

  @Override
  public PizzaManager getManager() {
    return manager;
  }

  public void setManager(PizzaManager manager) {
    this.manager = manager;
  }

  @Override
  public ConfigurablePizzaSlice copy() {
    var newSlice = new RunnableSlice(this.name, this.circleSlice, manager);
    newSlice.circleSlice = new CircleSlice(circleSlice.startAngle, circleSlice.endAngle);
    newSlice.icon = this.icon;
    newSlice.onLeftClick = this.onLeftClick.copy();
    newSlice.onRightClick = this.onRightClick.copy();
    newSlice.color = this.color;
    return newSlice;
  }

  transient PizzaManager manager;

  public RunnableSlice(String name, CircleSlice circleSlice, PizzaManager manager) {
    this.name = name;
    setSlice(circleSlice);
    var r = new Random();
    this.color = ARGB.color(255, r.nextInt(255), r.nextInt(255), r.nextInt(255));
    // ActionTextRenderer.sendChatMessage(Integer.toHexString(this.color));
    this.manager = manager;
  }

  @Override
  public void onLeftClick() {
    Minecraft.getInstance().setScreen(null);
    onLeftClick.run();
  }

  @Override
  public void onRightClick() {
    Minecraft.getInstance().setScreen(null);
    onRightClick.run();
  }

  public Component getName() {
    return Component.literal(name);
  }

  public ResourceLocation getIconTexture() {
    return icon;
  }

  @Override
  public CircleSlice getSlice() {
    return this.circleSlice;
  }

  public void setSlice(CircleSlice inCircleSlice) {
    if (Math.abs(inCircleSlice.startAngle.getDegree() - inCircleSlice.endAngle.getDegree()) < 5)
      this.circleSlice =
          new CircleSlice(
              inCircleSlice.startAngle.add(Angle.newDegree(-5)),
              inCircleSlice.endAngle.add(Angle.newDegree(5)));
    else this.circleSlice = inCircleSlice;
  }

  public int getBackgroundColor() {
    return color;
  }

  public Screen getConfiguratorScreen(Runnable updateCallback) {
    return new RunnableScreen(this, updateCallback);
  }
}
