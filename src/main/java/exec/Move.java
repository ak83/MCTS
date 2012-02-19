package exec;

/**
 * Represent a ply. Ply is uniquely identifiable by its ply number.
 * 
 * @author Andraz Kohne
 */
public class Move {

    /**
     * Ply number.
     */
    public int plyNumber;


    /**
     * Creates Ply with desired ply number.
     * 
     * @param plyNumber
     *            ply number
     */
    public Move(int plyNumber) {
        this.plyNumber = plyNumber;
    }


    /**
     * Return string representation of current instance.
     * 
     * @return plys string representation
     */
    @Override
    public String toString() {
        int from = Utils.getFromFromMoveNumber(this.plyNumber);
        int to = Utils.getToFromMoveNumber(this.plyNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(this.plyNumber);
        int targetPiece = Utils.getTargetPieceFromMoveNumber(this.plyNumber);

        return "move: " + Utils.singleMoveNumberToString(this.plyNumber)
                + "\tfrom: " + from + "\tto: " + to + "\tmovedPiece: "
                + movedPiece + "\ttargetPiece: " + targetPiece;
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
            return this.plyNumber == ((Move) m1).plyNumber;
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
        return this.plyNumber;
    }

}
