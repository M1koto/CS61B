import java.util.Arrays;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 * <p>
 * Class containing all the sorting algorithms from 61B to date.
 * <p>
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 * <p>
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 * <p>
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 * <p>
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /**
     * Insertion sorts the provided data.
     */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i < k; i++) {
                int small = array[i];
                for (int j = 0; j < i; j++) {
                    if (array[j] > small) {
                        System.arraycopy(array, j, array, j + 1, i - j);
                        array[j] = small;
                        break;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i < k; i++) {
                int small = array[i];
                int index = i;
                for (int j = i; j < k; j++) {
                    if (array[j] < small) {
                        small = array[j];
                        index = j;
                    }
                }
                int temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /**
     * Your merge sort implementation. An iterative merge
     * method is easier to write than a recursive merge method.
     * Note: I'm only talking about the merge operation here,
     * not the entire algorithm, which is easier to do recursively.
     */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int[] ans = new int[array.length];
            int hi = Math.min(k - 1, array.length - 1);
            sort(array, ans, 0, hi);
        }

        private static void sort(int[] a, int[] ans, int lo, int hi) {
            if (hi <= lo) {
                return;
            }
            int mid = lo + (hi - lo) / 2;
            sort(a, ans, lo, mid);
            sort(a, ans, mid + 1, hi);
            merge(a, ans, lo, mid, hi);
        }

        private static void merge(int[] a, int[] ans, int lo, int mid, int hi) {
            if (hi + 1 - lo >= 0) {
                System.arraycopy(a, lo, ans, lo, hi + 1 - lo);
            }
            int i = lo;
            int j = mid + 1;
            for (int k = lo; k <= hi; k++) {
                if (i > mid) {
                    a[k] = ans[j++];
                } else if (j > hi) {
                    a[k] = ans[i++];
                } else if (ans[j] < ans[i]) {
                    a[k] = ans[j++];
                } else {
                    a[k] = ans[i++];
                }
            }
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /**
     * Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /**
     * Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            int N = Math.min(k, a.length);
            int[] ans = new int[N];
            int temp = 1 << 8;
            int mask = temp - 1;

            for (int d = 0; d < 4; d++) {

                int[] count = new int[temp + 1];
                for (int i = 0; i < N; i++) {
                    int c = (a[i] >> 8 * d) & mask;
                    count[c + 1]++;
                }

                for (int r = 0; r < temp; r++)
                    count[r + 1] += count[r];

                if (d == 3) {
                    int shift1 = count[temp] - count[temp / 2];
                    int shift2 = count[temp / 2];
                    for (int r = 0; r < temp / 2; r++)
                        count[r] += shift1;
                    for (int r = temp / 2; r < temp; r++)
                        count[r] -= shift2;
                }

                for (int i = 0; i < N; i++) {
                    int c = (a[i] >> 8 * d) & mask;
                    ans[count[c]++] = a[i];
                }

                for (int i = 0; i < N; i++)
                    a[i] = ans[i];
            }
        }

    @Override
    public String toString() {
        return "LSD Sort";
    }
}

/**
 * MSD Sort implementation.
 */
public static class MSDSort implements SortingAlgorithm {
    @Override
    public void sort(int[] a, int k) {
        // FIXME
    }

    @Override
    public String toString() {
        return "MSD Sort";
    }
}

    /**
     * Exchange A[I] and A[J].
     */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
