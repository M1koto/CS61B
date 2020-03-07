package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author kenny liao
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _alphabet = super.alphabet();
        _perm = super.permutation();
        _name = super.name();
        _notches = notches;
        _setting = 0;
    }
    /** True for Moving Rotors. */
    @Override
    boolean rotates() {
        return true;
    }

    /** Has notch. */
    @Override
    boolean atNotch() {
        char nowAtChar = _alphabet.toChar(this._setting);
        for (int i = 0; i < _notches.length(); i++) {
            if (nowAtChar == _notches.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    /** Moves rotor forward. */
    @Override
    void advance() {
        if (this._setting == _alphabet.size() - 1) {
            this.set(0);
        } else {
            this.set(this._setting + 1);
        }
    }

    /** Return my current setting. */
    int setting() {
        return this._setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        this._setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        this.set(_alphabet.toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. 這個要換算一整路：完整地從右邊進左邊出.*/
    int convertForward(int p) {
        return _perm.makePositive(
                _perm.permute(p + this.setting()) - this.setting());
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. 這個要換算一整路：完整地從左邊進右邊出.*/
    int convertBackward(int e) {
        return _perm.makePositive(
                _perm.invert(e + this.setting()) - this.setting());
    }
    /** My name. */
    private String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _perm;

    /** Alphabets. */
    private Alphabet _alphabet;

    /** settings. */
    private int _setting;

    /** notches. */
    private String _notches;
}
