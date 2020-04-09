/** HW #7, Two-sum problem.
 * @author KENNY L
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        MySortingAlgorithms.MergeSort t = new MySortingAlgorithms.MergeSort();
        t.sort(A, A.length);
        t.sort(B, B.length);
        for (int value : A) {
            if (value > m) {
                return false;
            }
            int keep = m - value;
            for (int i : B) {
                if (keep - i == 0) {
                    return true;
                } else if (keep - i < 0) {
                    break;
                }
            }
        }
        return false;
    }

}
