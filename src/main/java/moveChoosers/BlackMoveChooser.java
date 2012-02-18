package moveChoosers;

import java.util.logging.Logger;

import chessboard.Chessboard;

import moveFinders.BlackMoveFinder;
import moveFinders.BlackMoveFinderStrategy;

import exceptions.ChessboardException;
import exec.Utils;

public class BlackMoveChooser {

    private static Logger log = Logger.getLogger("MCTS.BlackMoveChooser");


    private BlackMoveChooser() {}


    /**
     * Find a move number based on strategy.
     * 
     * @param board
     *            board on which we look for move number
     * @param strategy
     *            Black king strategy
     * @return selected move number
     * @throws ChessboardException
     */
    public static int chooseBlackKingMove(Chessboard board,
            BlackMoveFinderStrategy strategy) throws ChessboardException
            {
        int rez = BlackMoveFinder.findBlackKingMove(board, strategy);
        BlackMoveChooser.log.fine("V polpotezi "
                + (board.getNumberOfPliesMade() + 1)
                + " je èrni izbral potezo "
                + Utils.singleMoveNumberToString(rez).toLowerCase());
        return rez;
    }

}
