package exec;

import java.util.Comparator;

public class Move {

    public int moveNumber;


    public Move(int moveNumber) {
	this.moveNumber = moveNumber;
    }


    public String toString() {
	int from = Utils.getFromFromMoveNumber(moveNumber);
	int to = Utils.getToFromMoveNumber(moveNumber);
	int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);
	int targetPiece = Utils.getTargetPieceFromMoveNumber(moveNumber);

	return "from: " + from + "\tto: " + to + "\tmovedPiece: " + movedPiece
		+ "\ttargetPiece: " + targetPiece;
    }


    public boolean equals(Object m1) {
        
        if (m1 instanceof Move) { return this.moveNumber == ((Move) m1).moveNumber;
        } else {
            return m1.equals(m1);
        }
    }

}
