package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineList<T> {

    private class ListNode {
        T item;
        int key;
        ListNode next;
        Lock lock;

        ListNode(T item) {
            this.item = item;
            this.key = item.hashCode();
            this.lock = new ReentrantLock();
        }

        ListNode(int key) {
            this.item = null;
            this.key = key;
            this.lock = new ReentrantLock();
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }

    private ListNode head;

    public FineList() {
        head = new ListNode(Integer.MIN_VALUE);
        head.next = new ListNode(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();

        head.lock();
        ListNode prev = head;

        try {
            ListNode curr = prev.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    return false;
                }
                ListNode newNode = new ListNode(item);
                newNode.next = curr;
                prev.next = newNode;
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    public boolean remove(T item) {
        ListNode prev = null;
        ListNode curr;

        int key = item.hashCode();
        head.lock();

        try {
            prev = head;
            curr = prev.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    prev.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    public boolean contains(T item) {
        ListNode prev = null;
        ListNode curr;

        int key = item.hashCode();
        head.lock();

        try {
            prev = head;
            curr = prev.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return curr.key == key;
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    @Override
    public synchronized String toString() {
        ListNode curr = head.next;
        StringBuilder sb = new StringBuilder("[ ");

        while (curr.item != null) {
            sb.append(curr.item.toString()).append(" ");
            curr = curr.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
