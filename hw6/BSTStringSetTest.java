import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author kenny liao
 */
public class BSTStringSetTest  {
    @Test
    public void test1() {
        BSTStringSet tester = new BSTStringSet();
        tester.put("a");
        tester.put("b");
        tester.put("b");
        tester.put("c");
        System.out.println(tester.printer(tester.get_root()));
    }

    @Test
    public void testNothing() {
        // FIXME: Delete this function and add your own tests
    }
}
