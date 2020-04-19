package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatCodePointException;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author kenny liao
 */
public class Main {

    /**
     * Creates a new User for gitlet.
     */
    private static User _user;

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        File buffer = new File(".gitlet/USER");
        if (buffer.exists()) {
            _user = Utils.readObject(buffer, User.class);
        }
        switch (args[0]) {
            case "init":
                if (_user != null) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    break;
                    //FIXME
                }
                _user = new User();
                _user.save();
                break;
            case "add":
                File temp = new File(args[1]);
                if (!temp.exists()) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                _user.add(temp);
                _user.save();
                break;
            case "commit":
                _user.commit(args[1]);
                //FIXME
                _user.save();
                break;
            case "rm":
                _user.rm(args[1]);
                //FIXME
                _user.save();
                break;
            case "log":
                //FIXME
                break;
            case "global-log":
                //FIXME
                break;
            case "find":
                _user.find(args[1]);
                break;
            case "status":
                //FIXME
                break;
            case "checkout":
                //FIXME
                break;
            case "branch":
                _user.addBranch(args[1]);
                _user.save();
                //FIXME
                break;
            case "rm-branch":
                _user.rmBranch(args[1]);
                _user.save();
                //FIXME
                break;
            case "reset":
                //FIXME
                break;
            case "merge":
                //FIXME
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

}
