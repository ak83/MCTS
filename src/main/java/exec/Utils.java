package exec;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import moveChoosers.WhiteChooserStrategy;
import moveFinders.BlackFinderStrategy;

public class Utils {

    private Utils() {}


    /**
     * Checks if position is on board or not.
     * 
     * @param a
     *            position
     * @return <code>true</code> if position is on board, otherwise
     *         <code>false</code>
     */
    public static boolean isPositionLegal(int a) {
        if ((a & 0x88) != 0)
            return false;
        else
            return true;
    }


    // from,to,movedPiece,tagetPiece
    public static int constructMoveNumber(int from, int to, int movedPiece,
            int targetPiece) {
        int rez = targetPiece & 0xFF;
        rez |= (movedPiece & 0xFF) << 8;
        rez |= (to & 0xFF) << 16;
        rez |= (from & 0xFF) << 24;
        return rez;
    }


    /**
     * Checks if piece is white or black.
     * 
     * @param pieceNumber
     *            piece number
     * @return <code>true</code> if piece is white, <code>false</code> otherwise
     * @throws UtilsException
     */
    public static boolean isPieceWhite(int pieceNumber) {
        // ce vrne true, je figure bela, drugace je crna
        if (pieceNumber >= 0 && pieceNumber < 16)
            return true;
        else
            return false;
    }


