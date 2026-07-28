package io.github.JumperOnJava.jjpizza.pizzamenu.widgets.pizza;

import io.github.JumperOnJava.jjpizza.datatypes.CircleSlice;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public interface PizzaSlice {
  default void onRightClick() {}

  default void onLeftClick() {}

  default void onScroll(double scrollX, double scrollY) {}

  default int getBackgroundColor() {
    return ARGB.color(255, 0, 0, 0);
  }

  /**
   * Icon texture identifier. Can return null to not display anything
   *
   * @return
   */
  default ResourceLocation getIconTexture() {
    return null;
  }

  default Component getName() {
    return Component.empty();
  }

  /**
   * Piece of circle that this slice represents
   *
   * @return
   */
  CircleSlice getSlice();
}
