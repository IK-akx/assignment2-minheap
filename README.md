# Assignment 2 – Min-Heap Implementation and Analysis

##  Overview
This project implements a **Min-Heap** data structure in Java as part of the *Design and Analysis of Algorithms* course (Assignment 2).  
It includes performance tracking, unit testing, CLI benchmarking, and asymptotic complexity analysis.

---

##  Features
- Fully functional **Min-Heap** with:
    - `insert`
    - `extractMin`
    - `peekMin`
    - `decreaseKey`
    - `merge` (optimized O(n) version)
- **Performance metrics** via `PerformanceTracker` (comparisons, swaps, array accesses, etc.)
- **Comprehensive JUnit tests** for all edge cases
- **Command-line benchmark runner** with configurable input sizes and CSV export
- **Empirical performance validation** compatible with plotting tools (Python, Excel, etc.)

---

---

## How to Run

### 1️. Build and test the project
```bash
mvn clean test
```

### 2. Run benchmark with default parameters
```bash 
mvn compile
java -cp target/classes cli.BenchmarkRunner
```

### 3. Run benchmark with custom parameters
```bash
java -cp target/classes cli.BenchmarkRunner \
  --minSize=100 \
  --maxSize=100000 \
  --step=10 \
  --repeats=3 \
  --output=docs/performance.csv

```

### With JMH
```bash 
mvn clean compile dependency:copy-dependencies
```
```bash
java -cp "target/classes;target/dependency/*" cli.BenchmarkRunner
```

---

### View results

Benchmark results are stored in docs/performance.csv and can be visualized (e.g. using Excel or matplotlib).

### Example CSV Output
| n   | operation | time_ns | comparisons | swaps | array_accesses | recursive_calls | allocations | mem_before | mem_after |
| --- | --------- | ------- | ----------- | ----- | -------------- | --------------- | ----------- | ---------- | --------- |
| 100 | insert    | 215000  | 99          | 43    | 128            | 0               | 0           | 12345678   | 12457890  |
| 100 | extract   | 170000  | 120         | 65    | 210            | 0               | 0           | 12340000   | 12341000  |
| 100 | merge     | 30000   | 70          | 32    | 80             | 0               | 1           | 12340000   | 12360000  |


### Complexity Analysis


#### Insert
| Case    | Time     | Space | Description                              |
| ------- | -------- | ----- | ---------------------------------------- |
| Best    | Ω(1)     | O(1)  | Element placed at bottom without swaps   |
| Average | Θ(log n) | O(1)  | Percolates up on average half the height |
| Worst   | O(log n) | O(1)  | Must bubble up to the root               |

#### Extract-Min
| Case    | Time     | Space | Description             |
| ------- | -------- | ----- | ----------------------- |
| Best    | Ω(1)     | O(1)  | Single element in heap  |
| Average | Θ(log n) | O(1)  | Half-height percolation |
| Worst   | O(log n) | O(1)  | Full height percolation |


#### Decrease-Key
| Case    | Time     | Space | Description                   |
| ------- | -------- | ----- | ----------------------------- |
| Best    | Ω(1)     | O(1)  | New key does not need to move |
| Average | Θ(log n) | O(1)  | Moves up partway              |
| Worst   | O(log n) | O(1)  | Moves to root                 |

#### Merge (Optimized)
| Case             | Time                  | Space    | Description                                      |
| ---------------- | --------------------- | -------- | ------------------------------------------------ |
| All              | Θ(n + m)              | O(n + m) | Heapify-based merge — single linear-time rebuild |
| Previous version | O((n + m) log(n + m)) | —        | Insert-based merge (less efficient)              |

---

### Space Complexity Summary
 - Heap array: O(n)
 - Index map: O(n)
 - Auxiliary space per operation: O(1)
 - Total: Θ(n)

---

### Theoretical vs Empirical Alignment

Empirical benchmarks (CSV) confirm that:
 - insert and extractMin grow logarithmically.
 - merge now grows linearly after optimization.
 - Memory usage remains proportional to input size n.

When plotting time vs n (log scale), slopes align with theoretical predictions.