package enigma;

import java.util.ArrayList;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author kenny liao
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        container = new ArrayList<String>();
        addCycle(_cycles);
        MakeMapSelf(cycles, alphabet);
        check_Duplicates(container);
    }
    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm.
     *  Haven't taken into account cycles :
     *  chars not in alphabet or
     *  repeated chars
     *  in cycle*/
    private void addCycle(String cycle) {
        int count = 0; int frontindex = -1; int backindex = -1;
        while (count < cycle.length()) {
            char target = cycle.charAt(count);
            if (target == '(') {
                frontindex = count;
                _leftParen += 1;
            }
            if (target == ')') {
                backindex = count;
                _rightParen += 1;
            }
            if (frontindex != -1 && backindex != -1) {
                _alphabet.checkUnique
                        (cycle.substring(frontindex + 1, backindex));
                container.add(cycle.substring(frontindex + 1, backindex));
                addCycle(cycle.substring(backindex + 1));
                break;
            }
            count++;
        }
        if (_leftParen != _rightParen) {
            throw new EnigmaException("Wrong format: parenthesis");
        }
    }

    /** Throws exception if a single character appears
     * in more than one space in container. L*/
    void check_Duplicates(ArrayList<String> L) {
        for (int i = 0; i < L.size() - 1; i++) {
            for (int j = i + 1; j < L.size(); j++) {
                for (int k = 0; k < L.get(i).length(); k++) {
                    char temp = L.get(i).charAt(k);
                    if (L.get(j).indexOf(temp) != -1) {
                        throw new
                                EnigmaException("No duplicated chars");
                    }
                }
            }
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
    /** Let non-included alphabets map to themselves. */
    /** S A */
    void MakeMapSelf(String s, Alphabet a) {
        for (int i = 0; i < a.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < s.length(); j++) {
                if (a.toChar(i) == s.charAt(j)) {
                    flag = true;
                }
            }
            if (!flag) {
                String temp = "" + a.toChar(i);
                container.add(temp);
            }
        }
    }

    /** Makes integer positive by adding alphabet lengths to it */
    /** P*/
    int makePositive(int p) {
        if (p < 0) {
            return makePositive(p + _alphabet.size());
        } else {
            return p;
        }
    }

    /** maps straightly to target */
    /** CYCLE A*/
    char MapStraight(String cycle, char a) {
        int index = cycle.indexOf(a);
        if (index == -1) {
            return a;
        }
        if (cycle.length() == 1 || index == cycle.length() - 1) {
            return cycle.charAt(0);
        }
        index += 1;
        return cycle.charAt(index);
    }
    /** maps inversely to target */
    char MapInverse(String cycle, char a){
        int index = cycle.indexOf(a);
        if (cycle.length() == 1) {
            return cycle.charAt(0);
        }
        if (index == 0) {
            return cycle.charAt(cycle.length() - 1);
        }
        index -= 1;
        return cycle.charAt(index);
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(makePositive(p));
        char letter = _alphabet.toChar(p);
        return _alphabet.toInt(permute(letter));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(makePositive(c));
        char letter = _alphabet.toChar(c);
        return _alphabet.toInt(invert(letter));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("Does not contain this letter");
        }
        char target = ' ';
        for (int i = 0; i < container.size(); i++) {
            String temp = container.get(i);
            if (temp.indexOf(p) != -1) {
                target = MapStraight(temp, p);
            }
        }
        if (target == ' '){
            return p;
        } else {
            return target;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("Does not contain this letter");
        }
        char target = ' ';
        for (int i = 0; i < container.size(); i++) {
            String temp = container.get(i);
            if (temp.indexOf(c) != -1) {
                target = MapInverse(temp, c);
            }
        }
        return target;
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
                return false;
            }
        }
        return true;
    }
    /** checks format of plugboard */
    void checkPlugboard() throws EnigmaException{
        int temp = container.size();
        for (int j = temp - 1; j >= _leftParen; j--) {
            container.remove(j);
        }
        for (int i = 0; i < container.size(); i++) {
            if (container.get(i).length() != 2) {
                throw new EnigmaException("This cannot be a plugboard");
            }
        }
    }
    /** Applies a*/
    int applyPlugboard(int a) {
        return permute(a);
    }
    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycle of permutation. */
    private String _cycles;
    /**  container of perms. */
    private ArrayList<String> container;
    /** total num of (. */
    private int _leftParen = 0;
    /** total num of ). */
    private int _rightParen = 0;
}
