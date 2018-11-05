package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class BakeryLock implements Lock {

    private AtomicBoolean[] flag;
    private AtomicInteger[] label;

    private int n;

    public BakeryLock(int n) {
        flag = new AtomicBoolean[n];
        label = new AtomicInteger[n];
        this.n = n;
        for (int i = 0; i < n; i++) {
            flag[i] = new AtomicBoolean();
            label[i] = new AtomicInteger();
        }
    }

    @Override
    public void lock() {
        int id = (int) Thread.currentThread().getId() % n;
        flag[id].set(true);
        label[id].set(findLargestValue(label) + 1);
        for (int i = 0; i < n; i++) {
            while ((i != id) && flag[i].get() && ((label[i].get() < label[id].get()) || ((label[i].get() == label[id].get()) && i < id))) {
                // wait here
            }
        }
    }

    @Override
    public void unlock() {
        int id =  (int) Thread.currentThread().getId() % n;
        flag[id].set(false);
        label[id].set(0);
    }

    private int findLargestValue(AtomicInteger[] array) {
        int max = Integer.MIN_VALUE;
        for (AtomicInteger a : array) {
            int val = a.get();
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
