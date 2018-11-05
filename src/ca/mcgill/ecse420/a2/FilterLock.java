package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FilterLock implements Lock {

    private AtomicInteger[] level;
    private AtomicInteger[] victim;
    private int n;

    public FilterLock(int n) {
        level = new AtomicInteger[n];
        victim = new AtomicInteger[n];
        this.n = n;
        for (int i = 0; i < n; i++) {
            level[i] = new AtomicInteger();
            victim[i] = new AtomicInteger();
        }
    }

    @Override
    public void lock() {
        int id = (int) Thread.currentThread().getId() % n;
        for (int i = 1; i < n; i++) {
            level[id].set(i);
            victim[i].set(id);
            for (int j = 0; j < n; j++) {
                while ((j != id) && (level[j].get() >= i && victim[i].get() == id)) {
                    // wait here
                }
            }
        }
    }

    @Override
    public void unlock() {
        int id = (int) Thread.currentThread().getId() % n;
        level[id].set(0);
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
