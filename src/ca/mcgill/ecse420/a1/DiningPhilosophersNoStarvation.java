package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersNoStarvation {

  public static void main(String[] args) {

    int numberOfPhilosophers = 50;
    Chopstick[] chopsticks = new Chopstick[numberOfPhilosophers];
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

    // Initialize Chopsticks
    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Chopstick();
    }

    // Initialize Philosophers and execute the Thread
    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(i, i > 0 ? chopsticks[i - 1] : chopsticks[chopsticks.length - 1], chopsticks[i]);
      executor.execute((philosophers[i]));
      try {
        Thread.sleep((long) (Math.random() * 10));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    executor.shutdown();
  }

  public static class Chopstick {

    private ReentrantLock reLock = new ReentrantLock(true);

    public Chopstick() {
    }

    // Attempt to pick up chopstick, if successful lock resource
    public boolean grabStick() {
      return reLock.tryLock();
    }

    // Release the lock on the chopstick
    public void dropStick() {
      reLock.unlock();
    }
  }

  public static class Philosopher implements Runnable {

    int ate = 0;
    final int id;
    final Chopstick rightChopstick;
    final Chopstick leftChopstick;

    public Philosopher(int position, Chopstick rightChopstick, Chopstick leftChopstick) {
      this.id = position + 1;
      this.rightChopstick = rightChopstick;
      this.leftChopstick = leftChopstick;
    }

    @Override
    public void run() {
      long start;
      long totalWait = 0;

      for (int x = 0; x < 1000; x++) {
        start = System.nanoTime();

        try {
          if (rightChopstick.grabStick()) {
            System.out.println(id + " - Holding Right Chopstick");
//            Thread.sleep((long) (Math.random() * 5));

            if (leftChopstick.grabStick()) {
//              System.out.println(id + " - Holding Left Chopstick");
//              System.out.println(id + " - Eating");
              totalWait += System.nanoTime() - start;
              ate++;
              Thread.sleep((long) (Math.random() * 5));
              leftChopstick.dropStick();
//              System.out.println(id + " - Released Left Chopstick");
            }

            rightChopstick.dropStick();
//            System.out.println(id + " - Released Right Chopstick");
          }
          Thread.sleep((long) (Math.random() * 10));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      // To see # of times each philosopher ate, comment out the other System.out.println() lines
      System.out.println("Philosopher " + id + " ate " + ate + " times and waited for " + totalWait/1000000000.0 + " seconds.");
    }
  }
}
