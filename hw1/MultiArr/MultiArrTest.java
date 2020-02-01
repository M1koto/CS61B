import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        //TODO: Your code here!
        int [][] arr1 = {
                {1, 2},
                {2, 3},
        };
         int [][] arr2 = {
                 {2, 8, 0},
                 {0, 0, -1},
         };

        assertEquals(3 , MultiArr.maxValue(arr1));
        assertEquals(8 , MultiArr.maxValue(arr2));
    }

    @Test
    public void testAllRowSums() {
        //TODO: Your code here!
        int [][] tester1 = {
                {1, 2},
                {2, 3},
        };
        int [] ans1 = {3, 5};

        int [][] tester2 = {
                {-1, 0, 7, 8},
                {2, 3, 8, 0},
        };
        int [] ans2 = {14, 13};
        assertArrayEquals(ans1, MultiArr.allRowSums(tester1));
        assertArrayEquals(ans2, MultiArr.allRowSums(tester2));
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
