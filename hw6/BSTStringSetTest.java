import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
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
        List<String> Tester2 = tester.asList();
        ArrayList ans = new ArrayList<String>();
        ans.add("a");
        ans.add("b");
        ans.add("c");
        assertEquals(ans, Tester2);
        assertTrue(tester.contains("b"));
        assertFalse(tester.contains("x"));
    }
}
