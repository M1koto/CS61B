package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author kenny liao
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();

        if (!_input.hasNext("\\*")) {
            throw new EnigmaException("No asterik");
        }
        _input.next();
        String[] insert = new String[_total];
        for (int i = 0; i < _total; i++) {
            insert[i] = _input.next();
        }
        M.insertRotors(insert);
        String instructions = _input.nextLine();
        setUp(M, instructions);

        while (_input.hasNext("[^\\*]+")) {
            String put = "";
            put += _input.nextLine();
            printMessageLine(M.convert(put));
        }
        if (_input.hasNext("[\\*]")) {
            _input = getInput(makeString(_input));
        }
    }
    /** Turn rest of scanner to string. */
    String makeString(Scanner S) {
        String target = "";
        while (S.hasNext()) {
            target += S.nextLine();
            target += "\n";
        }
        return target;
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            _total = _config.nextInt();
            int movable = _config.nextInt();
            ArrayList<Rotor> Rotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                Rotors.add(readRotor());
            }
            return new Machine(_alphabet, _total, movable, Rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String type = _config.next();
            String permutations = "";
            while (_config.hasNext("\\(.+\\)")) {
                permutations += _config.nextLine();
            }
            Permutation perm = new Permutation(permutations, _alphabet);
            if (type.charAt(0) == 'M') {
                return new MovingRotor(name, perm, type.substring(1));
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(name, perm);
            } else if (type.charAt(0) == 'R') {
                return new Reflector(name, perm);
            } else {
                throw new EnigmaException(String.format("Wrong Rotor format at %s", name));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        settings = settings.trim();
        String[] temp = settings.split("\\s+");
        M.setRotors(temp[0]);
        String str = "";
        for (int i = 1; i < temp.length; i++) {
            str += temp[i];
        }
        Permutation plugboard = new Permutation(str, _alphabet);
        M.setPlugboard(plugboard);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg.length() <= 5) {
            for (int i = 0; i < msg.length(); i++) {
                _output.append(msg.charAt(i));
            }
            _output.append('\n');
        } else {
            for (int i = 0; i < 5; i++) {
                _output.append(msg.charAt(i));
            }
            _output.append(' ');
            printMessageLine(msg.substring(5));
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** total number of rotors. */
    private int _total;
}
