package chess.chessgame;

import java.io.IOException;
import java.util.logging.Logger;

import mct.MCT;
import mct.MCTStats;
import utils.FruitUtils;
import utils.IOUtils;
import utils.Utils;
import chess.chessboard.ChessboardEvalState;
import exceptions.ChessboardException;
import exec.Constants;

/**
 * Class that handles playing a match logic.
 * 
 * @author Andraz Kohne
 */
public class ChessGame {

    /**
     * Which turn currently is.
     */
    // private int turnDepth = 1;

    /**
     * This matches fen.
     */
    private String              fen        = "";

    /**
     * File path where this match will be saved if sacing invidiual games is
     * enabled.
     */
    private String              pgnFileName;

    /** MC tree */
    private MCT                 MCTree     = new MCT();

    /** Logger */
    private Logger              log        = Logger.getLogger("MCTS.ChessGame");

    /** For keeping this matches statistics */
    private ChessGameStatistics matchStats = new ChessGameStatistics();


    /**
     * Constructor
     * 
     * @param outputFilename
     *            file name to where individual game will be saved
     */
    public ChessGame(String outputFilename) {
        this.pgnFileName = outputFilename;
    }


    /**
     * Plays a match.
     * 
     * @param round
     *            which which round is played, needed for logging purposes
     * @return matches fen
     * @throws ChessboardException
     * @throws IOException
     */
    public ChessGameResults playGame(int round) throws ChessboardException {

        this.log.info("\r\n*************************\r\nZACETEK NOVE IGRE\r\n*************************\r\n");

        long startTime = System.currentTimeMillis();
        this.fen += "[fen \"" + this.MCTree.getFEN() + "\"]\n\n";
        boolean didWhiteWin = false;

        for (int x = 0; x < Constants.NUMBER_OF_INITAL_STEPS; x++) {
            this.MCTree.oneMCTStep();
        }

        boolean whitesTurn = true;

        int turnDepth = 1;
        while (true) {

            this.log.fine("Stanje sahovnice je:\r\n"
                    + this.MCTree.getMainChessboard()
                    + "To stanje se je pojavilo "
                    + this.MCTree.getMainChessboard()
                            .howManyTimeHasCurrentStateAppeared()
                    + "-krat.\r\n");

            ChessboardEvalState eval = this.MCTree
                    .evaluateMainChessBoardState();

            // has match ended
            if (eval != ChessboardEvalState.NORMAl) {
                // whites victory
                if (eval == ChessboardEvalState.BLACK_KING_MATED) {
                    didWhiteWin = true;

                }
                // blacks victory
                else {
                    didWhiteWin = false;
                }

                this.fen += "{average white DTM diff = "
                        + this.matchStats.getAverageWhitesDTMDiff()
                        + ", average black DTM diff = "
                        + this.matchStats.getAverageBlacksDTMDiff() + " }";

                break;
            }

            // get best move and best move DTM from fruit
            String fruitOutput = FruitUtils.getOutputFromFruit(this.MCTree
                    .getFEN());
            String perfectMove = FruitUtils.getMoveFromFruit(fruitOutput);
            int perfectDTM = FruitUtils.getDTMOfMoveFromFruitOutput(
                    perfectMove, fruitOutput);

            int moveNumber = -1;

            // DTM difference of players move from optimal move
            int dtmDiff = -1;

            if (whitesTurn) {

                for (int x = 0; x < Constants.NUMBER_OF_RUNNING_STEPS; x++) {
                    this.MCTree.oneMCTStep();
                }

                moveNumber = this.MCTree.chooseAMoveNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);

                dtmDiff = FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils
                        .moveNumberToFruitString(moveNumber), fruitOutput)
                        - perfectDTM;

                this.matchStats.whitesDiffFromOptimal.put(turnDepth, dtmDiff);

                this.fen += Utils.whiteMoveNumberToFenString(moveNumber,
                        turnDepth, perfectMove + ", diff=" + dtmDiff)
                        + " ";
            }
            else {
                moveNumber = this.MCTree.chooseAMoveNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);

