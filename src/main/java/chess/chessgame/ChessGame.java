package chess.chessgame;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mct.MCT;
import mct.MCTStats;
import utils.FruitUtils;
import utils.IOUtils;
import utils.Utils;
import chess.chessboard.ChessboardEvalState;
import config.DatabaseSetup;
import config.IOSetup;
import config.MCTSSetup;
import database.DBHandler;
import exceptions.ChessboardException;

/**
 * Class that handles playing a chess game.
 * 
 * @author Andraz Kohne
 */
public class ChessGame {

    /**
     * This matches fen.
     */
    private String              fen        = "";

    /**
     * File path where this match will be saved if saving individual games is
     * enabled.
     */
    private String              pgnFileName;

    /** Logger */
    private Logger              log        = Logger.getLogger("MCTS.ChessGame");

    /** MC tree */
    private MCT                 MCTree     = new MCT(this.log);

    /** {@link FileHandler} for individual games log */
    private FileHandler         individualGameLog;

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
        try {
            this.individualGameLog = new FileHandler(this.pgnFileName + ".log");
            this.individualGameLog.setFormatter(new SimpleFormatter());
            this.log.addHandler(this.individualGameLog);
        }
        catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Plays a chess game. It outputs game's information to log and database.
     * 
     * @param round
     *            which which round is played, needed for logging purposes
     * @return matches fen
     * @throws ChessboardException
     * @throws IOException
     */
    public ChessGameResults playGame(int round) throws ChessboardException {

        this.log.info("\r\n*************************\r\nSTARTING NEW GAME " + round + "\r\n*************************\r\n");

        long startTime = System.currentTimeMillis();
        this.fen += "[fen \"" + this.MCTree.getFEN() + "\"]\n\n";
        boolean didWhiteWin = false;

        for (int x = 0; x < MCTSSetup.NUMBER_OF_INITAL_STEPS; x++) {
            this.MCTree.oneMCTStep();
        }

        boolean whitesTurn = true;

        int turnDepth = 1;
        while (true) {
            this.log.fine("Stanje sahovnice je:\r\n" + this.MCTree.getMainChessboard() + "To stanje se je pojavilo "
                    + this.MCTree.getMainChessboard().howManyTimeHasCurrentStateAppeared() + "-krat.\r\n");

            ChessboardEvalState eval = this.MCTree.evaluateMainChessBoardState();

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

                this.fen += "{average white DTM diff = " + this.matchStats.getAverageWhitesDTMDiff() + ", average black DTM diff = "
                        + this.matchStats.getAverageBlacksDTMDiff() + " }";

                break;
            }

            // get best move and best move DTM from fruit
            String fruitOutput = FruitUtils.getOutputFromFruit(this.MCTree.getFEN());
            String perfectMove = FruitUtils.getMoveFromFruit(fruitOutput);
            int perfectDTM = FruitUtils.getDTMOfMoveFromFruitOutput(perfectMove, fruitOutput);

            int moveNumber = -1;

            // DTM difference of players move from optimal move
            int dtmDiff = -1;

            if (whitesTurn) {

                for (int x = 0; x < MCTSSetup.NUMBER_OF_RUNNING_STEPS; x++) {
                    this.MCTree.oneMCTStep();
                }

                moveNumber = this.MCTree.chooseAMoveNumber(MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY, MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY);

                dtmDiff = FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils.moveNumberToFruitString(moveNumber), fruitOutput) - perfectDTM;

                this.matchStats.whitesDiffFromOptimal.put(turnDepth, dtmDiff);

                this.fen += Utils.whiteMoveNumberToFenString(moveNumber, turnDepth, perfectMove + ", diff=" + dtmDiff) + " ";
            }
            else {
                moveNumber = this.MCTree.chooseAMoveNumber(MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY, MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY);

                dtmDiff = perfectDTM - FruitUtils.getDTMOfMoveFromFruitOutput(FruitUtils.moveNumberToFruitString(moveNumber), fruitOutput);

                this.matchStats.blacksDiffFromOptimal.put(turnDepth, dtmDiff);

                this.fen += Utils.blackMoveNumberToFenString(moveNumber, perfectMove + ", diff=" + dtmDiff);

                turnDepth++;
            }

            whitesTurn = !whitesTurn;

