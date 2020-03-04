package enigma;

import javax.crypto.spec.PSource;
import java.util.*;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author kenny liao
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<>(allRotors);
        checkReflector();
        _myRotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }
    /** Throws exception if see reflector not in fat left */
    void checkReflector() throws EnigmaException{
        if (!(_allRotors.get(0) instanceof Reflector)) {
            throw new EnigmaException("Reflector needed in the far left");
        }
        for (int i = 1; i < _allRotors.size(); i++) {
            if (_allRotors.get(i) instanceof Reflector) {
                throw new EnigmaException(String.format("Reflector cannot be in the %d", i));
            }
        }
    }
    /** Function that loops through a rotor array and throws exception if any of the elements are null
     * : tagetting insertRotors when rotors not in allRotors is passed in*/
    void CheckRotorArray(Rotor[] array) throws EnigmaException {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new EnigmaException(String.format("%d th element in the array is null, something must be wrong!", i));
            }
        }
    }
    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.size(); j++) {
                if (_allRotors.get(j).name() == rotors[i]) {
                    _myRotors[i] = _allRotors.get(j);
                }
            }
        }
        CheckRotorArray(_myRotors);
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _myRotors.length; i++) {
            Rotor target = _myRotors[i];
            target.set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD.
     * HAVEN'T CHECK IF ONLY COMES IN PAIR FORM*/
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    int convFoward(int c) {
        int count = _myRotors.length - 1;
        int memorize = c;
        while (count >= 0) {
            Rotor target = _myRotors[count];
            memorize = target.convertForward(memorize);
            count -= 1;
        }
        return convBack(memorize);
    }

    int convBack(int c) {
        int count = 1;
        int memorize = c;
        while (count != _myRotors.length) {
            Rotor target = _myRotors[count];
            memorize = target.convertBackward(memorize);
            count += 1;
        }
        return memorize;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (int i = _myRotors.length - 1; i > 0; i--) {
            if (_myRotors[i].atNotch()) {
                _myRotors[i - 1].advance();
            }
        }
         _myRotors[_myRotors.length - 1].advance();
        return convFoward(c);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        return ""; // FIXME
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    private int _numRotors;

    private int _pawls;

    protected Permutation _plugboard;

    private ArrayList<Rotor> _allRotors;
    
    private Rotor[] _myRotors;
    // FIXME: ADDITIONAL FIELDS HERE, IF NEEDED.
}
