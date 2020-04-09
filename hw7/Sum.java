/** HW #7, Two-sum problem.
 * @author KENNY L
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        MySortingAlgorithms.MergeSort t = new MySortingAlgorithms.MergeSort();
        t.sort(A, A.length);
        t.sort(B, B.length);
        for (int i = 0; i < A.length; i++) {
            int keep = m - A[i];
            for (int j = 0; j < B.length; j++) {
                if (keep - B[j] == 0) {
                    return true;
                } else if (keep - B[j] < 0) {
                    break;
                }
            }
        }
        return false;
    }

}
