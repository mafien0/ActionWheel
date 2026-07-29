package io.github.JumperOnJava.jjpizza.pizzamenu.configurer;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.pizzamenu.widgets.TextureWidget;
import io.github.JumperOnJava.lavajumper.common.Tr;
import io.github.JumperOnJava.lavajumper.gui.AskScreen;
import io.github.JumperOnJava.lavajumper.gui.widgets.ScrollListWidget;
import java.util.LinkedList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TextureListAsk extends AskScreen<Identifier> {
  ScrollListWidget list;
  // private static Map<Identifier, AbstractTexture> textures;
  private static final java.util.List<Identifier> textures = new LinkedList<>();

  static {
    var paths = new LinkedList<String>();
    paths.add("textures/item");
    paths.add("textures/block");
    paths.add("icon");
    paths.add("icons");
    paths.add("textures/particle");
    paths.add("textures/painting");
    paths.add("textures/mob_effect");
    paths.add("textures/gui/sprites/hud/heart");
    paths.add("textures/gui/sprites/icon");
    paths.add("textures/gui/sprites/pending_invite");
    paths.add("textures/gui/sprites/player_list");
    paths.add("textures/gui/sprites/server_list");
    paths.add("textures/gui/sprites/spectator");
    paths.add("textures/gui/sprites/statistics");
    for (var path : paths) {
      var manager = Minecraft.getInstance().getResourceManager();
      var resources = manager.listResources(path, i -> i.toString().endsWith(".png"));
      textures.addAll(resources.keySet().stream().toList());
    }
  }

  private Identifier selectedTexture = Identifier.parse("empty");

  protected TextureListAsk(Consumer<Identifier> onSuccess, Runnable onFail) {
    super(onSuccess, onFail);
  }

  private TextureWidget selectedTextureWidget;

  @Override
  protected void init() {
    list = new ScrollListWidget(minecraft, width, height - 22 * 2, 0, 22, 40);
    filterList("");
    addRenderableWidget(list);

    var search = new EditBox(minecraft.font, 0, 0, width, 20, Component.empty());
    search.setResponder(this::filterList);
    addRenderableWidget(search);

    var accept =
        new Button.Builder(Tr.get("jjpizza.texture.accept"), _ -> success(selectedTexture))
            .bounds(40 + gap, height - 20 - gap, 100, 20)
            .build();
    var cancel =
        new Button.Builder(Tr.get("jjpizza.texture.cancel"), _ -> fail())
            .bounds((int) (140 + gap * 1.5), height - 20 - gap, 100, 20)
            .build();
    addRenderableWidget(accept);
    addRenderableWidget(cancel);
    selectedTextureWidget = new TextureWidget(null, gap / 2, height - 40 - gap / 2, 40, 40);
    addRenderableWidget(selectedTextureWidget);
  }

  private void filterList(String s) {
    list.clearList();
    list.setScrollAmount(0);
    for (var key : textures) {
      var id = key.toString();
      if (!id.toLowerCase().contains(s.toLowerCase())) continue;
      var button =
          new Button.Builder(
                  Component.literal(id),
                  b -> {
                    this.selectedTexture = Identifier.parse(b.getMessage().getString());
                    selectedTextureWidget.setTexture(selectedTexture);
                  })
              .pos(40, 10)
              .size(width - 40 - 6 - gap, 20)
              .build();
      var entry = new ScrollListWidget.ScrollListEntry();
      entry.addDrawableChild(button, true);
      entry.addDrawableChild(new TextureWidget(key, 0, 0, 40, 40), false);
      list.addEntry(entry);
    }
  }

  public void extractRenderState(
      @NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);
    context.text(
        font,
        Tr.get("jjpizza.texture.selected").append(": ").append(selectedTexture.toString()),
        45,
        height - 30 - 6 - gap / 2,
        0xFFFFFFFF,
        true);
  }

  public static class Builder extends AskScreen.Builder<Identifier> {
    @Override
    public TextureListAsk build() {
      return new TextureListAsk(onSuccess, onFail);
    }
  }
}
