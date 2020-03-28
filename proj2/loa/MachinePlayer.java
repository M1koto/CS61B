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
            _me = WP;
            value = findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            _me = BP;
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
     * Assigns estimated values to the each BOARD considering P.
     */
    private int heuristic(Board board, Piece p) {
        ArrayList<Integer> mGroup = board.getRegionSizes(p);
        ArrayList<Integer> oGroup = board.getRegionSizes(board.getOpp(p));
        if (board.gameOver() && board.winner() == p) {
            return INFTY;
        } else if (board.winner() == board.getOpp(p)) {
            return -INFTY;
        } else {
            return (oGroup.size() - mGroup.size()) * 2
                    + board.sum(oGroup) - board.sum(mGroup) + middle(board, p, board.getOpp(p))

                    + quad(board, p) + lateGame(board, mGroup, oGroup);
            //+ middle(board, p, board.getOpp(p))
            //+ midGame(board, mGroup, oGroup) + lateGame(board, mGroup);
        }
    }

    private int quad(Board board, Piece p) {
        if (p != _me) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < board.getSize() * board.getSize(); i++) {
            Square sq = board.getSq(i);
            if (board.get(sq) == p) {
                for (int j = 1; j < 8; j += 2) {
                    Square target = sq.moveDest(j, 1);
                    if (target != null && board.get(target) == p) {
                        count ++;
                    }
                }
            }
        }
        return count * 3;
    }

    /**
     * Return weight for BOARD considering A for early game.
     */
    private boolean middle(Board board) {
        return board.movesMade() * 4 >= board.getLimit(); // <15
    }

    /**
     * Return weight for BOARD considering A for mid-game.
     */
    private int midGame(Board board, ArrayList<Integer> a, ArrayList<Integer> o) {
        if (board.movesMade() * 4 >= board.getLimit()
                && board.movesMade() * 2 <= board.getLimit() * 3) { // >15
            return (board.sum(o) - board.sum(a)) * 10;
        }
        return 0;
    }

    /**
     * Return weight for BOARD considering A for lategame.
     */
    private int lateGame(Board board, ArrayList<Integer> a, ArrayList<Integer> o) {
        if (board.movesMade() * 3 >= board.getLimit() * 2) { //45
            return (o.size() - a.size()) * 5;
        }
        return 0;
    }

    /**
     * Return weights for mid square in BOARD considering P and OPP.
     */
    private int middle(Board board, Piece p, Piece opp) {
        if (!middle(board)) {
            return 0;
        }
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
        return ans * 50;
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
                    alpha += 2;
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
                    beta -= 2;
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
                    alpha += 2;
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
                    beta -= 2;
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

    /** Store my side. */
    private Piece _me;
}
