package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        // FIXME
        _cycles = cycles;
        container = new ArrayList<String>();
        addCycle(_cycles);
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        // FIXME
        int count = 0; int frontindex = 0; int backindex = 0;
        while (count < cycle.length()) {
            if (cycle.charAt(count) == '(') {
                frontindex = count;
            }
            if (cycle.charAt(count) == ')') {
                backindex = count;
            }
            if (backindex != 0) {
                container.add(cycle.substring(frontindex + 1, backindex));
                addCycle(cycle.substring(backindex + 1));
                break;
            }
            count ++;
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Makes integer positive by adding alphabet lengths to it */
    int MakePositive(int p) {
        if (p < 0) {
            return MakePositive(p + _alphabet.size());
        } else {
            return p;
        }
    }

    /** maps straightly to target */
    char MapStraight(String cycle, char a){
        int index = cycle.indexOf(a);
        if (index == cycle.length() - 1) {
            return cycle.charAt(0);
        }
        index += 1;
        return cycle.charAt(index);
    }
    /** maps inversely to target */
    char MapInverse(String cycle, char a){
        int index = cycle.indexOf(a);
        if (index == 0) {
            return cycle.charAt(cycle.length());
        }
        index -= 1;
        return cycle.charAt(index);
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size(); // FIXME
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(MakePositive(p));
        char letter = _alphabet.toChar(p);
        return _alphabet.toInt(permute(letter));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return 0;  // FIXME
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        char target = ' ';
        for (int i = 0; i < container.size(); i++) {
            String temp = container.get(i);
            if (temp.indexOf(p) != -1) {
                target = MapStraight(temp, p);
            }
        }
        return target;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return 0;  // FIXME
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < container.size(); i++) {
            if (container.get(i).length() == 1) {
                return true;
            }
        }
        return false;  // FIXME
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    /** Cycle of permutation */
    private String _cycles;

    public ArrayList<String> container;
}
