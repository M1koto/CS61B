import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of String values.
 *
 * @author KENNY LIAO
 */
class ECHashStringSet implements StringSet {
    int n = 0;
    Object[] ans = new Object[5];

    public void put(String s) {
        check();
        int temp = s.hashCode();
        if (ans[temp % ans.length] == null) {
            ans[temp % ans.length] = new ArrayList<>();
        }
    }

    private void check() {
        if (n / ans.length > 5) {
            int[] temp = new int[ans.length * 2];
            System.arraycopy(ans, 0, temp, 0, ans.length + 1);
        }
    }

    @Override
    public boolean contains(String s) {
        return true;
    }

    @Override
    public List<String> asList() {
        return null; // FIXME
    }
}
