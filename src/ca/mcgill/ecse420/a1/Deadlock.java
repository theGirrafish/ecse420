package ca.mcgill.ecse420.a1;

public class Deadlock {

  public static Object lock1 = new Object();
  public static Object lock2 = new Object();

  public static void main(String[] args) {
    DeadlockThread thread1 = new DeadlockThread(lock1, lock2);
    DeadlockThread thread2 = new DeadlockThread(lock2, lock1);
    thread1.start();
    thread2.start();
  }

  public static class DeadlockThread extends Thread {
    final Object lock1;
    final Object lock2;

    public DeadlockThread(Object lock1, Object lock2) {
      this.lock1 = lock1;
      this.lock2 = lock2;
    }

    public void run() {
      synchronized (lock1) {
        System.out.println("Lock 1");
        try {
          // Gives time for other thread to acquire lock on its first resource
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        synchronized (lock2) {
          System.out.println("Lock 2");
        }
      }
    }
  }
}
