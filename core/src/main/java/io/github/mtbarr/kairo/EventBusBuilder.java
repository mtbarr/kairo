package io.github.mtbarr.kairo;

import io.github.mtbarr.kairo.exception.EventExceptionHandler;


/**
 * Builder for EventBus to allow easy customization and setup.
 *  @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public class EventBusBuilder {

  private EventExceptionHandler exceptionHandler = EventBus.DEFAULT_EXCEPTION_HANDLER;

  /**
   * Builds and returns a new instance of EventBus with the specified configurations.
   *
   * @return a configured EventBus instance.
   */
  public EventBus build() {
    EventBus eventBus = new EventBus();
    eventBus.setExceptionHandler(exceptionHandler);
    return eventBus;
  }

  /**
   * Sets a custom exception handler for the EventBus.
   *
   * @param exceptionHandler the custom exception handler to set.
   * @return the builder instance for method chaining.
   */
  public EventBusBuilder withExceptionHandler(EventExceptionHandler exceptionHandler) {
    if (exceptionHandler == null) {
      throw new IllegalArgumentException("ExceptionHandler cannot be null");
    }
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  /**
   * Returns a new instance of the EventBusBuilder.
   *
   * @return a new EventBusBuilder instance.
   */
  public static EventBusBuilder create() {
    return new EventBusBuilder();
  }
}