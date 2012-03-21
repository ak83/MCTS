package experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import chess.chessgame.ChessGame;

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
    private String name = "";

    private Logger log;

    private String pgn  = "";


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

        for (int x = 0; x < Constants.NUMBER_OF_GAMES_PLAYED; x++) {
            ChessGame game = new ChessGame(individualGamesDirPath + "/game" + x
                    + ".pgn");
            try {
                this.pgn += game.playGame(x);
            }
            catch (ChessboardException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

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

        this.log.info("Chess games were written in " + pgnFilePath
                + ", details of these matches are in " + this.name + "/"
                + Constants.LOG_FILENAME + ".\r\n END OF EXPERIMENT");
    }

}
