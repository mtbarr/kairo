# Kairo - Lightweight Event Bus for Java

**Kairo** is a lightweight event bus designed for efficient event handling in Java applications. Inspired by the
publisher/subscriber (pub/sub) pattern, Kairo offers a straightforward and practical solution to decouple event
producers and consumers.

## Features

- **Lightweight and Simple**: Easily integrates with any Java application.
- **Annotations for Event Handling**: Use `@Subscribe` to define subscriber methods.
- **Functional Subscribers**: Register event handlers using lambda expressions for increased flexibility.
- **Cancellable Events**: Supports events that can be cancelled during the event propagation.
- **Priority-based Event Handling**: Control the order of event handling by assigning priorities.


## Usage Examples

### Creating and Registering Subscribers

You can register subscribers using the `@Subscribe` annotation or functional interfaces. Below are examples of how to get started with Kairo.

#### 1. Using `@Subscribe` Annotation

Create an event subscriber class with methods annotated by `@Subscribe` to handle specific event types:

```java
import io.github.mtbarr.kairo.annotation.Subscribe;

public class MyEventSubscriber {
    @Subscribe(priority = 1)
    public void onMyEvent(MyEvent event) {
        System.out.println("Handling MyEvent: " + event);
    }
}
```

Then, register the subscriber object with `EventBus`:

```java
EventBus eventBus = new EventBus();
MyEventSubscriber subscriber = new MyEventSubscriber();
eventBus.subscribe(subscriber);
```

### 2. Registering Functional Subscribers

You can also register event listeners using a functional approach, allowing for greater flexibility:

```java
EventBus eventBus = new EventBus();

// Register a functional listener
Consumer<MyEvent> eventConsumer = event -> System.out.println("Handling MyEvent using lambda: " + event);
eventBus.subscribe(MyEvent.class, eventConsumer);
```

### Posting Events

To trigger event processing, you need to post an event. All registered subscribers will be notified:

```java
MyEvent myEvent = new MyEvent();
eventBus.post(myEvent);
```

### Cancellable Events

Kairo supports cancellable events, which can stop further event propagation if needed. Create an event class implementing `CancellableEvent`:

```java
import io.github.mtbarr.kairo.cancellable.CancellableEvent;

public class MyCancellableEvent implements CancellableEvent {
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
```

Register a subscriber that can cancel the event:

```java
public class CancellableSubscriber {
    @Subscribe
    public void onMyCancellableEvent(MyCancellableEvent event) {
        System.out.println("Event received. Cancelling...");
        event.setCancelled(true);
    }
}

EventBus eventBus = new EventBus();
eventBus.subscribe(new CancellableSubscriber());

MyCancellableEvent cancellableEvent = new MyCancellableEvent();
eventBus.post(cancellableEvent);
```

In this example, once the event is received by `CancellableSubscriber`, it sets the event as cancelled, preventing further handling.

### Event Priorities

Subscribers can define priorities to control the order in which they receive events. Subscribers with higher priority values are notified first.

```java
public class PrioritySubscriber {
    @Subscribe(priority = 10)
    public void handleHighPriority(MyEvent event) {
        System.out.println("High priority handling: " + event);
    }

    @Subscribe(priority = 1)
    public void handleLowPriority(MyEvent event) {
        System.out.println("Low priority handling: " + event);
    }
}

EventBus eventBus = new EventBus();
eventBus.subscribe(new PrioritySubscriber());

MyEvent event = new MyEvent();
eventBus.post(event);
```

In this example, the handler with the highest priority (`handleHighPriority`) will be called before the lower priority handler (`handleLowPriority`).


## License

Kairo is distributed under the MIT License. See [LICENSE](LICENSE.md) for more information.