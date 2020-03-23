import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of String values.
 *
 * @author KENNY LIAO
 */
class ECHashStringSet implements StringSet {
    public ECHashStringSet() {
        ans = new LinkedList[(int)(1/0.2)];
        size = 0;
    }

    @Override
    public List<String> asList() {
        ArrayList<String> temp = new ArrayList<>();
        for (LinkedList<String> an : ans) {
            if (an != null) {
                temp.add(an.toString());
            }
        }
        return temp;
    }

    public void put(String s) {
        check();
        int temp = get_pos(s.hashCode());
        if (ans[temp] == null) {
            ans[temp] = new LinkedList<String>();
        }
        ans[temp].add(s);
        size += 1;
    }

    private int get_pos(int hashCode) {
        int last = hashCode & 1;
        int change = (hashCode & 0x7fffffff) | last;
        return change % ans.length;
    }

    private void check() {
        if (((double)size / (double)ans.length) > 5) {
            LinkedList<String>[] temp = ans;
            ans = new LinkedList[temp.length * 2];
            java.lang.System.arraycopy(temp, 0, ans, 0, temp.length);
        }
    }

    @Override
    public boolean contains(String s) {
        int position = get_pos(s.hashCode());
        if (ans[position] == null) {
            return false;
        }
        return ans[position].contains(s);
    }


    private int size;
    private LinkedList<String>[] ans;
}
