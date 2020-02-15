package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author kenny
 */

public class ArraysTest {
    /** FIXME
     */
    @Test
    public void test1() {
        assertArrayEquals(new int[] {1, 4, 6, 2, 7, 9},
                Arrays.catenate(new int[] {1, 4, 6}, new int[] {2, 7, 9}));
    }
    @Test
    public void test2() {
        assertArrayEquals(new int[] {1, 2, 7, 9},
                Arrays.remove(new int[] {1, 4, 6, 2, 7, 9}, 4, 2));
        assertArrayEquals(new int[] {1}, Arrays.
                remove(new int[] {1, 4, 6, 2, 7, 9}, 4, 5));
    }

    @Test
    public void test3() {
        assertArrayEquals(new int[][] {{1, 4, 6}, {2, 7, 9}},
                Arrays.naturalRuns(new int[] {1, 4, 6, 2, 7, 9}));
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
