package cli;

import algorithms.MinHeap;
import metrics.PerformanceTracker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BenchmarkRunner {

    public static void main(String[] args) {
        int minSize = 100;
        int maxSize = 10000;
        int step = 10;
        int repeats = 3;
        String output = "docs/performance.csv";

        for (String arg : args) {
            if (arg.startsWith("--minSize=")) minSize = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--maxSize=")) maxSize = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--step=")) step = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--repeats=")) repeats = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--output=")) output = arg.split("=")[1];
        }

        System.out.printf("Running benchmarks: n = [%d .. %d], step=%d, repeats=%d%n",
                minSize, maxSize, step, repeats);

        try (FileWriter fw = new FileWriter(output)) {
            PerformanceTracker.writeCsvHeader(fw);

            for (int n = minSize; n <= maxSize; n *= step) {
                for (int r = 0; r < repeats; r++) {
                    runSingleBenchmark(fw, n);
                }
            }

            System.out.println("Benchmark completed. Results saved to: " + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runSingleBenchmark(FileWriter fw, int n) throws IOException {
        PerformanceTracker tracker = new PerformanceTracker();
        MinHeap<Integer> heap = new MinHeap<>(n, tracker);
        Random rand = new Random();

        // --- Insert  ---
        tracker.reset();
        tracker.startTimer();
        for (int i = 0; i < n; i++) {
            heap.insert(rand.nextInt(1_000_000));
        }
        tracker.stopTimer();
        tracker.appendCsv(fw, n, "insert");

        // --- Extrat  ---
        tracker.reset();
        tracker.startTimer();
        while (!heap.isEmpty()) {
            heap.extractMin();
        }
        tracker.stopTimer();
        tracker.appendCsv(fw, n, "extract");

        // --- Decrease-key ---
        tracker.reset();
        heap = new MinHeap<>(n, tracker);
        var handles = new MinHeap.HeapHandle[n];
        for (int i = 0; i < n; i++) {
            handles[i] = heap.insert(rand.nextInt(1_000_000));
        }

        tracker.startTimer();
        for (int i = 0; i < n / 10; i++) {
            int idx = rand.nextInt(n);
            int newKey = rand.nextInt(500_000);
            try {
                heap.decreaseKey(handles[idx], newKey);
            } catch (IllegalArgumentException ignored) {}
        }
        tracker.stopTimer();
        tracker.appendCsv(fw, n, "decreaseKey");

        // --- Merge ---
        tracker.reset();
        MinHeap<Integer> other = new MinHeap<>(n, tracker);
        for (int i = 0; i < n / 2; i++) {
            other.insert(rand.nextInt(1_000_000));
        }

        tracker.startTimer();
        heap.merge(other);
        tracker.stopTimer();
        tracker.appendCsv(fw, n, "merge");

        fw.flush();
    }
}
