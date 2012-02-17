package moveChoosers;

import java.util.logging.Logger;

import chessboard.Chessboard;

import moveFinders.BlackMoveFinder;
import moveFinders.BlackMoveFinderStrategy;

import exceptions.BlackMoveFinderException;
import exceptions.ChessboardException;
import exec.Utils;

public class BlackMoveChooser {

    private Logger log = Logger.getLogger("MCTS.BlackMoveChooser");


    public BlackMoveChooser() {
        BlackMoveFinder.initRandom();
    }


    public BlackMoveChooser(long randomSeed) {
        BlackMoveFinder.initRandom(randomSeed);
    }


    /**
     * @param board
     *            plosca na kateri iscemo potezo
     * @param strategy
     *            0 - random strategija, 1 - kralj tezi k centru
     * @return stevilko izbrane poteze
     * @throws ChessboardException
     * @throws BlackMoveFinderException
     */
    public int chooseBlackKingMove(Chessboard board,
            BlackMoveFinderStrategy strategy) throws ChessboardException,
            BlackMoveFinderException {
        int rez = BlackMoveFinder.findBlackKingMove(board, strategy);
        this.log.fine("V polpotezi " + (board.getNumberOfMovesMade() + 1)
                + " je èrni izbral potezo "
                + Utils.singleMoveNumberToString(rez).toLowerCase());
        return rez;
    }

}
