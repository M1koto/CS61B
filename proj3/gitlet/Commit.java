package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This class calls compare class on all file and get
 * difference between latest commit and version now?
 * Tracks all files in staging class, receives
 * info from user class for latest commit info, synthesis
 * data adding on metadata to produce a new commit
 * In case of merge calls user class twice, merge, and create new commit.
 *
 * @author kenny liao
 */
public class Commit implements Serializable {

    /** Records the first commit. */
    static final String FIRSTCOMMIT = "Thu Jan 1 00:00:00 1970 -0700";

    /** For convienence. */
    static final int FORMAT = 8;

    /**
     * Creates a new Commit with following
     * MESSAGE, PARENT, STAGED and TIME.
     */
    Commit(String message, Date time, ArrayList<File> parent, ArrayList<File> staged) {
        _message = message;
        _timestamp = time;
        if (_timestamp != null) {
            _time = _timestamp.toString();
        } else {
            _time = FIRSTCOMMIT;
        }
        _tracked = combine(parent, staged);
        real = new HashMap<>();
        makeCode();
    }

    /**
     * Combine all data of PARENT and STAGED files
     * passed in to be committed.
     */
    private ArrayList<File> combine(ArrayList<File> parent, ArrayList<File> staged) {
        ArrayList<File> comb = new ArrayList<>();
        if (parent != null) {
            comb.addAll(parent);
        }
        if (staged != null) {
            comb.addAll(staged);
        }
        return comb;
    }

    /**
     * Remove file with NAME in tracked.
     */
    public void remove(String name) {
        for (File f : _tracked) {
            if (f.getName().equals(name)) {
                _tracked.remove(f);
                break;
            }
        }
    }
    /** Set real to given hashmap H. */
    public void setReal(HashMap<String, String> h) {
        real.putAll(h);
    }

    /** Set _father to D. */
    public void setFather(DoubleHT d) {
        _father = d;
    }

    /** Returns _father. */
    public DoubleHT getFather() {
        return _father;
    }


    /**
     * Returns the tracked files.
     */
    public ArrayList<File> getTracked() {
        return _tracked;
    }

    /** Return the file under track with name S. */
    public File getFile(String s) {
        for (File f: _tracked) {
            if (f.getName().equals(s)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Return _code.
     */
    public String getCode() {
        return _code;
    }

    /** Return real. */
    public HashMap<String, String> getReal() {
        return real;
    }

    /**
     * Return _message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Returns true if this Commit is tracking File with name NAME.
     */
    public boolean tracking(String name) {
        for (File f : _tracked) {
            if (f.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns true if this Commit is tracking File with REAL name.
     */
    public boolean trackingR(String real) {
        return real.contains(real);
    }


    /**
     * Make code of this commit.
     */
    private void makeCode() {
        File temp = new File("temp");
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(temp, this);
        _code = Utils.sha1(Utils.readContentsAsString(temp));
        temp.delete();
    }

    /**
     * Return string form of _timestamp.
     */
    public String time() {
        if (_timestamp == null) {
            return _time;
        } else {
            return _time.substring(0, _time.length() - FORMAT) + "2020 -0700";
        }
    }

    /** Change _tracked to BUFFER. */
    public void changeTracked(ArrayList<File> buffer) {
        _tracked = buffer;
    }

    /**
     * An arraylist that stores all the tracked files.
     */
    private ArrayList<File> _tracked;

    /**
     * Records time of Commit.
     */
    private Date _timestamp;

    /**
     * String of time.
     */
    private String _time;
    /**
     * Records message.
     */
    private String _message;

    /** Stores the DoubleHT that contains this commit. */
    private DoubleHT _father;

    /**
     * Stores the code of the commit this Commit contains.
     */
    private String _code;

    /** Hashmap that has code as key, and real file name as value. */
    private HashMap<String, String> real;
}
