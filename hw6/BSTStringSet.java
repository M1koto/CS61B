import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 *
 * @author kenny liao
 */
public class BSTStringSet implements StringSet, Iterable<String> {
    /**
     * Creates a new empty set.
     */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        _root = helperP(s, _root);
    }

    private Node helperP(String s, Node root) {
        if (root == null) {
            return new Node(s);
        }

        int cmp = s.compareTo(root.s);

        if (cmp < 0) {
            root.left = helperP(s, root.left);
        }
        if (cmp > 0) {
            root.right = helperP(s, root.right);
        }

        return root;
    }

    @Override
    public boolean contains(String s) {
        return helperC(s, _root);
    }

    private boolean helperC(String s, Node root) {
        if (root == null) {
            return false;
        } else if (root.s.equals(s)) {
            return true;
        } else {
            return helperC(s, root.left) || helperC(s, root.right);
        }
    }

    @Override
    public List<String> asList() {
        BSTIterator target = new BSTIterator(_root);
        ArrayList<String> ans = new ArrayList<String>();
        while (target.hasNext()) {
            ans.add(target.next());
        }
        ans.sort(String::compareToIgnoreCase);
        return ans;
    }

    /**
     * prints node n's string.
     */
    public String printer(Node n) {
        return n.s;
    }

    /**
     * Returns root.
     */
    public Node get_root() {
        return _root;
    }

    /**
     * Represents a single Node of the tree.
     */
    private static class Node {
        /**
         * String stored in this Node.
         */
        private String s;
        /**
         * Left child of this Node.
         */
        private Node left;
        /**
         * Right child of this Node.
         */
        private Node right;

        /**
         * Creates a Node containing SP.
         */
        Node(String sp) {
            s = sp;
        }
    }

    /**
     * An iterator over BSTs.
     */
    private static class BSTIterator implements Iterator<String> {
        /**
         * Stack of nodes to be delivered.  The values to be delivered
         * are (a) the label of the top of the stack, then (b)
         * the labels of the right child of the top of the stack inorder,
         * then (c) the nodes in the rest of the stack (i.e., the result
         * of recursively applying this rule to the result of popping
         * the stack.
         */
        private Stack<Node> _toDo = new Stack<>();

        /**
         * A new iterator over the labels in NODE.
         */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add the relevant subtrees of the tree rooted at NODE.
         */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }


    public Iterator<String> iterator(String low, String high) {
        Inorder inorder_tree = new Inorder(_root, low, high);
        return inorder_tree;
    }

    private static class Inorder extends BSTIterator {
        /**
         * A new iterator over the labels in NODE.
         *
         * @param low high
         */
        Inorder(Node node, String low, String high) {
            super(node);
            _low = low;
            _high = high;
        }
        @Override
        public boolean hasNext() {
            return _root.s.compareTo(_high) < 0;
        }
        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String ans = "";
            while (ans.isEmpty() || super.next().compareTo(_low) < 0) {
                ans = super.next();
            }
            return ans;
        }

        private String _low;
        private String  _high;
        private Node _root;
    }


    /**
     * Root node of the tree.
     */
    private Node _root;
}
