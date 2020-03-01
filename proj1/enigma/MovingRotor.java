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
        _setting = super._setting;
        _alphabet = super._alphabet;
        _notches = notches;
    }
    /** True for Moving Rotors */
    @Override
    boolean rotates() {return true; }

    /** Has notch */
    @Override
    boolean atNotch() {
        char NowAtChar = _alphabet.toChar(_setting);
        for (int i = 0; i < _notches.length(); i++) {
            if (NowAtChar == _notches.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    /** Moves rotor forward */
    @Override
    void advance() {
        if (_setting == _alphabet.size() - 1) {
            set(0);
        } else {
            set(_setting + 1);
        }
    }

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    private String _name;

    private Permutation _perm;

    private Alphabet _alphabet;

    private int _setting;

    private String _notches;
}
