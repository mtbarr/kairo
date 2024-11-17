package io.github.mtbarr.kairo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a method as a subscriber to an event.
 * Methods annotated with @Subscribe will be invoked when the event they are subscribed to is posted.
 *
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

  /**
   * Indicates whether the subscriber should ignore cancelled events.
   * if set to true, the subscriber will not be invoked if the event is cancelled.
   *
   * @return true if the subscriber should ignore cancelled events, false otherwise.
   */
  boolean ignoreCancelled() default false;

  /**
   * Defines the priority of the subscriber.
   * higher priority subscribers will be invoked before lower priority subscribers.
   *
   * @return the priority of the subscriber.
   */
  int priority() default 0;
}