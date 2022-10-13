package com.hcmut.admin.utrafficsystem.business;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

public class PriorityThreadFactory implements ThreadFactory {
    private int threadPriority;
    private String prefix;
    private int threadCount = 1;

    public PriorityThreadFactory(int threadPriority, String prefix) {
        this.threadPriority = threadPriority;
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(threadPriority);
                } catch (Throwable t) {
                    // just to be safe
                }
                runnable.run();
            }
        };
        String name = prefix + " - " + threadCount;
        threadCount++;
        return new Thread(wrapperRunnable, name);
    }
}
