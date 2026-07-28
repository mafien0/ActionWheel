package io.github.JumperOnJava.lavajumper.common;

import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Tr {
  static Map<String, String> translationMap = new HashMap<>();

  /** Generates file for translation in game and puts it into .autoTranslateOutput folder */
  public static void generateTranslationMap() {
    var file =
        FabricLoader.getInstance()
            .getGameDir()
            .resolve(".autoTranslateOutput")
            .resolve("output.txt")
            .toFile();
    FileReadWrite.write(
        file, new GsonBuilder().setPrettyPrinting().create().toJson(translationMap));
  }

  public static void clearTranslationMap() {
    translationMap = new HashMap<>();
  }

  private static void addKeyToTranslation(String key) {
    if (Language.getInstance().getOrDefault(key).equals(key)) {
      translationMap.put(key, "");
    }
  }

  /**
   * Gets translatable text component for key and adds it to list if it is not have translation.
   * After getting all translations you should call generateTranslationMap() static method to save
   * it to .autoTranslateOutput folder
   *
   * @param key
   * @return
   */
  public static MutableComponent get(String key) {
    addKeyToTranslation(key);
    return Component.translatable(key);
  }
}
