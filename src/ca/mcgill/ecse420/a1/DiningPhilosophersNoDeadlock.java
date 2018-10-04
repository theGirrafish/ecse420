package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophersNoDeadlock {

  public static void main(String[] args) {

    int numberOfPhilosophers = 10;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];
    ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);


    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Object();
      System.out.println(chopsticks[i]);
    }

    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(chopsticks, i, numberOfPhilosophers);
      executor.execute((philosophers[i]));
      try {
        Thread.sleep((long) (Math.random() * 10));
      } catch (Exception e) {
      }
    }
  }

  public static class Philosopher implements Runnable {

    final Object rightChopstick;
    final Object leftChopstick;

    Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
      if (position != 0) {
        this.rightChopstick = chopsticks[position];
        this.leftChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
      } else {
        this.rightChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
        this.leftChopstick = chopsticks[position];
      }
    }

    @Override
    public void run() {
      while (true) {
        String name = Thread.currentThread().getName();
        synchronized (rightChopstick) {
          try {
            System.out.println(name + " - Holding Right Chopstick");
            Thread.sleep((long) (Math.random() * 5));
          } catch (Exception e) {
          }
          synchronized (leftChopstick) {
            try {
              System.out.println(name + " - Holding Left Chopstick");
              System.out.println(name + " - Eating");
              Thread.sleep((long) (Math.random() * 5));
            } catch (Exception e) {
            }
          }
          System.out.println(name + " - Released Left Chopstick");
        }
        System.out.println(name + " - Released Right Chopstick");
        try {
          Thread.sleep((long) (Math.random() * 10));
        } catch (Exception e) {
        }
      }
    }
  }
}
