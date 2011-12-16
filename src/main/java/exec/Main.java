package exec;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

public class Main {

    /**
     * @param args
     */
    private static Logger log;


    public static void main(String[] args) throws Exception {
        if (true) {
            if (args.length < 9 && false) {
                Print.println(Constants.HELP);
                System.exit(0);
            }
            else {
                Constants.readConfigFile();
                Constants.initConstants(args);
                Print.println(Constants.constantsString());
            }
        }

        if (Constants.WRITE_INDIVIDUAL_GAMES) {
            new File("sgames").mkdir();
        }
        Logs.initLoggers();
        Main.log = Logger.getLogger("MCTS.Main");
        Main.log.info(Constants.constantsString());
        String gamePgn = "";
        String filename = Constants.PGN_FILENAME;
        if (true) {
            for (int x = 0; x < Constants.NUMBER_OF_GAMES_PLAYED; x++) {
                ChessGame game = new ChessGame("sgames/Game" + (x + 1) + ".pgn");
                gamePgn += game.playGame(x) + "\n\n";
            }

            File output = new File(filename);
            FileWriter fw = new FileWriter(output);
            fw.write(gamePgn);
            fw.close();
            Print.println("Igre so bile zapisane v " + filename
                    + ", prodrobnosti so pa zapisane v "
                    + Constants.LOG_FILENAME + ".");
            Print.println("Konec programa.");
        }
        else {
            Print.println(Utils.arePsotionsDiagonallyAdjacent(96, 20));
        }

    }
}
