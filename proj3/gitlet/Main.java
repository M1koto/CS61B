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
            System.out.println("Not in an initialized Gitlet directory.");
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
                File temp2 = new File(args[1]);
                _user.rm(temp2);
                _user.save();
                break;
            case "log":
                _user.log();
                break;
            case "global-log":
                _user.global();
                break;
            case "find":
                _user.find(split(args));
                break;
            case "status":
                _user.status();
                break;
            case "checkout":
                File temp3;
                if (_user.warning()) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                if (args[1].equals("--")) {
                    temp3 = new File(args[2]);
                    _user.checkout(_user.getH().getCommit().getCode(), temp3);
                } else {
                    if (args.length > 2 && args[2].equals("--")) {
                        temp3 = new File(args[3]);
                        _user.checkout(args[1], temp3);
                    } else if (args.length == 2){
                        _user.switchBranch(args[1]);
                        _user.checkAll();
                    }
                    else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
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
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                _user.reset(args[1]);
                _user.save();
                break;
            case "merge":
                if (_user.warning()) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                _user.merge(args[1]);
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
