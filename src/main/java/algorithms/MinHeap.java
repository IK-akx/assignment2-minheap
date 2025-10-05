package algorithms;

import metrics.PerformanceTracker;
import java.util.*;


public class MinHeap<T extends Comparable<T>> {
    private static class HeapNode<T> {
        long id;
        T key;
        HeapNode(long id, T key) { this.id = id; this.key = key; }
    }

    public static class HeapHandle {
        private final long id;
        private HeapHandle(long id) { this.id = id; }
        public long getId() { return id; }
    }

    private HeapNode<T>[] heap;
    private int size;
    private Map<Long, Integer> indexMap;
    private PerformanceTracker tracker;

    @SuppressWarnings("unchecked")
    public MinHeap(int capacity, PerformanceTracker tracker) {
        heap = new HeapNode[capacity + 1]; // index 0 unused
        size = 0;
        indexMap = new HashMap<>();
        this.tracker = tracker;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public T peekMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");
        tracker.incArrayAccesses();
        return heap[1].key;
    }

    public HeapHandle insert(T value) {
        long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        HeapNode<T> node = new HeapNode<>(id, value);

        if (++size >= heap.length) resize();
        heap[size] = node;
        indexMap.put(id, size);

        percolateUp(size);
        return new HeapHandle(id);
    }

    public T extractMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");
        tracker.incArrayAccesses();
        T minValue = heap[1].key;

        indexMap.remove(heap[1].id);
        heap[1] = heap[size];
        size--;

        if (size > 0) {
            indexMap.put(heap[1].id, 1);
            heapifyDown(1);
        }
        return minValue;
    }

    // --- Optimized ---
    private void percolateUp(int idx) {
        HeapNode<T> node = heap[idx];
        while (idx > 1) {
            int parent = idx / 2;

            tracker.incComparisons();
            tracker.incArrayAccesses(2);

            if (node.key.compareTo(heap[parent].key) < 0) {
                heap[idx] = heap[parent];
                indexMap.put(heap[parent].id, idx);
                idx = parent;
            } else break;
        }
        heap[idx] = node;
        indexMap.put(node.id, idx);
    }

    // --- Optimized ---
    private void heapifyDown(int idx) {
        HeapNode<T> node = heap[idx];
        while (true) {
            int left = 2 * idx;
            int right = left + 1;
            int smallest = idx;

            if (left <= size) {
                tracker.incComparisons();
                tracker.incArrayAccesses(2);
                if (heap[left].key.compareTo(node.key) < 0) smallest = left;
            }

            if (right <= size) {
                tracker.incComparisons();
                tracker.incArrayAccesses(2);
                T smallestKey = (smallest == idx) ? node.key : heap[smallest].key;
                if (heap[right].key.compareTo(smallestKey) < 0) smallest = right;
            }

            if (smallest != idx) {
                heap[idx] = heap[smallest];
                indexMap.put(heap[smallest].id, idx);
                idx = smallest;
            } else break;
        }
        heap[idx] = node;
        indexMap.put(node.id, idx);
    }

    // --- Optimized ---
    private void swap(int i, int j) {
        if (i == j) return;
        tracker.incSwaps();
        HeapNode<T> tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
        indexMap.put(heap[i].id, i);
        indexMap.put(heap[j].id, j);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        tracker.incAllocations();
        HeapNode<T>[] newHeap = new HeapNode[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }


    public void decreaseKey(HeapHandle handle, T newKey) {
        Integer idx = indexMap.get(handle.getId());
        if (idx == null) {
            throw new IllegalArgumentException("Invalid handle: element not found");
        }

        tracker.incArrayAccesses();
        if (newKey.compareTo(heap[idx].key) > 0) {
            throw new IllegalArgumentException("New key is greater than current key");
        }

        heap[idx].key = newKey;
        percolateUp(idx);
    }


    // --- Optimized ---
    @SuppressWarnings("unchecked")
    public void merge(MinHeap<T> other) {
        int newSize = this.size + other.size;
        HeapNode<T>[] newHeap = new HeapNode[newSize + 1];
        tracker.incAllocations();

        System.arraycopy(this.heap, 1, newHeap, 1, this.size);
        System.arraycopy(other.heap, 1, newHeap, this.size + 1, other.size);

        this.heap = newHeap;
        this.size = newSize;

        indexMap.clear();
        for (int i = 1; i <= size; i++) {
            indexMap.put(heap[i].id, i);
        }

        // O(n) heapify build
        for (int i = size / 2; i >= 1; i--) {
            heapifyDown(i);
        }
    }
}
