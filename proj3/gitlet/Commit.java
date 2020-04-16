package gitlet;

/** This class calls compare class on all file and get difference between latest commit and version now?
 Tracks all files in staging class, receives
 info from user class for latest commit info, synthesis data adding on metadata to produce a new commit
 In case of merge calls user class twice, merge, and create new commit.
 *
 *  @author kenny liao
 *  */
public class Commit {
    private String _code;
    private String _message;

    /** Return _code. */
    public String getCode() {
        return _code;
    }

    /** Return _message. */
    public String getMessage() {
        return _message;
    }
}
