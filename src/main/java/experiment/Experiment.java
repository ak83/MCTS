package experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import logging.Logs;
import utils.IOUtils;
import chess.chessgame.ChessGame;
import chess.chessgame.ChessGameResults;
import exceptions.ChessboardException;
import exec.Constants;

/**
 * This class represent experiment (series of chess games with same parameters
 * 
 * @author Andraz
 */
public class Experiment {

    /**
     * Name of experiment, this is also the name of directory where this
     * experiments output will be saved
     */
    private String               name            = "";

    private Logger               log;

    private String               pgn             = "";

    private ExperimentStatistics experimentStats = new ExperimentStatistics();


    public Experiment(String name) {
        this.name = name;

    }


    public void runExperiment() {
        // create experiment directory and individual games directory
        String individualGamesDirPath = this.name + "/"
                + Constants.INDIVIDUAL_GAMES_DIR_NAME;
        new File(this.name).mkdir();
        new File(individualGamesDirPath).mkdir();

        // initialize logging
        Logs.initLoggers(this.name);
        this.log = Logger.getLogger("MCTS.Experiment");

        this.log.info(Constants.constantsString());

        // run chess games
        for (int x = 0; x < Constants.NUMBER_OF_GAMES_PLAYED; x++) {
            ChessGame game = new ChessGame(individualGamesDirPath + "/game" + x
                    + ".pgn");
            try {
                ChessGameResults gameResult = game.playGame(x);
                this.pgn += gameResult.getPgn();
                this.experimentStats.addChessGameStatistics(gameResult
                        .getStatistics());
            }
            catch (ChessboardException e) {
                System.err.println("There is an error in chessboard logic!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // write DTM diff to cvs files
        String whiteDTMDiffCSVFilePath = this.name + "/"
                + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME;
        this.experimentStats.writeAverageDTMDiffToCVS(whiteDTMDiffCSVFilePath
                + ".csv");
        this.experimentStats
                .saveWhiteDTMDifferenceGraph(whiteDTMDiffCSVFilePath + ".jpg");

        // output number of collapses files
        this.experimentStats.writeNumberOfCollapsesToCSV(this.name + "/"
                + IOUtils.NUMBER_OF_MCTS_TREE_COLLAPSES_FILE_NAME + ".csv");

        // save chart that show with multiple output options
        this.experimentStats.saveCollapsesDTMTreeSizechart(this.name
                + "/DTMTreeCollapsesTreeSize.jpg");

        // save length per chess game chart
        this.experimentStats.saveGameLengthPerGameChart(this.name + "/"
                + IOUtils.GAME_LENGTH + ".jpg");

        this.experimentStats.saveCollapseDTMChart(this.name
                + "/collapsesDTM.jpg");

        // write average tree size to cvs file
        this.experimentStats.writeAverageTreeSizeToCVS(this.name + "/"
                + IOUtils.TREE_SIZE_FILE_NAME + ".csv");

        this.experimentStats.writeUltimateCSV(this.name + "/"
                + IOUtils.ULTIMATE_FILE_NAME + ".csv");

        String pgnFilePath = this.name + "/" + Constants.PGN_FILENAME;
        try {

            File output = new File(pgnFilePath);
            FileWriter fw = new FileWriter(output);
            fw.write(this.pgn);
            fw.close();
        }
        catch (IOException e) {
            System.err.println("Could not write file " + pgnFilePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // write this experiments info to log file

        this.log.info("××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××\r\n××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");

        String whiteAvgDTMDiff = "Whites average DTM difference from optimal move was "
                + experimentStats.getWhitesAverageDTMDiff();
        String averageGameLength = "Average game length (in turns) was: "
                + this.experimentStats.getAverageGameLength();

        this.log.info("Experiment " + this.name + " summary:\r\n"
                + whiteAvgDTMDiff + "\r\n" + averageGameLength);
        this.log.info("Chess games were written in "
                + pgnFilePath
                + ", details of these matches are in "
                + this.name
                + "/"
                + Constants.LOG_FILENAME
                + ".\r\n END OF EXPERIMENT"
                + "××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××\r\n××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");

    }


    public ExperimentStatistics getExperimentStats() {
        return this.experimentStats;
    }

}
