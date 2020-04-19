package gitlet;

import edu.neu.ccs.util.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This class stores permanent information for 'user'
 * and supports finding, managing, reading all hash.
 *
 * @author kenny liao
 */

public class User implements Serializable {

    /**
     * File in which the .gitlet directory exists.
     */
    static final File DIRECTORY = new File(".gitlet");

    static final File STAGING = new File(".gitlet/stage");

    File USER = new File(".gitlet/USER");
    static DoubleHT INITIAL;

    public User() {
        staged = new ArrayList<File>();
        DIRECTORY.mkdir();
        STAGING.mkdir();
        try {
            USER.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Commit first = new Commit("initial commit", Commit.FIRSTCOMMIT, null, null);
        INITIAL = new DoubleHT(null, first, "Master");
        HEAD = INITIAL;
        _current = "Master";
        _branchHeads = new HashMap<String, DoubleHT>();
        _branchHeads.put("Master", INITIAL);
        save();
    }

    /**
     * Adds file with name file to the staging area.
     * Overwrites same file if exists.
     */
    public void add(File file) {
        String name = ".gitlet/stage/" + Utils.sha1(file.getName());
        File f = new File(name);
        if (FileUtils.contentEquals()) {
            rm(file.getName());
            return;
        }
        delete(name);
        staged.remove(file);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        staged.add(file);
    }

    /**
     * Actual removal of file in directory based on name passed in.
     */
    private void delete(String name) {
        File temp = new File(name);
        if (temp.exists()) {
            temp.delete();
        }
    }

    /** Makes a commit with message. */
    public void commit(String message) {
        Commit c = new Commit(message, time().toString(),
                HEAD.getCommit().getTracked(), staged);
        DoubleHT d = new DoubleHT(HEAD, c, _current);
        staged.clear();
    }

    /**
     * Removes file with name name.
     */
    public void rm(String name) {
        String stage = ".gitlet/stage/" + Utils.sha1(name);
        delete(stage);
        if (HEAD.getCommit().tracking(stage)) {
            HEAD.getCommit().remove(stage);
            delete(name);
        }
    }

    /**
     * Returns the Commit time.
     */
    private Date time() {
        return new Date();
    }

    /** Adds a pointer of name name to tip of the branch. */
    public void addBranch(String name) {
        if (_branchHeads.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
        } else {
            DoubleHT temp = _branchHeads.get(_current);
            _branchHeads.put(name, temp);
        }
    }

    /**
     * Saves this User.
     */
    public void save() {
        Utils.writeObject(USER, this);
    }

    /**
     * Responds to the command with message m.
     */
    public void find(String m) {
        ArrayList<String> ans = new ArrayList<>();
        INITIAL.findMessage(m, ans);
        if (ans.size() == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * An arraylist that stores all the tracked files.
     */
    private ArrayList<File> staged;

    /**
     * Where the DOUBLEHT HEAD is on.
     */
    private DoubleHT HEAD;

    /**
     * Stores name : position of branch head.
     */
    private HashMap<String, DoubleHT> _branchHeads;

    /** Stores the current branch the user is on. */
    private String _current;
}
