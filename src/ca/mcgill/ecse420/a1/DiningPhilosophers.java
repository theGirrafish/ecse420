package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

  public static void main(String[] args) {

    int numberOfPhilosophers = 5;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];
    ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
    ReentrantLock lock = new ReentrantLock();


    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Object();
      System.out.println(chopsticks[i]);
    }

    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(chopsticks, i, numberOfPhilosophers);
      executor.execute((philosophers[i]));
    }
  }

  public static class Philosopher implements Runnable {

    final Object rightChopstick;
    final Object leftChopstick;

    Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
      this.rightChopstick = chopsticks[position];
      this.leftChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
    }

    @Override
    public void run() {
      while (true) {
        String name = Thread.currentThread().getName();
        synchronized (rightChopstick) {
          try {
            System.out.println(name + " - Holding Right Chopstick");
            Thread.sleep((long) (Math.random() * 1));
          } catch (Exception e) {
          }
          synchronized (leftChopstick) {
            try {
              System.out.println(name + " - Holding Left Chopstick");
              Thread.sleep((long) (Math.random() * 1));
            } catch (Exception e) {
            }
          }
          System.out.println(name + " - Released Left Chopstick");
        }
        System.out.println(name + " - Released Right Chopstick");
        try {
          Thread.sleep((long) (Math.random() * 1));
        } catch (Exception e) {}
      }
    }
  }
}
