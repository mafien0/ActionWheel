package cc.mafien0.ActionWheel.core.gui;

import cc.mafien0.ActionWheel.core.gui.widgets.SubScreen;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class AskScreen<T> extends Screen {
  private final Consumer<T> onSuccess;
  private final Runnable onFail;

  public AskScreen(Consumer<T> onSuccess, Runnable onFail) {
    super(Component.empty());
    this.onSuccess = onSuccess;
    this.onFail = onFail;
  }

  public abstract static class Builder<T> {
    protected Consumer<T> onSuccess;
    protected Runnable onFail;

    public Builder<T> onSuccess(Consumer<T> onSuccess) {
      this.onSuccess = onSuccess;
      return this;
    }

    public Builder<T> onFail(Runnable onFail) {
      this.onFail = onFail;
      return this;
    }

    public abstract AskScreen<T> build();
  }

  public void success(T ret) {
    onSuccess.accept(ret);
    this.initClose();
  }

  public void fail() {
    onFail.run();
    this.initClose();
  }

  public void initClose() {
    closeScreen(this);
  }

  @Override
  public void onClose() {
    fail();
  }

  public static <T> void ask(AskScreen<T> askScreen) {
    var client = Minecraft.getInstance();
    var currentScreen = client.gui.screen();
    if (currentScreen == null) {
      client.gui.setScreen(askScreen);
      return;
    }
    var askSubScreen =
        new OverlayScreen(0, 0, currentScreen.width, currentScreen.height).setScreen(askScreen);
    currentScreen.children();
    currentScreen.addRenderableWidget(askSubScreen);
  }

  private static <T extends AskScreen<?>> void closeScreen(T screen) {
    var client = Minecraft.getInstance();
    if (client.gui.screen() == null) {
      return;
    }
    List<? extends GuiEventListener> children;

    children = client.gui.screen().children();
    var del = new ArrayList<GuiEventListener>();
    for (var c : children) {
      if (c instanceof OverlayScreen overlayScreen) {
        // bruh gonna use reflection
        try {
          Field field = SubScreen.class.getDeclaredField("screen");
          field.setAccessible(true);
          if (field.get(overlayScreen) == screen) {
            del.add(c);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        } // lmao
      }
    }
    for (var d : del) {
      client.gui.screen().removeWidget(d);
    }
  }

  public static class OverlayScreen extends SubScreen {
    private OverlayScreen(int x, int y, int width, int height) {
      super(x, y, width, height);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
      super.isMouseOver(mouseX, mouseY);
      return true;
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
      super.mouseClicked(event, doubleClick);
      return true;
    }

    @Override
    public boolean mouseScrolled(
        double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      return true;
    }

    @Override
    public boolean mouseDragged(
        final @NonNull MouseButtonEvent event, final double dx, final double dy) {
      return true;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
      super.mouseReleased(event);
      return true;
    }
  }
}
