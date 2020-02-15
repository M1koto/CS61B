package image;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author kenny
 */

public class MatrixUtilsTest {

    @Test
    public void test3() {
        assertArrayEquals(new double[][] {{1, 4, 6}, {3, 8, 13}, {11, 11, 16}},
                MatrixUtils.accumulateVertical
                        (new double[][] {{1, 4, 6}, {2, 7, 9}, {8, 8, 8}}));
    }

    @Test
    public void test4() {
        assertArrayEquals(new double[][] {{1, 2}, {4, 7}, {6, 9}},
                MatrixUtils.helper1(new double[][] {{1, 4, 6}, {2, 7, 9}}));
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
