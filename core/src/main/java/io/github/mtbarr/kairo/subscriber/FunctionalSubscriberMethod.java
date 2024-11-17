package io.github.mtbarr.kairo.subscriber;

import java.util.function.Consumer;


/**
 * Represents a subscriber method that is a functional interface.
 *
 * @param <E> the event type
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public class FunctionalSubscriberMethod<E> implements SubscriberMethod {

  private final Class<E> clazz;
  private final Consumer<E> consumer;
  private final boolean ignoreCancelled;
  private final int priority;

  public FunctionalSubscriberMethod(Class<E> clazz, Consumer<E> consumer, boolean ignoreCancelled, int priority) {
    this.clazz = clazz;
    this.consumer = consumer;
    this.ignoreCancelled = ignoreCancelled;
    this.priority = priority;
  }

  @Override
  public Class<?> eventClass() {
    return clazz;
  }

  @Override
  public void invoke(Object event) {
    if (!clazz.isInstance(event)) {
      throw new IllegalArgumentException("Event is not an instance of " + clazz.getName());
    }

    consumer.accept(clazz.cast(event));
  }

  @Override
  public boolean ignoreCancelled() {
    return ignoreCancelled;
  }

  @Override
  public int priority() {
    return priority;
  }
}
