/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;

import static loa.Piece.*;

/**
 * An automated Player.
 *
 * @author kenny liao
 */
class MachinePlayer extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new MachinePlayer with no piece or controller (intended to produce
     * a template).
     */
    MachinePlayer() {
        this(null, null);
    }

    /**
     * A MachinePlayer that plays the SIDE pieces in GAME.
     */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;

        assert side() == getGame().getBoard().turn();
        int depth;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /**
     * Return a move after searching the game tree to DEPTH>0 moves
     * from the current position. Assumes the game is not over.
     */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            value = findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            value = findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _foundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _foundMove. If the game is over
     * on BOARD, does not set _foundMove.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (board.gameOver()) {
            return INFTY;
        } else if (depth == 0) {
            return heuristic(board);
        } else if (saveMove) {
            if (sense == 1) {
                _foundMove = findMax(board, depth, alpha, beta);
            } else if (sense == -1) {
                _foundMove = findMin(board, depth, alpha, beta);
            }
            board.makeMove(_foundMove);
            int ans = heuristic(board);
            board.retract();
            return ans;
        }
        return 0;
    }

    /**
     * Return a search depth for the current position.
     */
    private int chooseDepth() {
        return 3;
    }

    // FIXME: Other methods, variables here.

    /**
     * Assigns estimated values to the each BOARD considering
     * all possible moves Returns largest value.
     */
    private int heuristic(Board board) {
        ArrayList<Move> legal = board.legalMoves();
        ArrayList<Integer> ans = new ArrayList<>();
        Piece p = side();
        int mGroup = board.getRegionSizes(p).size();
        int oGroup = board.getRegionSizes(board.getOpp(p)).size();
        for (Move m : legal) {
            board.makeMove(m);
            int mAfter = board.getRegionSizes(p).size();
            if (board.piecesContiguous(p)) {
                return WINNING_VALUE;
            }
            int oAfter = board.getRegionSizes(board.getOpp(p)).size();
            board.retract();
            ans.add(((mGroup - mAfter) - (oGroup - oAfter)) * 20);
        }
        return largest(ans);
    }

    /**
     * Returns largest Integer in Arraylist A.
     */
    private int largest(ArrayList<Integer> a) {
        int ans = -INFTY;
        for (int i : a) {
            if (i > ans) {
                ans = i;
            }
        }
        return ans;
    }

    /**
     * Static evaluation.
     * Find a single layer of move in BOARD
     * where ALPHA < BETA and returns best move.
     */
    private Move simpleFindMax(Board board, double alpha, double beta) {
        ArrayList<Move> legal = board.legalMoves();
        if (board.winner() != null) {
            return board.lastmove();
        }
        Move best = legal.get(0);
        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp);

            board.makeMove(m);
            board.setValue(heuristic(board));

            if (board.getValue() >= compare) {
                best = m;
                alpha = Double.max(alpha, (double) board.getValue());
                board.retract();
                if (alpha >= beta) {
                    break;
                }
            } else {
                board.retract();
            }
        }
        return best;
    }

    /**
     * Static evaluation.
     * Find a single layer of move in BOARD
     * where ALPHA > BETA and returns best move.
     */
    private Move simpleFindMin(Board board, double alpha, double beta) {
        ArrayList<Move> legal = board.legalMoves();
        if (board.winner() != null) {
            return board.lastmove();
        }

        Move best = legal.get(0);
        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp);

            board.makeMove(m);
            board.setValue(heuristic(board));

            if (board.getValue() <= compare) {
                best = m;
                beta = Double.min(beta, (double) board.getValue());
                board.retract();
                if (alpha >= beta) {
                    break;
                }
            } else {
                board.retract();
            }
        }
        return best;
    }

    /**
     * Return the next move in BOARD considering DEPTH and
     * ALPHA BETA to prune from maximizing player's perspective.
     */
    private Move findMax(Board board, int depth, double alpha, double beta) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMax(board, alpha, beta);
        }
        ArrayList<Move> legal = board.legalMoves();

        Move best = legal.get(0);

        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp);


            board.makeMove(m);
            Move response = findMin(board, depth - 1, alpha, beta);
            board.makeMove(response);
            int responseVal = heuristic(board);

            if (responseVal >= compare) {
                best = m;
                board.retract();
                alpha = Double.max(alpha, (double) responseVal);
                if (alpha >= beta) {
                    board.retract();
                    break;
                } else {
                    board.retract();
                }
            } else {
                board.retract();
                board.retract();
            }
        }
        return best;
    }

    /**
     * Return the next move in BOARD considering DEPTH and
     * ALPHA BETA to prune from minimizing player's perspective.
     */
    private Move findMin(Board board, int depth, double alpha, double beta) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMin(board, alpha, beta);
        }
        ArrayList<Move> legal = board.legalMoves();

        Move best = legal.get(0);

        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp);

            board.makeMove(m);
            Move response = findMax(board, depth - 1, alpha, beta);
            board.makeMove(response);
            int responseVal = heuristic(board);

            if (responseVal <= compare) {
                best = m;
                board.retract();
                beta = Double.min(beta, (double) responseVal);
                if (alpha >= beta) {
                    board.retract();
                    break;
                } else {
                    board.retract();
                }
            } else {
                board.retract();
                board.retract();
            }
        }
        return best;
    }

    /**
     * Used to convey moves discovered by findMove.
     */
    private Move _foundMove;

}
