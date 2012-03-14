package exec;

import java.io.IOException;
import java.util.logging.Logger;

import utils.FruitUtils;
import utils.Utils;
import chessboard.ChessboardEvalState;
import exceptions.ChessboardException;

/**
 * Class that handles playing a match logic.
 * 
 * @author Andraz Kohne
 */
public class ChessGame {

    /**
     * Which turn currently is.
     */
    private int                 turnDepth  = 1;

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
    public String playGame(int round) throws ChessboardException, IOException {

        this.log.info("\r\n*************************\r\nZACETEK NOVE IGRE\r\n*************************\r\n");

        long startTime = System.currentTimeMillis();
        this.fen += "[fen \"" + this.MCTree.getFEN() + "\"]\n\n";
        boolean didWhiteWin = false;

        for (int x = 0; x < Constants.NUMBER_OF_INITAL_STEPS; x++) {
            this.MCTree.oneMCTStep();
        }

        boolean whitesTurn = true;
        int mW = -1;
        int mB = -1;
        while (true) {

            this.log.fine("Stanje sahovnice je:\r\n"
                    + this.MCTree.getMainChessboard()
                    + "To stanje se je pojavilo "
                    + this.MCTree.getMainChessboard()
                            .howManyTimeHasCurrentStateAppeared()
                    + "-krat.\r\n");

            String fruitOutput = FruitUtils.getOutputFromFruit(this.MCTree
                    .getFEN());

            String perfectMove = FruitUtils.getMoveFromFruit(fruitOutput);

            int perfectDTM = FruitUtils.getDTMOfMoveFromFruitOutput(
                    perfectMove, fruitOutput);

            ChessboardEvalState eval = this.MCTree
                    .evaluateMainChessBoardState();

            if (eval == ChessboardEvalState.BLACK_KING_MATED) {
                // ZMAGA BELEGA
                didWhiteWin = true;
                this.fen += Utils.whiteMoveNumberToFenString(mW,
                        this.turnDepth, perfectMove);

                this.fen += "{average white DTM diff = "
                        + this.matchStats.getAverageWhitesDTMDiff()
                        + ", average black DTM diff = "
                        + this.matchStats.getAverageBlacksDTMDiff() + " }";
                break;
            }
            else if (eval != ChessboardEvalState.NORMAl) {
                // ZMAGA ÈRNEGA
                didWhiteWin = false;
                if (!whitesTurn) {
                    this.fen += Utils.whiteMoveNumberToFenString(mW,
                            this.turnDepth, perfectMove);

                    this.fen += "{average white DTM diff = "
                            + this.matchStats.getAverageWhitesDTMDiff()
                            + ", average black DTM diff = "
                            + this.matchStats.getAverageBlacksDTMDiff() + " }";
                }

                break;
            }

            int moveNumber = -1;

            if (whitesTurn) {

                for (int x = 0; x < Constants.NUMBER_OF_RUNNING_STEPS; x++) {
                    this.MCTree.oneMCTStep();
                }

                moveNumber = this.MCTree.chooseAMoveNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);
                mW = moveNumber;

                int mwDTM = FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils
                        .moveNumberToFruitString(mW), fruitOutput)
                        - perfectDTM;

                this.matchStats.whitesDiffFromOptimal
                        .put(this.turnDepth, mwDTM);

                this.fen += Utils.whiteMoveNumberToFenString(mW,
                        this.turnDepth, perfectMove + ", diff=" + mwDTM)
                        + " ";
            }
            else {
                moveNumber = this.MCTree.chooseAMoveNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);
                mB = moveNumber;

                int mBDTM = perfectDTM
                        - FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils
                                .moveNumberToFruitString(mB), fruitOutput);

                this.matchStats.blacksDiffFromOptimal
                        .put(this.turnDepth, mBDTM);

                this.fen += Utils.blackMoveNumberToFenString(mB, perfectMove
                        + ", diff=" + mBDTM);
                this.turnDepth++;
            }

            whitesTurn = !whitesTurn;

            this.log.fine("Velikost drevesa je "
                    + this.MCTree.getCurrentTreeSize() + "\r\n"
                    + "Optimalna poteza, ki bi jo lahko naredi igralec je "
                    + perfectMove);

            this.MCTree.makeMCMove(moveNumber);

        }

        this.logGameSummary(round, startTime, didWhiteWin);

        String whiteStrat = Utils
                .whiteStrategyToString(Constants.WHITE_MOVE_CHOOSER_STRATEGY);

        String blackStrat = Utils
                .blackStrategyToString(Constants.BLACK_MOVE_CHOOSER_STRATEGY);
        this.turnDepth = (this.turnDepth - 1) * 2;

        String preamble = Utils.constructPreamble(whiteStrat, blackStrat,
                Constants.C, Constants.GOBAN, didWhiteWin, round,
                this.turnDepth, Constants.NUMBER_OF_INITAL_STEPS,
                Constants.NUMBER_OF_RUNNING_STEPS,
                Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION);
        this.fen = preamble + this.fen;
        if (Constants.WRITE_INDIVIDUAL_GAMES) {
            Utils.writePGN(this.pgnFileName, this.fen);
        }
        return this.fen;
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
    private void logGameSummary(int round, long startTime, boolean didWhiteWin) {
        long runTime = System.currentTimeMillis() - startTime;
        MCTStats stats = this.MCTree.getMCTStatistics();
        String logString0 = "\r\n##########################\r\n###########################\r\nPOVZETEK igre "
                + (round + 1)
                + ". Igra je bila odigrana v "
                + Utils.timeMillisToString(runTime);
        if (didWhiteWin) {
            logString0 += "\r\nZmagal je BELI v " + (this.turnDepth - 1)
                    + " potezah";
        }
        else {
            logString0 += "\r\nZmagal je CRNI v " + (this.turnDepth - 1)
                    + " potezah";
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
