package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */
    @Test
    public void testing() {
        int[][] a = {
                {1, 4, 6},
                {2, 4, 7}
        };
        int[] args_test = {1, 4, 6, 2, 4, 7};
        IntList for_testing = IntList.list(args_test);
        assertEquals("wrong", IntListList.list(a), Lists.naturalRuns(for_testing));
    }
    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
