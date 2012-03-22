package experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import chess.chessgame.ChessGame;
import chess.chessgame.ChessGameResults;

import logging.Logs;
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
                e.printStackTrace();
                System.exit(1);
            }
        }

        // write DTM diff to cvs file
        String whiteDTMDiffCSVFilePath = this.name + "/" + "whiteDTMDiff.csv";
        this.experimentStats.writeAverageDTMDiffToCVS(whiteDTMDiffCSVFilePath);
        
        //write average tree size to cvs file
        String treeSizeCSVFilePath = this.name + "/" + "treeSize.csv";
        this.experimentStats.writeAverageTreeSizeToCVS(treeSizeCSVFilePath);
        
        

        String pgnFilePath = this.name + "/" + Constants.PGN_FILENAME;
        try {

            File output = new File(pgnFilePath);
            FileWriter fw = new FileWriter(output);
            fw.write(this.pgn);
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // write this experiments info to log file

        this.log.info("××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××\r\n××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");

        String whiteAvgDTMDiff = "Whites average DTM difference from optimal move was "
                + experimentStats.getWhitesAverageDTMDiff();
        this.log.info("Experiment " + this.name + " summary:\r\n"
                + whiteAvgDTMDiff);
        this.log.info("Chess games were written in " + pgnFilePath
                + ", details of these matches are in " + this.name + "/"
                + Constants.LOG_FILENAME + ".\r\n END OF EXPERIMENT");

    }

}
