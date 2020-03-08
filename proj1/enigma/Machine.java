package enigma;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Class that represents a complete enigma machine.
 *
 * @author kenny liao
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<>(allRotors);
        _myRotors = new Rotor[numRotors];
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Throws exception if see reflector not in fat left.
     */
    void checkReflector() throws EnigmaException {
        if (!(_myRotors[0] instanceof Reflector)) {
            throw new EnigmaException("Reflector needed in the far left");
        }
        for (int i = 1; i < _myRotors.length; i++) {
            if (_myRotors[i] instanceof Reflector) {
                throw new EnigmaException(String.format("Reflector at %d", i));
            }
        }
    }

    /**
     * Function that loops through a rotor ARRAY
     * and throws exception if any of the elements are null
     * : targetting insertRotors when rotors not in allRotors is passed in.
     */
    void checkRotorArray(Rotor[] array) throws EnigmaException {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new EnigmaException(String.format
                        ("%d th element in the array is null", i));
            }
        }
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        int actual = 0;
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.size(); j++) {
                if (rotors[i].equals(_allRotors.get(j).name())) {
                    _myRotors[i] = _allRotors.get(j);
                    if (_allRotors.get(j) instanceof MovingRotor) {
                        actual += 1;
                    }
                }
            }
        }
        if (actual != _pawls) {
            throw new EnigmaException("Movable rotors wrong num");
        }
        checkReflector();
        checkRotorArray(_myRotors);
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        for (int i = 1; i < _myRotors.length; i++) {
            Rotor target = _myRotors[i];
            target.set(setting.charAt(i - 1));
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        plugboard.checkPlugboard();
        _plugboard = plugboard;
    }

    /**
     * convert C forward returns convback.
     */
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

    /**
     * convert C back returns memorize.
     */
    int convBack(int c) {
        int count = 1;
        int memorize = c;
        while (count != _myRotors.length) {
            Rotor target = _myRotors[count];
            memorize = target.convertBackward(memorize);
            count += 1;
        }
        memorize = applyPlugboard(memorize);
        return memorize;
    }

    /**
     * applyplugboard C returns applying.
     */
    int applyPlugboard(int c) {
        return _plugboard.applyPlugboard(c);
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        boolean[] flag = new boolean[_myRotors.length];
        ArrayList<Integer> position = new ArrayList<Integer>();
        for (int i = 0; i < _myRotors.length; i++) {
            if (_myRotors[i].atNotch()) {
                position.add(i);
            }
        }
        for (int k = 0; k < position.size(); k++) {
            _myRotors[position.get(k) - 1].advance();
            flag[position.get(k) - 1] = true;
        }
        for (int j = 0; j < position.size(); j++) {
            if (_myRotors[position.get(j) - 1]
                    instanceof MovingRotor && !flag[position.get(j)]) {
                _myRotors[position.get(j)].advance();
                flag[position.get(j)] = true;
            }
        }
        if (!flag[_myRotors.length - 1]) {
            _myRotors[_myRotors.length - 1].advance();
        }
        c = applyPlugboard(c);
        return convFoward(c);
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String ret = "";
        msg = msg.replaceAll("\\s+", "");
        for (int i = 0; i < msg.length(); i++) {
            int temp = convert(_alphabet.toInt(msg.charAt(i)));
            ret = ret + _alphabet.toChar(temp);
        }
        return ret;
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /**
     * total num of rotors.
     */
    private int _numRotors;
    /**
     * total num of pawls.
     */
    private int _pawls;
    /**
     * plugboard.
     */
    protected Permutation _plugboard;
    /**
     * all rotors.
     */
    private ArrayList<Rotor> _allRotors;
    /**
     * my rotors.
     */
    private Rotor[] _myRotors;
}
