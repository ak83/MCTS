package moveChoosers;

import java.util.logging.Logger;

import chessboard.Chessboard;

import moveFinders.BlackMoveFinder;
import moveFinders.BlackMoveFinderStrategy;

import exceptions.ChessboardException;
import exec.Utils;

/**
 * Class that handles blacks choosing of moves.
 * 
 * @author Andraz
 */
public class BlackMoveChooser {

    /** Logger */
    private static Logger log = Logger.getLogger("MCTS.BlackMoveChooser");


    /** This is static class. */
    private BlackMoveChooser() {}


    /**
     * Finds a move number based on strategy.
     * 
     * @param board
     *            board on which we look for move number
     * @param strategy
     *            Black king strategy
     * @return selected move number
     * @throws ChessboardException
     */
    public static int chooseBlackKingMove(Chessboard board,
            BlackMoveFinderStrategy strategy) throws ChessboardException {
        int rez = BlackMoveFinder.findBlackKingMove(board, strategy);
        BlackMoveChooser.log.fine("V polpotezi "
                + (board.getNumberOfPliesMade() + 1)
                + " je �rni izbral potezo "
                + Utils.singleMoveNumberToString(rez).toLowerCase());
        return rez;
    }

}
