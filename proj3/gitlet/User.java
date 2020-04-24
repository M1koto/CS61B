package gitlet;

import edu.neu.ccs.util.FileUtilities;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
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

    /**
     * Dir Staging.
     */
    static final File STAGING = new File(".gitlet/stage");

    /**
     * File for permanent storage.
     */
    File USER = new File(".gitlet/USER");

    /**
     * First commit's doubleHT.
     */
    DoubleHT INITIAL;

    /** For convienence. */
    static final int GITLET = 9;

    /**
     * Creates a unique user for gitlet.
     */
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
        modified = new ArrayList<>();
        deleted = new ArrayList<>();
        removal = new ArrayList<>();
        branches = new ArrayList<>();
        rmList = new ArrayList<>();

        Commit first = new Commit("initial commit", null, initParent(), null);
        total.add(first);
        branches.add("master");
        INITIAL = new DoubleHT(null, first, "master");
        first.setFather(INITIAL);
        HEAD = INITIAL;
        _current = "master";
        _branchHeads = new HashMap<String, DoubleHT>();
        _branchHeads.put("master", INITIAL);

        publish(first, first.getCode());
    }

    /**
     * Adds all existing files when initializing to the first commit.
     */
    private ArrayList<File> initParent() {
        File[] temp = DIRECTORY.listFiles();
        ArrayList<File> ret = new ArrayList<>();
        if (temp != null) {
            Collections.addAll(ret, temp);
        }
        return ret;
    }

    /**
     * File writing of Commit c with code as name.
     */
    private void publish(Commit c, String code) {
        ArrayList<File> buffer = new ArrayList<>();
        File store = new File(".gitlet/" + code);
        store.mkdir();
        for (File f : c.getTracked()) {
            File temp = new File(".gitlet/" + code + "/" + f.getName());
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Utils.writeContents(temp, Utils.readContentsAsString(f));
            } catch (IllegalArgumentException ignored) {
            }
            buffer.add(temp);
        }
        c.changeTracked(buffer);
    }

    /**
     * Adds file with name FILE to the staging area.
     * Overwrites same file if exists.
     */
    public void add(File file) {
        String buffer = Utils.sha1(file.getName());
        String name = ".gitlet/stage/" + buffer;
        File f = new File(name);
        delete(name);
        staged.remove(file);
        real.remove(buffer);
        //if (compare(f, file)) {

        //}
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(f, Utils.readContentsAsString(file));
        real.putIfAbsent(buffer, file.getName());
        staged.add(file);
        untracked.remove(file);
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
     * Delete file if B has the same name with A.
     */
    private ArrayList<File> delSimilar(ArrayList<File> a, ArrayList<File> b) {
        ArrayList<File> ret = new ArrayList<>();
        boolean flag = true;
        for (File f1 : a) {
            for (File f2 : b) {
                if (f1.getName().equals(f2.getName())) {
                    flag = false;
                    break;
                }
            }
            if (flag && !removal.contains(f1.getName())) {
                ret.add(f1);
            }
        }
        removal.clear();
        return ret;
    }

    /**
     * Makes a commit with MESSAGE.
     */
    public void commit(String message) {
        if (staged.size() == 0 && removal.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        ArrayList<File> pass = delSimilar(HEAD.getCommit().getTracked(), staged);
        Commit c = new Commit(message, time(),
                pass, staged);
        c.setReal(real); // add staged file's real name to this commit's real name hashmap
        c.setReal(HEAD.getCommit().getReal()); // add real name of files of parent commit to this commit's real name

        total.add(c);
        DoubleHT d = new DoubleHT(HEAD, c, _current);
        c.setFather(d);
        _branchHeads.remove(_current);
        _branchHeads.put(_current, d);
        HEAD = d;

        publish(c, c.getCode());
        staged.clear();
        removal.clear();
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
        if (!removal.contains(name) && HEAD.getCommit().tracking(name)) {
            removed = true;
            delete(name);
            removal.add(name);
            rmList.add(name);
        } else if (!removed) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * Corresponds to reset command
     * and checks out all with CODE.
     */
    public void reset(String code) {
        Commit target = null;
        for (Commit c : total) {
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
                temp.addBranch(name);
                _branchHeads.put(name, temp);
                branches.add(name);
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
            branches.remove(name);
            _branchHeads.remove(name);
            HEAD.rmbranch(name);
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
        for (Commit c : total) {
            if (c.getMessage().equals(m)) {
                ans.add(c.getCode());
            }
        }
        if (ans.size() == 0) {
            System.out.println("Found no commit with that message.");
        } else {
            for (String s : ans) {
                System.out.println(s);
            }
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
        File[] all = DIRECTORY.listFiles();
        for (File file : all) {
            file.delete();
        }
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
            File t = new File(".gitlet/" + file);
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

    /**
     * Show the status of this gitlet.
     */
    public void status() {
        Collections.sort(staged);
        Collections.sort(removal);
        Collections.sort(deleted);
        Collections.sort(modified);
        Collections.sort(untracked);
        System.out.println("=== Branches ===");
        for (String s : branches) {
            if (s.equals(_current)) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
        System.out.println("");
        System.out.println("=== Staged Files ===");
        for (File f : staged) {
            System.out.println(f.getName());
        }
        System.out.println("");
        System.out.println("=== Removed Files ===");
        for (File f : HEAD.getCommit().getTracked()) {
            if (f.getName().contains(".txt") && rmList.contains(f.getName())) {
                System.out.println(f.getName());
            }
        }
        System.out.println("");
        System.out.println("=== Modifications Not Staged For Commit ===");
        compareTwo(deleted, modified);
        System.out.println("");
        System.out.println("=== Untracked Files ===");
        for (File f : untracked) {
            System.out.println(f.getName());
        }
        System.out.println("");
    }

    /**
     * Decide the ordering of printing of DELETED and MODIFIED.
     */
    private void compareTwo(ArrayList<File> deleted, ArrayList<File> modified) {
        int d = 0;
        int m = 0;
        while (d < deleted.size() || m < modified.size()) {
            if (m <= modified.size() - 1 && d >= deleted.size() - 1) {
                System.out.println(modified.get(m).getName() + " (modified)");
                m += 1;
            } else if (m >= modified.size() - 1 && d <= deleted.size() - 1) {
                System.out.println(deleted.get(d).getName() + " (deleted)");
                d += 1;
            } else {
                String del = deleted.get(d).getName();
                String mod = modified.get(m).getName();
                if (del.compareTo(mod) > 0) {
                    System.out.println(del + " (deleted)");
                    d += 1;
                } else {
                    System.out.println(mod + " (modified)");
                    m += 1;
                }
            }
        }
    }

    /**
     * Compares two files,
     * returns true if same content false otherwise.
     */
    private boolean compare(File a, File b) {
        String temp1 = Utils.readContentsAsString(a);
        return temp1.equals(Utils.readContentsAsString(b));
    }

    /**
     * Search through dir and
     * add untracked files to untrack.
     * add modified files to modified.
     */
    public void update() {
        ArrayList<File> prev = new ArrayList<>(HEAD.getCommit().getTracked());
        FilenameFilter forNow = (dir, name) -> !dir.getPath().equals(STAGING.getPath());
        File[] now = DIRECTORY.listFiles(forNow);
        if (now == null) {
            return;
        }
        for (int i = 0; i < now.length; i++) {
            if (now[i].getName().contains(".txt")) {
                if (has(prev, now[i]) != -1) {
                    int x = has(prev, now[i]);
                    if (!compare(prev.get(x), now[i])) {
                        modified.add(now[i]);
                    }
                } else if (!staged.contains(now[i])
                        && !untracked.contains(now[i])) {
                    untracked.add(now[i]);
                }
            }
            File lamb = now[i];
            prev.removeIf(f -> f.getName().equals(lamb.getName()));
            now[i] = null;
        }
        for (String s: removal) {
            for (File f: prev) {
                if (f.getName().contains(s.substring(GITLET))) {
                    prev.remove(f);
                    break;
                }
            }
        }
        deleted.addAll(prev);

        for (File f : staged) {
            while (modified.remove(f)) {
                while (deleted.remove(f)) ;
            }
        }
    }

    /**
     * Returns index if arraylist PREV contains file with name FILE.
     * Returns -1 otherwise
     */
    private int has(ArrayList<File> prev, File file) {
        for (File f : prev) {
            if (f.getName().equals(file.getName())) {
                return prev.indexOf(f);
            }
        }
        return -1;
    }

    /**
     * Returns whether there is an untracked file.
     */
    public boolean warning() {
        return untracked.size() != 0;
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

    /**
     * Arraylist that keeps track of untracked files.
     */
    private ArrayList<File> untracked;

    /**
     * Arraylist that keeps track of modified files.
     */
    private ArrayList<File> modified;

    /**
     * Arraylist that keeps track of deleted files.
     */
    private ArrayList<File> deleted;

    /**
     * Arraylist that keeps track of REMOVAl files.
     */
    private ArrayList<String> removal;

    /**
     * Arraylist that keeps track of branch names.
     */
    private ArrayList<String> branches;

    /** Arraylist specifically for rm command. */
    private ArrayList<String> rmList;
}
