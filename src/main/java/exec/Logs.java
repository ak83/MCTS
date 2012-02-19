package exec;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class that holds and configures loggers.
 * 
 * @author Andraz
 */
public class Logs {

    private static Logger fileLogger;


    /**
     * Configures loggers so that levels for file and console logging can be set
     * separately.
     * 
     * @throws SecurityException
     * @throws IOException
     */
    public static void initLoggers() throws SecurityException, IOException {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.removeHandler(rootLogger.getHandlers()[0]);

        Logs.fileLogger = Logger.getLogger("MCTS");
        Handler fh = new FileHandler(Constants.LOG_FILENAME);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        Handler ch = new ConsoleHandler();
        ch.setLevel(Constants.CONSOLE_LOG_LEVEL);
        Logs.fileLogger.addHandler(fh);
        Logs.fileLogger.addHandler(ch);
        Logs.fileLogger.setLevel(Constants.FILE_LOG_LEVEL);
    }

}
