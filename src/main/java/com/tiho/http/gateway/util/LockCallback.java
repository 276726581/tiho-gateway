package com.tiho.http.gateway.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class LockCallback {

    public interface Callback<T> {

        T onCall();
    }

    private LockCallback() {
    }

    public static <T> T writeLock(ReadWriteLock readWriteLock, Callback<T> callback) {
        try {
            readWriteLock.writeLock().lock();
            T result = callback.onCall();
            return result;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public static <T> T readLock(ReadWriteLock readWriteLock, Callback<T> callback) {
        try {
            readWriteLock.readLock().lock();
            T result = callback.onCall();
            return result;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public static <T> T lock(Lock lock, Callback<T> callback) {
        try {
            lock.lock();
            T result = callback.onCall();
            return result;
        } finally {
            lock.unlock();
        }
    }
}
