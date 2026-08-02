package cc.mafien0.actionWheel.core.common;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class Binder {
  private static final KeyMapping.Category LAVAJUMPER_CATEGORY =
      KeyMapping.Category.register(Identifier.fromNamespaceAndPath("core", "core"));

  public static KeyMapping addBind(
      String displayName,
      KeyMapping.Category category,
      int defaultKey,
      Consumer<Minecraft> callback) {

    KeyMapping bind = new KeyMapping(displayName, InputConstants.Type.KEYSYM, defaultKey, category);

    KeyMappingHelper.registerKeyMapping(bind);

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (bind.consumeClick()) {
            callback.accept(client);
          }
        });

    return bind;
  }

  public static KeyMapping addBind(
      String displayName, int defaultKey, Consumer<Minecraft> callback) {
    return addBind(displayName, LAVAJUMPER_CATEGORY, defaultKey, callback);
  }

  public static KeyMapping addBind(
      String displayName, KeyMapping.Category category, Consumer<Minecraft> callback) {
    return addBind(displayName, category, -1, callback);
  }
}
