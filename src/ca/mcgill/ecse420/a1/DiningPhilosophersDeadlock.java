package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosophersDeadlock {

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

    final Object rightChopstick;
    final Object leftChopstick;

    public Philosopher(Object[] chopsticks, int position, int numberOfPhilosophers) {
      this.rightChopstick = chopsticks[(position + 1) % numberOfPhilosophers];
      this.leftChopstick = chopsticks[position];
    }

    @Override
    public void run() {
      while (true) {
        String name = Thread.currentThread().getName();

        try {
          // Lock the philosopher's right chopstick
          // If chopstick is already locked, wait until available
          synchronized (rightChopstick) {
            System.out.println(name + " - Holding Right Chopstick");
            Thread.sleep((long) (Math.random() * 5));

            // Lock the philosopher's left chopstick and eat
            // If chopstick is already locked, wait until available
            synchronized (leftChopstick) {
              System.out.println(name + " - Holding Left Chopstick");
              System.out.println(name + " - Eating");
              Thread.sleep((long) (Math.random() * 5));
            }
            System.out.println(name + " - Released Left Chopstick");
          }
          System.out.println(name + " - Released Right Chopstick");
          Thread.sleep((long) (Math.random() * 10));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
