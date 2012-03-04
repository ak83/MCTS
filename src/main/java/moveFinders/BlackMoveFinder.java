package moveFinders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import utils.FruitUtils;
import utils.MoveFindersUtils;

import chessboard.Chessboard;
import chessboard.SimpleChessboard;

import exceptions.ChessboardException;
import exec.Constants;
import exec.Move;

/**
 * Class handles searching for black player moves in simulations.
 * 
 * @author Andraz Kohne
 */
public class BlackMoveFinder {

    /** Random used by this class */
    private static Random random = new Random();


    /**
     * Finds blacks black move number for given strategy.
     * 
     * @param board
     *            board on which we look for ply number
     * @param strategy
     *            desired black king strategy
     * @return move number for given strategy
     * @throws ChessboardException
     */
    public static int findBlackKingMove(Chessboard board,
            BlackFinderStrategy strategy) throws ChessboardException {
        ArrayList<Move> moves = board.getAllLegalBlackKingMoves();

        if (moves.size() == 0) { return -1; }

        switch (strategy) {
            case RANDOM:
                return BlackMoveFinder.findBlackKingRandomMove(moves);
            case CENTER:
                return BlackMoveFinder.findBlackKingCenterMove(moves);
            case PERFECT:
                return BlackMoveFinder.findBlackPerfectMove(board);
            case GOOD: { // crni tezi proti centru, toda ce je mozno pa poje
                         // belo
                // figuro, poleg tega se pa tudi izgiba opoziciji kraljev
                ArrayList<Move> blackEats = board
                        .movesWhereBlackKingEatsWhite();
                if (blackEats.size() == 0) {
                    if (true) {
                        ArrayList<Move> avoidsOpp = board
                                .movesWhereBlackKingEvadesOposition(moves);
                        if (avoidsOpp.size() == 0) {
                            return BlackMoveFinder
                                    .findBlackKingCenterMove(moves);
                        }
                        else {
                            return BlackMoveFinder
                                    .findBlackKingCenterMove(avoidsOpp);
                        }
                    }
                }
                else {
                    return BlackMoveFinder.findBlackKingRandomMove(blackEats);
                }
            }
            default:
                return -1;
        }

    }


    /**
     * Finds black king ply number where black king tries to move toward center
     * of the board.
     * 
     * @param moves
     *            moves from which we get those who go towards center.
     * @return move number for center black king strategy
     */
    private static int findBlackKingCenterMove(ArrayList<Move> moves) {
        int minDist = MoveFindersUtils
                .findMinimumDistanceFromCenterFromPlies(moves);
        ArrayList<Move> rezMoves = new ArrayList<Move>();

        for (int x = 0; x < moves.size(); x++) {
            int dist = MoveFindersUtils
                    .distanceOfMoveFromCenter(moves.get(x).moveNumber);
            if (dist == minDist) {
                rezMoves.add(moves.get(x));
            }
        }

        return BlackMoveFinder.findBlackKingRandomMove(rezMoves);
    }


    /**
     * Returns a random move from a list of plies
     * 
     * @param moves
     *            list of moves
     * @return random move from <code>moves</code>.
     */
    private static int findBlackKingRandomMove(ArrayList<Move> moves) {
        int rez = BlackMoveFinder.random.nextInt(moves.size());
        return moves.get(rez).moveNumber;
    }


    /**
     * Finds an optimal move number for black king.
     * 
     * @param board
     *            board on which we search for move
     * @return move number for black king that uses perfect strategy
     */
    private static int findBlackPerfectMove(SimpleChessboard board) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(Constants.FRUIT_FILEPATH);
            FruitUtils.writeToProcess(pr, "ucinewgame");
            FruitUtils.writeToProcess(pr, "setoption name Hash value 128");
            FruitUtils.writeToProcess(pr, "setoption name MultiPV value 100");
            FruitUtils.writeToProcess(pr, "setoption name NalimovPath value "
                    + Constants.EMD_DIR);
            FruitUtils.writeToProcess(pr,
                    "setoption name NalimovCache value 32");
            FruitUtils.writeToProcess(pr, "setoption name EGBB Path value "
                    + Constants.EMD_DIR);
            FruitUtils.writeToProcess(pr, "setoption name EGBB Cache value 32");
            // writeToProcess(pr,
            // "position fen r7/8/5k2/8/8/8/R7/K7 w - - 0 1");
            FruitUtils.writeToProcess(pr, "position fen " + board.boardToFen());
            FruitUtils.writeToProcess(pr, "go depth 2");
            pr.getOutputStream().close();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr
                    .getInputStream()));

            String line = null;
            String h = null;
            while ((line = input.readLine()) != null) {
                h = line;
            }
            input.close();
            pr.destroy();
            h = h.substring(9, 13);
            return board.constructMoveNumberFromString(h);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    private BlackMoveFinder() {}

}
