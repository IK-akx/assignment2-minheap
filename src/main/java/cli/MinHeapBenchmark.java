package cli;

import algorithms.MinHeap;
import metrics.PerformanceTracker;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
public class MinHeapBenchmark {

    @Param({"100", "1000", "10000"})
    private int n;

    private MinHeap<Integer> heap;
    private MinHeap<Integer> otherHeap;
    private PerformanceTracker tracker;
    private Random rand;
    private MinHeap.HeapHandle[] handles;

    @Setup(Level.Trial)
    public void setup() {
        tracker = new PerformanceTracker();
        heap = new MinHeap<>(n, tracker);
        otherHeap = new MinHeap<>(n, tracker);
        rand = new Random();

        handles = new MinHeap.HeapHandle[n];
        for (int i = 0; i < n; i++) {
            handles[i] = heap.insert(rand.nextInt(1_000_000));
        }

        for (int i = 0; i < n / 2; i++) {
            otherHeap.insert(rand.nextInt(1_000_000));
        }
    }

    @Benchmark
    public void benchmarkInsert() {
        PerformanceTracker localTracker = new PerformanceTracker();
        MinHeap<Integer> localHeap = new MinHeap<>(n, localTracker);
        for (int i = 0; i < n; i++) {
            localHeap.insert(rand.nextInt(1_000_000));
        }
    }

    @Benchmark
    public void benchmarkExtractMin() {
        PerformanceTracker localTracker = new PerformanceTracker();
        MinHeap<Integer> localHeap = new MinHeap<>(n, localTracker);
        for (int i = 0; i < n; i++) {
            localHeap.insert(rand.nextInt(1_000_000));
        }

        while (!localHeap.isEmpty()) {
            localHeap.extractMin();
        }
    }

    @Benchmark
    public void benchmarkDecreaseKey() {
        int idx = rand.nextInt(n);
        try {
            heap.decreaseKey(handles[idx], rand.nextInt(500_000));
        } catch (IllegalArgumentException ignored) {}
    }

    @Benchmark
    public void benchmarkMerge() {
        MinHeap<Integer> localHeap = new MinHeap<>(n, tracker);
        for (int i = 0; i < n; i++) {
            localHeap.insert(rand.nextInt(1_000_000));
        }
        localHeap.merge(otherHeap);
    }
}

