package chess;

import utils.Utils;

/**
 * Represent a ply. Ply is uniquely identifiable by its ply number.
 * 
 * @author Andraz Kohne
 */
public class Move {

    /**
     * Ply number.
     */
    public int moveNumber;


    /**
     * Creates Ply with desired ply number.
     * 
     * @param plyNumber
     *            ply number
     */
    public Move(int plyNumber) {
        this.moveNumber = plyNumber;
    }


    /**
     * Return string representation of current instance.
     * 
     * @return plys string representation
     */
    @Override
    public String toString() {
        int from = Utils.getStartingPositionFromMoveNumber(this.moveNumber);
        int to = Utils.getTargetPositionFromMoveNumber(this.moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(this.moveNumber);
        int targetPiece = Utils.getTargetPieceFromMoveNumber(this.moveNumber);

        return "move: " + Utils.singleMoveNumberToString(this.moveNumber) + "\tfrom: " + from + "\tto: " + to + "\tmovedPiece: " + movedPiece
                + "\ttargetPiece: " + targetPiece;
    }


    /**
     * Checks if two plies are same.
     * 
     * @return <code>true</code> if current instance is same as m1,
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object m1) {

        if (m1 instanceof Move) {
            return this.moveNumber == ((Move) m1).moveNumber;
        }
        else {
            return false;
        }
    }


    /**
     * Gets hash (which is ply number) code of current instance
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        return this.moveNumber;
    }

}
