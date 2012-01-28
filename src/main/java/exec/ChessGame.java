package exec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import exceptions.BlackMoveFinderException;
import exceptions.ChessboardException;
import exceptions.MCTException;
import exceptions.MCTNodeException;
import exceptions.MCTUtilsException;
import exceptions.UtilsException;
import exceptions.WhiteMoveChooserException;
import exceptions.WhiteMoveFinderException;

public class ChessGame {

    private int    depth  = 1;
    private String fen    = "";
    private String pgnFileName;
    File           file;
    FileWriter     fw;

    private MCT    MCTree = new MCT();

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
     * Plays one game
     * 
     * @param round
     *            which which round is played, needed for logging purposes
     * @return
     * @throws ChessboardException
     * @throws MCTException
     * @throws WhiteMoveFinderException
     * @throws BlackMoveFinderException
     * @throws WhiteMoveChooserException
     * @throws UtilsException
     * @throws IOException
     * @throws MCTUtilsException
     * @throws MCTNodeException
     */
    public String playGame(int round) throws ChessboardException, MCTException,
            WhiteMoveFinderException, BlackMoveFinderException,
            WhiteMoveChooserException, UtilsException, IOException,
            MCTUtilsException, MCTNodeException {

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
            int eval = this.MCTree.evaluateMainChessBoardState();
            if (eval > 0) {
                // ZMAGA BELEGA
                didWhiteWin = true;
                this.fen += Utils.moveNumberToString(mW, this.depth);
                break;
            }
            if (eval < 0) {
                // ZMAGA ÈRNEGA
                didWhiteWin = false;
                if (!whitesTurn) {
                    this.fen += Utils.moveNumberToString(mW, this.depth);
                }

                break;
            }

            int plyNumber = -1;

            if (whitesTurn) {

                for (int x = 0; x < Constants.NUMBER_OF_RUNNING_STEPS; x++) {
                    this.MCTree.oneMCTStep();
                }

                plyNumber = this.MCTree.chooseAPlyNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);
                mW = plyNumber;
            }
            else {
                plyNumber = this.MCTree.chooseAPlyNumber(
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY);
                mB = plyNumber;

                this.fen += Utils.moveNumberToString(mW, mB, this.depth) + " ";
                this.depth++;
            }

            whitesTurn = !whitesTurn;

            this.log.fine("Velikost drevesa je "
                    + this.MCTree.getCurrentTreeSize());
            this.MCTree.makeMCPly(plyNumber);

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

        String whiteStrat = Utils.whiteStrategyToString(
                Constants.WHITE_MOVE_CHOOSER_STRATEGY,
                Constants.WHITE_SIMULATION_STRATEGY);

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
            this.writePGN();
        }
        return this.fen;
    }


    private void writePGN() {
        try {
            this.file = new File(this.pgnFileName);
            this.fw = new FileWriter(this.file);
            this.fw.write(this.fen);
            this.fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
