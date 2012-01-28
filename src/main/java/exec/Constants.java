package exec;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import javax.management.RuntimeErrorException;

public class Constants {

    private Constants() {};

    // public final static long RANDOM_SEED = 765768;
    public static int          NUMBER_OF_GAMES_PLAYED;
    public static String       PGN_FILENAME                                                                    = "test.pgn";
    public static String       LOG_FILENAME                                                                    = "test.log";
    public static String       CONFIG_FILENAME                                                                 = "MCTS.conf";
    public static String       FRUIT_FILEPATH                                                                  = "Fruit-2-3-1.exe";
    public static String       EMD_DIR                                                                         = System.getProperty("user.dir");
    /**
     * maximum tree depth ( maximum ply count)
     */
    public static int          MAX_DEPTH                                                                       = 100;
    public static double       C;
    public static int          GOBAN;
    public static int          NUMBER_OF_SIMULATIONS_PER_EVALUATION;
    public static int          BLACK_SIMULATION_STRATEGY                                                       = 2;
    public static int          WHITE_SIMULATION_STRATEGY;
    public static int          NUMBER_OF_INITAL_STEPS;
    public static int          NUMBER_OF_RUNNING_STEPS;
    public static int          WHITE_MOVE_CHOOSER_STRATEGY                                                     = 1;
    public static int          BLACK_MOVE_CHOOSER_STRATEGY                                                     = 2;
    public static boolean      SELECTION_EVALUATES_CHESSBOARD                                                  = false;
    public static boolean      SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING                               = false;                                                  ;
    public static String       ENDING;
    public static boolean      WRITE_INDIVIDUAL_GAMES                                                          = true;
    public static Level        FILE_LOG_LEVEL                                                                  = Level.ALL;
    public static Level        CONSOLE_LOG_LEVEL                                                               = Level.OFF;

    public static boolean      KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition                         = false;
    public static boolean      KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals                = false;

    public static boolean      HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;
    public static boolean      HEURISTICS_white_KING_only_moves_coser_to_black_king                            = false;
    public static boolean      HEURISTICS_check_for_urgent_moves                                               = false;
    public static boolean      HEURISTICS_only_safe_moves                                                      = false;
    public static boolean      HEURISTICS_avoid_move_repetition                                                = false;

    public static boolean[]    file_mandatory_parameters                                                       = {
            false, false, false, false, false, false, false, false                                            };

    public static final String HELP                                                                            = "Uporaba: \r\n"
                                                                                                                       + "java -jar MCTS.jar [OPT]"
                                                                                                                       + "\r\n\r\n"
                                                                                                                       + "OPT - opcijski argumenti"
                                                                                                                       + "\r\n\t --ig"
                                                                                                                       + "\r\n\t\t ne zapisuj posameznih iger v pgn-je"
                                                                                                                       + "\r\n\t --emd [EMD_DIR]"
                                                                                                                       + "\r\n\t\t nastavi emd direktorij na [EMD_DIR] (privzeto je v trenutne direktoriju)"
                                                                                                                       + "\r\n\t --conf [CONF_FILE]"
                                                                                                                       + "\r\n\t\t za konfiguracijsko datoteko uporabi CONF_FILE"
                                                                                                                       + "\r\n\t --pgn [PGN_FILE]"
                                                                                                                       + "\r\n\t\t shrani partijo v PGN_FILE (privzeto je test.pgn)"
                                                                                                                       + "\r\n\t --log [LOG_FILE]"
                                                                                                                       + "\r\n\t\t nastavi pot za log datoteko (privzeto je test.log)"
                                                                                                                       + "\r\n\t --fruit [FRUIT]"
                                                                                                                       + "\r\n\t\t nastavi pot do fruit programa (privzeto je Fruit-2-3-1.exe)"
                                                                                                                       + "\r\n\t --help"
                                                                                                                       + "\r\n\t\t program izpise to pomoc in se konca";


