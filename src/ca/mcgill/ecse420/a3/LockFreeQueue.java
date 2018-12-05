package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

// Bounded lock-free blocking queue
public class LockFreeQueue<T> {

  public AtomicReferenceArray<T> queue;
  public AtomicInteger head, tail;

  public LockFreeQueue(int maxSize) {
    this.queue = new AtomicReferenceArray<>(maxSize);
    this.head = new AtomicInteger(0);
    this.tail = new AtomicInteger(0);
  }

  // Add object to the end of the queue
  public void enqueue(T value) {
    this.queue.set(this.tail.getAndIncrement(), value);

    if (this.tail.get() == this.queue.length()) {
      this.tail.set(0);
    }
  }

  // Remove and return the head of the queue
  public T dequeue() {
    T value = this.queue.getAndSet(this.head.getAndIncrement(), null);

    if (this.head.get() == this.queue.length()) {
      this.head.set(0);
    }

    return value;
  }
}
