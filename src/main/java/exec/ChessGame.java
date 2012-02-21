package exec;

import java.io.IOException;
import java.util.logging.Logger;

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
    private int    depth  = 1;

    /**
     * This matches fen.
     */
    private String fen    = "";

    /**
     * File path where this match will be saved if sacing invidiual games is
     * enabled.
     */
    private String pgnFileName;

    /** MC tree */
    private MCT    MCTree = new MCT();

    /** Logger */
    private Logger log    = Logger.getLogger("MCTS.ChessGame"); ;


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
     * @return
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
            ChessboardEvalState eval = this.MCTree
                    .evaluateMainChessBoardState();
            if (eval == ChessboardEvalState.BLACK_KING_MATED) {
                // ZMAGA BELEGA
                didWhiteWin = true;
                this.fen += Utils.moveNumberToString(mW, this.depth);
                break;
            }
            else if (eval != ChessboardEvalState.NORMAl) {
                // ZMAGA �RNEGA
                didWhiteWin = false;
                if (!whitesTurn) {
                    this.fen += Utils.moveNumberToString(mW, this.depth);
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
            }
            else {
                moveNumber = this.MCTree.chooseAMoveNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);
                mB = moveNumber;

                this.fen += Utils.moveNumberToString(mW, mB, this.depth) + " ";
                this.depth++;
            }

            whitesTurn = !whitesTurn;

            this.log.fine("Velikost drevesa je "
                    + this.MCTree.getCurrentTreeSize());
            this.MCTree.makeMCPly(moveNumber);

        }

        long runTime = System.currentTimeMillis() - startTime;
        MCTStats stats = this.MCTree.getMCTStatistics();
        String logString0 = "\r\n##########################\r\n###########################\r\nPOVZETEK igre "
                + (round + 1)
                + ". Igra je bila odigrana v "
                + Utils.timeMillisToString(runTime);
        if (didWhiteWin) {
            logString0 += "\r\nZmagal je BELI v " + (this.depth - 1)
                    + " potezah";
        }
        else {
            logString0 += "\r\nZmagal je CRNI v " + (this.depth - 1)
                    + " potezah";
        }
        String logString1 = "V igri je bilo:";
        String logString2 = stats.numberOfMatsInSimAddsOneNode
                + " matov v simulationAddsOneNode.";

        String logString3 = stats.numberOfMatsInSimulation
                + " matov v simulation";

        String logString4 = "Crni je " + stats.numberOfMCTreeColapses
                + "-krat izbral potezo, ki je ni v drevesu";
        if (stats.numberOfMCTreeColapses > 0) {
            logString4 += ", te poteze crnega so se zgodile v potezah "
                    + Utils.intArrayListToString(stats.movesWhereMCTreeCollapsed);
            logString4 += ".\r\n Pred temi potezami je bilo drevo veliko: "
                    + Utils.intArrayListToString(stats.sizeOfTreeBeforeCollapses);
        }
        logString4 += ".\r\n Pred koncem igre pa je bila velikost drevesa "
                + this.MCTree.getCurrentTreeSize();

        this.log.info(logString0
                + "\r\n"
                + logString1
                + "\r\n"
                + logString2
                + "\r\n"
                + logString3
                + "\r\n"
                + logString4
                + "\r\n###########################################################################################\r\n###########################################################################################\r\n");

        String whiteStrat = Utils
                .whiteStrategyToString(Constants.WHITE_MOVE_CHOOSER_STRATEGY);

        String blackStrat = Utils
                .blackStrategyToString(Constants.BLACK_MOVE_CHOOSER_STRATEGY);
        this.depth = (this.depth - 1) * 2;

        String preamble = Utils.constructPreamble(whiteStrat, blackStrat,
                Constants.C, Constants.GOBAN, didWhiteWin, round, this.depth,
                Constants.NUMBER_OF_INITAL_STEPS,
                Constants.NUMBER_OF_RUNNING_STEPS,
                Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION);
        this.fen = preamble + this.fen;
        if (Constants.WRITE_INDIVIDUAL_GAMES) {
            Utils.writePGN(this.pgnFileName, this.fen);
        }
        return this.fen;
    }
}
