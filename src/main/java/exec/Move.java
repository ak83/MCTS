package exec;

public class Move {

    public int moveNumber;


    public Move(int moveNumber) {
	this.moveNumber = moveNumber;
    }


    @Override
    public String toString() {
	int from = Utils.getFromFromMoveNumber(this.moveNumber);
	int to = Utils.getToFromMoveNumber(this.moveNumber);
	int movedPiece = Utils.getMovedPieceFromMoveNumber(this.moveNumber);
	int targetPiece = Utils.getTargetPieceFromMoveNumber(this.moveNumber);

	return "from: " + from + "\tto: " + to + "\tmovedPiece: " + movedPiece
		+ "\ttargetPiece: " + targetPiece;
    }


    @Override
    public boolean equals(Object m1) {
        
        if (m1 instanceof Move) { return this.moveNumber == ((Move) m1).moveNumber;
        } else {
            return m1.equals(m1);
        }
    }

}
