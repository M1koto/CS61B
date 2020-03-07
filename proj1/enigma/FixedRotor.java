package enigma;

import static enigma.EnigmaException.*;

/**
 * Class that represents a rotor that has no ratchet and does not advance.
 *
 * @author kenny liao
 */
class FixedRotor extends Rotor {

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM.
     */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _alphabet = super.alphabet();
        _perm = super.permutation();
        _name = super.name();
        _setting = 0;
    }

    /**
     * setting.
     */
    int setting() {
        return this._setting;
    }

    /**
     * Set setting() to POSN.
     */
    void set(int posn) {
        this._setting = posn;
    }

    /**
     * Set setting() to character CPOSN.
     */
    void set(char cposn) {
        this.set(_alphabet.toInt(cposn));
    }

    /**
     * Return the conversion of P (an integer in the range 0..size()-1)
     * according to my permutation. 這個要換算一整路：完整地從右邊進左邊出
     */
    int convertForward(int p) {
        return _perm.makePositive(
                _perm.permute(p + this.setting()) - this.setting());
    }

    /**
     * Return the conversion of E (an integer in the range 0..size()-1)
     * according to the inverse of my permutation. 這個要換算一整路：完整地從左邊進右邊出
     */
    int convertBackward(int e) {
        return _perm.makePositive(
                _perm.invert(e + this.setting()) - this.setting());
    }

    /**
     * name.
     */
    private String _name;
    /**
     * perm.
     */
    private Permutation _perm;
    /**
     * alphabet.
     */
    private Alphabet _alphabet;
    /**
     * setting.
     */
    private int _setting;
}
