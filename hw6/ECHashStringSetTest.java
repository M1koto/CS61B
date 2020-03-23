import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import java.lang.Integer;
/**
 * Test of a BST-based String Set.
 * @author KENNY LIAO
 */
public class ECHashStringSetTest  {

    @Test
    public void test1() {
        ECHashStringSet test = new ECHashStringSet();
        test.put("a");
        test.put("b");
        test.put("c");
        System.out.println(test.asList());
        String s1 = "Irit";
        String s2 = "hi";
        System.out.println(s1.compareTo(s2));
    }
}
