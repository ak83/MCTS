package moveFinders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import chessboard.Chessboard;

import exceptions.BlackMoveFinderException;
import exceptions.ChessboardException;
import exec.Constants;
import exec.Move;
import exec.Utils;

/**
 * za nekatere funkcije je pred samo funkcijo potrebno vsaj enkrat klicati
 * initRandom
 * 
 * @author Administrator
 */
public class BlackMoveFinder {

    private static Random random;


    private BlackMoveFinder() {}


    public static void initRandom() {
        BlackMoveFinder.random = new Random();
    }


    public static void initRandom(long seed) {
        BlackMoveFinder.random = new Random(seed);
    }


    /**
     * @param board
     *            postavitev za katero najdemo naslednjo potezo
     * @param strategy
     *            0 za random strategijo, 1 kralj te�i k centru
     * @return
     */
    public static int findBlackKingMove(Chessboard board,
            BlackMoveFinderStrategy strategy) throws ChessboardException,
            BlackMoveFinderException {
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
                    return BlackMoveFinder.selectRandomMoveNumber(blackEats);
                }
            }
            default:
                throw new BlackMoveFinderException("strategija je neustrezna: "
                        + strategy);
        }

    }


    private static int findBlackKingCenterMove(ArrayList<Move> moves) {
        int minDist = BlackMoveFinder
                .findMinimumDistanceFromCenterFromMoves(moves);
        ArrayList<Move> rezMoves = new ArrayList<Move>();

        for (int x = 0; x < moves.size(); x++) {
            int dist = BlackMoveFinder
                    .distanceOfMoveFromCenter(moves.get(x).moveNumber);
            if (dist == minDist) {
                rezMoves.add(moves.get(x));
            }
        }

        int rez = BlackMoveFinder.randomIntUpTo(rezMoves.size());
        return rezMoves.get(rez).moveNumber;
    }


    private static int findBlackKingRandomMove(ArrayList<Move> moves) {
        int rez = BlackMoveFinder.randomIntUpTo(moves.size());
        return moves.get(rez).moveNumber;
    }


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
            return board.constructMoveNumberFromString(h);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**************************************************************************************************************************
     * + POMOZNE
     * FUNKCIJE********************************************************
     * ****************************************
     ****************************************************************************************************************************/

    private static int distanceOfMoveFromCenter(int moveNumber) {
        int fileDiff = BlackMoveFinder.rankDistanceOfMoveFromCenter(moveNumber);
        int rankDiff = BlackMoveFinder.fileDistanceOfMoveFromCenter(moveNumber);

        return fileDiff + rankDiff;
    }


    private static int rankDistanceOfMoveFromCenter(int moveNumber) {
        int to = Utils.getToFromMoveNumber(moveNumber);
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


    private static int fileDistanceOfMoveFromCenter(int moveNumber) {
        int to = Utils.getToFromMoveNumber(moveNumber);
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


    private static int findMinimumDistanceFromCenterFromMoves(
            ArrayList<Move> moves) {
        int minDist = -1;
        for (int x = 0; x < moves.size(); x++) {
            int moveNumber = moves.get(x).moveNumber;
            int dist = BlackMoveFinder.distanceOfMoveFromCenter(moveNumber);

            if (dist < minDist || minDist == -1) {
                minDist = dist;
            }
        }

        return minDist;
    }


    private static int randomIntUpTo(int n) {
        return BlackMoveFinder.random.nextInt(n);
    }


    private static int selectRandomMoveNumber(ArrayList<Move> moves) {
        int index = BlackMoveFinder.randomIntUpTo(moves.size());
        return moves.get(index).moveNumber;
    }


    private static void writeToProcess(Process process, String msg)
            throws IOException {
        char[] chars = (msg + "\n").toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int x = 0; x < chars.length; x++) {
            bytes[x] = (byte) chars[x];
        }
        process.getOutputStream().write(bytes);
    }


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
