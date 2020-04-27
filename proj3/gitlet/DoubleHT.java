package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class implements linked list by allowing
 * its pointer to point to at most two double HT objects (branch),
 * and allows it to be pointed by at most two double HT object (merge).
 *
 * @author kenny liao
 */

public class DoubleHT implements Serializable {

    /** Creates a DoubleHT data structure.
     * @param parent
     * @param c
     * @param branch
     */
    DoubleHT(DoubleHT parent, Commit c, String branch) {
        _parent1 = parent;
        _commit = c;
        _branch1 = branch;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * Make DoubleHT D the children of this DoubleHT.
     */
    public void addChild(DoubleHT d) {
        assert branchMatch(d);
        if (_child1 == null) {
            _child1 = d;
        } else if (_child2 == null) {
            _child2 = d;
        } else {
            System.out.println("More than two branches in the same commit");
        }
    }

    /**
     * Check if D is in same branch as this.
     */
    public boolean branchMatch(DoubleHT d) {
        return d._branch1.equals(_branch1) || d._branch1.equals(_branch2)
                || d._branch2.equals(_branch1) || d._branch2.equals(_branch2);
    }

    /**
     * Add string S to all of its parents.
     */
    public void addBranch(String s) {
        _branch2 = s;
        if (_parent1 != null) {
            _parent1.addBranch(s);
        }
        if (_parent2 != null) {
            _parent2.addBranch(s);
        }
    }

    /**
     * For a given Commit ID S return the corresponding Commit C.
     */
    public DoubleHT findCommit(String s) {
        DoubleHT a;
        DoubleHT b = null;
        if (_commit.getCode().equals(s)) {
            return this;
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

    /**
     * For a given Commit Message M add all corresponding Commit to ArrayList ANS.
     */
    public void findMessage(String m, ArrayList<String> ans) {
        if (_commit.getMessage().equals(m)) {
            ans.add(_commit.getMessage());
            System.out.println(_commit.getCode());
        }
        if (_parent1 != null) {
            _parent1.findMessage(m, ans);
        }
        if (_parent2 != null) {
            _parent2.findMessage(m, ans);

        }
    }

    /**
     * Return the Commit stored here.
     */
    public Commit getCommit() {
        return _commit;
    }

    /** Removes branch NAME for it and all of its parents. */
    public void rmbranch(String name) {
        if (_branch1 != null && _branch1.equals(name)) {
            _branch1 = null;
        } else if (_branch2 != null && _branch2.equals(name)) {
            _branch2 = null;
        }
        if (_parent1 != null) {
            _parent1.rmbranch(name);
        }
        if (_parent2 != null) {
            _parent2.rmbranch(name);
        }
    }

    /** Prints Commited info on branch _current. */
    public void printlog(String current) {
        if (_branch1.equals(current) || (_branch2 != null && _branch2.equals(current))) {
            Commit c = _commit;
            System.out.println(String.format("===\ncommit %s\nDate: %s\n%s\n",
                    c.getCode(), c.time(), c.getMessage()));
        }
        if (_parent1 != null) {
            _parent1.printlog(current);
        } else if (_parent2 != null){
            _parent2.printlog(current);
        }
    }

    /** Returns paren1. */
    public DoubleHT getParent() {
        return _parent1;
    }

    /** Return if b1 or b2 is BRANCH .*/
    public boolean bran(String branch) {
        return (_branch1 != null && _branch1.equals(branch))
                || (_branch2 != null && _branch2.equals(branch));
    }

    /**
     * The commit stored in this DoubleHT.
     */
    private Commit _commit;

    /**
     * The first branch of where this DoubleHT is on.
     * This field is never null.
     */
    public String _branch1;

    /**
     * The second branch of where this DoubleHT is on.
     */
    public String _branch2;

    /**
     * The first parent of this DoubleHT.
     * Is never null except for the first ever commit.
     */
    private DoubleHT _parent1;

    /**
     * The second parent of this DoubleHT.
     * Null if does not exist.
     */
    private DoubleHT _parent2;

    /**
     * The first child of this DoubleHT.
     * If doesn't have any, then is null.
     */
    private DoubleHT _child1;

    /**
     * The second child of this DoubleHT.
     * If doesn't have any, then is null.
     */
    private DoubleHT _child2;
}

