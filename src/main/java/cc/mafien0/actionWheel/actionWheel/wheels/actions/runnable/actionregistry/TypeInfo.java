package cc.mafien0.actionWheel.actionWheel.wheels.actions.runnable.actionregistry;

import com.google.gson.TypeAdapter;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class TypeInfo implements Function<Boolean, ConfigurableRunnable>, Comparable<TypeInfo> {
  private final Function<Boolean, ConfigurableRunnable> factory;
  private final int hashCode;
  public final Class<? extends ConfigurableRunnable> type;
  public final TypeAdapter<? extends ConfigurableRunnable> adapter;

  public TypeInfo(
      Function<Boolean, ConfigurableRunnable> configurableRunnableSupplier,
      TypeAdapter<? extends ConfigurableRunnable> adapter) {
    this.factory = configurableRunnableSupplier;
    this.adapter = adapter;

    var testObj = factory.apply(false);
    this.type = testObj.getClass();
    this.hashCode = testObj.getClass().getName().hashCode();
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  public ConfigurableRunnable apply(Boolean isReal) {
    return factory.apply(isReal);
  }

  @Override
  public int compareTo(@NotNull TypeInfo o) {
    return hashCode() - o.hashCode();
  }
}
