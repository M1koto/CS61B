package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;

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
    static final File USER = new File(".gitlet/USER");

    /**
     * First commit's doubleHT.
     */
    private DoubleHT initial;

    /**
     * For convienence.
     */
    static final int GITLET = 9;

    /**
     * For convienence.
     */

    static final int ABBREV = 6;

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

        Commit first = new Commit("initial commit", null, initParent(), null);
        total.add(first);
        branches.add("master");
        initial = new DoubleHT(null, first, "master");
        first.setFather(initial);
        head = initial;
        _current = "master";
        _branchHeads = new HashMap<String, DoubleHT>();
        _branchHeads.put("master", initial);

        publish(first, first.getCode());
    }

    /**
     * Return all existing files when initializing to the first commit.
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
     * File writing of Commit C with CODE as name.
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
                int a = 3;
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
        Commit c = head.getCommit();
        if (file.exists() && c.getFile(file.getName()) != null
                && compare(file, c.getFile(file.getName()))) {
            removal.remove(file.getName());
            return;
        }
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
     * Return the Delete file if B has the same name with A.
     */
    private ArrayList<File> delSimilar(ArrayList<File> a, ArrayList<String> b) {
        ArrayList<File> ret = new ArrayList<>();
        for (File f1 : a) {
            boolean flag = true;
            for (String s : b) {
                if (f1.getName().equals(s)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(f1);
            }
        }
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
        ArrayList<File> pass =
                delSimilar(head.getCommit().getTracked(), removal);
        Commit c = new Commit(message, time(),
                pass, staged);
        c.setReal(real);
        c.setReal(head.getCommit().getReal());

        total.add(c);
        DoubleHT d = new DoubleHT(head, c, _current);
        c.setFather(d);
        _branchHeads.remove(_current);
        _branchHeads.put(_current, d);
        head = d;

        publish(c, c.getCode());
        staged.clear();
        removal.clear();
        real.clear();
        for (File file : STAGING.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    /**
     * Removes file TEMP.
     */
    public void rm(File temp) {
        String stage = ".gitlet/stage/" + Utils.sha1(temp.getName());
        boolean removed = staged.remove(temp);
        delete(stage);
        if (!removal.contains(temp) && head.getCommit().tracking(temp)) {
            removed = true;
            removal.add(temp.getName());
            temp.delete();
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
        staged.clear();
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
            head = target.getFather();
            _branchHeads.remove(_current);
            _branchHeads.put(_current, head);
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
            if (temp.b2() == null) {
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
            head.rmbranch(name);
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
            head = _branchHeads.get(_current);
        }
    }

    /**
     * Returns the HEAD.
     */
    public DoubleHT getH() {
        return head;
    }

    /**
     * Checks out all files in a commit.
     */
    public void checkAll() {
        Commit c = head.getCommit();
        File here = new File(System.getProperty("user.dir"));
        for (File f : here.listFiles()) {
            f.delete();
        }
        for (File f : c.getTracked()) {
            if (f.getName().contains(".txt")) {
                checkout(head.getCommit().getCode(), new File(f.getName()));
            }
        }
    }

    /**
     * Switches to commit with CODE, and checkout file with name FILE.
     */
    public void checkout(String code, File file) {
        Commit c = null;
        for (Commit c2 : total) {
            if (c2.getCode().substring(0,
                    ABBREV).equals(code.substring(0, ABBREV))) {
                c = c2;
                break;
            }
        }
        if (c == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (file.getName().contains("warg")
                || !c.trackingR(file.getName())) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String s = ".gitlet/" + c.getCode() + "/" + file.getName();
            File f = new File(s);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Utils.writeContents(file, Utils.readContentsAsString(f));
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
        for (String s : removal) {
            System.out.println(s);
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
     * Decide the ordering of printing of DELETE and MODIFY.
     */
    private void compareTwo(ArrayList<File> delete, ArrayList<File> modify) {
        int d = 0;
        int m = 0;
        while (d < delete.size() || m < modify.size()) {
            if (m <= modify.size() - 1 && d >= delete.size() - 1) {
                System.out.println(modify.get(m).getName() + " (modified)");
                m += 1;
            } else if (m >= modify.size() - 1 && d <= delete.size() - 1) {
                System.out.println(delete.get(d).getName() + " (deleted)");
                d += 1;
            } else {
                String del = delete.get(d).getName();
                String mod = modify.get(m).getName();
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
     * Compares two files A B,
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
        deleted.clear();
        modified.clear();
        untracked.clear();
        ArrayList<File> prev = new ArrayList<>(head.getCommit().getTracked());
        prev.removeIf(f -> !f.getName().contains(".txt"));
        File[] now = new File(System.getProperty("user.dir")).listFiles();
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
        deleted.addAll(prev);
        deleted.removeIf(f -> removal.contains(f.getName()));

        for (File f : staged) {
            deleted.removeIf(f2 -> f.getName().equals(f2.getName()));
            modified.removeIf(f3 -> f.getName().equals(f3.getName()));
            untracked.removeIf(f4 -> f.getName().equals(f4.getName()));
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
     * Merges the file from the tip of the given BRANCH with
     * the file at the tip of the current branch.
     */
    public void merge(String branch) {
        if (_branchHeads.get(branch) == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (removal.size() != 0 || staged.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        DoubleHT splitPoint = getSplit(branch);
        if (splitPoint == _branchHeads.get(branch)) {
            System.out.println("Given branch is "
                    + "an ancestor of the current branch.");
            System.exit(0);
        } else if (splitPoint == _branchHeads.get(_current)) {
            System.out.println("Current branch fast-forwarded.");
            switchBranch(branch);
            checkAll();
            System.exit(0);
        } else {
            Commit tip = _branchHeads.get(_current).getCommit();
            Commit split = splitPoint.getCommit();
            Commit given = _branchHeads.get(branch).getCommit();

            File here = new File(System.getProperty("user.dir"));
            for (File f : here.listFiles()) {
                f.delete();
            }

            classify(tip.getTracked(), split.getTracked(), given.getTracked(),
                    tip.getCode(), split.getCode(), given.getCode());
            
            commit(String.format("Merged %s into %s", branch, _current));
        }
    }

    /**
     * Classify files in CURR SPLIT GIVEN using split's perspective.
     */
    private void classify(ArrayList<File> curr,
   ArrayList<File> split, ArrayList<File> given,
                String c, String s, String g) {
            for (File spl: split) {
                boolean curExist = false;
                boolean givExist = false;
                File cacheC = null;
                File cacheG = null;
                for (File cur: curr) {
                    if (spl.getName().equals(cur.getName())) {
                        curExist = true;
                        cacheC = cur;
                    }
                }
                for (File giv: given) {
                    if (spl.getName().equals(giv.getName())) {
                        givExist = true;
                        cacheG = giv;
                    }
                }
                if (curExist && givExist) {
                    work(cacheC, spl, cacheG, c, s, g);
                    curr.remove(cacheC);
                    given.remove(cacheG);
                } else if (curExist) {
                    if (!compare(cacheC, spl)) { // 6
                        conflict(cacheC, null);
                    }
                    curr.remove(cacheC);
                } else if (givExist) {
                    if (!compare(cacheG, spl)) { // 7
                        conflict(null, cacheG);
                    }
                    given.remove(cacheG);
                }
                split.remove(spl);
            }
            File t = null;
            for (File f1: curr) {
                for (File f2: given) {
                    if (f1.getName().equals(f2.getName())) {
                        if (compare(f1, f2)) {
                            checkout(c, f1);
                        } else {
                            conflict(f1, f2);
                        }
                        t = f1;
                    }
                    given.remove(f2);
                }
                curr.remove(t);
            }
            for (File f1: curr) {
                checkout(c, f1);
            }
            for (File f2: given) {
                checkout(g, f2);
                add(f2);
            }
        }


    /** Process files CACHEC, SPL, CACHEG, C, S , G. */
    private void work(File cacheC, File spl, File cacheG, String c, String s, String g) {
        if (!compare(spl, cacheG) && compare(spl, cacheC)) { // 1
            checkout(g, cacheG);
            add(cacheG);
        } else if (compare(spl, cacheG) && !compare(spl, cacheC)) { //2
            checkout(c, cacheC);
        } else if (!compare(spl, cacheG) && !compare(spl, cacheC)) { // 8
            File temp = conflict(cacheC, cacheG);
            add(temp);
        } else {
            checkout(c, cacheC); // 3
        }
    }

    /** Format conflicting file CACHEC and CACHEG. */
    private File conflict(File cacheC, File cacheG) {
        File ret = new File(System.getProperty("user.dir"));
        try {
            ret.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = "<<<<<<< HEAD\n";
        if (cacheC == null) {
            content += "=======";
        } else {
            content += Utils.readContentsAsString(cacheC) + "=======";
        }
        if (cacheG == null) {
            content += ">>>>>>>";
        } else {
            content += Utils.readContentsAsString(cacheG) + ">>>>>>>";
        }
        Utils.writeContents(ret, content);
        return ret;
    }

    /**
     * Return the doubleHT split point between current branch and BRANCH.
     */
    private DoubleHT getSplit(String branch) {
        DoubleHT split = _branchHeads.get(_current);
        while (split != null && !split.bran(branch)) {
            split = split.getParent();
        }
        return split;
    }


    /**
     * An arraylist that stores all the staged files.
     */
    private ArrayList<File> staged;

    /**
     * Where the HEAD is on.
     */
    private DoubleHT head;

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
}
