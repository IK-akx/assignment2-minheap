package algorithms;

import metrics.PerformanceTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {
    private PerformanceTracker tracker;
    private MinHeap<Integer> heap;

    @BeforeEach
    void setUp() {
        tracker = new PerformanceTracker();
        heap = new MinHeap<>(10, tracker);
    }

    @Test
    void testInsertAndExtractMin() {
        heap.insert(5);
        heap.insert(2);
        heap.insert(8);

        assertEquals(3, heap.size());
        assertEquals(2, heap.extractMin());
        assertEquals(2, heap.size());
        assertEquals(5, heap.extractMin());
        assertEquals(1, heap.size());
        assertEquals(8, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testPeekMin() {
        heap.insert(4);
        heap.insert(1);
        assertEquals(1, heap.peekMin());
        assertEquals(2, heap.size()); // не извлекает
    }

    @Test
    void testExtractFromEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> heap.extractMin());
        assertThrows(NoSuchElementException.class, () -> heap.peekMin());
    }

    @Test
    void testSingleElement() {
        heap.insert(42);
        assertEquals(42, heap.peekMin());
        assertEquals(42, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testDuplicates() {
        heap.insert(5);
        heap.insert(5);
        heap.insert(5);
        assertEquals(3, heap.size());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
    }

    @Test
    void testDecreaseKey() {
        var h1 = heap.insert(50);
        var h2 = heap.insert(30);
        var h3 = heap.insert(40);

        heap.decreaseKey(h1, 10);
        assertEquals(10, heap.peekMin());
    }

    @Test
    void testDecreaseKeyInvalid() {
        var h = heap.insert(10);
        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(h, 20));
    }

    @Test
    void testMerge() {
        MinHeap<Integer> h1 = new MinHeap<>(10, new PerformanceTracker());
        MinHeap<Integer> h2 = new MinHeap<>(10, new PerformanceTracker());

        h1.insert(1);
        h1.insert(3);
        h2.insert(2);
        h2.insert(4);

        h1.merge(h2);
        assertEquals(4, h1.size());
        assertEquals(1, h1.extractMin());
        assertEquals(2, h1.extractMin());
        assertEquals(3, h1.extractMin());
        assertEquals(4, h1.extractMin());
    }

    @Test
    void testPerformanceTrackerIncrements() {
        heap.insert(10);
        heap.insert(5);
        heap.extractMin();

        assertTrue(tracker.getComparisons() > 0);
        assertTrue(tracker.getArrayAccesses() > 0);
        assertTrue(tracker.getSwaps() >= 0);
    }

    @Test
    void testSortingProperty() {
        Random rand = new Random(42);
        int n = 1000;
        for (int i = 0; i < n; i++) heap.insert(rand.nextInt(10000));

        int prev = Integer.MIN_VALUE;
        while (!heap.isEmpty()) {
            int curr = heap.extractMin();
            assertTrue(curr >= prev, "Heap property violated!");
            prev = curr;
        }
    }
}
