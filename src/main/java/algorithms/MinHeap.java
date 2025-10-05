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

    // --- helpers ---
    private void percolateUp(int idx) {
        while (idx > 1) {
            int parent = idx / 2;

            tracker.incComparisons();
            tracker.incArrayAccesses(2);

            if (heap[idx].key.compareTo(heap[parent].key) < 0) {
                swap(idx, parent);
                idx = parent;
            } else break;
        }
    }

    private void heapifyDown(int idx) {
        while (true) {
            int left = 2 * idx;
            int right = left + 1;
            int smallest = idx;

            if (left <= size) {
                tracker.incComparisons();
                tracker.incArrayAccesses(2);
                if (heap[left].key.compareTo(heap[smallest].key) < 0) smallest = left;
            }

            if (right <= size) {
                tracker.incComparisons();
                tracker.incArrayAccesses(2);
                if (heap[right].key.compareTo(heap[smallest].key) < 0) smallest = right;
            }

            if (smallest != idx) {
                swap(idx, smallest);
                idx = smallest;
            } else break;
        }
    }

    private void swap(int i, int j) {
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


    public void merge(MinHeap<T> other) {
        for (int i = 1; i <= other.size; i++) {
            tracker.incArrayAccesses();
            this.insert(other.heap[i].key);
        }
    }

}
