package cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionproviders;

import static cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ActionTypeRegistry.gap;

import cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry.ConfigurableRunnable;
import cc.mafien0.actionWheel.core.common.Tr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChatMessageActionProvider implements ConfigurableRunnable {
  private String message = "Hello, world!";

  @Override
  public Screen getConfiguratorScreen() {
    return new ChatMessageEditScreen(this);
  }

  @Override
  public ConfigurableRunnable copy() {
    var cm = new ChatMessageActionProvider();
    cm.message = this.message;
    return cm;
  }

  @Override
  public void run() {
    var n = Minecraft.getInstance().getConnection();
    if (n != null) {
      if (message.startsWith("/")) n.sendCommand(message.substring(1));
      else n.sendChat(message);
    }
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
              Tr.get("actionWheel.chat.messagehere"));
      field.setMaxLength(255);
      field.setValue(target.message);
      field.setResponder(s -> target.message = s);
      addRenderableWidget(field);
    }
  }
}
