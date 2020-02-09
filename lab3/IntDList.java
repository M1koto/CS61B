/**
 * Scheme-like pairs that can be used to form a list of integers.
 *
 * @author P. N. Hilfinger; updated by Vivant Sakore (1/29/2020)
 */
public class IntDList {

    /**
     * First and last nodes of list.
     */
    protected DNode _front, _back;

    /**
     * An empty list.
     */
    public IntDList() {
        _front = _back = null;
    }

    /**
     * @param values the ints to be placed in the IntDList.
     */
    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /**
     * @return The first value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getFront() {
        return _front._val;
    }

    /**
     * @return The last value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getBack() {
        return _back._val;
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        // FIXME: Implement this method and return correct value
        DNode temp = _front;
        int ans = 0;
        while (temp != null) {
            temp = temp._next;
            ans = ans + 1;
        }
        return ans;
    }

    /**
     * @param i index of element to return,
     *          where i = 0 returns the first element,
     *          i = 1 returns the second element,
     *          i = -1 returns the last element,
     *          i = -2 returns the second to last element, and so on.
     *          You can assume i will always be a valid index, i.e 0 <= i < size for positive indices
     *          and -size <= i <= -1 for negative indices.
     * @return The integer value at index i
     */
    public int get(int i) {
        // FIXME: Implement this method and return correct value
        if (i > 0) {
            DNode temp = _front;
            while (i != 0) {
                temp = temp._next;
                i -= 1;
            }
            return temp._val;
        } else if (i < 0){
            DNode temp = _back;
            while (i != -1) {
                temp = temp._prev;
                i += 1;
            }
            return temp._val;
        } else {
            return _front._val;
        }
    }

    /**
     * @param d value to be inserted in the front
     */
    public void insertFront(int d) {
        // FIXME: Implement this method
        if (_front != null && _back != null) {
            DNode temp = new DNode(null, d, null);
            _front._prev = temp;
            temp._next = _front;
            _front = temp;
        } else {
            DNode temp = new DNode(null, d, null);
            _front = temp;
            _back = temp;
        }

    }

    /**
     * @param d value to be inserted in the back
     */
    public void insertBack(int d) {
        // FIXME: Implement this method
        if (_front != null && _back != null) {
            DNode temp = new DNode(null, d, null);
            temp._prev = _back;
            _back._next = temp;
            _back = temp;
        } else {
            DNode temp = new DNode(null, d, null);
            _front = temp;
            _back = temp;
        }
    }

    /**
     * @param d     value to be inserted
     * @param index index at which the value should be inserted
     *              where index = 0 inserts at the front,
     *              index = 1 inserts at the second position,
     *              index = -1 inserts at the back,
     *              index = -2 inserts at the second to last position, and so on.
     *              You can assume index will always be a valid index,
     *              i.e 0 <= index <= size for positive indices (including insertions at front and back)
     *              and -(size+1) <= index <= -1 for negative indices (including insertions at front and back).
     */
    public void insertAtIndex(int d, int index) {
        // FIXME: Implement this method
        if (index > 0) {
            if (index == size()) {
                insertBack(d);
            } else {
                DNode temp = _front;
                while (index != 1) {
                    temp = temp._next;
                    index -= 1;
                }
                DNode extra = new DNode(temp, d, temp._next);
                temp._next._prev = extra;
                temp._next = extra;
            }
        } else if (index < -1){
            if (-index == size()+1) {
                insertFront(d);
            } else {
                DNode temp = _back;
                while (index != -2) {
                    temp = temp._prev;
                    index += 1;
                }
                DNode extra = new DNode(temp._prev, d, temp);
                temp._prev._next = extra;
                temp._prev = extra;
            }
        } else if (index == 0){
            insertFront(d);
        } else {
            insertBack(d);
        }
    }

    /**
     * Removes the first item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteFront() {
        // FIXME: Implement this method and return correct value
        boolean flag = false;
        if (size() == 1) {
            flag = true;
        }
        DNode temp = _front;
        _front = _front._next;
        if (flag) {
            return temp._val;
        }
        _front._prev = null;
        temp._next = null;
        return temp._val;
    }

    /**
     * Removes the last item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteBack() {
        // FIXME: Implement this method and return correct value
        boolean flag = false;
        if (size() == 1) {
            flag = true;
        }
        DNode temp = _back;
        _back = _back._prev;
        if (flag) {
            _front = null;
            return temp._val;
        }
        _back._next = null;
        temp._prev = null;
        return temp._val;
    }

    /**
     * @param index index of element to be deleted,
     *          where index = 0 returns the first element,
     *          index = 1 will delete the second element,
     *          index = -1 will delete the last element,
     *          index = -2 will delete the second to last element, and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size for positive indices (including deletions at front and back)
     *              and -size <= index <= -1 for negative indices (including deletions at front and back).
     * @return the item that was deleted
     */
    public int deleteAtIndex(int index) {
        // FIXME: Implement this method and return correct value
        if (size() == 1) {
           int temp = _front._val;
           _front = null;
           _back = null;
           return temp;
        }
        if (index > 0) {
            if (index == size()-1) {
                return deleteBack();
            } else {
                DNode temp = _front;
                while (index != 1) {
                    temp = temp._next;
                    index -= 1;
                }
                int ans = temp._next._val;
                temp._next = temp._next._next;
                temp._next._prev._next = null;
                temp._next._prev._prev = null;
                temp._next._prev = temp;
                return ans;
            }
        } else if (index < -1){
            if (-index == size()) {
                return deleteFront();
            } else {
                DNode temp = _back;
                while (index != -2) {
                    temp = temp._prev;
                    index += 1;
                }
                int ans = temp._prev._val;
                temp._prev = temp._prev._prev;
                temp._prev._next._prev = null;
                temp._prev._next._next = null;
                temp._prev._next = temp;
                return ans;
            }
        } else if (index == 0){
            return deleteFront();
        } else {
            return deleteBack();
        }
    }

    /**
     * @return a string representation of the IntDList in the form
     * [] (empty list) or [1, 2], etc.
     * Hint:
     * String a = "a";
     * a += "b";
     * System.out.println(a); //prints ab
     */
    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        String str = "[";
        DNode curr = _front;
        for (; curr._next != null; curr = curr._next) {
            str += curr._val + ", ";
        }
        str += curr._val +"]";
        return str;
    }

    /**
     * DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information!
     */
    static class DNode {
        /** Previous DNode. */
        protected DNode _prev;
        /** Next DNode. */
        protected DNode _next;
        /** Value contained in DNode. */
        protected int _val;

        /**
         * @param val the int to be placed in DNode.
         */
        protected DNode(int val) {
            this(null, val, null);
        }

        /**
         * @param prev previous DNode.
         * @param val  value to be stored in DNode.
         * @param next next DNode.
         */
        protected DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
