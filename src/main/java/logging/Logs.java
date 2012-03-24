package logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import exec.Constants;

/**
 * Class that holds and configures loggers.
 * 
 * @author Andraz
 */
public class Logs {

    private static Logger logger;


    /**
     * Configures loggers so that levels for file and console logging can be set
     * separately.
     * 
     * @param experimentDir
     *            directory name where experiments output will be saved
     */
    public static void initLoggers(String experimentDir) {
        Logger rootLogger = Logger.getLogger("");

        // remove all handlers
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        Logs.logger = Logger.getLogger("MCTS");

        // remove all handlers
        for (Handler handler : Logs.logger.getHandlers()) {
            Logs.logger.removeHandler(handler);
        }

        // file handler setup
        Handler fh = null;
        try {
            fh = new FileHandler(experimentDir + "/" + Constants.LOG_FILENAME);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // formatter for file handler
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        // console handler
        Handler ch = new ConsoleHandler();
        ch.setLevel(Constants.CONSOLE_LOG_LEVEL);

        // logger setup
        Logs.logger.addHandler(fh);
        Logs.logger.addHandler(ch);
        Logs.logger.setLevel(Constants.FILE_LOG_LEVEL);
    }

}