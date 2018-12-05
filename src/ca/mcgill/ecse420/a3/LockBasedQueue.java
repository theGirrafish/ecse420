package ca.mcgill.ecse420.a3;

public class LockBasedQueue {
  private int maxSize;
  private char[] queue;

  public LockBasedQueue(int maxSize) {
    this.maxSize = maxSize;
    this.queue = new char[maxSize];
  }

}
