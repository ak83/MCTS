package start;

import java.io.File;

import utils.IOUtils;
import config.DatabaseSetup;
import config.IOSetup;
import config.MCTSSetup;
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
        MCTSSetup.readConfigFile();
        MCTSSetup.initConstants(args);

        // load setting throughout test from xml file
        try {
            IOUtils.createNewSettingsXMLFile(IOSetup.TEST_XML_FILENAME);
        }
        catch (IllegalArgumentException e) {
            // nothing to do
        }
        finally {
            IOSetup.testLabels = IOUtils.readXMLFile(IOSetup.TEST_XML_FILENAME);
        }

        DatabaseSetup.readConfigFile(DatabaseSetup.CONFIG_FILE);

        if (!new File(IOSetup.FRUIT_FILEPATH).exists()) {
            System.err.println("Fruit file path is not set correctly. Ending program");
            System.exit(1);
        }

        ExperimentSeries es = new ExperimentSeries();
        es.runExperiments();
    }
}
