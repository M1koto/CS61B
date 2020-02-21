import java.io.Reader;
import java.io.IOException;


/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author kenny liao
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    private String word;
    private int count;
    public TrReader(Reader str, String from, String to) throws IOException{
        // TODO: YOUR CODE HERE;
        this.count = 0;
        this.word = new String();
        int keep = str.read();
        while (keep != -1) {
            char current = (char) keep;
            int the_index = from.indexOf(current);
            if (the_index != -1) {
                current = to.charAt(the_index);
            }
            word += current;
            keep = str.read();
        }
    }
    @Override
    public int read(char cbuf[], int off, int len) throws IOException {
        if (off >= word.length() || word.length() == count) {
            return -1;
        }
        word.getChars(count, count + len, cbuf, off);
        return len;
    }
    @Override
    public void close() {

    }

    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */
}
