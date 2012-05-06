package start;

import java.io.File;

import exec.Constants;
import experiment.ExperimentSeries;

/**
 * Class that runs the application.
 * 
 * @author Andraz Kohne
 */
public class Main {

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

        ExperimentSeries es = new ExperimentSeries();
        es.runExperiments();
    }
}
