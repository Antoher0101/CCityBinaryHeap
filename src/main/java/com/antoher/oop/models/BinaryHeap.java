package com.antoher.oop.models;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class BinaryHeap<T extends Comparable<T>> implements Iterable<T> {
    final Class<?> itemClass;
    private List<Consumer<BinaryHeapChangeEvent<T>>> listeners = new ArrayList<>();

    private static final int DEFAULT_CAPACITY = 10;
    private T[] heap;
    private int size;

    public BinaryHeap(Class<T> itemClass) {
        this(itemClass, DEFAULT_CAPACITY);
    }

    public BinaryHeap(Class<?> itemClass, int capacity) {
        heap = (T[]) new Comparable[capacity];
        size = 0;

        this.itemClass = itemClass;
    }

    public void addListener(Consumer<BinaryHeapChangeEvent<T>> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(BinaryHeapChangeEvent<T> event) {
        for (Consumer<BinaryHeapChangeEvent<T>> listener : listeners) {
            listener.accept(event);
        }
    }

    public BinaryHeap(BinaryHeap<T> other) {
        heap = (T[]) new Comparable[other.size];
        size = other.getSize();
        itemClass = other.getItemClass();
        System.arraycopy(other.heap, 0, heap, 0, size);
    }

    public void insert(T item) {
        if (size == heap.length - 1) {
            resize(2 * heap.length);
        }
        int index = ++size;
        while (index > 1 && item.compareTo(heap[index / 2]) < 0) {
            heap[index] = heap[index / 2];
            index /= 2;
        }
        heap[index] = item;

        BinaryHeapChangeEvent<T> event = new BinaryHeapChangeEvent<>(this, BinaryHeapChangeType.ADD, item);
        notifyListeners(event);
    }

    public T deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Пирамида пуста.");
        }
        T min = heap[1];
        heap[1] = heap[size];
        heap[size--] = null;
        percolateDown(1);
        if (size > 0 && size < heap.length / 4) {
            resize(heap.length / 2);
        }

        BinaryHeapChangeEvent<T> event = new BinaryHeapChangeEvent<>(this, BinaryHeapChangeType.REMOVE, min);
        notifyListeners(event);
        return min;
    }

    private void percolateDown(int index) {
        T temp = heap[index];
        int child;
        while (2 * index <= size) {
            child = 2 * index;
            if (child != size && heap[child + 1].compareTo(heap[child]) < 0) {
                child++;
            }
            if (heap[child].compareTo(temp) < 0) {
                heap[index] = heap[child];
            } else {
                break;
            }
            index = child;
        }
        heap[index] = temp;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public T[] getHeap() {
        return Arrays.copyOfRange(heap,1, size + 1);
    }

    private void resize(int capacity) {
        T[] newHeap = (T[]) new Comparable[capacity];
        System.arraycopy(heap, 0, newHeap, 0, size + 1);
        heap = newHeap;
    }

    public void clear() {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new BinaryHeapIterator();
    }

    public Class<?> getItemClass() {
        return itemClass;
    }

    private class BinaryHeapIterator implements Iterator<T> {
        private int currentIndex = 1;

        @Override
        public boolean hasNext() {
            return currentIndex <= size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return heap[currentIndex++];
        }
    }
}
