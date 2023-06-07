package com.hcmut.admin.utrafficsystem.tbt.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Debounce {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Future<?> futureTask = null;

    private final long delay;
    private final ReentrantLock lock = new ReentrantLock();

    public Debounce(long delay) {
        this.delay = delay;
    }

    public void debounce(final Runnable runnable) {
        lock.lock();
        // If a task is already scheduled, cancel it
        if (futureTask != null && !futureTask.isDone()) {
            futureTask.cancel(false);
        }

        // Schedule the new task
        futureTask = scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        lock.unlock();
    }
}
