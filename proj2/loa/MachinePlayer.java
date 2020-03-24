/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.List;

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
        findMax(board)
        return 0;
    }

    /**
     * Return a search depth for the current position.
     */
    private int chooseDepth() {
        return getBoard().getLimit() - getBoard().movesMade();
    }

    // FIXME: Other methods, variables here.

    /**
     * Check if Piece p improved in terms of quantity
     * after making Move m on Board board.
     */
    private int heuristic(Move m) {
        if (m == null) {
            return -INFTY;
        }
        Piece p = side();
        Board board = getBoard();
        int Mgroup = board.getRegionSizes(p).size();
        int Ogroup = board.getRegionSizes(board.getOpp(p)).size();
        board.makeMove(m);
        int Mafter = board.getRegionSizes(p).size();
        if (board.piecesContiguous(p)) {
            return WINNING_VALUE;
        }
        int Oafter = board.getRegionSizes(board.getOpp(p)).size();
        if (board.piecesContiguous(board.getOpp(p))) {
            return -WINNING_VALUE;
        }
        board.retract();
        return (Mgroup - Mafter) - (Ogroup - Oafter);
    }
    private Move simpleFind(Board board) {
        if (board.winner() == board.getOpp(side())) {
            return null;
        }
        ArrayList<Move> legal = board.legalMoves();
        int ans
        for (Move m : legal) {

        }
    }

    private Move findMax(Board board, int depth, double alpha, double beta) {
        if (depth == 1 || board.gameOver()) {
            return simpleFind(board);
        }
        Move best = null;
        ArrayList<Move> legal = board.legalMoves();
        for (Move m : legal) {
            int next = heuristic(m);
            board.makeMove(m);
            Move response = findMin(board, depth, alpha, beta);
            if (heuristic(response) >= heuristic(best)) {
                best = m;
                next = heuristic(response);
                alpha = Double.max(alpha, next);
                if (alpha >= beta) {
                    break;
                }
            }
            board.retract();
        }
        return best;
    }

    private Move findMin(Board board, int depth, double alpha, double beta) {

    }
    /**
     * Used to convey moves discovered by findMove.
     */
    private Move _foundMove;

}
