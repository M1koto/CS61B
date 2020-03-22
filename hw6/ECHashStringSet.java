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
        return 1;
    }

    private void check() {
        if (((double)size / (double)ans.length) > 5) {
            LinkedList<String>[] temp = ans;
            ans = new LinkedList[temp.length * 2];
            size = 0;
            for (LinkedList<String> target : temp) {
                if (target != null) {
                    for (String s : target) {
                        this.put(s);
                    }
                }
            }
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

    private int size;
    private LinkedList<String>[] ans;
}