                dtmDiff = perfectDTM
                        - FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils
                                .moveNumberToFruitString(moveNumber),
                                fruitOutput);

                this.matchStats.blacksDiffFromOptimal.put(turnDepth, dtmDiff);

                this.fen += Utils.blackMoveNumberToFenString(moveNumber,
                        perfectMove + ", diff=" + dtmDiff);

                turnDepth++;
            }

            whitesTurn = !whitesTurn;

            this.log.fine("Tree size is " + this.MCTree.getCurrentTreeSize()
                    + "\r\n" + "Optimal move that could have been done is "
                    + perfectMove + ", DTM difference from players move is "
                    + dtmDiff);

            // update statistics
            this.matchStats.treeSize.put(turnDepth, this.MCTree
                    .getCurrentTreeSize());
            this.matchStats.numberOfPliesMade++;

            this.MCTree.makeMCMove(moveNumber);

        }

        if (!didWhiteWin) {
            turnDepth--;
        }

        this.logGameSummary(round, startTime, didWhiteWin, turnDepth);

        String whiteStrat = Utils
                .whiteStrategyToString(Constants.WHITE_MOVE_CHOOSER_STRATEGY);

        String blackStrat = Utils
                .blackStrategyToString(Constants.BLACK_MOVE_CHOOSER_STRATEGY);

        // calculate number of plies made
        int plyCount = didWhiteWin ? (turnDepth * 2 - 1) : (turnDepth * 2);

        // construct, write and return this matches pgn
        String preamble = Utils.constructPreamble(whiteStrat, blackStrat,
                Constants.C, Constants.GOBAN, didWhiteWin, round, plyCount,
                Constants.NUMBER_OF_INITAL_STEPS,
                Constants.NUMBER_OF_RUNNING_STEPS,
                Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION);
        this.fen = preamble + this.fen;
        if (Constants.WRITE_INDIVIDUAL_GAMES) {
            IOUtils.writeToFile(this.pgnFileName, this.fen);
        }

        return new ChessGameResults(this.fen + "\n\n", this.matchStats);
    }


    /**
     * Logs game summary.
     * 
     * @param round
     *            which consecutive game match was played
     * @param startTime
     *            when did match start in milliseconds
     * @param didWhiteWin
     *            <code>true</code> if white won the match, <code>false</code>
     *            otherwise
     */
    private void logGameSummary(int round, long startTime, boolean didWhiteWin,
            int turnsMade) {
        long runTime = System.currentTimeMillis() - startTime;
        MCTStats stats = this.MCTree.getMCTStatistics();
        String logString0 = "\r\n##########################\r\n###########################\r\nPOVZETEK igre "
                + this.pgnFileName
                + ". Igra je bila odigrana v "
                + Utils.timeMillisToString(runTime);
        if (didWhiteWin) {
            logString0 += "\r\nZmagal je BELI v " + turnsMade + " potezah";
        }
        else {
            logString0 += "\r\nZmagal je CRNI v " + turnsMade + " potezah";
        }
        String logString1 = "V igri je bilo:";
        String logString2 = stats.numberOfMatsInSimAddsOneNode
                + " matov v simulationAddsOneNode.";

        String logString3 = stats.numberOfMatsInSimulation
                + " matov v simulation";

        String logString4 = "Crni je " + stats.numberOfMCTreeColapses
                + "-krat izbral potezo, ki je ni v drevesu";
        logString4 += ".\r\n Pred koncem igre pa je bila velikost drevesa "
                + this.MCTree.getCurrentTreeSize();

        // average difference from optimal moves
        String whitesAverageDiff = "Average whites DTM difference from optimal move is "
                + this.matchStats.getAverageWhitesDTMDiff();
        String blacksAverageDiff = "Average black DTM difference from optimal move is "
                + this.matchStats.getAverageBlacksDTMDiff();

        this.log.info(logString0
                + "\r\n"
                + logString1
                + "\r\n"
                + logString2
                + "\r\n"
                + logString3
                + "\r\n"
                + logString4
                + "\r\n"
                + whitesAverageDiff
                + "\r\n"
                + blacksAverageDiff
                + "\r\n###########################################################################################\r\n###########################################################################################\r\n");
    }
}