    public static void initConstants(String[] param) {

        if (Constants.ENDING.equalsIgnoreCase("KRRK")) {
            Constants.WHITE_SIMULATION_STRATEGY = 1;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KQK")) {
            Constants.WHITE_SIMULATION_STRATEGY = 2;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KRK")) {
            Constants.WHITE_SIMULATION_STRATEGY = 3;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KBBK")) {
            Constants.WHITE_SIMULATION_STRATEGY = 4;
        }

        // Otional arguments
        for (int x = 0; x < param.length; x++) {
            if (param[x].equals("--ig")) {
                Constants.WRITE_INDIVIDUAL_GAMES = false;
            }
            else if (param[x].substring(0, 4).equals("--emd")) {
                x++;
                Constants.EMD_DIR = param[x];
            }
            else if (param[x].equals("--conf")) {
                x++;
                Constants.CONFIG_FILENAME = param[x];
            }
            else if (param[x].equals("--pgn")) {
                x++;
                Constants.PGN_FILENAME = param[x];
            }
            else if (param[x].equals("--log")) {
                x++;
                Constants.LOG_FILENAME = param[x];
            }
            else if (param[x].equals("--fruit")) {
                x++;
                Constants.FRUIT_FILEPATH = param[x];
            }
            else if (param[x].equals("--help")) {
                System.out.println(Constants.HELP);
                System.exit(0);
            }
            else {
                System.err.println("Nepoznan parameter: " + param[x]);
                System.exit(1);
            }
        }
    }


    public static String constantsString() {
        String rez = "NUMBER_OF_GAMES_PLAYED "
                + Constants.NUMBER_OF_GAMES_PLAYED + "\r\n";
        rez += "PGN_FILENAME " + Constants.PGN_FILENAME + "\r\n";
        rez += "LOG_FILENAME " + Constants.LOG_FILENAME + "\r\n";
        rez += "CONFIG_FILENAME " + Constants.CONFIG_FILENAME + "\r\n";
        rez += "FRUIT_FILEPATH " + Constants.FRUIT_FILEPATH + "\r\n";
        rez += "EMD_DIR " + Constants.EMD_DIR + "\r\n";
        rez += "MAX_DEPTH " + Constants.MAX_DEPTH
                + " (maximum number of plys in chessgame)\r\n";
        rez += "C " + Constants.C + "\r\n";
        rez += "GOBAN " + Constants.GOBAN + "\r\n";
        rez += "NUMBER_OF_SIMULATIONS_PER_EVALUATION "
                + Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION
                + " (how many simulation we run per every added node)\r\n";
        rez += "ENDING " + Constants.ENDING + "\r\n";

        rez += "BLACK_SIMULATION_STRATEGY "
                + Constants.BLACK_SIMULATION_STRATEGY;
        switch (Constants.BLACK_SIMULATION_STRATEGY) {
            case 0:
                rez += " (black doesn' use heuristics).\r\n";
                break;
            case 1:
                rez += " (black king tries to move towards center).\r\n";
                break;
            case 2:
                rez += " (black king tries to move towards center, but if it's possible eats white figure and evades king opposition).\r\n";
                break;
            case 3:
                rez += " (black king playes with perfect information).\r\n";
                break;
        }

        rez += "WHITE_SIMULATION_STRATEGY "
                + Constants.WHITE_SIMULATION_STRATEGY;
        switch (Constants.WHITE_SIMULATION_STRATEGY) {
            case 0:
                rez += " (white is using random strategy - it doesn't use heuristics)\r\n";
                break;

            default:
                rez += " (white is using heuristics based on current ending)\r\n";
                break;
        }

        rez += "NUMBER_OF_INITAL_STEPS " + Constants.NUMBER_OF_INITAL_STEPS
                + " (number of MC steps before game starts).\r\n";
        rez += "NUMBER_OF_RUNNING_STEPS " + Constants.NUMBER_OF_RUNNING_STEPS
                + " (number of MC steps performed before whites move).\r\n";

        rez += "WHITE_MOVE_CHOOSER_STRATEGY "
                + Constants.WHITE_MOVE_CHOOSER_STRATEGY
                + " (white draws moves by selecting child node of root -";
        switch (Constants.WHITE_MOVE_CHOOSER_STRATEGY) {
            case 0:
                rez += " chooses random node).\r\n";
                break;
            case 1:
                rez += " chooses node with maximum visit count).\r\n";
                break;
            case 2:
                rez += " choose with maximum rating).\r\n";
                break;
        }

        rez += "BLACK_MOVE_CHOOSER_STRATEGY "
                + Constants.BLACK_MOVE_CHOOSER_STRATEGY;
        switch (Constants.BLACK_MOVE_CHOOSER_STRATEGY) {
            case 0:
                rez += " (black doesn' use heuristics).\r\n";
                break;
            case 1:
                rez += " (black king tries to move towards center).\r\n";
                break;
            case 2:
                rez += " (black king tries to move towards center, but if it's possible eats white figure and evades king opposition).\r\n";
                break;
            case 3:
                rez += " (black king playes with perfect information).\r\n";
                break;
        }

        rez += "SELECTION_EVALUATES_CHESSBOARD "
                + Constants.SELECTION_EVALUATES_CHESSBOARD
                + " (if set and if selection find node that represents mat it ends).\r\n";

        rez += "SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING "
                + Constants.SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING
                + " (if set selection, when choosing next node, only takes nodes with highest visitcount in account).\r\n";

        rez += "WRITE_INDIVIDUAL_GAMES "
                + Constants.WRITE_INDIVIDUAL_GAMES
                + " (if set program writes individual games also to sgames dir).\r\n";
        rez += "\r\n";
        rez += "HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 "
                + Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3
                + " (if set and white king is more than 3 title away from black king, then white moves king closer to black king).\r\n";

        rez += "HEURISTICS_white_KING_only_moves_coser_to_black_king "
                + Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king
                + " (if set white king can't increase distance to white king).\r\n";

        rez += "HEURISTICS_check_for_urgent_moves "
                + Constants.HEURISTICS_check_for_urgent_moves
                + " (if set white checks if any of his figures are in dagner and tries to move them to safety).\r\n";

        rez += "HEURISTICS_only_safe_moves "
                + Constants.HEURISTICS_only_safe_moves
                + " (if set white tries to move figures to safe titles)\r\n";

        rez += "HEURISTICS_avoid_move_repetition "
                + Constants.HEURISTICS_avoid_move_repetition + "\r\n";

        if (Constants.ENDING.equalsIgnoreCase("KRK")) {
            rez += "KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition "
                    + Constants.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition
                    + " (if set, when king are in opposition white tries to check).\r\n";
        }
        else if (Constants.ENDING.equalsIgnoreCase("KBBK")) {
            rez += "KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals "
                    + Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals
                    + " (if set white tries to put bishops on adjacent diagonals).\r\n";
        }

        return rez;
    }


