package io.github.mtbarr.kairo.exception;


public class SubscriberRegistrationException extends RuntimeException {

  public SubscriberRegistrationException(String message) {
    super(message);
  }

  public SubscriberRegistrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
