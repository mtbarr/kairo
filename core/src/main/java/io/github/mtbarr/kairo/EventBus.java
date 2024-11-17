package io.github.mtbarr.kairo;

import io.github.mtbarr.kairo.annotation.Subscribe;
import io.github.mtbarr.kairo.cancellable.CancellableEvent;
import io.github.mtbarr.kairo.exception.SubscriberRegistrationException;
import io.github.mtbarr.kairo.subscriber.FunctionalSubscriberMethod;
import io.github.mtbarr.kairo.subscriber.ReflectiveSubscriberMethod;
import io.github.mtbarr.kairo.subscriber.SubscriberMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * EventBus is a central mechanism for managing the registration and posting of events.
 * It allows for reflective and functional subscriber methods to be registered and triggered
 * whenever an event of a particular type is posted.
 *
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public class EventBus {

  /**
   * A map of event types to lists of subscriber methods that should be called when an event of that type is posted.
   */
  private final Map<Class<?>, List<SubscriberMethod>> subscribersMap;

  /**
   * Constructs an EventBus with an empty subscriber map.
   */
  public EventBus() {
    this.subscribersMap = new ConcurrentHashMap<>();
  }


  /**
   * Subscribes a functional listener for a particular type of event.
   *
   * @param clazz the class of the event to listen for.
   * @param ignoreCancelled whether the subscriber should ignore cancelled events.
   * @param priority the priority of the subscriber (higher priority subscribers are called first).
   * @param consumer the functional handler that will process the event.
   * @param <E> the type of event.
   */
  public <E> void subscribe(Class<E> clazz, boolean ignoreCancelled, int priority, Consumer<E> consumer) {
    this.sortAdd(new FunctionalSubscriberMethod<>(clazz, consumer, ignoreCancelled, priority));
  }

  /**
   * Subscribes a functional listener for a particular type of event.
   *
   * @param clazz the class of the event to listen for.
   * @param consumer the functional handler that will process the event.
   * @param <E> the type of event.
   */
  public <E> void subscribe(Class<E> clazz, Consumer<E> consumer) {
    this.subscribe(clazz, false, 0, consumer);
  }

  /**
   * Subscribes a functional listener for a particular type of event.
   *
   * @param clazz the class of the event to listen for.
   * @param ignoreCancelled whether the subscriber should ignore cancelled events.
   * @param consumer the functional handler that will process the event.
   * @param <E> the type of event.
   */
  public <E> void subscribe(Class<E> clazz, boolean ignoreCancelled, Consumer<E> consumer) {
    this.subscribe(clazz, ignoreCancelled, 0, consumer);
  }

  /**
   * Subscribes a functional listener for a particular type of event.
   *
   * @param clazz the class of the event to listen for.
   * @param priority the priority of the subscriber (higher priority subscribers are called first).
   * @param consumer the functional handler that will process the event.
   * @param <E> the type of event.
   */
  public <E> void subscribe(Class<E> clazz, int priority, Consumer<E> consumer) {
    this.subscribe(clazz, false, priority, consumer);
  }

  /**
   * Subscribes all methods of a given object that are annotated with {@link Subscribe}.
   *
   * @param object the subscriber object containing methods annotated with {@link Subscribe}.
   * @throws NullPointerException if the subscriber object is null.
   * @throws SubscriberRegistrationException if an error occurs while registering the subscriber methods.
   */
  public void subscribe(Object object) {
    if (object == null) {
      throw new NullPointerException("Subscriber object cannot be null.");
    }

    try {
      this.registerSubscriberMethods(object);
    } catch (Throwable throwable) {
      throw new SubscriberRegistrationException("Failed to register subscriber.", throwable);
    }
  }

  /**
   * Adds a subscriber method to the list of subscribers for its event type and sorts them by priority.
   *
   * @param method the subscriber method to add.
   */
  private void sortAdd(SubscriberMethod method) {
    List<SubscriberMethod> subscriberMethods = this.getSubscribersForType(method.eventClass());
    int index = 0;
    while (index < subscriberMethods.size() && subscriberMethods.get(index).priority() <= method.priority()) {
      index++;
    }

    subscriberMethods.add(index, method);
  }

  /**
   * Counts the number of subscribers for a particular event type.
   * @return the number of subscribers for the event type.
   */
  public int getSubscribersCount() {
    return subscribersMap.size();
  }

  /**
   * Posts an event to all registered subscribers for the event's type.
   *
   * @param event the event object to post.
   */
  public void post(Object event) {
    List<SubscriberMethod> subscriberMethods = subscribersMap.get(event.getClass());
    if (subscriberMethods == null || subscriberMethods.isEmpty()) {
      return;
    }

    boolean canBeCancelled = event instanceof CancellableEvent;
    boolean cancelled = canBeCancelled && ((CancellableEvent) event).isCancelled();

    for (int i = 0, size = subscriberMethods.size(); i < size; i++) {
      SubscriberMethod subscriberMethod = subscriberMethods.get(i);
      if (cancelled && subscriberMethod.ignoreCancelled()) {
        continue;
      }

      subscriberMethod.invoke(event);

      if (canBeCancelled) {
        cancelled = ((CancellableEvent) event).isCancelled();
      }
    }
  }

  /**
   * Retrieves the list of subscriber methods for a specific event type, creating it if necessary.
   *
   * @param clazz the class of the event type.
   * @return the list of subscriber methods for the event type.
   */
  private List<SubscriberMethod> getSubscribersForType(Class<?> clazz) {
    return subscribersMap.computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>());
  }

  /**
   * Wraps all methods of a subscriber object that are annotated with {@link Subscribe}.
   *
   * @param subscriber the object containing methods annotated with {@link Subscribe}.
   */
  private void registerSubscriberMethods(Object subscriber) {
    Method[] methods = subscriber.getClass().getDeclaredMethods();
    for (Method method : methods) {
      Subscribe annotation = method.getAnnotation(Subscribe.class);
      if (annotation == null) {
        continue;
      }

      if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("Method " + method.getName() + " must have exactly one parameter.");
      }

      Class<?> eventClazz = method.getParameterTypes()[0];
      this.addReflectiveMethod(eventClazz, subscriber, method, annotation.ignoreCancelled(), annotation.priority());
    }
  }

  /**
   * Adds a reflective subscriber method for a given event type.
   *
   * @param clazz the class of the event type.
   * @param object the subscriber object containing the method.
   * @param method the method to be invoked when an event of the specified type is posted.
   * @param ignoreCancelled whether the subscriber should ignore cancelled events.
   * @param priority the priority of the subscriber (higher priority subscribers are called first).
   */
  private void addReflectiveMethod(Class<?> clazz, Object object, Method method, boolean ignoreCancelled, int priority) {
    try {
      this.sortAdd(new ReflectiveSubscriberMethod(clazz, object, method, ignoreCancelled, priority));
    } catch (Throwable throwable) {
      throw new IllegalArgumentException("Failed to create subscriber method.", throwable);
    }
  }

}
