package chessboard;

import exec.Piece;
import exec.Utils;

/**
 * Class that hold utils methods for Chessboard (some older methods are in
 * exec.Utils.java).
 * 
 * @author Andraz
 */
public class ChessboardUtils {

    // only static methods allowed
    private ChessboardUtils() {}


    /**
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
     * Checks if one position is near some other position
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


    /**
     * Converts number that represents piece to Piece. It doesn't warn if
     * pieceNumber is invalid.
     * 
     * @param pieceNumber
     * @return Piece
     */
    public static Piece pieceNumberToPiece(int pieceNumber) {
        switch (pieceNumber) {
            case 0:
            case 7:
                return Piece.WHITE_ROOK;
            case 1:
            case 6:
                return Piece.WHITE_KNIGHT;
            case 2:
            case 5:
                return Piece.WHITE_BISHOP;
            case 3:
                return Piece.WHITE_QUEEN;
            case 4:
                return Piece.WHITE_KING;
            case 24:
            case 31:
                return Piece.BLACK_ROOK;
            case 25:
            case 30:
                return Piece.BLACK_KNIGHT;
            case 26:
            case 29:
                return Piece.BLACK_BISHOP;
            case 27:
                return Piece.BLACK_QUEEN;
            case 28:
                return Piece.BLACK_KING;
            default: {
                if (pieceNumber < 16) {
                    return Piece.WHITE_PAWN;
                }
                else {
                    return Piece.BLACK_PAWN;
                }
            }
        }
    }
}
