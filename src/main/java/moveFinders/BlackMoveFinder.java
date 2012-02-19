package moveFinders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import chessboard.Chessboard;

import exceptions.ChessboardException;
import exec.Constants;
import exec.Ply;
import exec.Utils;

/**
 * Class handles searching for black player moves in simulations.
 * 
 * @author Andraz Kohne
 */
public class BlackMoveFinder {

    /** Random used by this class */
    private static Random random = new Random();


    private BlackMoveFinder() {}


    /**
     * Finds blacks black ply number for given strategy.
     * 
     * @param board
     *            board on which we look for ply number
     * @param strategy
     *            desired black king strategy
     * @return ply number for given strategy
     * @throws ChessboardException
     */
    public static int findBlackKingMove(Chessboard board,
            BlackFinderStrategy strategy) throws ChessboardException {
        ArrayList<Ply> moves = board.getAllLegalBlackKingMoves();

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
                ArrayList<Ply> blackEats = board
                        .movesWhereBlackKingEatsWhite();
                if (blackEats.size() == 0) {
                    if (true) {
                        ArrayList<Ply> avoidsOpp = board
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
                    return BlackMoveFinder.selectRandomMoveNumber(blackEats);
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
     * @param plies
     *            plies from which we get those who go towards center.
     * @return ply number for center black king strategy
     */
    private static int findBlackKingCenterMove(ArrayList<Ply> plies) {
        int minDist = BlackMoveFinder
                .findMinimumDistanceFromCenterFromPlies(plies);
        ArrayList<Ply> rezMoves = new ArrayList<Ply>();

        for (int x = 0; x < plies.size(); x++) {
            int dist = BlackMoveFinder
                    .distanceOfMoveFromCenter(plies.get(x).plyNumber);
            if (dist == minDist) {
                rezMoves.add(plies.get(x));
            }
        }

        int rez = BlackMoveFinder.randomIntUpTo(rezMoves.size());
        return rezMoves.get(rez).plyNumber;
    }


    /**
     * Returns a random ply from a list of plies
     * 
     * @param plies
     *            plies
     * @return random ply from <code>moves</code>.
     */
    private static int findBlackKingRandomMove(ArrayList<Ply> plies) {
        int rez = BlackMoveFinder.randomIntUpTo(plies.size());
        return plies.get(rez).plyNumber;
    }


    /**
     * Finds an optimal ply number for black king.
     * 
     * @param board
     *            board on which we search for ply
     * @return ply number for black king that uses perfect strategy
     */
    private static int findBlackPerfectMove(Chessboard board) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(Constants.FRUIT_FILEPATH);
            BlackMoveFinder.writeToProcess(pr, "ucinewgame");
            BlackMoveFinder.writeToProcess(pr, "setoption name Hash value 128");
            BlackMoveFinder.writeToProcess(pr,
                    "setoption name MultiPV value 100");
            BlackMoveFinder.writeToProcess(pr,
                    "setoption name NalimovPath value " + Constants.EMD_DIR);
            BlackMoveFinder.writeToProcess(pr,
                    "setoption name NalimovCache value 32");
            BlackMoveFinder.writeToProcess(pr,
                    "setoption name EGBB Path value " + Constants.EMD_DIR);
            BlackMoveFinder.writeToProcess(pr,
                    "setoption name EGBB Cache value 32");
            // writeToProcess(pr,
            // "position fen r7/8/5k2/8/8/8/R7/K7 w - - 0 1");
            BlackMoveFinder.writeToProcess(pr, "position fen "
                    + board.boardToFen());
            BlackMoveFinder.writeToProcess(pr, "go depth 2");
            pr.getOutputStream().close();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr
                    .getInputStream()));

            String line = null;
            String h = null;
            while ((line = input.readLine()) != null) {
                h = line;
            }
            input.close();
            h = h.substring(9, 13);
            return board.constructPlyNumberFromString(h);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Calculates distance between chess board center and target position of
     * ply.
     * 
     * @param plyNumber
     *            ply number
     * @return distance between chess board center and target position of ply
     */
    private static int distanceOfMoveFromCenter(int plyNumber) {
        int fileDiff = BlackMoveFinder.rankDistanceOfMoveFromCenter(plyNumber);
        int rankDiff = BlackMoveFinder.fileDistanceOfMoveFromCenter(plyNumber);

        return fileDiff + rankDiff;
    }


    /**
     * Calculates distance between chess board center and target rank of ply.
     * 
     * @param plyNumber
     *            ply number
     * @return distance between chess board center and target rank of ply
     */
    private static int rankDistanceOfMoveFromCenter(int plyNumber) {
        int to = Utils.getToFromMoveNumber(plyNumber);
        int toRank = Utils.getRankFromPosition(to);

        int rankDiff = -1;

        if (toRank > 5) {
            rankDiff = Math.abs(toRank - 5);
        }
        else if (toRank < 4) {
            rankDiff = Math.abs(4 - toRank);
        }
        else {
            rankDiff = 0;
        }

        return rankDiff;
    }


    /**
     * Calculates distance between chess board center and target file of ply.
     * 
     * @param plyNumber
     *            ply number
     * @return distance between chess board center and target file of ply
     */
    private static int fileDistanceOfMoveFromCenter(int plyNumber) {
        int to = Utils.getToFromMoveNumber(plyNumber);
        int toFile = Utils.getFileFromPosition(to);

        int fileDiff = -1;

        if (toFile > 5) {
            fileDiff = Math.abs(toFile - 5);
        }
        else if (toFile < 4) {
            fileDiff = Math.abs(4 - toFile);
        }
        else {
            fileDiff = 0;
        }

        return fileDiff;
    }


    /**
     * Finds minimum distance of target positions of plies from chess board
     * center.
     * 
     * @param plies
     *            plies
     * @return minimum distance of ply target position from chess board center
     */
    private static int findMinimumDistanceFromCenterFromPlies(
            ArrayList<Ply> plies) {
        int minDist = -1;
        for (int x = 0; x < plies.size(); x++) {
            int moveNumber = plies.get(x).plyNumber;
            int dist = BlackMoveFinder.distanceOfMoveFromCenter(moveNumber);

            if (dist < minDist || minDist == -1) {
                minDist = dist;
            }
        }

        return minDist;
    }


    /**
     * Gets random integer from 0 to n
     * 
     * @param n
     *            n
     * @return random integer
     */
    private static int randomIntUpTo(int n) {
        return BlackMoveFinder.random.nextInt(n);
    }


    /**
     * Select random ply from a list of plies
     * 
     * @param plies
     *            plies
     * @return random ply number from list of plies
     */
    private static int selectRandomMoveNumber(ArrayList<Ply> plies) {
        int index = BlackMoveFinder.randomIntUpTo(plies.size());
        return plies.get(index).plyNumber;
    }


    /**
     * Inputs string to external process.
     * 
     * @param process
     *            process
     * @param msg
     *            input message
     * @throws IOException
     */
    private static void writeToProcess(Process process, String msg)
            throws IOException {
        char[] chars = (msg + "\n").toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int x = 0; x < chars.length; x++) {
            bytes[x] = (byte) chars[x];
        }
        process.getOutputStream().write(bytes);
    }


    /**
     * Checks if external program fruit is ready.
     * 
     * @return <code>true</code> if fruit is ready, othwerwise
     *         <code>false</code>.
     */
    public static boolean isFruitReady() {
        String h = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(Constants.FRUIT_FILEPATH);
            BlackMoveFinder.writeToProcess(pr, "isready");

            pr.getOutputStream().close();
            BufferedReader input = new BufferedReader(new InputStreamReader(pr
                    .getInputStream()));

            String line = null;
            while ((line = input.readLine()) != null) {
                h = line;
            }
            input.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return h.equals("readyok");
    }

}
