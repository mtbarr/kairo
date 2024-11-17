package io.github.mtbarr.kairo.cancellable;

/**
 * Represents an event that can be cancelled.
 * @author Matheus Barreto <a href="https://github.com/mtbarr">mtbarr</a>
 */
public interface CancellableEvent {

  boolean isCancelled();

  void setCancelled(boolean cancelled);
}