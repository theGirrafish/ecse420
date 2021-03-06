package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophersNoDeadlock {

  public static void main(String[] args) {

    int numberOfPhilosophers = 50;
    Object[] chopsticks = new Object[numberOfPhilosophers];
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

    // Initialize Chopsticks
    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Object();
    }

    // Initialize Philosophers and execute the Thread
    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] = new Philosopher(chopsticks, i, numberOfPhilosophers);
      executor.execute((philosophers[i]));
      try {
        Thread.sleep((long) (Math.random() * 10));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    executor.shutdown();
  }

  public static class Philosopher implements Runnable {

    int ate = 0;
    final int id;
    final Object rightChopstick;
    final Object leftChopstick;

    Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
      this.id = position + 1;

      // Force the first philosopher to grab the chopstick to his left first
      // As a result, we end up in a circular wait situation where each philosopher
      // is holding on to one chopstick
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
      long start;
      long totalWait = 0;

      for (int x = 0; x < 1000; x++) {
        start = System.nanoTime();

        try {
          // Lock the philosopher's right chopstick
          // If chopstick is already locked, wait until available
          synchronized (rightChopstick) {
//            System.out.println(id + " - Holding Right Chopstick");
            Thread.sleep((long) (Math.random() * 5));

            // Lock the philosopher's left chopstick and eat
            // If chopstick is already locked, wait until available
            synchronized (leftChopstick) {
//              System.out.println(id + " - Holding Left Chopstick");
//              System.out.println(id + " - Eating");
              totalWait += System.nanoTime() - start;
              ate++;
              Thread.sleep((long) (Math.random() * 5));
            }
//            System.out.println(id + " - Released Left Chopstick");
          }
//          System.out.println(id + " - Released Right Chopstick");
          Thread.sleep((long) (Math.random() * 10));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      // To see # of times each philosopher ate, comment out the other System.out.println() lines
      System.out.println("Philosopher " + id + " ate " + ate + " times and waited for " + totalWait/1000000000.0 + " seconds");
    }
  }
}