    /**
     * set MCT parameters from configuration file
     */
    public static void readConfigFile() {
        int currentLine = 0;
        try {
            File confFile = new File(Constants.CONFIG_FILENAME);
            if (!confFile.exists()) {
                System.out.println("Konfiguracijska datoteka ne obstaja!");
                System.exit(2);
            }

            Scanner scan = new Scanner(confFile);
            while (scan.hasNextLine()) {
                currentLine++;
                String line = scan.nextLine();
                if (line.equals("") || line.charAt(0) == '#') {
                    continue;
                }

                String[] words = line.split("[ \t]");
                if (words[0].equalsIgnoreCase("number_of_games")
                        || words[0].equalsIgnoreCase("number_of_games_played")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'number_of_games' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfGames = -1;
                    try {
                        numOfGames = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'number_of_games' must be an integer (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }
                    Constants.NUMBER_OF_GAMES_PLAYED = numOfGames;
                    Constants.file_mandatory_parameters[0] = true;

                }
                else if (words[0].equalsIgnoreCase("c")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'c' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    double c = Double.NaN;
                    try {
                        c = Double.parseDouble(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'c' must be double (line: "
                                        + currentLine + ").");
                    }

                    Constants.C = c;
                    Constants.file_mandatory_parameters[1] = true;
                }
                else if (words[0].equalsIgnoreCase("goban")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'goban' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    int goban = -1;
                    try {
                        goban = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'goban' must be an integer (line: "
                                        + currentLine + " ).");
                        System.exit(1);
                    }

                    Constants.GOBAN = goban;
                    Constants.file_mandatory_parameters[2] = true;

                }
                else if (words[0]
                        .equalsIgnoreCase("number_of_simulations_per_evaluation")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'number_of_simulations_per_evaluation' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSim = -1;
                    try {
                        numOfSim = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'number_of_simulations_per_evaluation' must be an integer (line: "
                                        + currentLine + " ).");
                        System.exit(1);
                    }

                    Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION = numOfSim;
                    Constants.file_mandatory_parameters[3] = true;
                }
                else if (words[0].equalsIgnoreCase("number_of_inital_steps")
                        || words[0].equalsIgnoreCase("initial_steps")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'number_of_inital_steps' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSteps = -1;
                    try {
                        numOfSteps = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'number_of_inital_steps' must be an integer (line: "
                                        + currentLine + " ).");
                        System.exit(1);
                    }

                    Constants.NUMBER_OF_INITAL_STEPS = numOfSteps;
                    Constants.file_mandatory_parameters[4] = true;

                }
                else if (words[0].equalsIgnoreCase("number_of_running_steps")
                        || words[0].equalsIgnoreCase("running_steps")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'number_of_running_steps' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSteps = -1;
                    try {
                        numOfSteps = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err
                                .println("*** ERROR: In config file token after 'number_of_running_steps' must be an integer (line: "
                                        + currentLine + " ).");
                        System.exit(1);
                    }

                    Constants.NUMBER_OF_RUNNING_STEPS = numOfSteps;
                    Constants.file_mandatory_parameters[5] = true;

                }
                else if (words[0].equalsIgnoreCase("ending")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'ending' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    boolean notKRRK = !words[1].equalsIgnoreCase("krrk");
                    boolean notKQK = !words[1].equalsIgnoreCase("kqk");
                    boolean notKRK = !words[1].equalsIgnoreCase("krk");
                    boolean notKBBK = !words[1].equalsIgnoreCase("kbbk");
                    if (notKRRK && notKQK && notKRK && notKBBK) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid ending.");
                        System.exit(1);
                    }

                    Constants.ENDING = words[1];
                    Constants.file_mandatory_parameters[6] = true;
                }
                else if (words[0]
                        .equalsIgnoreCase("white_move_chooser_strategy")
                        || words[0].equalsIgnoreCase("white_strat")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'white_move_chooser_strategy' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    boolean visitCount = words[1]
                            .equalsIgnoreCase("visitcount")
                            || words[1].equalsIgnoreCase("visit_count");
                    boolean rating = words[1].equalsIgnoreCase("rating");
                    boolean random = words[1].equalsIgnoreCase("random");

                    if (!visitCount && !rating && !random) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid white move chooser strategy.");
                        System.exit(1);
                    }

                    if (visitCount) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 1;
                    }
                    else if (rating) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 2;
                    }
                    else if (random) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 0;
                    }

                    Constants.file_mandatory_parameters[7] = true;
                }

