package io.github.mtbarr.kairo;

import io.github.mtbarr.kairo.annotation.Subscribe;
import io.github.mtbarr.kairo.cancellable.CancellableEvent;
import io.github.mtbarr.kairo.exception.SubscriberRegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

  private EventBus eventBus;

  @BeforeEach
  void setUp() {
    eventBus = new EventBus();
  }

  @Test
  void subscribeToEvent() {
    TestEventListener listener = new TestEventListener();
    eventBus.subscribe(listener);

    int newSubscribers = eventBus.getSubscribersCount();
    assertTrue(newSubscribers > 0);
  }

  @Test
  void testFunctionalSubscriber() {
    AtomicInteger counter = new AtomicInteger();
    eventBus.subscribe(TestEvent.class, false, 9, event -> counter.incrementAndGet());

    TestEvent event = new TestEvent();
    eventBus.post(event);

    assertEquals(1, counter.get());
  }


  @Test
  void postEventToRegisteredListener() {
    TestEventListener listener = new TestEventListener();
    eventBus.subscribe(listener);

    TestEvent event = new TestEvent();
    eventBus.post(event);

    assertTrue(listener.isEventHandled());
  }

  @Test
  void postEventToMultipleListeners() {
    TestEventListener listener1 = new TestEventListener();
    TestEventListener listener2 = new TestEventListener();
    eventBus.subscribe(listener1);
    eventBus.subscribe(listener2);

    TestEvent event = new TestEvent();
    eventBus.post(event);

    assertTrue(listener1.isEventHandled());
    assertTrue(listener2.isEventHandled());
  }

  @Test
  void subscribeNullObjectThrowsException() {
    assertThrows(NullPointerException.class, () -> eventBus.subscribe(null));
  }

  @Test
  void subscribeMethodWithInvalidParameterCountThrowsException() {
    InvalidParameterCountListener listener = new InvalidParameterCountListener();
    assertThrows(SubscriberRegistrationException.class, () -> eventBus.subscribe(listener));
  }

  @Test
  void postEventToListenerWithPriority() {
    PriorityEventListener listener = new PriorityEventListener();
    eventBus.subscribe(listener);

    PriorityEvent event = new PriorityEvent();
    eventBus.post(event);

    assertEquals(1, listener.getHandledEvents().size());
  }

  @Test
  void postCancellableEvent() {
    CancellableEventListener listener = new CancellableEventListener();
    eventBus.subscribe(listener);

    CancellableTestEvent event = new CancellableTestEvent();
    eventBus.post(event);

    assertTrue(event.isCancelled());
  }

  @Test
  void postEventToListenerIgnoringCancelled() {
    IgnoreCancelledEventListener listener = new IgnoreCancelledEventListener();
    eventBus.subscribe(listener);

    CancellableTestEvent event = new CancellableTestEvent();
    event.setCancelled(true);
    eventBus.post(event);

    assertFalse(listener.isEventHandled());
  }

  // Helper classes for testing
  static class TestEvent {
  }

  static class PriorityEvent {
  }

  static class CancellableTestEvent implements CancellableEvent {
    private boolean cancelled;

    @Override
    public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }
  }

  public static class TestEventListener {
    private boolean eventHandled = false;

    @Subscribe
    public void onTestEvent(TestEvent event) {
      eventHandled = true;
    }

    public boolean isEventHandled() {
      return eventHandled;
    }
  }

  public static class InvalidParameterCountListener {
    @Subscribe
    public void onInvalidEvent(TestEvent event, String extraParam) {
    }
  }

  public static class PriorityEventListener {
    private final List<PriorityEvent> handledEvents = new ArrayList<>();

    @Subscribe(priority = 99)
    public void onPriorityEvent(PriorityEvent event) {
      handledEvents.add(event);
    }

    public List<PriorityEvent> getHandledEvents() {
      return handledEvents;
    }
  }

  public static class CancellableEventListener {
    @Subscribe
    public void onCancellableEvent(CancellableTestEvent event) {
      event.setCancelled(true);
    }
  }

  public static class IgnoreCancelledEventListener {
    private boolean eventHandled = false;

    @Subscribe(ignoreCancelled = true)
    public void onCancellableEvent(CancellableTestEvent event) {
      eventHandled = true;
    }

    public boolean isEventHandled() {
      return eventHandled;
    }
  }
}
