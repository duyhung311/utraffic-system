package com.hcmut.admin.utrafficsystem.business;

import java.util.concurrent.FutureTask;

public class PriorityFutureTask extends FutureTask<PriorityRunable> implements
        Comparable<PriorityFutureTask> {
    private PriorityRunable priorityRunable;

    public PriorityFutureTask(PriorityRunable callable) {
        super(callable, null);
        this.priorityRunable = callable;
    }

    /*
     * compareTo() method is defined in interface java.lang.Comparable and it is used
     * to implement natural sorting on java classes. natural sorting means the the sort
     * order which naturally applies on object e.g. lexical order for String, numeric
     * order for Integer or Sorting employee by there ID etc. most of the java core
     * classes including String and Integer implements CompareTo() method and provide
     * natural sorting.
     */
    @Override
    public int compareTo(PriorityFutureTask other) {
        Priority p1 = priorityRunable.getPriority();
        Priority p2 = other.priorityRunable.getPriority();
        return p2.ordinal() - p1.ordinal();
    }

}
