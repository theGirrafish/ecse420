package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Bounded lock-based blocking queue
public class LockBasedQueue<T> {

  public Object[] queue;                // Array version of linked list
  public int head;                      // First entry in queue
  public int tail;                      // Last entry in queue
  public int maxSize;                   // Max number of objects allowed in queue
  public AtomicInteger size;            // Number of used slots in queue
  public ReentrantLock eLock, dLock;    // Lock out other enqueuers/dequeuers
  public Condition notEmpty, notFull;   // When queue is not empty or not full

  public LockBasedQueue(int maxSize) {
    this.queue = new Object[maxSize];
    this.head = 0;
    this.tail = this.head;
    this.maxSize = maxSize;
    this.size = new AtomicInteger(0);
    this.eLock = new ReentrantLock();
    this.dLock = new ReentrantLock();
    this.notFull = eLock.newCondition();
    this.notEmpty = dLock.newCondition();
  }

  // Add object to the end of the queue
  public void enqueue(T value) {
    if (value == null) {
      throw new NullPointerException();
    }

    boolean wakeUpDequeuers = false;
    eLock.lock();

    try {
      while (this.size.get() == this.maxSize) {
        try {
          this.notFull.await();
        } catch (InterruptedException e) {}
      }

      add(value);
      if (this.size.getAndIncrement() == 0) {
        wakeUpDequeuers = true;
      }
    } finally {
      this.eLock.unlock();
    }

    if (wakeUpDequeuers) {
      this.dLock.lock();
      try {
        this.notEmpty.signalAll();
      } finally {
        this.dLock.unlock();
      }
    }
  }

  // Remove and return the head of the queue
  public T dequeue() {
    T value;
    boolean wakeUpEnqueuers = false;
    this.dLock.lock();

    try {
      while (this.size.get() == 0) {
        try {
          this.notEmpty.await();
        } catch (InterruptedException e) {}
      }

      value = remove();
      if (this.size.getAndDecrement() == this.maxSize) {
        wakeUpEnqueuers = true;
      }
    } finally {
      this.dLock.unlock();
    }
    if (wakeUpEnqueuers) {
      this.eLock.lock();
      try {
        this.notFull.signalAll();
      } finally {
        this.eLock.unlock();
      }
    }
    return value;
  }

  public void add(T value) {
    final Object[] items = this.queue;
    items[this.tail] = value;
    if (++this.tail == items.length) {
      this.tail = 0;
    }
  }

  public T remove() {
    final Object[] items = this.queue;
    T value = (T) items[this.head];
    items[this.head] = null;
    if (++this.head == items.length)
        this.head = 0;
    return value;
  }
}
