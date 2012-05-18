package experiment;

import java.io.File;
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
        String individualGamesDirPath = this.name + File.separator + Constants.INDIVIDUAL_GAMES_DIR_NAME;
        new File(this.name).mkdir();
        new File(individualGamesDirPath).mkdir();

        // initialize logging
        Logs.initLoggers(this.name);
        this.log = Logger.getLogger("MCTS.Experiment");

        this.log.info(Constants.constantsString());

        // run chess games
        for (int x = 0; x < Constants.NUMBER_OF_GAMES_PLAYED; x++) {
            ChessGame game = new ChessGame(individualGamesDirPath + File.separator + "game" + x + ".pgn");
            try {
                ChessGameResults gameResult = game.playGame(x);
                this.pgn += gameResult.getPgn();
                this.experimentStats.addChessGameStatistics(gameResult.getStatistics());
            }
            catch (ChessboardException e) {
                System.err.println("There is an error in chessboard logic!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // write DTM diff to cvs files
        String whiteDTMDiffCSVFilePath = this.name + File.separator + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME;

        this.experimentStats.writeAverageDTMDiffToCVS(whiteDTMDiffCSVFilePath + ".csv");

        this.experimentStats.saveAllSingleDatasetCharts(this.name);

        // output number of collapses files
        this.experimentStats.writeNumberOfCollapsesToCSV(this.name + File.separator + IOUtils.NUMBER_OF_MCTS_TREE_COLLAPSES_FILE_NAME + ".csv");

        // save chart that show with multiple output options
        this.experimentStats.saveCollapsesDTMTreeSizechart(this.name + File.separator + "DTMTreeCollapsesTreeSize.jpg");

        // save game length histogram
        this.experimentStats.saveGameLengthHistogram(this.name + File.separator + "gameLengthHistogram.jpg");

        this.experimentStats.saveCollapseDTMChart(this.name + File.separator + "collapsesDTM.jpg");

        // write average tree size to cvs file
        this.experimentStats.writeAverageTreeSizeToCVS(this.name + File.separator + IOUtils.TREE_SIZE_FILE_NAME + ".csv");

        this.experimentStats.writeUltimateCSV(this.name + File.separator + IOUtils.ULTIMATE_FILE_NAME + ".csv");

        this.experimentStats.saveUltimateChart(this.name + File.separator + IOUtils.ULTIMATE_FILE_NAME + ".jpg");

        String pgnFilePath = this.name + File.separator + Constants.PGN_FILENAME;

        IOUtils.writeToFile(pgnFilePath, this.pgn);

        // write this experiments info to log file

        this.log.info("××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××\r\n××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");

        String whiteAvgDTMDiff = "Whites average DTM difference from optimal move was " + experimentStats.getWhitesAverageDTMDiff();
        String averageGameLength = "Average game length (in turns) was: " + this.experimentStats.getAverageGameLength();

        this.log.info("Experiment " + this.name + " summary:\r\n" + whiteAvgDTMDiff + "\r\n" + averageGameLength);
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
