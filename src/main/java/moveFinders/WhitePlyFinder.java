package moveFinders;

import java.util.ArrayList;
import java.util.Random;

import javax.management.RuntimeErrorException;

import chessboard.Chessboard;
import exceptions.ChessboardException;
import exec.Constants;
import exec.Ply;

/**
 * Class that handles choosing white simulation ply number.
 * 
 * @author Andraz Kohne
 */
public class WhitePlyFinder {

    private static Random random = new Random();


    /**
     * Finds whites ply number for given strategy and set heuristics.
     * 
     * @param board
     *            board on which we search
     * @param strategy
     *            white simulation strategy
     * @return ply number
     * @throws ChessboardException
     */
    public static int findWhiteMove(Chessboard board,
            WhiteFinderStrategy strategy) throws ChessboardException {
        switch (strategy) {
            case RANDOM:
                return WhitePlyFinder.findRandomWhiteMove(board
                        .getAllLegalWhitePlies());
            case KRRK_ENDING:
                return WhitePlyFinder.findKRRKWhiteMove(board);
            case KQK_ENDING:
                return WhitePlyFinder.findKQKWhiteMove(board);
            case KRK_ENDING:
                return WhitePlyFinder.findKRKWhiteMove(board);
            case KBBK_ENDING:
                return WhitePlyFinder.findKBBKWhiteMove(board);
            default:
                return -1;
        }
    }


    /**
     * Finds next whites ply number in simulations for KRK ending for set
     * heuristics.
     * 
     * @param board
     *            state of chess board on which we search for move
     * @return whites move corresponding to set heuristics
     * @throws ChessboardException
     */
    private static int findKRKWhiteMove(Chessboard board)
            throws ChessboardException {
        ArrayList<Ply> rez = WhitePlyFinder.generalHeuristics(board);

        if (Constants.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition) {
            ArrayList<Ply> opp = board
                    .KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(rez);
            if (opp.size() != 0) {
                rez = opp;
            }
        }

        return WhitePlyFinder.getRandomPlyNumberFromArrayList(rez);
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
        ArrayList<Ply> rez = board.getAllLegalWhitePlies();

        if (Constants.HEURISTICS_check_for_urgent_moves) {
            ArrayList<Ply> urgent = board.KBBKWhiteUrgentMoves(rez);
            if (urgent.size() != 0) { return WhitePlyFinder
                    .getRandomPlyNumberFromArrayList(urgent); }
        }

        if (Constants.HEURISTICS_only_safe_moves) {
            ArrayList<Ply> safe = board.KBBKWhiteSafeMoves(rez);
            if (safe.size() != 0) {
                rez = safe;
            }
        }

        if (Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3
                && board.distanceBewteenKings() > 3) {
            ArrayList<Ply> kingMoves = board.filterMovesToWhiteKingMoves(rez);
            kingMoves = board
                    .pliesWhereWhiteKingMovesCloserOrEqualToBlackKind(kingMoves);
            if (kingMoves.size() != 0) {
                rez = kingMoves;
            }
        }

        if (Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals) {
            ArrayList<Ply> diagonal = board
                    .KBBKWhiteMovesWhereBishopsAreOnAdjacentDiagonals(rez);
            if (diagonal.size() != 0) {
                rez = diagonal;
            }
        }

        return WhitePlyFinder.getRandomPlyNumberFromArrayList(rez);
    }


    /**
     * Fetches random ply number from a list of plies.
     * 
     * @param plies
     *            plies
     * @return random ply number from list
     */
    private static int getRandomPlyNumberFromArrayList(ArrayList<Ply> plies) {
        int index = WhitePlyFinder.random.nextInt(plies.size());
        return plies.get(index).plyNumber;
    }


    /**
     * Gets list of plies for set general heuristics.
     * 
     * @param board
     *            chess board on which we search for moves
     * @return plies that are allowed by general heuristics
     */
    protected static ArrayList<Ply> generalHeuristics(Chessboard board) {
        ArrayList<Ply> rez = null;
        try {
            rez = board.getAllLegalWhitePlies();
        }
        catch (ChessboardException e) {
            throw new RuntimeErrorException(new Error(e));
        }

        if (Constants.HEURISTICS_check_for_urgent_moves) {
            ArrayList<Ply> urgent = board.whiteUrgentPlies(rez);
            if (urgent.size() > 0) {
                rez = urgent;
            }
        }

        if (Constants.HEURISTICS_only_safe_moves) {
            ArrayList<Ply> safe = board.whiteSafePlies(rez);
            if (safe.size() != 0) {
                rez = safe;
            }
        }

        if (Constants.HEURISTICS_avoid_move_repetition) {
            ArrayList<Ply> avoidance = new ArrayList<Ply>();
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
            ArrayList<Ply> kingCloser = board
                    .pliesWhereWhiteKingMovesCloserOrEqualToBlackKind(rez);
            if (kingCloser.size() > 0) {
                rez = kingCloser;
            }
        }

        if (Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3) {
            if (board.distanceBewteenKings() > 3) {
                ArrayList<Ply> kingMoves = board
                        .filterMovesToWhiteKingMoves(rez);
                kingMoves = board
                        .pliesWhereWhiteKingMovesCloserOrEqualToBlackKind(kingMoves);
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
    private static int findRandomWhiteMove(ArrayList<Ply> plies) {
        int rez = WhitePlyFinder.random.nextInt(plies.size());
        return plies.get(rez).plyNumber;
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
        ArrayList<Ply> rez = WhitePlyFinder.generalHeuristics(board);

        return WhitePlyFinder.getRandomPlyNumberFromArrayList(rez);
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
        ArrayList<Ply> rez = WhitePlyFinder.generalHeuristics(board);

        return WhitePlyFinder.getRandomPlyNumberFromArrayList(rez);
    }

}
