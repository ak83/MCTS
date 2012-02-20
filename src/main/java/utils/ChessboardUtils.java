package utils;

/**
 * Class that hold util methods for Chessboard (some older methods are in
 * exec.Utils.java).
 * 
 * @author Andraz
 */
public class ChessboardUtils {

    /**
     * Check if <code>positionX</code> is between <code>positionA</code> and
     * <code>positionB</code>.
     * 
     * @param positionX
     *            position that we want to check if it's between other positions
     * @param positionA
     *            other position
     * @param positionB
     *            other position
     * @return <code>true</code> if positionX between other position and all
     *         position are on same line, otherwise <code>false</code>
     */
    public static boolean isPositionBetweenPositionsOnLine(int positionX,
            int positionA, int positionB) {

        // if positionX is not between other positions, it can't be between
        // them. Duh...
        boolean greaterThanBoth = (positionX > positionA)
                && (positionX > positionB);
        boolean lowertThanBoth = (positionX < positionA)
                && (positionX < positionB);

        if (greaterThanBoth || lowertThanBoth) { return false; }

        // all positions must be on same line
        boolean sameRank = (Utils.getRankFromPosition(positionX) == Utils
                .getRankFromPosition(positionA))
                && (Utils.getRankFromPosition(positionX) == Utils
                        .getRankFromPosition(positionB));
        boolean sameFile = (Utils.getFileFromPosition(positionX) == Utils
                .getFileFromPosition(positionA))
                && (Utils.getFileFromPosition(positionX) == Utils
                        .getFileFromPosition(positionB));

        if (sameFile || sameRank) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Checks if one position is adjacent some other position
     * 
     * @param positionA
     *            position on chess board
     * @param positionB
     *            position on chess board
     * @return true if positions are adjacent, otherwise false
     */
    public static boolean arePositionsAdjacent(int positionA, int positionB) {
        int diff = Math.abs(positionB - positionA);

        if (diff == 1 || diff == 15 || diff == 16 || diff == 17) {
            return true;
        }
        else {
            return false;
        }
    }


    // only static methods allowed
    private ChessboardUtils() {}
}
