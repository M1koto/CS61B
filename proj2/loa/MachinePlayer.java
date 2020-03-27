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
            return heuristic(board, side());
        } else if (saveMove) {
            if (sense == 1) {
                _foundMove = findMax(board, depth, alpha, beta, true);
            } else if (sense == -1) {
                _foundMove = findMin(board, depth, alpha, beta, true);
            }
            board.makeMove(_foundMove);
            int ans = heuristic(board, side());
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


    /**
     * Assigns estimated values to the each BOARD considering
     * all possible moves Returns largest value.
     */
    private int heuristic(Board board, Piece p) {
        ArrayList<Integer> mGroup = board.getRegionSizes(p);
        ArrayList<Integer> oGroup = board.getRegionSizes(board.getOpp(p));
        if (board.gameOver() && board.winner() == p) {
            return INFTY;
        } else if (board.winner() == board.getOpp(p)) {
            return -INFTY;
        } else {
            return ((oGroup.size() - mGroup.size()) * 20
                    - board.sum(mGroup) + board.sum(oGroup) +
                    middle(board, p, board.getOpp(p)) * earlygame(board)) * 10
                    + lategame(board, mGroup);
        }
    }

    private int earlygame(Board board) {
        if (board.movesMade() * 5 <= board.getLimit()) {
            return 200;
        }
        return 1;
    }

    /**
     * Return weight for BOARD considering A for lategame.
     */
    private int lategame(Board board, ArrayList<Integer> a) {
        if (board.movesMade() * 2 >= board.getLimit()) {
            return (10 - board.sum(a)) * 200;
        }
        return 0;
    }

    /**
     * Return weights for mid square in BOARD considering P and OPP.
     */
    private int middle(Board board, Piece p, Piece opp) {
        int ans = 0;
        if (board.twoEight() == p) {
            ans += 1;
        } else if (board.twoEight() == opp) {
            ans -= 1;
        }
        if (board.twoSev() == p) {
            ans += 1;
        } else if (board.twoSev() == opp) {
            ans -= 1;
        }
        if (board.thrFive() == p) {
            ans += 1;
        } else if (board.thrFive() == opp) {
            ans -= 1;
        }
        if (board.thrSix() == p) {
            ans += 1;
        } else if (board.thrSix() == opp) {
            ans -= 1;
        }
        return ans * 200 - (board.movesMade() ^ 2);
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
    private Move simpleFindMax(Board board, double alpha, double beta, boolean me) {
        ArrayList<Move> legal = board.legalMoves();
        if (board.winner() != null) {
            //System.out.println(board.toString());
            //System.out.println("from max");
            return null;
        }
        Move best = legal.get(0);
        for (Move m : legal) {
            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp, WP);

            board.makeMove(m);
            board.setValue(heuristic(board, WP));

            if (board.getValue() >= compare) {
                best = m;
                alpha = Double.max(alpha, (double) board.getValue());
                if (!me) {
                    alpha += 100;
                }
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
    private Move simpleFindMin(Board board, double alpha, double beta, boolean me) {
        ArrayList<Move> legal = board.legalMoves();
        if (board.winner() != null) {
            //System.out.println(board.toString());
            //System.out.println("from min");
            return null;
        }

        Move best = legal.get(0);
        for (Move m : legal) {
            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp, BP);

            board.makeMove(m);
            board.setValue(heuristic(board, BP));

            if (board.getValue() <= compare) {
                best = m;
                beta = Double.min(beta, (double) board.getValue());
                if (!me) {
                    beta -= 100;
                }
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
     * ALPHA BETA to prune from maximizing player's perspective. White.
     */
    private Move findMax(Board board, int depth, double alpha, double beta, boolean me) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMax(board, alpha, beta, me);
        }
        ArrayList<Move> legal = board.legalMoves();

        Move best = legal.get(0);

        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp, WP);

            board.makeMove(m);

            Move response = findMin(board, depth - 1, alpha, beta, !me);

            int responseVal = heuristic(board, WP);
            //System.out.println(responseVal);
            if (response != null) {
                board.makeMove(response);
                responseVal = heuristic(board, WP);
                board.retract();
            }

            if (responseVal >= compare) {
                if (response == null) {
                    Move ret = board.lastmove();
                    board.retract();
                    return ret;
                }
                best = m;
                alpha = Double.max(alpha, (double) responseVal);
                if (!me) {
                    alpha += 100;
                }
                if (alpha >= beta) {
                    board.retract();
                    break;
                } else {
                    board.retract();
                }
            } else {
                board.retract();
            }
        }
        return best;
    }

    /**
     * Return the next move in BOARD considering DEPTH and
     * ALPHA BETA to prune from minimizing player's perspective. Black.
     */
    private Move findMin(Board board, int depth, double alpha, double beta, boolean me) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMin(board, alpha, beta, me);
        }
        ArrayList<Move> legal = board.legalMoves();

        Move best = legal.get(0);

        for (Move m : legal) {

            Board temp = new Board(board);
            temp.makeMove(best);
            int compare = heuristic(temp, BP);

            board.makeMove(m);

            Move response = findMax(board, depth - 1, alpha, beta, !me);

            int responseVal = heuristic(board, BP);
            //System.out.println(responseVal);
            if (response != null) {
                board.makeMove(response);
                responseVal = heuristic(board, BP);
                board.retract();
            }

            if (responseVal <= compare) {
                if (response == null) {
                    Move ret = board.lastmove();
                    board.retract();
                    return ret;
                }
                best = m;
                beta = Double.min(beta, (double) responseVal);
                if (!me) {
                    beta -= 100;
                }
                if (alpha >= beta) {
                    board.retract();
                    break;
                } else {
                    board.retract();
                }
            } else {
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
