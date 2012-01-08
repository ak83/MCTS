package exec;

public class Move {

    public int moveNumber;


    public Move(int moveNumber) {
        this.moveNumber = moveNumber;
    }


    public Move(int from, int to, int movedPiece, int targetPiece) {
        this.moveNumber = Utils.constructMoveNumber(from, to, movedPiece,
                targetPiece);
    }


    @Override
    public String toString() {
        int from = Utils.getFromFromMoveNumber(this.moveNumber);
        int to = Utils.getToFromMoveNumber(this.moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(this.moveNumber);
        int targetPiece = Utils.getTargetPieceFromMoveNumber(this.moveNumber);

        return "move: " + Utils.singleMoveNumberToString(this.moveNumber)
                + "\tfrom: " + from + "\tto: " + to + "\tmovedPiece: "
                + movedPiece + "\ttargetPiece: " + targetPiece;
    }


    @Override
    public boolean equals(Object m1) {

        if (m1 instanceof Move) {
            return this.moveNumber == ((Move) m1).moveNumber;
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.moveNumber;
    }


    /**
     * @return position to which movedPiece will be moved.
     */
    public int getTo() {
        return Utils.getToFromMoveNumber(this.moveNumber);
    }


    /**
     * @return position on which movedPiece is standing.
     */
    public int getFrom() {
        return Utils.getFromFromMoveNumber(this.moveNumber);
    }


    /**
     * @return movedPiece of this move.
     */
    public int getMovedPiece() {
        return Utils.getMovedPieceFromMoveNumber(this.moveNumber);
    }


    /**
     * @return piece that get eaten by movedPiece or -1 if position to which
     *         movedPiece moves is empty.
     */
    public int getTargetPiece() {
        return Utils.getTargetPieceFromMoveNumber(this.moveNumber);
    }

}
