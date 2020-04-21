package gitlet;

import edu.neu.ccs.util.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

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

    /** Dir Staging. */
    static final File STAGING = new File(".gitlet/stage");

    /** File for permanent storage. */
    File USER = new File(".gitlet/USER");

    /** First commit's doubleHT. */
    DoubleHT INITIAL;

    /** Creates a unique user for gitlet. */
    public User() {
        staged = new ArrayList<File>();
        DIRECTORY.mkdir();
        STAGING.mkdir();
        try {
            USER.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        total = new ArrayList<Commit>();
        real = new HashMap<>();
        untracked = new ArrayList<>();


        Commit first = new Commit("initial commit", null, null, null);
        total.add(first);
        INITIAL = new DoubleHT(null, first, "Master");
        first.setFather(INITIAL);
        HEAD = INITIAL;
        _current = "Master";
        _branchHeads = new HashMap<String, DoubleHT>();
        _branchHeads.put("Master", INITIAL);

        publish(first, first.getCode());
    }

    /**
     * File writing of Commit c with code as name.
     */
    private void publish(Commit c, String code) {
        File store = new File(".gitlet/" + code);
        store.mkdir();
        for (File f: c.getTracked()) {
            File temp = new File(".gitlet/" + code + "/" + f.getName());
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Utils.writeContents(temp, Utils.readContentsAsString(f));
        }
    }

    /**
     * Adds file with name FILE to the staging area.
     * Overwrites same file if exists.
     */
    public void add(File file) {
        String buffer = Utils.sha1(file.getName());
        String name = ".gitlet/stage/" + buffer;
        File f = new File(name);
        //if (FileUtils.contentEquals()) {
        //rm(file.getName());
        //return;
        //}
        delete(name);
        staged.remove(file);
        real.remove(buffer);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(f, Utils.readContentsAsString(file));
        real.putIfAbsent(buffer, file.getName());
        staged.add(file);
    }

    /**
     * Actual removal of file in directory based on NAME passed in.
     */
    private void delete(String name) {
        File temp = new File(name);
        if (temp.exists()) {
            temp.delete();
        }
    }

    /**
     * Makes a commit with MESSAGE.
     */
    public void commit(String message) {
        if (staged.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit c = new Commit(message, time(),
                HEAD.getCommit().getTracked(), staged);
        c.setReal(HEAD.getCommit().getReal()); // add real name of files of parent commit to this commit's real name
        c.setReal(real); // add staged file's real name to this commit's real name hashmap
        total.add(c);
        DoubleHT d = new DoubleHT(HEAD, c, _current);
        c.setFather(d);
        _branchHeads.remove(_current);
        _branchHeads.put(_current, d);
        HEAD = d;

        publish(c, c.getCode());
        staged.clear();
        real.clear();
        for (File file : STAGING.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    /**
     * Removes file with name NAME.
     */
    public void rm(String name) {
        String stage = ".gitlet/stage/" + Utils.sha1(name);
        File temp = new File(name);
        boolean removed = staged.remove(temp);
        delete(stage);
        if (HEAD.getCommit().tracking(stage)) {
            HEAD.getCommit().remove(stage);
            delete(name);
        } else if (!removed) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** Corresponds to reset command
     * and checks out all with CODE. */
    public void reset(String code) {
        Commit target = null;
        for (Commit c: total) {
            if (c.getCode().equals(code)) {
                target = c;
                break;
            }
        }
        if (target == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            HEAD = target.getFather();
            _branchHeads.remove(code);
            _branchHeads.put(code, target.getFather());
            checkAll();
        }
    }

    /**
     * Returns the Commit time.
     */
    private Date time() {
        return new Date();
    }

    /**
     * Adds a pointer of name NAME to tip of the branch.
     */
    public void addBranch(String name) {
        if (_branchHeads.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
        } else {
            DoubleHT temp = _branchHeads.get(_current);
            if (temp._branch2 == null) {
                temp._branch2 = name;
                _branchHeads.put(name, temp);
            } else {
                System.out.println("Only two branch");
            }
        }
    }

    /**
     * Removes pointer of NAME of branch.
     */
    public void rmBranch(String name) {
        if (name.equals(_current)) {
            System.out.println("Cannot remove the current branch.");
        } else if (!_branchHeads.containsKey(name)) {
            System.out.println("branch with that name does not exist.");
        } else {
            _branchHeads.remove(name);
        }
    }

    /**
     * Saves this User.
     */
    public void save() {
        Utils.writeObject(USER, this);
    }

    /**
     * Responds to the command with message M.
     */
    public void find(String m) {
        ArrayList<String> ans = new ArrayList<>();
        HEAD.findMessage(m, ans);
        if (ans.size() == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Prints log since initial commit until now.
     * In chronological order.
     */
    public void log() {
        DoubleHT temp = _branchHeads.get(_current);
        temp.printlog(_current);
    }

    /**
     * Prints out all commits since the first first one.
     */
    public void global() {
        for (Commit c : total) {
            System.out.println(String.format("===\ncommit %s\nDate: %s\n%s\n",
                    c.getCode(), c.time(), c.getMessage()));
        }
    }

    /**
     * Switches Branch from to ARG and updating _current.
     */
    public void switchBranch(String arg) {
        if (!_branchHeads.containsKey(arg)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (arg.equals(_current)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        } else {
            _current = arg;
            HEAD = _branchHeads.get(_current);
        }
    }

    /**
     * Returns the HEAD.
     */
    public DoubleHT getH() {
        return HEAD;
    }

    /**
     * Checks out all files in a commit.
     */
    public void checkAll() {
        Commit c = HEAD.getCommit();
        for (File f : c.getTracked()) {
            checkout(HEAD.getCommit().getCode(), f.getName());
        }
    }

    /**
     * Switches to commit with CODE, and checkout file with name FILE.
     */
    public void checkout(String code, String file) {
        DoubleHT temp = HEAD;
        Commit c = temp.getCommit();
        Iterator<DoubleHT> i = _branchHeads.values().iterator();
        while (c != null && !c.getCode().equals(code)) {
            if (i.hasNext()) {
                temp = i.next().findCommit(code);
                if (temp != null) {
                    c = temp.getCommit();
                }
            }
        }
        if (c == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (!c.trackingR(file)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String s = ".gitlet/" + c.getCode() + "/" + file;
            File f = new File(s);
            File t = new File(file);
            if (t.exists()) {
                t.delete();
            }
            try {
                t.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Utils.writeContents(t, Utils.readContentsAsString(f));
        }
    }

    /** Compares two files,
     * returns true if same content false otherwise. */
    private boolean compare(File a, File b) {
        String temp1 = Utils.readContentsAsString(a);
        return temp1.equals(Utils.readContentsAsString(b));
    }

    /** Search through dir and add untracked files to untrack. */
    public void update() {
        DIRECTORY.listFiles()
    }

    /**
     * An arraylist that stores all the staged files.
     */
    private ArrayList<File> staged;

    /**
     * Where the HEAD is on.
     */
    private DoubleHT HEAD;

    /**
     * Stores name : position of branch head.
     */
    private HashMap<String, DoubleHT> _branchHeads;

    /**
     * Stores the current branch the user is on.
     */
    private String _current;

    /**
     * Stores all commit since the first.
     */
    private ArrayList<Commit> total;

    /**
     * Hashmap that has code as key, and real file name as value.
     */
    private HashMap<String, String> real;

    /** Arraylist that keeps track of untracked files. */
    private ArrayList<File> untracked;
}
