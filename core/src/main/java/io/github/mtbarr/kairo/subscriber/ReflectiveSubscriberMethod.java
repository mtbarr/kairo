package io.github.mtbarr.kairo.subscriber;

import io.github.mtbarr.kairo.exception.EventInvocationException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;


/**
 * Represents a subscriber method that uses reflection to invoke the method.
 *
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public class ReflectiveSubscriberMethod implements SubscriberMethod {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  private final Class<?> clazz;
  private final boolean ignoreCancelled;
  private final int priority;
  private final MethodHandle handle;

  public ReflectiveSubscriberMethod(Class<?> clazz, Object object, Method method, boolean ignoreCancelled, int priority) throws Throwable {
    this.clazz = clazz;
    this.ignoreCancelled = ignoreCancelled;
    this.priority = priority;
    this.handle = LOOKUP.unreflect(method).bindTo(object);
  }

  @Override
  public Class<?> eventClass() {
    return clazz;
  }

  @Override
  public void invoke(Object event) {
    try {
      handle.invoke(event);
    } catch (Throwable throwable) {
      throw new EventInvocationException(throwable);
    }
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
