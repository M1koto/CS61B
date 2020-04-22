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
            _user.update();
        } else if (!args[0].equals("init")) {
            System.out.println("Please init first");
            System.exit(0);
        }
        switch (args[0]) {
            case "init":
                if (_user != null) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    break;
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
                _user.commit(split(args));
                _user.save();
                break;
            case "rm":
                _user.rm(args[1]);
                _user.save();
                break;
            case "log":
                _user.log();
                break;
            case "global-log":
                _user.global();
                break;
            case "find":
                _user.find(args[1]);
                break;
            case "status":
                //FIXME
                break;
            case "checkout":
                if (_user.warning()) {
                    System.out.println("There is an untracked file in the way; delete it or add it first.");
                    System.exit(0);
                }
                if (args[1].equals("--")) {
                    _user.checkout(_user.getH().getCommit().getCode(), args[2]);
                } else {
                    if (args[2].equals("--")) {
                        _user.checkout(args[1], args[3]);
                    } else {
                        _user.switchBranch(args[1]);
                        _user.checkAll();
                    }
                }
                _user.save();
                break;
            case "branch":
                _user.addBranch(args[1]);
                _user.save();
                break;
            case "rm-branch":
                _user.rmBranch(args[1]);
                _user.save();
                break;
            case "reset":
                if (_user.warning()) {
                    System.out.println("There is an untracked file in the way; delete it or add it first.");
                    System.exit(0);
                }
                _user.reset(args[1]);
                _user.save();
                break;
            case "merge":
                //FIXME
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    static String split(String[] args) {
        StringBuilder ret = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            ret.append(args[i].replace("'", ""));
            if (i != args.length - 1) {
                ret.append(' ');
            }
        }
        return ret.toString();
    }

}
