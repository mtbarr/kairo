

package io.github.mtbarr.kairo.exception;

import io.github.mtbarr.kairo.subscriber.SubscriberMethod;

/**
 * Represents an exception that occurs when an event handler encounters an exception.
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public interface EventExceptionHandler {

  /**
   * Handles an exception that occurred while invoking a subscriber method.
   *
   * @param method the subscriber method that encountered the exception.
   * @param event the event that was being handled when the exception occurred.
   * @param throwable the exception that occurred.
   */
  void handleEventException(SubscriberMethod method, Object event, Throwable throwable);
}
