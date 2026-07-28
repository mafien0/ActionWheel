package io.github.JumperOnJava.lavajumper.common;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class Binder {
  /**
   * Adds bind
   *
   * @param displayName
   * @param category
   * @param defaultKey
   * @param callback
   */
  public static KeyMapping addBind(
      String displayName, String category, int defaultKey, Consumer<Minecraft> callback) {
    var bind =
        new KeyMapping(
            displayName, InputConstants.Type.KEYSYM, defaultKey, category); // GLFW.GLFW_KEY_DOWN
    KeyBindingHelper.registerKeyBinding(bind);
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (bind.consumeClick()) {
            callback.accept(client);
          }
        });
    return bind;
  }

  /**
   * Adds bind under "LavaJumper" category
   *
   * @param displayName
   * @param defaultKey
   * @param callback
   * @return
   * @api
   */
  public static KeyMapping addBind(
      String displayName, int defaultKey, Consumer<Minecraft> callback) {
    return addBind(displayName, "LavaJumper", defaultKey, callback);
  }

  /**
   * Adds bind without default key
   *
   * @param displayName
   * @param category
   * @param callback
   */
  public static KeyMapping addBind(
      String displayName, String category, Consumer<Minecraft> callback) {
    return addBind(displayName, category, -1, callback);
  }
}