                /*
                 * ***********************************************************************
                 * *************************************OPTIONAL
                 * STUFF********************
                 * ************************************
                 * ************************************
                 */
                else if (words[0].equalsIgnoreCase("file_log_level")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'file_log_level' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    Level level = null;
                    try {
                        level = Level.parse(words[1]);
                    }
                    catch (IllegalArgumentException e) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid level.");
                        System.exit(1);
                    }
                    catch (NullPointerException e) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid level.");
                        System.exit(1);
                    }

                    Constants.FILE_LOG_LEVEL = level;
                }
                else if (words[0].equalsIgnoreCase("console_log_level")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 1 token after 'console_log_level' (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    Level level = null;
                    try {
                        level = Level.parse(words[1]);
                    }
                    catch (IllegalArgumentException e) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid level.");
                        System.exit(1);
                    }
                    catch (NullPointerException e) {
                        System.err
                                .println("Line "
                                        + currentLine
                                        + " in config file does not contain valid level.");
                        System.exit(1);
                    }

                    Constants.CONSOLE_LOG_LEVEL = level;
                }
                else if (words[0].equalsIgnoreCase("selection_evaluates")) {
                    if (words.length != 1) {
                        System.err
                                .println("There must be no other tokens after selection_evaluates (line: "
                                        + currentLine + " ).");
                        System.exit(1);
                    }
                    Constants.SELECTION_EVALUATES_CHESSBOARD = true;
                }
                else if (words[0]
                        .equalsIgnoreCase("selection_uses_visti_count")
                        || words[0].equalsIgnoreCase("suvc")) {
                    if (words.length != 1) {
                        System.err
                                .println("There must be no other tokens after "
                                        + words[0] + " (line: " + currentLine
                                        + " ).");
                        System.exit(1);
                    }

                    Constants.SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING = true;
                }
                /* *************************************************************************************************
                 * *****************************HEURISTICS***********************
                 * ********************************
                 * *****************************
                 * *********************************
                 * ********************************
                 */
                else if (words[0].equalsIgnoreCase("GENERAL")
                        || words[0].equalsIgnoreCase(Constants.ENDING)) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 2 token when setting heuristics (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }
                    if (words[1].equalsIgnoreCase("kingMovesCloserIfTooFar")) {
                        Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = true;
                    }
                    else if (words[1].equalsIgnoreCase("kingMovesOnlyCloser")) {
                        Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king = true;
                    }
                    else if (words[1].equalsIgnoreCase("checkForUrgentMoves")) {
                        Constants.HEURISTICS_check_for_urgent_moves = true;
                    }
                    else if (words[1].equalsIgnoreCase("safeMovesOnly")) {
                        Constants.HEURISTICS_only_safe_moves = true;
                    }
                    else if (words[1].equalsIgnoreCase("basic")) {
                        Constants.HEURISTICS_check_for_urgent_moves = true;
                        Constants.HEURISTICS_only_safe_moves = true;
                        Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;
                        Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king = false;
                    }
                    else if (words[1].equalsIgnoreCase("avoidMoveRepetition")) {
                        Constants.HEURISTICS_avoid_move_repetition = true;
                    }
                    else if (words[1]
                            .equalsIgnoreCase("checksIfKingsInOpposition")
                            && Constants.ENDING.equalsIgnoreCase("KRK")) {
                        Constants.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition = true;
                    }
                    else if (words[1]
                            .equalsIgnoreCase("bishopsOnAdjacentDiagonals")
                            && Constants.ENDING.equalsIgnoreCase("KBBK")) {
                        Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals = true;
                    }
                    else {
                        System.err.println("*** ERROR: " + words[1]
                                + " does not represent valid heuristic (line: "
                                + currentLine + ").");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("BLACK")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 2 token when setting black move strategy (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }

                    if (words[1].equalsIgnoreCase("random")) {
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY = 0;
                    }
                    else if (words[1].equalsIgnoreCase("center")
                            || words[1].equalsIgnoreCase("centre")) {
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY = 1;
                    }
                    else if (words[1].equalsIgnoreCase("normal")) {
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY = 2;
                    }
                    else if (words[1].equalsIgnoreCase("perfect")) {
                        Constants.BLACK_MOVE_CHOOSER_STRATEGY = 3;
                    }
                    else {
                        System.err.println(words[1]
                                + " is not valid strategy (line: "
                                + currentLine + " ).");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("white")) {
                    if (words.length != 2) {
                        System.err
                                .println("*** ERROR: Config file must have exactly 2 token when setting white move strategy (line: "
                                        + currentLine + ").");
                        System.exit(1);
                    }
                    if (words[1].equalsIgnoreCase("random")) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 0;
                    }
                    else if (words[1].equalsIgnoreCase("vc")
                            || words[1].equalsIgnoreCase("visit_count")
                            || words[1].equalsIgnoreCase("visitCount")) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 1;
                    }
                    else if (words[1].equalsIgnoreCase("rating")) {
                        Constants.WHITE_MOVE_CHOOSER_STRATEGY = 2;
                    }
                    else {
                        System.err.println(words[1]
                                + " is not valid strategy (line: "
                                + currentLine + " ).");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("NWH")
                        || words[0].equalsIgnoreCase("whiteNoHeuristics")) {
                    if (words.length != 1) {
                        System.err
                                .println("There mustn't be any other token besides "
                                        + words[0]
                                        + " (line: "
                                        + currentLine
                                        + ").");
                        System.exit(1);
                    }

                    Constants.HEURISTICS_check_for_urgent_moves = false;
                    Constants.HEURISTICS_only_safe_moves = false;
                    Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;
                    Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king = false;
                    Constants.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition = false;
                    Constants.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals = false;

                    Constants.WHITE_SIMULATION_STRATEGY = 0;
                }
                else if (Constants.doesStringMatchValidEnding(words[0])) {
                    // we do nothing here, because, if token is valid ending,
                    // but not current ending then we ignore it
                }
                else {
                    System.err.println("Token " + words[0]
                            + " is invalid (line: " + currentLine + ").");
                    System.exit(1);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeErrorException(new Error(e));
        }

        // check if there are any mandatory parameters missing from config file

        StringBuffer sb = new StringBuffer(300);
        boolean missing = false;
        for (int x = 0; x < Constants.file_mandatory_parameters.length; x++) {
            if (!Constants.file_mandatory_parameters[x]) {
                missing = true;
                switch (x) {
                    case 0:
                        sb.append("\tNumber_of_games\r\n");
                        continue;
                    case 1:
                        sb.append("\tC\r\n");
                        continue;
                    case 2:
                        sb.append("\tGoban\r\n");
                        continue;
                    case 3:
                        sb.append("\tNumber_of_simulation_per_evaluation\r\n");
                        continue;
                    case 4:
                        sb.append("\tNumber_of_initial_steps\r\n");
                        continue;
                    case 5:
                        sb.append("\tNumber_of_running_steps\r\n");
                        continue;
                    case 6:
                        sb.append("\tEnding\r\n");
                        continue;
                    case 7:
                        sb.append("\tWhite_move chooser_strategy\r\n");
                        continue;
                }
            }
        }
        if (missing) {
            String rez = "There are parameters missing in config file:\r\n"
                    + sb.toString();
            System.err.print(rez);
            System.exit(1);
        }
    }


    /**
     * @param string
     *            string we want to match
     * @return true if string represents valid ending (case insensitive),
     *         otherwise false
     */
    private static boolean doesStringMatchValidEnding(String string) {
        String[] validEndings = { "KRRK", "KQK", "KRK", "KBBK" };
        for (String ending : validEndings) {
            if (string.equalsIgnoreCase(ending)) { return true; }
        }

        return false;
    }
}
