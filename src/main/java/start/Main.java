package start;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

import exec.ChessGame;
import exec.Constants;
import exec.Logs;

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

        if (Constants.WRITE_INDIVIDUAL_GAMES) {
            new File("sgames").mkdir();
        }
        try {
            Logs.initLoggers();
            Main.log = Logger.getLogger("MCTS.Main");
            Main.log.info(Constants.constantsString());
            String gamePgn = "";

            for (int x = 0; x < Constants.NUMBER_OF_GAMES_PLAYED; x++) {
                ChessGame game = new ChessGame("sgames/Game" + (x + 1) + ".pgn");
                gamePgn += game.playGame(x) + "\n\n";
            }

            File output = new File(Constants.PGN_FILENAME);
            FileWriter fw = new FileWriter(output);
            fw.write(gamePgn);
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Main.log.info("Igre so bile zapisane v " + Constants.PGN_FILENAME
                + ", prodrobnosti so pa zapisane v " + Constants.LOG_FILENAME
                + ".");
        Main.log.info("Konec programa.");
    }
}
