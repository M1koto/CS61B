package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author kenny Liao
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        checkUnique(_chars);
    }

    /** Throws Exception if not unique. */
    /** CHARS. */
    void checkUnique(String chars) {
        char[] temp = new char[chars.length()];
        for (int i = 0; i < temp.length; i++) {
            char theChar = chars.charAt(i);
            if (theChar == '*') {
                throw new EnigmaException("No '*' ");
            }
            temp[i] = theChar;
        }
        for (int j = 0; j < temp.length; j++) {
            for (int k = j + 1; k < temp.length; k++) {
                if (temp[j] == temp[k]) {
                    throw new EnigmaException("No Duplicates");
                }
            }
        }

    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.indexOf(ch) > -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _chars.indexOf(ch);
    }

    /** class variable - self work.*/
    private String _chars;

}
