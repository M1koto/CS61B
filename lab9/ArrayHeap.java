import java.util.ArrayList;

/**
 * A Generic heap class. Unlike Java's priority queue, this heap doesn't just
 * store Comparable objects. Instead, it can store any type of object
 * (represented by type T) and an associated priority value.
 *
 * @author
 */
public class ArrayHeap<T> {

    /* DO NOT CHANGE THESE METHODS. */

    /**
     * An ArrayList that stores the nodes in this binary heap.
     */
    private ArrayList<Node> contents;

    /**
     * A constructor that initializes an empty ArrayHeap.
     */
    public ArrayHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /**
     * Returns the number of elements in the priority queue.
     */
    public int size() {
        return contents.size() - 1;
    }

    /**
     * Returns the node at index INDEX.
     */
    private Node getNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /**
     * Sets the node at INDEX to N
     */
    private void setNode(int index, Node n) {
        // In the case that the ArrayList is not big enough
        // add null elements until it is the right size
        while (index + 1 > contents.size()) {
            contents.add(null);
        }
        contents.set(index, n);
    }

    /**
     * Returns and removes the node located at INDEX.
     */
    private Node removeNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.remove(index);
        }
    }

    /**
     * Swap the nodes at the two indices.
     */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        this.contents.set(index1, node2);
        this.contents.set(index2, node1);
    }

    /**
     * Prints out the heap sideways. Use for debugging.
     */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /**
     * Recursive helper method for toString.
     */
    private String toStringHelper(int index, String soFar) {
        if (getNode(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getNode(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getNode(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getNode(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /**
     * A Node class that stores items and their associated priorities.
     */
    public class Node {
        private T _item;
        private double _priority;

        private Node(T item, double priority) {
            this._item = item;
            this._priority = priority;
        }

        public T item() {
            return this._item;
        }

        public double priority() {
            return this._priority;
        }

        public void setPriority(double priority) {
            this._priority = priority;
        }

        @Override
        public String toString() {
            return this._item.toString() + ", " + this._priority;
        }
    }

    /* FILL IN THE METHODS BELOW. */

    /**
     * Returns the index of the left child of the node at i.
     */
    private int getLeftOf(int i) {
        return 2 * i;
    }

    /**
     * Returns the index of the right child of the node at i.
     */
    private int getRightOf(int i) {
        return 2 * i + 1;
    }

    /**
     * Returns the index of the node that is the parent of the
     * node at i.
     */
    private int getParentOf(int i) {
        return Math.floorDiv(i, 2);
    }

    /**
     * Returns the index of the node with smaller priority. If one
     * node is null, then returns the index of the non-null node.
     * Precondition: at least one of the nodes is not null.
     */
    private int min(int index1, int index2) {
        if (contents.get(index1) == null) {
            return index2;
        } else if (contents.get(index2) == null) {
            return index1;
        } else {
            if (contents.get(index1)._priority > contents.get(index2)._priority) {
                return index2;
            } else {
                return index1;
            }
        }
    }

    /**
     * Returns the item with the smallest priority value, but does
     * not remove it from the heap. If multiple items have the minimum
     * priority value, returns any of them. Returns null if heap is
     * empty.
     */
    public T peek() {
        if (contents.size() != 0) {
            return getNode(1)._item;
        }
        return null;
    }

    /**
     * Bubbles up the node currently at the given index until no longer
     * needed.
     */
    private void bubbleUp(int index) {
        while (index != 1 && min(index, getParentOf(index)) == index) {
            int temp = getParentOf(index);
            swap(index, getParentOf(index));
            index = temp;
        }
    }


    /**
     * Bubbles down the node currently at the given index until no longer
     * needed.
     */
    private void bubbleDown(int index) {
        if (getLeftOf(index) < contents.size()) {
            bubbleUp(getLeftOf(index));
        }
        if (getRightOf(index) < contents.size()) {
            bubbleUp(getRightOf(index));
        }
    }

    /**
     * Inserts an item with the given priority value. Assume that item is
     * not already in the heap. Same as enqueue, or offer.
     */
    public void insert(T item, double priority) {
        contents.add(new Node(item, priority));
        bubbleUp(contents.size() - 1);
    }

    /**
     * Returns the element with the smallest priority value, and removes
     * it from the heap. If multiple items have the minimum priority value,
     * removes any of them. Returns null if the heap is empty. Same as
     * dequeue, or poll.
     */
    public T removeMin() {
        swap(1, contents.size() - 1);
        T target = contents.get(contents.size() - 1)._item;
        contents.remove(contents.size() - 1);
        bubbleDown(1);
        return target;
    }

    /**
     * Changes the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the
     * same item. Does nothing if the item is not in the heap. Check for
     * item equality with .equals(), not ==
     */
    public void changePriority(T item, double priority) {
        int count = 1;
        while (true) {
            if (count >= contents.size()) {
                return ;
            }
            if (contents.get(count)._item == item) {
                contents.get(count)._priority = priority;
                break;
            }
            count += 1;
        }
        bubbleDown(count);
    }
}
