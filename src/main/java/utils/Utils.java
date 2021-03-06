package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import mct.MCTNode;
import moveChoosers.WhiteChooserStrategy;
import moveFinders.BlackFinderStrategy;

/** Class holds various utility methods */
public class Utils {
    
    
    public final static String CSV_DELIMITER = "\t";

    /**
     * Checks if two chess board positions are adjacent.
     * 
     * @param positionA
     *            position
     * @param positionB
     *            position
     * @return <code>true</code> if positions are adjacent, <code>false</code>
     *         otherwise
     */
    public static boolean arePositionsAdjacent(int positionA, int positionB) {
        if (!ChessboardUtils.isPositionLegal(positionA) || !ChessboardUtils.isPositionLegal(positionB)) { return false; }

        int diff = Math.abs(positionB - positionA);
        if (diff == 1 || diff == 15 || diff == 16 || diff == 17) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Checks if two chess board positions lie on adjacent diagonals.
     * 
     * @param position1
     *            chess board position
     * @param position2
     *            chess board position
     * @return <code>true</code> if positions lie on adjacent diagonals,
     *         <code>false</code> otherwise
     */
    public static boolean arePsotionsDiagonallyAdjacent(int position1, int position2) {
        int edgePos1 = Utils.getPositiveEdgePositionFromPosition(position1);
        int edgePos2 = Utils.getPositiveEdgePositionFromPosition(position2);
        if (edgePos1 < 8 && edgePos2 < 8) {
            int file1 = Utils.getFileFromPosition(edgePos1);
            int file2 = Utils.getFileFromPosition(edgePos2);
            return Math.abs(file2 - file1) == 1;
        }
        else if ((edgePos1 > 7 || edgePos1 == 0) && (edgePos2 > 7 || edgePos2 == 0)) {
            int rank1 = Utils.getRankFromPosition(edgePos1);
            int rank2 = Utils.getRankFromPosition(edgePos2);
            return Math.abs(rank2 - rank1) == 1;
        }

        edgePos1 = Utils.getNegativeEdgePositionFromPosition(position1);
        edgePos2 = Utils.getNegativeEdgePositionFromPosition(position2);
        if (edgePos1 < 8 && edgePos2 < 8) {
            int file1 = Utils.getFileFromPosition(edgePos1);
            int file2 = Utils.getFileFromPosition(edgePos2);
            return Math.abs(file2 - file1) == 1;
        }
        else if (edgePos1 > 6 && edgePos2 > 6) {
            int rank1 = Utils.getRankFromPosition(edgePos1);
            int rank2 = Utils.getRankFromPosition(edgePos2);
            return Math.abs(rank2 - rank1) == 1;
        }

        return false;
    }


    /**
     * Method used to get black move for pgn fen.
     * 
     * @param moveNumber
     *            move number
     * @param comment
     *            comment for move
     * @return fen for black part of turn
     */
    public static String blackMoveNumberToFenString(int moveNumber, String comment) {
        int from = Utils.getStartingPositionFromMoveNumber(moveNumber);
        int to = Utils.getTargetPositionFromMoveNumber(moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);

        String m = Utils.pieceToChar(movedPiece) + Utils.positionToString(from) + Utils.positionToString(to);

        return m + " { " + comment + " }";
    }


    /**
     * Converts black finder strategy to string.
     * 
     * @param strategy
     *            black finder strategy
     * @return string representation of black finder strategy
     */
    public static String blackStrategyToString(BlackFinderStrategy strategy) {
        String str = "izbiranje in simulacija: ";
        switch (strategy) {
            case RANDOM:
                str += "random";
                break;
            case CENTER:
                str += "tezi k centru ne glede na belega";
                break;
            case CUSTOM:
                str += "tezi k centru, ce je mozno poje belega";
                break;
            case PERFECT:
                str += "igra s popolno informacijo";
                break;
        }
        return str;
    }


    /**
     * Converts character to number corresponding to alphabet (ie. a-> 0).
     * 
     * @param ch
     *            character
     * @return characters number in alphabet, starting with 0
     */
    public static int charToIntFile(char ch) {
        return (byte) ch - 96;
    }


    /**
     * Construct move number.
     * 
     * @param from
     *            position from piece is moved
     * @param to
     *            position to which piece is moved
     * @param movedPiece
     *            piece that is moved
     * @param targetPiece
     *            piece on target position
     * @return move number
     */
    public static int constructMoveNumber(int from, int to, int movedPiece, int targetPiece) {
        int rez = targetPiece & 0xFF;
        rez |= (movedPiece & 0xFF) << 8;
        rez |= (to & 0xFF) << 16;
        rez |= (from & 0xFF) << 24;
        return rez;
    }


    /**
     * Constructs fen preable from set parameters and match statistics.
     * 
     * @param whiteStrategy
     *            white chooser strategy
     * @param blackStrategy
     *            black chooser strategy
     * @param c
     *            c constant
     * @param goban
     *            goban
     * @param didWhiteWin
     *            <code>true</code> if white won the match, <code>false</code>
     *            otherwise
     * @param whatRound
     *            which match was currently played
     * @param numberOfPliesMade
     *            number of turns made in match
     * @param numberOfInitialMCTSteps
     *            number of MCT steps before match
     * @param numberOfRunningMCTSteps
     *            number of MCT steps made before whites moves
     * @param numberOfSimulationsPerEvaluation
     *            number of simulation run per evaluation of a node
     * @return fen preamble
     */
    public static String constructPreamble(String whiteStrategy, String blackStrategy, double c, int goban, boolean didWhiteWin, int whatRound,
            int numberOfPliesMade, int numberOfInitialMCTSteps, int numberOfRunningMCTSteps, int numberOfSimulationsPerEvaluation) {
        String event = "[Event \"InitalMCTSteps: " + numberOfInitialMCTSteps + ", RunningMCTSteps: " + numberOfRunningMCTSteps
                + ", number of simulations per evaluation: " + numberOfSimulationsPerEvaluation + "\"]\n";
        String site = "[Site \"C = " + c + ", goban = " + goban + "\"]\n";
        String date = "[Date \"" + Utils.today() + "\"]\n";
        String round = "[Round \"" + whatRound + "\"]\n";
        String white = "[White \"" + whiteStrategy + "\"]\n";
        String black = "[Black \"" + blackStrategy + "\"]\n";
        String plyCount = "[PlyCount \"" + numberOfPliesMade + "\"]\n";
        String result = "[Result \"";
        if (didWhiteWin) {
            result += "1-0";
        }
        else {
            result += "0-1";
        }
        result += "\"]\n";
        return event + site + date + round + white + black + plyCount + result;
    }


    /**
     * Calculate distance between chess board positions.
     * 
     * @param positionA
     *            position
     * @param positionB
     *            position
     * @return distance between two positions
     */
    public static int distanceBetweenPositions(int positionA, int positionB) {
        int rankA = Utils.getRankFromPosition(positionA);
        int rankB = Utils.getRankFromPosition(positionB);
        int fileA = Utils.getFileFromPosition(positionA);
        int fileB = Utils.getFileFromPosition(positionB);

        int rankDiff = Math.abs(rankA - rankB);
        int fileDiff = Math.abs(fileA - fileB);
        return rankDiff + fileDiff;
    }


    /**
     * Gets file of position.
     * 
     * @param position
     *            position on board
     * @return file of selected position
     */
    public static int getFileFromPosition(int position) {
        return (position % 16) + 1;
    }


    /**
     * Gets piece that is moved from move number.
     * 
     * @param moveNumber
     *            move number
     * @return moved piece
     */
    public static int getMovedPieceFromMoveNumber(int moveNumber) {
        int rez = (moveNumber >>> 8) & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    /**
     * Projects selected position to edge position with negative diagonal
     * (negative diagonal travels from NW to SE). Returns gotten edge position.
     * 
     * @param position
     *            position to be projected
     * @return edge position
     */
    public static int getNegativeEdgePositionFromPosition(int position) {
        int multi1 = Utils.getRankFromPosition(position);
        int multi2 = 9 - Utils.getFileFromPosition(position);
        int multiplier = Math.min(multi1, multi2) - 1;
        int edgePosition = position - multiplier * 15;
        return edgePosition;
    }


    /**
     * Projects selected position to edge position with positive diagonal
     * (positive diagonal travels from SW to NE). Returns gotten edge position.
     * 
     * @param position
     *            position to be projected
     * @return edge position
     */
    public static int getPositiveEdgePositionFromPosition(int position) {
        int file = Utils.getFileFromPosition(position);
        int rank = Utils.getRankFromPosition(position);
        int multiplyer = Math.min(file, rank) - 1;
        int edgePosition = position - multiplyer * 17;
        return edgePosition;
    }


    /**
     * Gets rank of position.
     * 
     * @param position
     *            position
     * @return rank of selected position
     */
    public static int getRankFromPosition(int position) {
        return (position / 16) + 1;
    }


    /**
     * Gets starting position from move number.
     * 
     * @param moveNumber
     *            move number
     * @return starting position
     */
    public static int getStartingPositionFromMoveNumber(int moveNumber) {
        int rez = moveNumber >>> 24;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    /**
     * Get piece that is on target position from move number.
     * 
     * @param moveNumber
     *            move number
     * @return target piece
     */
    public static int getTargetPieceFromMoveNumber(int moveNumber) {
        int rez = moveNumber & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    /**
     * Gets target position from move number.
     * 
     * @param moveNumber
     *            move number
     * @return target position
     */
    public static int getTargetPositionFromMoveNumber(int moveNumber) {
        int rez = (moveNumber >>> 16);
        rez &= 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    /**
     * Converts list of integers to string, similar to pythons join metod.
     * 
     * @param list
     *            integer list
     * @return string representation of integer list
     */
    public static String intArrayListToString(ArrayList<Integer> list) {
        StringBuffer sb = new StringBuffer(50);
        for (int x = 0; x < list.size() - 1; x++) {
            sb.append(list.get(x) + ", ");
        }
        sb.append(list.get(list.size() - 1) + "");
        return sb.toString();
    }


    /**
     * Checks if it is whites turn on selected depth.
     * 
     * @param depth
     *            depth
     * @return <code>true</code> if it is whites turn, <code>false</code>
     *         otherwise
     */
    public static boolean isWhitesMoveAtDepth(int depth) {
        if (depth % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Calculates if its whites turn from node depth.
     * 
     * @param node
     *            node
     * @return <code>true</code> if it is whites turn, <code>false</code>
     *         otherwise
     */
    public static boolean isWhitesTurn(MCTNode node) {
        if (node.moveDepth % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Transforms piece to string. This method is used to print out chess board.
     * 
     * @param pieceNumber
     *            piece number
     * @return string representation of piece
     */
    public static String pieceNumberToString(int pieceNumber) {
        if (pieceNumber > 7 && pieceNumber < 16) { return "WP"; }
        if (pieceNumber > 15 && pieceNumber < 24) { return "BP"; }
        switch (pieceNumber) {
            case 0:
                return "WR";
            case 1:
                return "WN";
            case 2:
                return "WB";
            case 3:
                return "WQ";
            case 4:
                return "WK";
            case 5:
                return "WB";
            case 6:
                return "WN";
            case 7:
                return "WR";

            case 24:
                return "BR";
            case 25:
                return "BN";
            case 26:
                return "BB";
            case 27:
                return "BQ";
            case 28:
                return "BK";
            case 29:
                return "BB";
            case 30:
                return "BN";
            case 31:
                return "BR";

            default:
                return "XX";
        }
    }


    /**
     * Transforms piece nmber to cha representation (ie. 4 -> K).
     * 
     * @param piece
     *            piece number
     * @return char representation of piece
     * @throws UtilsException
     */
    public static String pieceToChar(int piece) {
        // white pieces
        if (piece == 0 || piece == 7) { return "R"; }
        if (piece == 1 || piece == 6) { return "N"; }
        if (piece == 2 || piece == 5) { return "B"; }
        if (piece == 3) { return "Q"; }
        if (piece == 4) { return "K"; }
        if (piece > 7 && piece < 16) { return ""; }

        // black pieces
        if (piece > 15 && piece < 24) { return ""; }
        if (piece == 24 || piece == 31) { return "R"; }
        if (piece == 25 || piece == 30) { return "N"; }
        if (piece == 26 || piece == 29) { return "B"; }
        if (piece == 27) { return "Q"; }
        if (piece == 28) { return "K"; }

        return null;
    }


    /**
     * Returns single letter that represents piece.
     * 
     * @param piece
     *            piece number
     * @return single letter piece representation
     */
    public static String pieceToCharFEN(int piece) {
        if (piece == 0 || piece == 7) { return "R"; }
        if (piece == 1 || piece == 6) { return "N"; }
        if (piece == 2 || piece == 5) { return "B"; }
        if (piece == 3) { return "Q"; }
        if (piece == 4) { return "K"; }
        if (piece > 7 && piece < 16) { return "P"; }

        if (piece > 15 && piece < 24) { return "p"; }
        if (piece == 24 || piece == 31) { return "r"; }
        if (piece == 25 || piece == 30) { return "n"; }
        if (piece == 26 || piece == 29) { return "b"; }
        if (piece == 27) { return "q"; }
        if (piece == 28) { return "k"; }

        return null;
    }


    /**
     * Calculates position from given rank and file.
     * 
     * @param file
     *            file
     * @param rank
     *            rank
     * @return position that is on given rank and file
     */
    public static int positionFromRankAndFile(int file, int rank) {
        return (rank - 1) * 16 + (file - 1);
    }


    /**
     * Gets numerical position from string position representation.
     * 
     * @param pos
     *            must be in format [file][rank] (ie. A1)
     * @return position on X88 format
     */
    public static int positionFromString(String pos) {
        int file = Utils.charToIntFile(pos.charAt(0));
        int rank = Integer.parseInt(pos.substring(1));
        return Utils.positionFromRankAndFile(file, rank);
    }


    /**
     * Transforms position to human readable format (ie. 0 -> A1).
     * 
     * @param position
     *            position desired position
     * @return human readable position representation
     * @throws UtilsException
     */
    public static String positionToString(int position) {

        int file = (byte) Utils.getFileFromPosition(position);
        int znak = 96 + file;
        char t = ((char) znak);

        int rank = Utils.getRankFromPosition(position);
        return t + "" + rank + "";
    }


    /**
     * Converts moveNumber to human readable format
     * 
     * @param moveNumber
     *            number we wish to convert to readable form
     * @return readable form of moveNumber ie. "Ke1-f2"
     */
    public static String singleMoveNumberToString(int moveNumber) {
        int from = Utils.getStartingPositionFromMoveNumber(moveNumber);
        int to = Utils.getTargetPositionFromMoveNumber(moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);
        return Utils.pieceToChar(movedPiece) + Utils.positionToString(from) + "-" + Utils.positionToString(to);
    }


    /**
     * Gets time in normal human readable format from milliseconds.
     * 
     * @param millis
     *            number of milliseconds
     * @return time in format "[hours]h[minutes]min[seconds]sec
     */
    public static String timeMillisToString(long millis) {
        millis /= 1000;
        long hours = millis / 3600;
        millis = millis - hours * 3600;
        long minutes = millis / 60;
        millis -= minutes * 60;
        return hours + "h " + minutes + "min " + millis + "sec";
    }


    /**
     * Gets todays date.
     * 
     * @return todays date
     */
    public static String today() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    /**
     * Converts mover number to string. This method is used for fen notation.
     * 
     * @param moveNumber
     *            move number
     * @param depth
     *            which consequential move is this in match.
     * @return string representation of move number
     */
    public static String whiteMoveNumberToFenString(int moveNumber, int depth, String comment) {
        int from = Utils.getStartingPositionFromMoveNumber(moveNumber);
        int to = Utils.getTargetPositionFromMoveNumber(moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);

        String m = Utils.pieceToChar(movedPiece) + Utils.positionToString(from) + Utils.positionToString(to);

        return depth + ". " + m + "{ " + comment + " }";

    }


    /**
     * Converts white chooser strategy to string.
     * 
     * @param chooserStrat
     *            white chooser strategy
     * @return string representation of white chooser strategy
     */
    public static String whiteStrategyToString(WhiteChooserStrategy chooserStrat) {
        String chooser = "izbiranje: ";
        switch (chooserStrat) {
            case RANDOM:
                chooser += "random";
                break;
            case MAX_VISIT_COUNT:
                chooser += "max Visit Count";
                break;
            case MAX_UCT:
                chooser += "max Rating";
                break;
        }
        return chooser;
    }


    private Utils() {}

}
