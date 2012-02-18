package moveFinders;

import java.util.ArrayList;
import java.util.Random;

import javax.management.RuntimeErrorException;

import chessboard.Chessboard;
import exceptions.ChessboardException;
import exceptions.WhiteMoveFinderException;
import exec.Constants;
import exec.Move;

/**
 * za nekatere funkcije je pred samo funkcijo potrebno vsaj enkrat klicati
 * initRandom
 * 
 * @author Administrator
 */
public class WhiteMoveFinder {

    private static Random random = new Random();


    /**
     * @param board
     *            postavitev za katero iscemo naslednje poteze
     * @param strategy
     *            0 - random strategy, 1 - KRRK ending, 2 - KQK ending, 3 - KRK
     *            ending, 4 - KBBK ending
     * @return movenumber
     */
    public static int findWhiteMove(Chessboard board,
            WhiteFinderStrategy strategy) throws ChessboardException,
            WhiteMoveFinderException {
        switch (strategy) {
            case RANDOM:
                return WhiteMoveFinder.findRandomWhiteMove(board
                        .getAllLegalWhiteMoves());
            case KRRK_ENDING:
                return WhiteMoveFinder.findKRRKWhiteMove(board);
            case KQK_ENDING:
                return WhiteMoveFinder.findKQKWhiteMove(board);
            case KRK_ENDING:
                return WhiteMoveFinder.findKRKWhiteMove(board);
            case KBBK_ENDING:
                return WhiteMoveFinder.findKBBKWhiteMove(board);
            default:
                throw new WhiteMoveFinderException("strategija je neustrezna: "
                        + strategy);
        }
    }


    /**
     * finds next whites move in simulations for KRK ending
     * 
     * @param board
     *            state of chess board on which we search for move
     * @return whites move corresponding to set heuristics
     * @throws ChessboardException
     */
    private static int findKRKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        if (Constants.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition) {
            ArrayList<Move> opp = board
                    .KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(rez);
            if (opp.size() != 0) {
                rez = opp;
            }
        }

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }


    private static int findKBBKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = board.getAllLegalWhiteMoves();

        if (Constants.HEURISTICS_check_for_urgent_moves) {
            ArrayList<Move> urgent = board.KBBKWhiteUrgentMoves(rez);
            if (urgent.size() != 0) { return WhiteMoveFinder
                    .getRandomMoveNumberFromArrayList(urgent); }
        }

        if (Constants.HEURISTICS_only_safe_moves) {
            ArrayList<Move> safe = board.KBBKWhiteSafeMoves(rez);
            if (safe.size() != 0) {
                rez = safe;
            }
        }

        if (Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3
                && board.distanceBewteenKings() > 3) {
            ArrayList<Move> kingMoves = board.filterMovesToWhiteKingMoves(rez);
            kingMoves = board
                    .movesWhereWhiteKingMovesCloserOrEqualToBlackKind(kingMoves);
            if (kingMoves.size() != 0) {
                rez = kingMoves;
            }
        }

        if (Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals) {
            ArrayList<Move> diagonal = board
                    .KBBKWhiteMovesWhereBishopsAreOnAdjacentDiagonals(rez);
            if (diagonal.size() != 0) {
                rez = diagonal;
            }
        }

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }


    private static int getRandomMoveNumberFromArrayList(ArrayList<Move> moves) {
        int index = WhiteMoveFinder.random.nextInt(moves.size());
        return moves.get(index).moveNumber;
    }


    /**
     * @param board
     *            chess board on which we search for moves
     * @return moves that are allowed by general heuristics
     */
    protected static ArrayList<Move> generalHeuristics(Chessboard board) {
        ArrayList<Move> rez = null;
        try {
            rez = board.getAllLegalWhiteMoves();
        }
        catch (ChessboardException e) {
            throw new RuntimeErrorException(new Error(e));
        }

        if (Constants.HEURISTICS_check_for_urgent_moves) {
            ArrayList<Move> urgent = board.whiteUrgentMoves(rez);
            if (urgent.size() > 0) {
                rez = urgent;
            }
        }

        if (Constants.HEURISTICS_only_safe_moves) {
            ArrayList<Move> safe = board.whiteSafeMoves(rez);
            if (safe.size() != 0) {
                rez = safe;
            }
        }

        if (Constants.HEURISTICS_avoid_move_repetition) {
            ArrayList<Move> avoidance = new ArrayList<Move>();
            try {
                avoidance = board.movesWhereWhiteAvoidsMoveRepetition(rez);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (avoidance.size() != 0) {
                rez = avoidance;
            }
            else {
                System.out.println("Zmanjkalo potez....");
            }
        }

        if (Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king) {
            ArrayList<Move> kingCloser = board
                    .movesWhereWhiteKingMovesCloserOrEqualToBlackKind(rez);
            if (kingCloser.size() > 0) {
                rez = kingCloser;
            }
        }

        if (Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3) {
            if (board.distanceBewteenKings() > 3) {
                ArrayList<Move> kingMoves = board
                        .filterMovesToWhiteKingMoves(rez);
                kingMoves = board
                        .movesWhereWhiteKingMovesCloserOrEqualToBlackKind(kingMoves);
                if (kingMoves.size() != 0) {
                    rez = kingMoves;
                }
            }
        }

        return rez;
    }


    /**
     * @param moves
     * @return movenumber from random move selected from moves
     */
    private static int findRandomWhiteMove(ArrayList<Move> moves) {
        int rez = WhiteMoveFinder.random.nextInt(moves.size());
        return moves.get(rez).moveNumber;
    }


    /**
     * @param board
     *            chess board on which we search for move
     * @return valid movenumber that corresponds which general heuristics
     * @throws ChessboardException
     */
    private static int findKRRKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }


    /**
     * @param board
     *            chess board on which we search for move
     * @return valid movenumber that corresponds which general heuristics
     * @throws ChessboardException
     */
    private static int findKQKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }

}
