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

    private RefreshMode refreshMode = RefreshMode.ALWAYS;

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
        if (refreshMode.equals(RefreshMode.ALWAYS)) {
            for (Consumer<BinaryHeapChangeEvent<T>> listener : listeners) {
                listener.accept(event);
            }
        }
    }

    public RefreshMode getRefreshMode() {
        return refreshMode;
    }

    public void setRefreshMode(RefreshMode refreshMode) {
        this.refreshMode = refreshMode;
    }

    public BinaryHeap(BinaryHeap<T> other) {
        heap = (T[]) new Comparable[other.size];
        size = other.getSize();
        itemClass = other.getItemClass();
        System.arraycopy(other.heap, 0, heap, 0, size);
    }

    public void resortHeap() {
        if (size <= 1) {
            return;
        }

        T[] tempHeap = (T[]) new Comparable[size];
        System.arraycopy(heap, 1, tempHeap, 0, size);

        clear();
        for (T item : tempHeap) {
            insert(item);
        }
    }

    public void reverseHeap() {
        if (size <= 1) {
            return;
        }

        T[] reversedHeap = (T[]) new Comparable[size + 1];

        for (int i = 1; i <= size; i++) {
            reversedHeap[i] = heap[size - i + 1];
        }
        heap = reversedHeap;
    }

    public void insert(T item) {
        if (size == heap.length - 1) {
            resize(2 * heap.length);
        }
        int index = ++size;

        heap[index] = item;
        percolateUp(index - 1);

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

    private void percolateUp(int index) {
        T item = heap[index];
        while (index > 1 && item.compareTo(heap[index / 2]) < 0) {
            heap[index] = heap[index / 2];
            index /= 2;
        }
        heap[index] = item;
    }

    public void update(T oldItem, T newItem) {
        int index = findItemIndex(oldItem);
        if (index == -1) {
            throw new IllegalArgumentException("Элемент не найден в пирамиде.");
        }

        heap[index] = newItem;

        BinaryHeapChangeEvent<T> event = new BinaryHeapChangeEvent<>(this, BinaryHeapChangeType.UPDATE, newItem);
        notifyListeners(event);
    }

    private int findItemIndex(T item) {
        for (int i = 1; i <= size; i++) {
            if (heap[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public T[] getHeap() {
        return Arrays.copyOfRange(heap, 1, size + 1);
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

    public void delete(T selectedElement) {
        int index = findItemIndex(selectedElement);
        if (index == -1) {
            throw new NoSuchElementException("Элемент не найден в куче");
        }

        T lastElement = heap[size];
        heap[index] = lastElement;
        heap[size] = null;
        size--;

        if (index > 1 && lastElement.compareTo(heap[index / 2]) < 0) {
            percolateUp(index);
        } else {
            percolateDown(index);
        }

        BinaryHeapChangeEvent<T> event = new BinaryHeapChangeEvent<>(this, BinaryHeapChangeType.REMOVE, selectedElement);
        notifyListeners(event);
    }


    public enum RefreshMode {
        ALWAYS,
        NEVER
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

    public static <T extends Comparable<T>> void heapify(T[] array) {
        int n = array.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyDown(array, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            T temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            heapifyDown(array, i, 0);
        }
    }

    private static <T extends Comparable<T>> void heapifyDown(T[] heap, int heapSize, int parentIndex) {
        int largestIndex = parentIndex;
        int leftChildIndex = 2 * parentIndex + 1;
        int rightChildIndex = 2 * parentIndex + 2;

        if (leftChildIndex < heapSize && heap[leftChildIndex].compareTo(heap[largestIndex]) > 0) {
            largestIndex = leftChildIndex;
        }

        if (rightChildIndex < heapSize && heap[rightChildIndex].compareTo(heap[largestIndex]) > 0) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex != parentIndex) {
            T temp = heap[parentIndex];
            heap[parentIndex] = heap[largestIndex];
            heap[largestIndex] = temp;

            heapifyDown(heap, heapSize, largestIndex);
        }
    }
}
