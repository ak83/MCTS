package config;

import java.util.ArrayList;
import java.util.logging.Level;




public class IOSetup {

    /**
     * directory name where output of individual games is saved in experiment
     * directory
     */
    public static final String      INDIVIDUAL_GAMES_DIR_NAME = "sgames";

    /**
     * XML file for test related parameters
     */
    public static final String      TEST_XML_FILENAME         = "testConfiguration.xml";
    
    /**
     * default value used for test identification
     */
    public static final String DEFAULT_TEST_LABEL                      = "none";

    /**
     * File to which ogn output will be saved.
     */
    public static String            PGN_FILENAME              = "test.pgn";
    /**
     * File to which mathes will be logged.
     */
    public static String            LOG_FILENAME              = "test.log";
    /**
     * Config file from which parameters are read.
     */
    public static String            CONFIG_FILENAME           = "MCTS.conf";
    /**
     * Fruit executable file path. Required only if black plays with perfect
     * information.
     */
    public static String            FRUIT_FILEPATH            = "Fruit-2-3-1.exe";
    /**
     * Directory where emd files are stored.
     */
    public static String            EMD_DIR                   = System.getProperty("user.dir");
    /**
     * If <code>true</code> application will also write pgns of individual
     * matches played in sgames directory.
     */
    public static boolean           WRITE_INDIVIDUAL_GAMES    = true;
    /**
     * Filters which information will be logged to log file.
     */
    public static Level             FILE_LOG_LEVEL            = Level.ALL;
    /**
     * Filters which information will be logged to console.
     */
    public static Level             CONSOLE_LOG_LEVEL         = Level.OFF;

    /**
     * List of all tests that this experiment series belongs to.
     */
    public static ArrayList<String> testLabels                = new ArrayList<String>();

    

}
