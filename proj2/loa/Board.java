/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;


import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;

/**
 * Represents the state of a game of Lines of Action.
 *
 * @author kenny liao
 */
class Board {

    /**
     * Default number of moves for each side that results in a draw.
     */
    static final int DEFAULT_MOVE_LIMIT = 60;

    /**
     * Pattern describing a valid square designator (cr).
     */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /**
     * A Board whose initial contents are taken from INITIALCONTENTS
     * and in which the player playing TURN is to move. The resulting
     * Board has
     * get(col, row) == INITIALCONTENTS[row][col]
     * Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     * <p>
     * CAUTION: The natural written notation for arrays initializers puts
     * the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /**
     * A new board in the standard initial position.
     */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /**
     * A Board whose initial contents and state are copied from
     * BOARD.
     */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /**
     * Set my state to CONTENTS with SIDE to move.
     */
    void initialize(Piece[][] contents, Piece side) {
        int count = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                _board[ALL_SQUARES[count].index()] = contents[i][j];
                count += 1;
            }
        }
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
    }

    /**
     * Set me to the initial configuration.
     */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /**
     * Set my state to a copy of BOARD.
     */
    void copyFrom(Board board) {
        if (board != this) {
            int count = 0;
            Piece[][] contents = new Piece[board.getSize()][board.getSize()];
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    contents[i][j] = board.get(board.getSq(count));
                    count += 1;
                }
            }
            initialize(contents, board.turn());
        }
    }

    /**
     * Return the square from ALLSQUARES in board at index I.
     */
    Square getSq(int i) {
        return ALL_SQUARES[i];
    }

    /**
     * Return the contents of the square at SQ.
     */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /**
     * Return BOARDSIZE of this board.
     */
    int getSize() {
        return BOARD_SIZE;
    }

    /**
     * Set the square at SQ to V and set the side that is to move next
     * to NEXT, if NEXT is not null.
     */
    void set(Square sq, Piece v, Piece next) {
        _board[sq.index()] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /**
     * Set the square at SQ to V, without modifying the side that
     * moves next.
     */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }


    /** Set limit on number of moves by each side that results in a tie to
     *  LIMIT, where 2 * LIMIT > movesMade(). */

    void setMoveLimit(int limit) {
        if (2 * limit <= movesMade()) {
            throw new IllegalArgumentException("move limit too small");
        }
        _moveLimit = 2 * limit;
    }


    /** Assuming isLegal(MOVE), make MOVE. This function assumes that
     *  MOVE.isCapture() will return false.  If it saves the move for
     *  later retraction, makeMove itself uses MOVE.captureMove() to produce
     *  the capturing move. */

    void makeMove(Move move) {
        assert isLegal(move);
        Boolean capture = isCapture(move);
        Square from = move.getFrom();
        Square to = move.getTo();
        if (capture) {
            _prev.add(to);
            _koma.add(get(to));
        } else {
            _prev.add(null);
        }
        _moves.add(move);
        actualMove(from, to);
        if (_turn == BP) {
            _turn = WP;
        } else {
            _turn = BP;
        }
    }

    /**
     * Actually move piece FROM - TO.
     */
    void actualMove(Square from, Square to) {
        int temp = from.index();
        _board[to.index()] = get(from);
        _board[temp] = EMP;
    }


    /**
     * Returns true if the MOVE is a capture move.
     */
    private Boolean isCapture(Move move) {
        Square from = move.getFrom();
        Square to = move.getTo();
        return !get(from).abbrev().equals(get(to).abbrev())
                && !get(to).abbrev().equals("-");
    }

    /**
     * Retract (unmake) one move, returning to the state immediately before
     * that move.  Requires that movesMade () > 0.
     */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.get(_moves.size() - 1);
        Square loc = _prev.get(_prev.size() - 1);
        _moves.remove(_moves.size() - 1);
        _prev.remove(_prev.size() - 1);
        actualMove(move.getTo(), move.getFrom());
        if (loc != null) {
            _board[loc.index()] = _koma.get(_koma.size() - 1);
            _koma.remove(_koma.size() - 1);
        }
        if (_turn == BP) {
            _turn = WP;
        } else {
            _turn = BP;
        }
    }

    /**
     * Return the Piece representing who is next to move.
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return true iff FROM - TO is a legal move for the player currently on
     * move.
     */
    boolean isLegal(Square from, Square to) {
        if (!from.isValidMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int dist = from.distance(to);
        int required = countPieces(from, to);
        String fromName = get(from).abbrev();
        if (fromName.equals("-")) {
            return false;
        }
        String toName = get(to).abbrev();
        if (fromName.equals(toName)) {
            return false;
        }
        while (from != to) {
            if (!get(from).abbrev().equals(fromName)
                    && !get(from).abbrev().equals("-")) {
                return false;
            }
            from = from.moveDest(dir, 1);
        }
        return required == dist;
    }

    /**
     * Return the total amount of pieces on the LOA of FROM - TO.
     */
    public int countPieces(Square from, Square to) {
        int dir = from.direction(to);
        int dirBack = to.direction(from);
        int count1 = 1;
        int count2 = 1;
        int ans = 0;
        while (from.moveDest(dir, count1) != null) {
            count1 += 1;
        }
        Square end1 = from.moveDest(dir, count1 - 1);
        while (to.moveDest(dirBack, count2) != null) {
            count2 += 1;
        }
        Square end2 = to.moveDest(dirBack, count2 - 1);
        dir = end1.direction(end2);
        while (end1 != end2) {
            if (!get(end1).abbrev().equals("-")) {
                ans += 1;
            }
            end1 = end1.moveDest(dir, 1);
        }
        if (!get(end2).abbrev().equals("-")) {
            ans += 1;
        }
        return ans;
    }

    /**
     * Return true iff MOVE is legal for the player currently on move.
     * The isCapture() property is ignored.
     */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /**
     * Return a sequence of all legal moves from this position.
     */
    ArrayList<Move> legalMoves() {
        ArrayList<Move> ans = new ArrayList<>();
        for (int i = 0; i < _board.length; i++) {
            if (get(ALL_SQUARES[i]).fullName().equals(_turn.fullName())) {
                for (int j = 0; j < ALL_SQUARES.length; j++) {
                    if (isLegal(ALL_SQUARES[i], ALL_SQUARES[j])) {
                        ans.add(Move.mv(ALL_SQUARES[i], ALL_SQUARES[j]));
                    }
                }
            }
        }
        return ans;
    }

    /**
     * Return true iff the game is over (either player has all his
     * pieces continguous or there is a tie).
     */
    boolean gameOver() {
        return winner() != null;
    }

    /**
     * Return true iff SIDE's pieces are continguous.
     */
    boolean piecesContiguous(Piece side) {
        return getRegionSizes(side).size() == 1;
    }

    /**
     * Return the winning side, if any.  If the game is not over, result is
     * null.  If the game has ended in a tie, returns EMP.
     */
    Piece winner() {
        _winnerKnown = false;
        _subsetsInitialized = false;
        computeRegions();
        _subsetsInitialized = true;
        if (_moves.size() >= _moveLimit && !_winnerKnown) {
            return EMP;
        }
        if (!_winnerKnown) {
            return null;
        }
        if (piecesContiguous(WP) && piecesContiguous(BP)) {
            if (turn() == BP) {
                _winner = WP;
            } else {
                _winner = BP;
            }
        } else if (piecesContiguous(WP)) {
            _winner = WP;
        } else if (piecesContiguous(BP)) {
            _winner = BP;
        }
        return _winner;
    }

    /**
     * Return and remove last move made in _moves.
     */
    public Move lastmove() {
        Move temp = _moves.get(_moves.size() - 1);
        return temp;
    }

    /**
     * Return the total number of moves that have been made (and not
     * retracted).  Each valid call to makeMove with a normal move increases
     * this number by 1.
     */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /**
     * Return true if a move from FROM to TO is blocked by an opposing
     * piece or by a friendly piece on the target square.
     */
    private boolean blocked(Square from, Square to) {
        return false;
    }

    /**
     * Return the size of the as-yet unvisited cluster of squares
     * containing P at and adjacent to SQ.  VISITED indicates squares that
     * have already been processed or are in different clusters.  Update
     * VISITED to reflect squares counted. Counts ORIG if sq is origin.
     */
    private int numContig(Square sq, boolean[][] visited,
                          Piece p, boolean orig) {
        if (orig && get(sq) == EMP) {
            return 0;
        }
        int count = 0;
        if (orig && get(sq) == p && !visited[sq.row()][sq.col()]) {
            visited[sq.row()][sq.col()] = true;
            count++;
        }
        for (int i = 0; i < 8; i++) {
            Square target = sq.moveDest(i, 1);
            if (target != null) {
                int first = Math.floorDiv(target.index(), BOARD_SIZE);
                int sec = target.index() % BOARD_SIZE;
                if (!visited[first][sec] && get(target) == p && get(sq) == p) {
                    visited[first][sec] = true;
                    count = count + 1 + numContig(target, visited, p, false);
                    connected.add(target);
                }
            }
        }
        return count;
    }

    /**
     * Set the values of _whiteRegionSizes and _blackRegionSizes.
     */
    private void computeRegions() {
        if (_subsetsInitialized) {
            return;
        }
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();
        boolean[][] whiteVis = new boolean[BOARD_SIZE][BOARD_SIZE];
        boolean[][] blackVis = new boolean[BOARD_SIZE][BOARD_SIZE];
        int whitePiece = 0;
        int blackPiece = 0;
        for (Piece piece : _board) {
            if (piece == WP) {
                whitePiece += 1;
            }
            if (piece == BP) {
                blackPiece += 1;
            }
        }
        int countW = 0;
        int countB = 0;
        while (sum(_whiteRegionSizes) != whitePiece) {
            _whiteRegionSizes.add
                    (numContig(ALL_SQUARES[countW], whiteVis, WP, true));
            countW += 1;
            update(whiteVis);
        }
        while (sum(_blackRegionSizes) != blackPiece) {
            _blackRegionSizes.add
                    (numContig(ALL_SQUARES[countB], blackVis, BP, true));
            countB += 1;
            update(blackVis);
        }
        _whiteRegionSizes.removeAll(Collections.singleton(0));
        _blackRegionSizes.removeAll(Collections.singleton(0));
        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
        if (_blackRegionSizes.size() == 1 || _whiteRegionSizes.size() == 1) {
            _winnerKnown = true;
        }
        _subsetsInitialized = true;
    }

    /**
     * Update VISITED for piece p starting from square.
     */
    private void update(boolean[][] visited) {
        for (Square target : connected) {
            if (target != null) {
                int first = Math.floorDiv(target.index(), BOARD_SIZE);
                int sec = target.index() % BOARD_SIZE;
                visited[first][sec] = true;
            }
        }
        connected.clear();
    }

    /**
     * Returns the Sum of element in array A.
     */
    public int sum(ArrayList<Integer> a) {
        int ans = 0;
        for (int i : a) {
            ans += i;
        }
        return ans;
    }

    /**
     * Return the sizes of all the regions in the current union-find
     * structure for side S.
     */
    ArrayList<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else {
            return _blackRegionSizes;
        }
    }

    /**
     * Return the movelimit.
     */
    public int getLimit() {
        return _moveLimit;
    }

    /**
     * Return opponent considering Piece P.
     */
    public Piece getOpp(Piece p) {
        if (p == WP) {
            return BP;
        } else {
            return WP;
        }
    }

    /** Set estimated value of board to N. */
    public void setValue(int n) {
        _estimate = n;
    }


    /** Return Sq I. */
    public Piece square(int i) {
        return get(ALL_SQUARES[i]);
    }


    /** Return the estimated of board. */
    public int getValue() {
        return _estimate;
    }

    /**
     * The standard initial configuration for Lines of Action (bottom row
     * first).
     */
    static final Piece[][] INITIAL_PIECES = {
            {EMP, BP, BP, BP, BP, BP, BP, EMP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {WP, EMP, EMP, EMP, EMP, EMP, EMP, WP},
            {EMP, BP, BP, BP, BP, BP, BP, EMP}
    };

    /**
     * Caches square with pieces connected with each other.
     */
    private ArrayList<Square> connected = new ArrayList<>();

    /**
     * Current contents of the board.  Square S is at _board[S.index()].
     */
    private final Piece[] _board = new Piece[BOARD_SIZE * BOARD_SIZE];

    /**
     * List of all captured piece.
     */
    private ArrayList<Piece> _koma = new ArrayList<>();

    /**
     * List of all captured piece's original place,
     * or null if is not a capture move.
     */
    private ArrayList<Square> _prev = new ArrayList<>();

    /**
     * List of all unretracted moves on this board, in order.
     */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /**
     * Current side on move.
     */
    private Piece _turn;
    /**
     * Limit on number of moves before tie is declared.
     */
    private int _moveLimit;
    /**
     * True iff the value of _winner is known to be valid.
     */
    private boolean _winnerKnown;
    /**
     * Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     * in progress).  Use only if _winnerKnown.
     */
    private Piece _winner;

    /**
     * True iff subsets computation is up-to-date.
     */
    private boolean _subsetsInitialized;

    /**
     * List of the sizes of continguous clusters of pieces, by color.
     */
    private final ArrayList<Integer>
            _whiteRegionSizes = new ArrayList<>(),
            _blackRegionSizes = new ArrayList<>();

    /** Value for heuristic value. */
    private int _estimate;
}
