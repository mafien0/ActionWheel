package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class NullActionProvider implements ConfigurableRunnable {
  public NullActionProvider() {}

  @Override
  public Screen getConfiguratorScreen() {
    return new Screen(Component.empty()) {
      public void extractRenderState(
          @NonNull GuiGraphicsExtractor context, int mx, int my, float d) {
        context.fill(0, 0, width, height, 0x3F220000);
        context.centeredText(
            Minecraft.getInstance().font, "No Action", width / 2, height / 2, 0xFFFFFFFF);
      }
    };
  }

  @Override
  public void run() {}

  @Override
  public ConfigurableRunnable copy() {
    return this;
  }
}
