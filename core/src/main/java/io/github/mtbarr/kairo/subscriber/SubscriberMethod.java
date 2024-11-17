package io.github.mtbarr.kairo.subscriber;

/**
 * Represents a subscriber method.
 *
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public interface SubscriberMethod {

  /**
   * Returns the class of the event that the subscriber is subscribed to.
   * @return the class of the event that the subscriber is subscribed to.
   */
  Class<?> eventClass();

  /**
   * Invokes the subscriber method with the given event.
   *
   * @param event the event to invoke the subscriber method with.
   */
  void invoke(Object event);

  /**
   * Indicates whether the subscriber should ignore cancelled events.
   *
   * @return true if the subscriber should ignore cancelled events, false otherwise.
   */
  boolean ignoreCancelled();

  /**
   * Returns the priority of the subscriber.
   * Higher priority subscribers will be invoked before lower priority subscribers.
   *
   * @return the priority of the subscriber.
   */
  int priority();
}