package com.antoher.oop.models;

public class BinaryHeapChangeEvent<T extends Comparable<T>> {
    private BinaryHeap<T> source;
    private BinaryHeapChangeType changeType;
    private T element;

    public BinaryHeapChangeEvent(BinaryHeap<T> source, BinaryHeapChangeType changeType, T element) {
        this.source = source;
        this.changeType = changeType;
        this.element = element;
    }

    public BinaryHeap<T> getSource() {
        return source;
    }

    public BinaryHeapChangeType getChangeType() {
        return changeType;
    }

    public T getElement() {
        return element;
    }
}

enum BinaryHeapChangeType {
    ADD,
    REMOVE,
    UPDATE
}