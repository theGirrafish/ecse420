package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestList {
    private static final int NUMBER_THREADS = 4;
    private static final int NUM_ITEMS = 100;
    private static final int THREAD_ITEMS = NUM_ITEMS / NUMBER_THREADS;

    private static FineList<Integer> fineList = new FineList<>();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        for (int i = 0; i < NUMBER_THREADS; i++) {
            executor.execute(new ListRunnable(i * THREAD_ITEMS));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(fineList.toString());
    }

    private static class ListRunnable implements Runnable {
        int cnt;

        private ListRunnable(int cnt) {
            this.cnt = cnt;
        }

        public void run() {
            for (int i = 0; i < THREAD_ITEMS; i++) {
                if (!fineList.add(cnt + i)) {
                    System.out.println("[FAIL] Could not add: " + (cnt + i));
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (fineList.contains(cnt + i)) {
                    if (!fineList.remove(cnt + i)) {
                        System.out.println("[FAIL] Could not remove: " + (cnt + i));
                    }
                } else {
                    System.out.println("[FAIL] Could not find: " + (cnt + i));
                }
            }
        }
    }
}
