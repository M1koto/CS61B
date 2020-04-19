package gitlet;

import java.io.File;
import java.util.ArrayList;

/** This class calls compare class on all file and get difference between latest commit and version now?
 Tracks all files in staging class, receives
 info from user class for latest commit info, synthesis data adding on metadata to produce a new commit
 In case of merge calls user class twice, merge, and create new commit.
 *
 *  @author kenny liao
 *  */
public class Commit {

    static final String FIRSTCOMMIT = "Thu 1 1 00:00:00 1970 -0700";

    Commit(String message, String time) {
        _message = message;
        _timestamp = time;
    }

    /** Remove file with name. */
    public void remove(String name) {
        for (File f: tracked) {
            if (f.getName().equals(name)) {
                tracked.remove(f);
                break;
            }
        }
    }

    /** Return _code. */
    public String getCode() {
        return _code;
    }

    /** Return _message. */
    public String getMessage() {
        return _message;
    }

    /** Returns true if this Commit is tracking File with name name. */
    public boolean tracking(String name) {
        return tracked.contains(name);
    }

    /** An arraylist that stores all the tracked files. */
    private ArrayList<File> tracked;

    /** Records time of Commit. */
    String _timestamp;

    /** Records message. */
    private String _message;

    /** Records SHA1 code. */
    private String _code;

}