    /**
     * Transforms piece to string. This method is used to print out chessboard.
     * 
     * @param pieceNumber
     *            piece number
     * @return string representation of piece
     */
    public static String pieceNumberToString(int pieceNumber) {
        if (pieceNumber > 7 && pieceNumber < 16)
            return "WP";
        if (pieceNumber > 15 && pieceNumber < 24)
            return "BP";
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


    public static int getFromFromMove(Ply a) {
        int rez = a.plyNumber >>> 24;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getFromFromMoveNumber(int moveNumber) {
        int rez = moveNumber >>> 24;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getToFromMove(Ply a) {
        int rez = (a.plyNumber >>> 16) & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getToFromMoveNumber(int moveNumber) {
        int rez = (moveNumber >>> 16);
        rez &= 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getMovedPieceFromMove(Ply a) {
        int rez = (a.plyNumber >>> 8) & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getMovedPieceFromMoveNumber(int moveNumber) {
        int rez = (moveNumber >>> 8) & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getTargetPieceFromMove(Ply a) {
        return Utils.getTargetPieceFromMoveNumber(a.plyNumber);
    }


    public static int getTargetPieceFromMoveNumber(int moveNumber) {
        int rez = moveNumber & 0xFF;

        if (rez == 255) {
            return -1;
        }
        else {
            return rez;
        }
    }


    public static int getRankFromPosition(int position) {
        return (position / 16) + 1;
    }


    public static int getFileFromPosition(int position) {
        return (position % 16) + 1;
    }


    public static boolean isWhitesTurn(MCTNode node) {
        if (node.plyDepth % 2 == 0) {
            return true;
        }
        else
            return false;
    }


    // ni stestirana
    public static boolean isMoveInNextMovesOfNode(int moveNumber, MCTNode node) {
        for (int x = 0; x < node.nextMoves.size(); x++) {
            if (moveNumber == node.nextMoves.get(x).moveNumber) { return true; }
        }
        return false;
    }


    // ni stestirana
    public static int indexOfMoveInNextMovesInNode(Ply move, MCTNode node) {
        int rez = -1;

        for (int x = 0; x < node.nextMoves.size(); x++) {
            if (move.plyNumber == node.nextMoves.get(x).moveNumber) {
                rez = x;
            }
        }

        return rez;
    }


    // ni steastirana
    public static int indexOfMoveNumberInNextMoves(int movenumber, MCTNode node) {
        for (int x = 0; x < node.nextMoves.size(); x++) {
            if (movenumber == node.nextMoves.get(x).moveNumber) { return x; }
        }

        return -1;
    }


    public static void printIntegerArrayList(ArrayList<Integer> list) {
        for (int x = 0; x < list.size() - 1; x++) {
            System.out.print(list.get(x) + ", ");
        }
        System.out.println(list.get(list.size() - 1));
    }


    public static void printMoveNmber(int moveNumber) {
        System.out.print("moveNumber: " + moveNumber);
        System.out.print("\tfrom: " + Utils.getFromFromMoveNumber(moveNumber));
        System.out.print("\tto: " + Utils.getToFromMoveNumber(moveNumber));
        System.out.print("\tmovedPiece: "
                + Utils.getMovedPieceFromMoveNumber(moveNumber));
        System.out.println("\ttargetPiece: "
                + Utils.getTargetPieceFromMoveNumber(moveNumber));
    }


    public static String moveNumberToString(int moveNumber) {
        String rez = "";
        rez += "moveNumber: " + moveNumber;
        rez += "\tfrom: " + Utils.getFromFromMoveNumber(moveNumber);
        rez += "\tto: " + Utils.getToFromMoveNumber(moveNumber);
        rez += "\tmovedPiece: " + Utils.getMovedPieceFromMoveNumber(moveNumber);
        rez += "\ttargetPiece: "
                + Utils.getTargetPieceFromMoveNumber(moveNumber);
        return rez;
    }


    public static void readLn() {
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }


    public static void delay(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void printIntArray(int[] array) {
        System.out.print("Mesto: vrednost.\t");
        for (int x = 0; x < array.length; x++) {
            System.out.print(x + ": " + array[x] + ", ");
        }
        System.out.println();
    }


    public static void printBoardArray(int[] array) {
        System.out.print("Mesto: vrednost.\t");
        for (int x = 0; x < array.length; x++) {
            if (Utils.isPositionLegal(x)) {
                System.out.print(x + ": " + array[x] + ", ");
            }
        }
        System.out.println();
    }


    public static void printMoveNumbersArray(ArrayList<Ply> array) {
        System.out.print("printMovesArray: ");
        for (int x = 0; x < array.size(); x++) {
            int temp = array.get(x).plyNumber;
            int from = Utils.getFromFromMoveNumber(temp);
            int to = Utils.getToFromMoveNumber(temp);

            System.out.print("from: " + from + " to: " + to + ", ");
        }
        System.out.println();
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
     * Transforms piece nmber to cha representation (ie. 4 -> K).
     * 
     * @param piece
     *            piece number
     * @return char representation of piece
     * @throws UtilsException
     */
    public static String pieceToChar(int piece) {
        // white pieces
        if (piece == 0 || piece == 7)
            return "R";
        if (piece == 1 || piece == 6)
            return "N";
        if (piece == 2 || piece == 5)
            return "B";
        if (piece == 3)
            return "Q";
        if (piece == 4)
            return "K";
        if (piece > 7 && piece < 16)
            return "";

        // black pieces
        if (piece > 15 && piece < 24)
            return "";
        if (piece == 24 || piece == 31)
            return "R";
        if (piece == 25 || piece == 30)
            return "N";
        if (piece == 26 || piece == 29)
            return "B";
        if (piece == 27)
            return "Q";
        if (piece == 28)
            return "K";

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
        if (piece == 0 || piece == 7)
            return "R";
        if (piece == 1 || piece == 6)
            return "N";
        if (piece == 2 || piece == 5)
            return "B";
        if (piece == 3)
            return "Q";
        if (piece == 4)
            return "K";
        if (piece > 7 && piece < 16)
            return "P";

        if (piece > 15 && piece < 24)
            return "p";
        if (piece == 24 || piece == 31)
            return "r";
        if (piece == 25 || piece == 30)
            return "n";
        if (piece == 26 || piece == 29)
            return "b";
        if (piece == 27)
            return "q";
        if (piece == 28)
            return "k";

        return null;
    }


    public static String moveNumberToString(int moveNumber1, int moveNumber2,
            int depth) {
        int from1 = Utils.getFromFromMoveNumber(moveNumber1);
        int to1 = Utils.getToFromMoveNumber(moveNumber1);
        int movedPiece1 = Utils.getMovedPieceFromMoveNumber(moveNumber1);
        String m1 = Utils.pieceToChar(movedPiece1)
                + Utils.positionToString(from1) + Utils.positionToString(to1);

        int from2 = Utils.getFromFromMoveNumber(moveNumber2);
        int to2 = Utils.getToFromMoveNumber(moveNumber2);
        int movedPiece2 = Utils.getMovedPieceFromMoveNumber(moveNumber2);
        String m2 = Utils.pieceToChar(movedPiece2)
                + Utils.positionToString(from2) + Utils.positionToString(to2);

        return depth + ". " + m1 + " " + m2;

    }


    /**
     * Converts moveNumber to human readable format
     * 
     * @param moveNumber
     *            number we wish to convert to readable form
     * @return readable form of moveNumber eg. "Ke1f2"
     * @throws UtilsException
     */
    public static String singleMoveNumberToString(int moveNumber) {
        int from = Utils.getFromFromMoveNumber(moveNumber);
        int to = Utils.getToFromMoveNumber(moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);
        return Utils.pieceToChar(movedPiece) + Utils.positionToString(from)
                + "-" + Utils.positionToString(to);
    }


    /**
     * Converts mover number to string. This method is used for fen notation.
     * 
     * @param plyNumber
     *            ply number
     * @param depth
     *            which consequential move is this in match.
     * @return string of ply
     */
    public static String moveNumberToString(int plyNumber, int depth) {
        int from = Utils.getFromFromMoveNumber(plyNumber);
        int to = Utils.getToFromMoveNumber(plyNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(plyNumber);

        String m = Utils.pieceToChar(movedPiece) + Utils.positionToString(from)
                + Utils.positionToString(to);

        return depth + ". " + m;

    }


    public static boolean isWhitesMoveAtDepth(int depth) {
        if (depth % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }


    public static boolean areIntArraysEqual(int[] a, int[] b) {
        if (a.length != b.length) { return false; }

        for (int x = 0; x < a.length; x++) {
            if (a[x] != b[x]) { return false; }
        }

        return true;
    }


    public static int distanceBetweenPositions(int positionA, int positionB) {
        int rankA = Utils.getRankFromPosition(positionA);
        int rankB = Utils.getRankFromPosition(positionB);
        int fileA = Utils.getFileFromPosition(positionA);
        int fileB = Utils.getFileFromPosition(positionB);

        int rankDiff = Math.abs(rankA - rankB);
        int fileDiff = Math.abs(fileA - fileB);
        return rankDiff + fileDiff;
    }


    public static String today() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    public static String constructPreamble(String whiteStrategy,
            String blackStrategy, double c, int goban, boolean didWhiteWin,
            int whatRound, int numberOfMovesMade, int numberOfInitialMCTSteps,
            int numberOfRunningMCTSteps, int numberOfSimulationsPerEvaluation) {
        String event = "[Event \"InitalMCTSteps: " + numberOfInitialMCTSteps
                + ", RunningMCTSteps: " + numberOfRunningMCTSteps
                + ", number of simulations per evaluation: "
                + numberOfSimulationsPerEvaluation + "\"]\n";
        String site = "[Site \"C = " + c + ", goban = " + goban + "\"]\n";
        String date = "[Date \"" + Utils.today() + "\"]\n";
        String round = "[Round \"" + whatRound + "\"]\n";
        String white = "[White \"" + whiteStrategy + "\"]\n";
        String black = "[Black \"" + blackStrategy + "\"]\n";
        String plyCount = "[PlyCount \"" + numberOfMovesMade + "\"]\n";
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


    public static String timeMillisToString(long millis) {
        millis /= 1000;
        long hours = millis / 3600;
        millis = millis - hours * 3600;
        long minutes = millis / 60;
        millis -= minutes * 60;
        return hours + "h " + minutes + "min " + millis + "sec";
    }


    public static ArrayList<Ply> mergeMoveArrayLists(ArrayList<Ply> list1,
            ArrayList<Ply> list2) {
        ArrayList<Ply> rez = new ArrayList<Ply>();
        for (int x = 0; x < list1.size(); x++) {
            rez.add(list1.get(x));
        }
        for (int x = 0; x < list2.size(); x++) {
            rez.add(list2.get(x));
        }
        return rez;
    }


    public static boolean arePositionsAdjacent(int positionA, int positionB) {
        if (!Utils.isPositionLegal(positionA)
                || !Utils.isPositionLegal(positionB)) { return false; }

        int diff = Math.abs(positionB - positionA);
        if (diff == 1 || diff == 15 || diff == 16 || diff == 17) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
     * pos ima chara prvi predstavlja file, drugi pa rank
     */
    public static int positionFromString(String pos) {
        int file = Utils.charToIntFile(pos.charAt(0));
        int rank = Integer.parseInt(pos.substring(1));
        return Utils.positionFromRankAndFile(file, rank);
    }


    public static int charToIntFile(char ch) {
        return (byte) ch - 96;
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


    public static String intArrayListToString(ArrayList<Integer> list) {
        StringBuffer sb = new StringBuffer(50);
        for (int x = 0; x < list.size() - 1; x++) {
            sb.append(list.get(x) + ", ");
        }
        sb.append(list.get(list.size() - 1) + "");
        return sb.toString();
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
            case VISIT_COUNT:
                chooser += "max Visit Count";
                break;
            case RATING:
                chooser += "max Rating";
                break;
        }
        return chooser;
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
            case GOOD:
                str += "tezi k centru, ce je mozno poje belega";
                break;
            case PERFECT:
                str += "igra s popolno informacijo";
                break;
        }
        return str;
    }


    public static ArrayList<Ply> copyMoveArrayList(ArrayList<Ply> copy) {
        ArrayList<Ply> rez = new ArrayList<Ply>();
        for (Ply currMove : copy) {
            rez.add(new Ply(currMove.plyNumber));
        }
        return rez;
    }


    public static int getPositiveEdgePositionFromPosition(int position) {
        int file = Utils.getFileFromPosition(position);
        int rank = Utils.getRankFromPosition(position);
        int multiplyer = Math.min(file, rank) - 1;
        int edgePosition = position - multiplyer * 17;
        return edgePosition;
    }


    public static int getNegativeEdgePositionFromPosition(int position) {
        int multi1 = Utils.getRankFromPosition(position);
        int multi2 = 9 - Utils.getFileFromPosition(position);
        int multiplier = Math.min(multi1, multi2) - 1;
        int edgePosition = position - multiplier * 15;
        return edgePosition;
    }


    public static boolean arePsotionsDiagonallyAdjacent(int position1,
            int position2) {
        int edgePos1 = Utils.getPositiveEdgePositionFromPosition(position1);
        int edgePos2 = Utils.getPositiveEdgePositionFromPosition(position2);
        if (edgePos1 < 8 && edgePos2 < 8) {
            int file1 = Utils.getFileFromPosition(edgePos1);
            int file2 = Utils.getFileFromPosition(edgePos2);
            return Math.abs(file2 - file1) == 1;
        }
        else if ((edgePos1 > 7 || edgePos1 == 0)
                && (edgePos2 > 7 || edgePos2 == 0)) {
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

}
