package gitlet;

import java.util.ArrayList;

/** This class implements linked list by allowing
 * its pointer to point to at most two double HT objects (branch),
 and allows it to be pointed by at most two double HT object (merge).
 *
 *  @author kenny liao
 *  */

public class DoubleHT{
    DoubleHT(DoubleHT parent, Commit c) {
        _parent1 = parent;
        _commit = c;
    }

    /** Make DoubleHT d the children of this DoubleHT.
     */
    public void addChild(DoubleHT d) {
        if (_child1 == null) {
            _child1 = d;
        } else if (_child2 == null) {
            _child2 = d;
        } else {
            System.out.println("More than two branches in the same commit");
            //FIXME
        }
    }

    /** Make DoubleHT d the children of this DoubleHT.
     */
    public void addParent(DoubleHT d) {
        if (_parent1 == null) {
            _parent1 = d;
        } else if (_parent2 == null) {
            _parent2 = d;
        } else {
            System.out.println("More than two branches in the same commit");
            //FIXME
        }
    }

    /** For a given Commit ID s return the corresponding Commit C. */
    public Commit findCommit(String s) {
        Commit a;
        Commit b = null;
        if (_commit.getCode().equals(s)) {
            return _commit;
        } else {
            a = _parent1.findCommit(s);
            if (_parent2 != null) {
                b = _parent2.findCommit(s);
            }
        }
        if (a == null && b == null) {
            return null;
        } else if (a != null) {
            return a;
        } else {
            return b;
        }
    }

    /** For a given Commit Message m add all corresponding Commit to ArrayList ans. */
    public void findMessage(String m, ArrayList<Commit> ans) {
        if (_commit.getMessage().equals(m)) {
            ans.add(_commit);
        } else {
            _parent1.findMessage(m, ans);
            if (_parent2 != null) {
                _parent2.findMessage(m, ans);
            }
        }
    }









    /** The commit stored in this DoubleHT. */
    private Commit _commit;

    /** The first branch of where this DoubleHT is on.
     * This field is never null. */
    private String _branch1;

    /** The second branch of where this DoubleHT is on.*/
    private String _branch2;

    /** The first parent of this DoubleHT.
     * Is never null except for the first ever commit. */
    private DoubleHT _parent1;

    /** The second parent of this DoubleHT.
     * Null if does not exist.
     */
    private DoubleHT _parent2;

    /** The first child of this DoubleHT.
     * If doesn't have any, then is null.
     */
    private DoubleHT _child1;

    /** The second child of this DoubleHT.
     * If doesn't have any, then is null.
     */
    private DoubleHT _child2;
}

