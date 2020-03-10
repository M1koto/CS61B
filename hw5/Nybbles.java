/**
 * Represents an array of integers each in the range -8..7.
 * Such integers may be represented in 4 bits (called nybbles).
 *
 * @author kenny liao
 */
public class Nybbles {

    /**
     * Maximum positive value of a Nybble.
     */
    public static final int MAX_VALUE = 7;

    /**
     * Return an array of size N.
     * DON'T CHANGE THIS.
     */
    public Nybbles(int N) {
        _data = new int[(N + 7) / 8];
        _n = N;
    }

    /**
     * Return the size of THIS.
     */
    public int size() {
        return _n;
    }

    /**
     * Return the Kth integer in THIS array, numbering from 0.
     * Assumes 0 <= K < N.
     */
    public int get(int k) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else {
            int position = k/8;
            int index = k%8;

            int elem = _data[position] >>> index * 4;
            elem &= 0b1111;

            if(elem >= 8)
                return  elem-16;
            else
                return elem;
        }
    }

    /**
     * Set the Kth integer in THIS array to VAL.  Assumes
     * 0 <= K < N and -8 <= VAL < 8.
     */
    public void set(int k, int val) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else if (val < (-MAX_VALUE - 1) || val > MAX_VALUE) {
            throw new IllegalArgumentException();
        } else {
            int position = k/8;
            int index = k%8;

            _data[position] = unsetByte(_data[position], index);

            int elem = val;
            if(elem < 0)
                elem += 16;

            _data[position] = setByte(_data[position], index, elem);

            int b = 5;
        }
    }

    private int setByte(int store, int pos, int val){
        int elem = val & 0b1111;
        elem <<= pos*4;
        return store |= elem;
    }

    private int unsetByte(int store, int pos){
        int unsetter = ~(0b1111 << pos*4);
        return store &= unsetter;

    }

    /** DON'T CHANGE OR ADD TO THESE.*/
    /**
     * Size of current array (in nybbles).
     */
    private int _n;
    /**
     * The array data, packed 8 nybbles to an int.
     */
    private int[] _data;
}
