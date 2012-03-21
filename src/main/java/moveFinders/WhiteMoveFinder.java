package moveFinders;

import java.util.ArrayList;
import java.util.Random;

import javax.management.RuntimeErrorException;

import chessboard.Chessboard;
import chessboard.Move;
import exceptions.ChessboardException;
import exec.Constants;

/**
 * Class that handles choosing white simulation ply number.
 * 
 * @author Andraz Kohne
 */
public class WhiteMoveFinder {

    private static Random random = new Random();


    /**
     * Finds whites move number for given strategy and set heuristics.
     * 
     * @param board
     *            board on which we search
     * @param strategy
     *            white simulation strategy
     * @return move number
     * @throws ChessboardException
     */
    public static int findWhiteMove(Chessboard board,
            WhiteFinderStrategy strategy) throws ChessboardException {
        switch (strategy) {
            case RANDOM:
                return WhiteMoveFinder.findRandomWhiteMove(board
                        .getAllLegalWhitePlies());
            case KRRK_ENDING:
                return WhiteMoveFinder.findKRRKWhiteMove(board);
            case KQK_ENDING:
                return WhiteMoveFinder.findKQKWhiteMove(board);
            case KRK_ENDING:
                return WhiteMoveFinder.findKRKWhiteMove(board);
            case KBBK_ENDING:
                return WhiteMoveFinder.findKBBKWhiteMove(board);
            default:
                return -1;
        }
    }


    /**
     * Finds next whites move number in simulations for KRK ending for set
     * heuristics.
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


    /**
     * Finds next whites ply number in simulations for KBBK ending for set
     * heuristics.
     * 
     * @param board
     *            state of chess board on which we search for move
     * @return whites move corresponding to set heuristics
     * @throws ChessboardException
     */
    private static int findKBBKWhiteMove(Chessboard board)
            throws ChessboardException {

        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        if (Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals) {
            ArrayList<Move> diagonal = board
                    .KBBKWhiteMovesWhereBishopsAreOnAdjacentDiagonals(rez);
            if (diagonal.size() != 0) {
                rez = diagonal;
            }
        }

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }


    /**
     * Fetches random move number from a list of moves.
     * 
     * @param moves
     *            moves
     * @return random move number from list
     */
    private static int getRandomMoveNumberFromArrayList(ArrayList<Move> moves) {
        int index = WhiteMoveFinder.random.nextInt(moves.size());
        return moves.get(index).moveNumber;
    }


    /**
     * Gets list of moves for set general heuristics.
     * 
     * @param board
     *            chess board on which we search for moves
     * @return plies that are allowed by general heuristics
     */
    protected static ArrayList<Move> generalHeuristics(Chessboard board) {
        ArrayList<Move> rez = null;
        try {
            rez = board.getAllLegalWhitePlies();
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
     * Gets random ply number from a list of plies
     * 
     * @param plies
     *            list of plies
     * @return ply number selected from plies
     */
    private static int findRandomWhiteMove(ArrayList<Move> plies) {
        int rez = WhiteMoveFinder.random.nextInt(plies.size());
        return plies.get(rez).moveNumber;
    }


    /**
     * Finds next whites ply number in simulations for KRRK ending for set
     * heuristics.
     * 
     * @param board
     *            chess board on which we search for ply number
     * @return valid ply number that corresponds to set heuristics
     * @throws ChessboardException
     */
    private static int findKRRKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }


    /**
     * Finds next whites ply number in simulations for KQK ending for set
     * heuristics.
     * 
     * @param board
     *            chess board on which we search for move
     * @return valid ply number that corresponds to set heuristics
     * @throws ChessboardException
     */
    private static int findKQKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Move> rez = WhiteMoveFinder.generalHeuristics(board);

        return WhiteMoveFinder.getRandomMoveNumberFromArrayList(rez);
    }

}
