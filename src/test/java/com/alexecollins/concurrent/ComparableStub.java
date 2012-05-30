package com.alexecollins.concurrent;

/**
 * @author: alexec (alex.e.c@gmail.com)
 */
public class ComparableStub implements Comparable<ComparableStub> {

    private final int priority;

    public ComparableStub(int priority) {
        this.priority = priority;
    }

    public int compareTo(ComparableStub comparableStub) {
        return this.priority - comparableStub.priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComparableStub that = (ComparableStub) o;

        if (priority != that.priority) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return priority;
    }
}
