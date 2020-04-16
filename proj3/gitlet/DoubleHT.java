package gitlet;

/** This class implements linked list by allowing
 * its pointer to point to at most two double HT objects (branch),
 and allows it to be pointed by at most two double HT object (merge).
 *
 *  @author kenny liao
 *  */

public class DoubleHT{
    DoubleHT(DoubleHT Parent, Commit C) {
        _parent1 = Parent;
        _data = C;
    }










    /** The commit stored in this DoubleHT. */
    private Commit _data;

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

