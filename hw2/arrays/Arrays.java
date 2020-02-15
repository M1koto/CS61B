package arrays;
/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author kenny
 */
class Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] c = new int[A.length + B.length];
        for (int i = 0; i < A.length; i++) {
            c[i] = A[i];
        }
        for (int k = A.length; k < c.length; k++) {
            c[k] = B[k - A.length];
        }
        return c;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int runner = 0;
        while (A[runner] != start) {
            runner += 1;
        }
        int[] front = new int[runner];
        for (int i = 0; i < runner; i++) {
            front[i] = A[i];
        }
        int nokori = A.length - runner - len;
        int[] back = new int[nokori];
        for (int i = 0; i < nokori; i++) {
            back[i] = A[runner + len + i];
        }

        return catenate(front, back);
    }
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A.length == 0) {
            return new int[0][];
        }
        int l = 1;
        int [][] r;
        int counter = 0;
        for (int i = 1; i < A.length; i++) {
            if (A[i] < A[i - 1]) {
                l++;
            }
        }
        r = new int[l][];
        l = 0;
        for (int i = 1; i < A.length; i++) {
            if (A[i] < A[i - 1]) {
                r[l++] = Utils.subarray(A, counter, i - counter);
                counter = i;
            }
        }
        if (l != r.length) {
            r[l] = Utils.subarray(A, counter, A.length - counter);
        }
        return r;
    }
}
