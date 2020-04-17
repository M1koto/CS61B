package gitlet;

import edu.neu.ccs.util.FileUtilities;

import java.io.File;
import java.util.ArrayList;

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

    static final File STAGING = new File(".gitlet/Stage");

    User() {
        lists = new ArrayList<File>();
        DIRECTORY.mkdir();
        STAGING.mkdir();
    }

    /**
     * Adds file with name file to the staging area.
     * Overwrites same file if exists.
     */
    public void add(String file) {
        File target = new File(String.format(".gitlet/%s"), Utils.sha1(file));
        if ()
        String code = Utils.sha1(target);

    }

    private void deleteDuplicate(File target) {
        for (File f : lists) {
            if (f == target) {
                Utils.sha1(f)
                lists.remove(f);
            }
            break;
        }
    }

    public void commit(String arg) {
    }

    /**
     * An arraylist that caches all staged but not committed files.
     */
    ArrayList<File> lists;
}