            this.log.fine("Tree size is " + this.MCTree.getCurrentTreeSize() + "\r\n" + "Optimal move that could have been done is " + perfectMove
                    + ", DTM difference from players move is " + dtmDiff);

            // update statistics
            this.matchStats.treeSize.put(turnDepth, this.MCTree.getCurrentTreeSize());
            this.matchStats.numberOfPliesMade++;

            this.MCTree.makeMCMove(moveNumber);

        }
        // End of the chess game. From here on we just deal with output.

        if (!didWhiteWin) {
            turnDepth--;
        }

        this.logGameSummary(round, startTime, didWhiteWin, turnDepth);

        this.matchStats.setStatisticsOfMCTS(this.MCTree.getMCTStatistics());
        this.matchStats.didWhiteWin = didWhiteWin;

        String whiteStrat = Utils.whiteStrategyToString(MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY);

        String blackStrat = Utils.blackStrategyToString(MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY);

        // calculate number of plies made
        int plyCount = didWhiteWin ? (turnDepth * 2 - 1) : (turnDepth * 2);

        // construct, write and return this matches pgn
        String preamble = Utils.constructPreamble(whiteStrat, blackStrat, MCTSSetup.C, MCTSSetup.THRESHOLD_T, didWhiteWin, round, plyCount,
                MCTSSetup.NUMBER_OF_INITAL_STEPS, MCTSSetup.NUMBER_OF_RUNNING_STEPS, MCTSSetup.NUMBER_OF_SIMULATIONS_PER_EVALUATION);
        this.fen = preamble + this.fen;
        if (IOSetup.WRITE_INDIVIDUAL_GAMES) {
            IOUtils.writeToFile(this.pgnFileName, this.fen);
        }

        if (DatabaseSetup.DB_ENABLED) {
            // write this chess game to the database
            DBHandler dbh = new DBHandler(DatabaseSetup.HOST, DatabaseSetup.USER, DatabaseSetup.PASSWORD);
            dbh.insertChessGame(this);
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
     * @param turnsMade
     *            number of turn made
     */
    private void logGameSummary(int round, long startTime, boolean didWhiteWin, int turnsMade) {
        String newLine = System.getProperty("line.separator");
        long runTime = System.currentTimeMillis() - startTime;
        MCTStats stats = this.MCTree.getMCTStatistics();
        String logString0 = "\r\n##########################\r\n###########################\r\nPOVZETEK igre " + this.pgnFileName + ". Igra je bila odigrana v "
                + Utils.timeMillisToString(runTime) + newLine;
        if (didWhiteWin) {
            logString0 += "Zmagal je BELI v " + turnsMade + " potezah";
        }
        else {
            logString0 += "Zmagal je CRNI v " + turnsMade + " potezah";
        }
        String logString1 = "V igri je bilo:";
        String logString2 = stats.numberOfMatsInSimAddsOneNode + " matov v simulationAddsOneNode.";

        String logString3 = stats.numberOfMatsInSimulation + " matov v simulation";

        String logString4 = "Crni je " + stats.numberOfMCTreeColapses + "-krat izbral potezo, ki je ni v drevesu." + newLine;
        logString4 += " Pred koncem igre pa je bila velikost drevesa " + this.MCTree.getCurrentTreeSize();

        // average difference from optimal moves
        String whitesAverageDiff = "Average whites DTM difference from optimal move is " + this.matchStats.getAverageWhitesDTMDiff();
        String blacksAverageDiff = "Average black DTM difference from optimal move is " + this.matchStats.getAverageBlacksDTMDiff();

        // MCTNode related statistics
        String nodes = "Nodes visited summary : " + stats.getNodeStatistics().toString();
        String selectedNodes = "Selected nodes summary : " + stats.getNodesSelectedStatistics().toString();

        this.log.info(logString0 + newLine + logString1 + newLine + logString2 + newLine + logString3 + newLine + logString4 + newLine + whitesAverageDiff
                + newLine + blacksAverageDiff + newLine + nodes + newLine + selectedNodes
                + "###########################################################################################" + newLine
                + "###########################################################################################" + newLine);

        this.log.removeHandler(this.individualGameLog);

    }


    public String getFen() {
        return fen;
    }


    public ChessGameStatistics getMatchStats() {
        return matchStats;
    }
}
