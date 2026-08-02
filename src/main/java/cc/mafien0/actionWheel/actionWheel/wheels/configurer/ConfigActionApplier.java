package cc.mafien0.actionWheel.actionWheel.wheels.configurer;

import cc.mafien0.actionWheel.actionWheel.wheels.actions.ConfigurableWheelAction;
import net.minecraft.client.gui.screens.Screen;

public interface ConfigActionApplier {

  void setSliceConfigScreen(Screen screen);

  void rebuildSlices();

  void removeSlice(ConfigurableWheelAction targetAction);

  void splitSlice(ConfigurableWheelAction configurableWheelAction);
}
