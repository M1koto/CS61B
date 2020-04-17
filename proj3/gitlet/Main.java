package gitlet;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author kenny liao
 */
public class Main {

    /** Creates a new User for gitlet. */
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
        switch (args[0]) {
            case "init":
                if (_user != null) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    break;
                    //FIXME
                }
                _user = new User();
                break;
            case "add":
                _user.add(args[1]);
                //FIXME
                break;
            case "commit":
                _user.commit(args[1]);
                //FIXME
                break;
            case "rm":
                //FIXME
                break;
            case "log":
                //FIXME
                break;
            case "global-log":
                //FIXME
                break;
            case "find":
                //FIXME
                break;
            case "status":
                //FIXME
                break;
            case "checkout":
                //FIXME
                break;
            case "branch":
                //FIXME
                break;
            case "rm-branch":
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
