package cc.mafien0.ActionWheel.actionWheel.wheels.widgets.wheel;

import cc.mafien0.ActionWheel.actionWheel.datatypes.CircleSlice;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public interface WheelAction {
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
  default Identifier getIconTexture() {
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
