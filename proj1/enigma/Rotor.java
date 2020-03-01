package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author kenny liao
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        // FIXME
        _setting = 0;
        _alphabet = perm.alphabet();
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(_alphabet.toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. 這個要換算一整路：完整地從右邊進左邊出*/
    int convertForward(int p) {
        return _permutation.MakePositive(
                _permutation.permute(p + _setting) - _setting);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. 這個要換算一整路：完整地從左邊進右邊出*/
    int convertBackward(int e) {
        return _permutation.MakePositive(
                _permutation.invert(e + _setting) - _setting);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    protected final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    protected Permutation _permutation;

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED

    protected int _setting;

    protected Alphabet _alphabet;

}
