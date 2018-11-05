package ca.mcgill.ecse420.a2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestLock {

    private static final int NUMBER_THREADS = 20;
    private static int cnt = 0;

    private static FilterLock filterLock = new FilterLock(NUMBER_THREADS);
    private static BakeryLock bakeryLock = new BakeryLock(NUMBER_THREADS);

    public static void main(String[] args) {

        // No Lock Test
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        for (int i = 0; i < NUMBER_THREADS; i++) {
            executor.execute(new NoLockRunnable());
        }

        executor.shutdown();
        try {
            executor.awaitTermination(  10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("No Lock: " + cnt);

        // Filter Lock Test
        cnt = 0;
        executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        for (int i = 0; i < NUMBER_THREADS; i++) {
            executor.execute(new FilterLockRunnable());
        }

        executor.shutdown();
        try {
            executor.awaitTermination(  10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Filter Lock: " + cnt);

        // Bakery Lock Test
        cnt = 0;
        executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        for (int i = 0; i < NUMBER_THREADS; i++) {
            executor.execute(new BakeryLockRunnable());
        }

        executor.shutdown();
        try {
            executor.awaitTermination(  10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Bakery Lock: " + cnt);
    }

    private static class NoLockRunnable implements Runnable {

        private NoLockRunnable() {

        }

        @Override
        public void run() {
            try {
                while (cnt < 100) {
                    Thread.sleep(1);
                    cnt++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class FilterLockRunnable implements Runnable {

        private FilterLockRunnable() {

        }

        @Override
        public void run() {
            try {
                filterLock.lock();
                while (cnt < 100) {
                    Thread.sleep(1);
                    cnt++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                filterLock.unlock();
            }
        }
    }

    private static class BakeryLockRunnable implements Runnable {

        private BakeryLockRunnable() {

        }

        @Override
        public void run() {
            try {
                bakeryLock.lock();
                while (cnt < 100) {
                    Thread.sleep(1);
                    cnt++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bakeryLock.unlock();
            }
        }
    }
}
