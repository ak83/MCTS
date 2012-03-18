package start;

import java.io.File;
import java.util.logging.Logger;

import logging.Logs;
import exec.Constants;
import experiment.ExperimentSeries;

/**
 * Class that runs the application.
 * 
 * @author Andraz Kohne
 */
public class Main {

    /** This classes logger */
    private static Logger log;


    /**
     * Main method that initializes everything and runs the application.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        Constants.readConfigFile();
        Constants.initConstants(args);

        if (!new File(Constants.FRUIT_FILEPATH).exists()) {
            System.err
                    .println("Fruit file path is not set correctly. Ending program");
            System.exit(1);
        }

        Logs.initLoggers("");
        Main.log = Logger.getLogger("MCTS.Main");
        Main.log.info(Constants.testParameterValues.size()
                + "experiments will be run");

        ExperimentSeries es = new ExperimentSeries();
        es.runExperiments();
    }
}
