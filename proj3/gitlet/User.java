package gitlet;

import edu.neu.ccs.util.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class stores permanent information for 'user'
 * and supports finding, managing, reading all hash.
 *
 * @author kenny liao
 */

public class User {

    /**
     * File in which the .gitlet directory exists.
     */
    static final File DIRECTORY = new File(".gitlet");

    static final File STAGING = new File(".gitlet/stage");

    static DoubleHT INITIAL;

    User() {
        lists = new ArrayList<String>();
        tracked = new ArrayList<String>();
        DIRECTORY.mkdir();
        STAGING.mkdir();
        Commit first = new Commit("initial commit", Commit.FIRSTCOMMIT);
        INITIAL = new DoubleHT(null, first, "Master");
        HEAD = INITIAL;
        _branchHeads = new HashMap<String, DoubleHT>();
        _branchHeads.put("Master", INITIAL);
    }

    /**
     * Adds file with name file to the staging area.
     * Overwrites same file if exists.
     */
    public void add(File file) {
        String name = STAGING.getName() + Utils.sha1(file.getName());
        delete(name);
        File f = new File(name);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(f, file);
        lists.add(name);
    }

    /** Actual removal of file name. */
    private void delete(String name) {
        for (String f : lists) {
            if (f.equals(name)) {
                lists.remove(f);
                new File("name").delete();
            }
            break;
        }
    }

    public void commit(String arg) {
    }

    /** Removes file with name name. */
    public void rm(String name) {
        delete(name);
        if (HEAD.getCommit().tracking(name)) {
            HEAD.getCommit().remove(name)
            //FIXME delete file
        }
    }

    /** Responds to the command with message m. */
    public void find(String m) {
        ArrayList<String> ans = new ArrayList<>();
        INITIAL.findMessage(m, ans);
        if (ans.size() == 0) {
            System.out.println("Found no commit with that message.");
        }
    }
    /**
     * An arraylist that caches all staged but not committed files.
     */
    private ArrayList<String> lists;
    
    /** An arraylist that stores all the tracked files. */
    private ArrayList<String> tracked;

    /** Where the DOUBLEHT HEAD is on. */
    private DoubleHT HEAD;

    /** Stores name : position of branch head. */
    private HashMap<String, DoubleHT> _branchHeads;
}
