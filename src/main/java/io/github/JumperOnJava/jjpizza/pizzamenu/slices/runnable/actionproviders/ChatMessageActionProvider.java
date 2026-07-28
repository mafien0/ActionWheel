package io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionproviders;

import static io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ActionTypeRegistry.gap;

import io.github.JumperOnJava.jjpizza.pizzamenu.slices.ConfigurablePizzaSlice;
import io.github.JumperOnJava.jjpizza.pizzamenu.slices.runnable.actionregistry.ConfigurableRunnable;
import io.github.JumperOnJava.lavajumper.common.Tr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChatMessageActionProvider implements ConfigurableRunnable {
  private String message = "Hello, world!";
  private transient ConfigurablePizzaSlice parent;

  public ChatMessageActionProvider(Boolean isReal) {}

  public void setParent(ConfigurablePizzaSlice pizzaSlice) {
    this.parent = pizzaSlice;
  }

  @Override
  public Screen getConfiguratorScreen() {
    return new ChatMessageEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var cm = new ChatMessageActionProvider(true);
    cm.message = new String(this.message);
    return cm;
  }

  @Override
  public void run() {
    var n = Minecraft.getInstance().getConnection();
    if (message.startsWith("/")) n.sendCommand(message.substring(1));
    else n.sendChat(message);
  }

  static class ChatMessageEditScreen extends Screen {
    private final ChatMessageActionProvider target;

    protected ChatMessageEditScreen(ChatMessageActionProvider target) {
      super(Component.empty());
      this.target = target;
    }

    protected void init() {
      var field =
          new EditBox(
              Minecraft.getInstance().font,
              gap / 2,
              gap / 2,
              width - gap,
              20,
              Tr.get("jjpizza.chat.messagehere"));
      field.setMaxLength(255);
      field.setValue(target.message);
      field.setResponder(s -> target.message = s);
      addRenderableWidget(field);
    }
  }
}
