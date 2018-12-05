package ca.mcgill.ecse420.a3;

public class Tester {

  public static void main(String[] args) {
    LockBasedQueue<Integer> lbq = new LockBasedQueue<>(4);
    lbq.enqueue(1);
    lbq.enqueue(2);
    lbq.enqueue(3);
    lbq.enqueue(4);
    lbq.dequeue();
    lbq.enqueue(5);
    lbq.dequeue();
    lbq.enqueue(6);

    LockFreeQueue<Integer> lfq = new LockFreeQueue<>(4);
    lfq.enqueue(1);
    lfq.enqueue(2);
    lfq.enqueue(3);
    lfq.enqueue(4);
    lfq.dequeue();
    lfq.enqueue(5);
    lfq.dequeue();
    lfq.enqueue(6);
  }
}
