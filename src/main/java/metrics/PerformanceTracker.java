package metrics;

import java.io.FileWriter;
import java.io.IOException;

public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;
    private long recursiveCalls;
    private long allocations;

    private long startTime;
    private long endTime;

    private long memBefore;
    private long memAfter;

    public void reset() {
        comparisons = swaps = arrayAccesses = recursiveCalls = allocations = 0;
        startTime = endTime = 0;
        memBefore = memAfter = 0;
    }

    // --- Incrementers ---
    public void incComparisons() { comparisons++; }
    public void incComparisons(long x) { comparisons += x; }

    public void incSwaps() { swaps++; }
    public void incSwaps(long x) { swaps += x; }

    public void incArrayAccesses() { arrayAccesses++; }
    public void incArrayAccesses(long x) { arrayAccesses += x; }

    public void incRecursiveCalls() { recursiveCalls++; }
    public void incRecursiveCalls(long x) { recursiveCalls += x; }

    public void incAllocations() { allocations++; }
    public void incAllocations(long x) { allocations += x; }

    // --- Timing ---
    public void startTimer() {
        Runtime r = Runtime.getRuntime();
        memBefore = r.totalMemory() - r.freeMemory();
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
        Runtime r = Runtime.getRuntime();
        memAfter = r.totalMemory() - r.freeMemory();
    }

    public long getElapsedTimeNs() { return endTime - startTime; }

    // --- Getters ---
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getArrayAccesses() { return arrayAccesses; }
    public long getRecursiveCalls() { return recursiveCalls; }
    public long getAllocations() { return allocations; }
    public long getMemBefore() { return memBefore; }
    public long getMemAfter() { return memAfter; }

    // --- CSV export ---
    public String toCsvLine(int n, String operation) {
        return String.format(
                "%d,%s,%d,%d,%d,%d,%d,%d,%d,%d%n",
                n, operation,
                getElapsedTimeNs(),
                comparisons, swaps, arrayAccesses,
                recursiveCalls, allocations,
                memBefore, memAfter
        );
    }

    public static void writeCsvHeader(FileWriter fw) throws IOException {
        fw.write("n,operation,time_ns,comparisons,swaps,array_accesses,recursive_calls,allocations,mem_before,mem_after\n");
    }

    public void appendCsv(FileWriter fw, int n, String operation) throws IOException {
        fw.write(toCsvLine(n, operation));
    }
}
