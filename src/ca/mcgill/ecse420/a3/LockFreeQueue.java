package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

// Bounded lock-free blocking queue
public class LockFreeQueue<T> {

  public AtomicReferenceArray<T> queue;
  public AtomicInteger head, tail, size;
  public int maxSize;

  public LockFreeQueue(int maxSize) {
    this.maxSize = maxSize;
    this.queue = new AtomicReferenceArray<>(maxSize);
    this.head = new AtomicInteger(0);
    this.tail = new AtomicInteger(0);
    this.size = new AtomicInteger(0);
  }

  // Add object to the end of the queue
  public void enqueue(T value) {
    int size = this.size.get();
    while (size == this.maxSize || !this.size.compareAndSet(size, size + 1)) {
      size = this.size.get();
    }

    this.queue.set(this.tail.getAndIncrement(), value);

    if (this.tail.get() == this.maxSize) {
      this.tail.set(0);
    }
  }

  // Remove and return the head of the queue
  public T dequeue() {
    int size = this.size.get();
    while (size == 0 || !this.size.compareAndSet(size, size - 1)) {
      size = this.size.get();
    }
    T value = this.queue.getAndSet(this.head.getAndIncrement(), null);

    if (this.head.get() == this.maxSize) {
      this.head.set(0);
    }

    return value;
  }
}
