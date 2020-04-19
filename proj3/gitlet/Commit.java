package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class calls compare class on all file and get difference between latest commit and version now?
 * Tracks all files in staging class, receives
 * info from user class for latest commit info, synthesis data adding on metadata to produce a new commit
 * In case of merge calls user class twice, merge, and create new commit.
 *
 * @author kenny liao
 */
public class Commit implements Serializable {

    static final String FIRSTCOMMIT = "Thu 1 1 00:00:00 1970 -0700";

    /**
     * Creates a new Commit with following message and time.
     */
    Commit(String message, String time, ArrayList<File> parent, ArrayList<File> staged) {
        _message = message;
        if (!time.equals(FIRSTCOMMIT)) {
            time = time.substring(0, time.length() - 8);
            time = time + "2020 -0700";
        }
        _timestamp = time;
        _tracked = combine(parent, staged);
        makeCode();
    }

    /**
     * Combine all data passed in to be committed.
     */
    private ArrayList<File> combine(ArrayList<File> parent, ArrayList<File> staged) {
        ArrayList<File> comb = new ArrayList<File>();
        if (parent != null) {
            comb.addAll(parent);
        }
        if (staged != null) {
            comb.addAll(staged);
        }
        return comb;
    }

    /**
     * Remove file with name in tracked.
     */
    public void remove(String name) {
        for (File f : _tracked) {
            if (f.getName().equals(name)) {
                _tracked.remove(f);
                break;
            }
        }
    }

    /**
     * Returns the tracked files.
     */
    public ArrayList<File> getTracked() {
        return _tracked;
    }

    /**
     * Return _code.
     */
    public String getCode() {
        return _code;
    }

    /**
     * Return _message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Returns true if this Commit is tracking File with name name.
     */
    public boolean tracking(String name) {
        for (File f: _tracked) {
            if (f.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** Make code of this commit */
    private void makeCode() {
        File temp = new File("temp");
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(temp, this);
        _code =  Utils.sha1(temp.toString());
        temp.delete();
    }

    /**
     * An arraylist that stores all the tracked files.
     */
    private ArrayList<File> _tracked;

    /**
     * Records time of Commit.
     */
    String _timestamp;

    /**
     * Records message.
     */
    private String _message;

    /** Stores the code of the commit this Commit contains. */
    private String _code;
}
