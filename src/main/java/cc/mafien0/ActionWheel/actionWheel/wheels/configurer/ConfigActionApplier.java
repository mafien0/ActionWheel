package cc.mafien0.ActionWheel.actionWheel.wheels.configurer;

import cc.mafien0.ActionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import net.minecraft.client.gui.screens.Screen;

public interface ConfigActionApplier {

  void setSliceConfigScreen(Screen screen);

  void rebuildSlices();

  void removeSlice(ConfigurableWheelAction targetAction);

  void splitSlice(ConfigurableWheelAction configurableWheelAction);
}
