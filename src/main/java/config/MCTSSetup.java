package config;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;

import javax.management.RuntimeErrorException;

import moveChoosers.WhiteChooserStrategy;
import moveFinders.BlackFinderStrategy;
import moveFinders.WhiteFinderStrategy;
import utils.ExperimentUtils;
import experiment.MCTestParameter;

/**
 * Class that controls the parameters of MCT algorithm. Also handles help output
 * and reading configuration file.
 * 
 * @author Andraz Kohne
 */
public class MCTSSetup {

    private MCTSSetup() {};

    /** Parameter on which experiments will be performed */
    public static MCTestParameter      testParameter;

    /** Values for MC parameter under test */
    public static Vector<Double>       testParameterValues                                                             = new Vector<Double>();

    /**
     * Number of match that will be played.
     */
    public static int                  NUMBER_OF_GAMES_PLAYED;

    /**
     * The maximum tree depth ( maximum ply count for single match).
     */
    public static int                  MAX_DEPTH                                                                       = 100;

    /**
     * C constant to be used by MCT nodes.
     */
    public static double               C;

    /**
     * Goban value to be used by MCT algorithm.
     */
    public static int                  THRESHOLD_T;

    /**
     * How many simulations is run on node that is newly added to MC tree.
     */
    public static int                  NUMBER_OF_SIMULATIONS_PER_EVALUATION;

    /**
     * Black simulation strategy that will be used by algorithm.
     */
    public static BlackFinderStrategy  BLACK_SIMULATION_STRATEGY                                                       = BlackFinderStrategy.CUSTOM;

    /**
     * White simulation strategy that will be used by algorithm.
     */
    public static WhiteFinderStrategy  WHITE_SIMULATION_STRATEGY;

    /**
     * Number of MCT steps run before the beggining of the match.
     */
    public static int                  NUMBER_OF_INITAL_STEPS;

    /**
     * Number of MCT steps run before every whites ply.
     */
    public static int                  NUMBER_OF_RUNNING_STEPS;

    /**
     * White chooser strategy used by application.
     */
    public static WhiteChooserStrategy WHITE_MOVE_CHOOSER_STRATEGY                                                     = WhiteChooserStrategy.MAX_VISIT_COUNT;

    /**
     * Black chooser strategy used by application.
     */
    public static BlackFinderStrategy  BLACK_MOVE_CHOOSER_STRATEGY                                                     = BlackFinderStrategy.CUSTOM;

    /**
     * If <code>true</code>, selection stops at nodes that don't represent
     * vanilla chess board state.
     */
    public static boolean              SELECTION_EVALUATES_CHESSBOARD                                                  = false;

    /**
     * If <code>true</code> selection doesn't just choose node with highest
     * rating. But it takes from nodes with highest rating nodes with highest
     * visit count.
     */
    public static boolean              SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING                               = false;

    /**
     * Which ending will be played by application.
     */
    public static String               ENDING;

    /**
     * If <code>true</code> white will try to check black king when they are in
     * opposition. Only applicable in KRK ending.
     */
    public static boolean              KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition                         = false;

    /**
     * If <code>true</code> white will to put bishops on adjacent diagonals.
     * Only applicable in KRK ending.
     */
    public static boolean              KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals                = false;

    /**
     * If <code>true</code> and distance between kings is more than 3 white will
     * try to move king closer or equal distance to black king.
     */
    public static boolean              HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;

    /**
     * If <code>true</code> white will when moving king try to never to move
     * king further away from black king.
     */
    public static boolean              HEURISTICS_white_KING_only_moves_coser_to_black_king                            = false;

    /**
     * If <code>true</code> white check for urgent move (moves that need to be
     * made so white doesn't loose a piece) and if such move is found white
     * makes it.
     */
    public static boolean              HEURISTICS_check_for_urgent_moves                                               = false;

    /**
     * If <code>true</code> white doesn't put pieces where they could be eaten
     * by black king.
     */
    public static boolean              HEURISTICS_only_safe_moves                                                      = false;

    /**
     * If <code>true</code> white tries to avoid moves that would lead same
     * chess board state that already appeared.
     */
    public static boolean              HEURISTICS_avoid_move_repetition                                                = false;

    /** Command line help */
    public static final String         HELP                                                                            = "Uporaba: \r\n"
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

    /**
     * Used for checking if all mandatory parameters have been set in cofig
     * file.
     */
    private static boolean[]           FILE_MANDATORY_PARAMETERS                                                       = { false, false, false, false, false,
            false, false, false                                                                                       };


    /**
     * Sets parameters gotten from command line
     * 
     * @param param
     *            command line input.
     */
    public static void initConstants(String[] param) {

        if (MCTSSetup.ENDING.equalsIgnoreCase("KRRK")) {
            MCTSSetup.WHITE_SIMULATION_STRATEGY = WhiteFinderStrategy.KRRK_ENDING;
        }
        else if (MCTSSetup.ENDING.equalsIgnoreCase("KQK")) {
            MCTSSetup.WHITE_SIMULATION_STRATEGY = WhiteFinderStrategy.KQK_ENDING;
        }
        else if (MCTSSetup.ENDING.equalsIgnoreCase("KRK")) {
            MCTSSetup.WHITE_SIMULATION_STRATEGY = WhiteFinderStrategy.KRK_ENDING;
        }
        else if (MCTSSetup.ENDING.equalsIgnoreCase("KBBK")) {
            MCTSSetup.WHITE_SIMULATION_STRATEGY = WhiteFinderStrategy.KBBK_ENDING;
        }

        // Otional arguments
        for (int x = 0; x < param.length; x++) {
            if (param[x].equals("--ig")) {
                IOSetup.WRITE_INDIVIDUAL_GAMES = false;
            }
            else if (param[x].substring(0, 4).equals("--emd")) {
                x++;
                IOSetup.EMD_DIR = param[x];
            }
            else if (param[x].equals("--conf")) {
                x++;
                IOSetup.CONFIG_FILENAME = param[x];
            }
            else if (param[x].equals("--pgn")) {
                x++;
                IOSetup.PGN_FILENAME = param[x];
            }
            else if (param[x].equals("--log")) {
                x++;
                IOSetup.LOG_FILENAME = param[x];
            }
            else if (param[x].equals("--fruit")) {
                x++;
                IOSetup.FRUIT_FILEPATH = param[x];
            }
            else if (param[x].equals("--help")) {
                System.out.println(MCTSSetup.HELP);
                System.exit(0);
            }
            else {
                System.err.println("Nepoznan parameter: " + param[x]);
                System.exit(1);
            }
        }
    }


    /**
     * Returns string representation of currently set parameters.
     * 
     * @return parameters converted to string.
     */
    public static String constantsString() {
        String rez = "NUMBER_OF_GAMES_PLAYED " + MCTSSetup.NUMBER_OF_GAMES_PLAYED + "\r\n";
        rez += "PGN_FILENAME " + IOSetup.PGN_FILENAME + "\r\n";
        rez += "LOG_FILENAME " + IOSetup.LOG_FILENAME + "\r\n";
        rez += "CONFIG_FILENAME " + IOSetup.CONFIG_FILENAME + "\r\n";
        rez += "FRUIT_FILEPATH " + IOSetup.FRUIT_FILEPATH + "\r\n";
        rez += "EMD_DIR " + IOSetup.EMD_DIR + "\r\n";
        rez += "MAX_DEPTH " + MCTSSetup.MAX_DEPTH + " (maximum number of plys in chessgame)\r\n";
        rez += "C " + MCTSSetup.C + "\r\n";
        rez += "GOBAN " + MCTSSetup.THRESHOLD_T + "\r\n";
        rez += "NUMBER_OF_SIMULATIONS_PER_EVALUATION " + MCTSSetup.NUMBER_OF_SIMULATIONS_PER_EVALUATION + " (how many simulation we run per every added node)\r\n";
        rez += "ENDING " + MCTSSetup.ENDING + "\r\n";

        rez += "BLACK_SIMULATION_STRATEGY " + MCTSSetup.BLACK_SIMULATION_STRATEGY;
        switch (MCTSSetup.BLACK_SIMULATION_STRATEGY) {
            case RANDOM:
                rez += " (black doesn' use heuristics).\r\n";
                break;
            case CENTER:
                rez += " (black king tries to move towards center).\r\n";
                break;
            case CUSTOM:
                rez += " (black king tries to move towards center, but if it's possible eats white figure and evades king opposition).\r\n";
                break;
            case PERFECT:
                rez += " (black king playes with perfect information).\r\n";
                break;
        }

        rez += "WHITE_SIMULATION_STRATEGY " + MCTSSetup.WHITE_SIMULATION_STRATEGY;
        switch (MCTSSetup.WHITE_SIMULATION_STRATEGY) {
            case RANDOM:
                rez += " (white is using random strategy - it doesn't use heuristics)\r\n";
                break;

            default:
                rez += " (white is using heuristics based on current ending)\r\n";
                break;
        }

        rez += "NUMBER_OF_INITAL_STEPS " + MCTSSetup.NUMBER_OF_INITAL_STEPS + " (number of MC steps before game starts).\r\n";
        rez += "NUMBER_OF_RUNNING_STEPS " + MCTSSetup.NUMBER_OF_RUNNING_STEPS + " (number of MC steps performed before whites move).\r\n";

        rez += "WHITE_MOVE_CHOOSER_STRATEGY " + MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY + " (white draws moves by selecting child node of root -";
        switch (MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY) {
            case RANDOM:
                rez += " chooses random node).\r\n";
                break;
            case MAX_VISIT_COUNT:
                rez += " chooses node with maximum visit count).\r\n";
                break;
            case MAX_UCT:
                rez += " choose with maximum rating).\r\n";
                break;
        }

        rez += "BLACK_MOVE_CHOOSER_STRATEGY " + MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY;
        switch (MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY) {
            case RANDOM:
                rez += " (black doesn' use heuristics).\r\n";
                break;
            case CENTER:
                rez += " (black king tries to move towards center).\r\n";
                break;
            case CUSTOM:
                rez += " (black king tries to move towards center, but if it's possible eats white figure and evades king opposition).\r\n";
                break;
            case PERFECT:
                rez += " (black king playes with perfect information).\r\n";
                break;
        }

        rez += "SELECTION_EVALUATES_CHESSBOARD " + MCTSSetup.SELECTION_EVALUATES_CHESSBOARD
                + " (if set and if selection find node that represents mat it ends).\r\n";

        rez += "SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING " + MCTSSetup.SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING
                + " (if set selection, when choosing next node, only takes nodes with highest visitcount in account).\r\n";

        rez += "WRITE_INDIVIDUAL_GAMES " + IOSetup.WRITE_INDIVIDUAL_GAMES + " (if set program writes individual games also to sgames dir).\r\n";
        rez += "\r\n";
        rez += "HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 "
                + MCTSSetup.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3
                + " (if set and white king is more than 3 title away from black king, then white moves king closer to black king).\r\n";

        rez += "HEURISTICS_white_KING_only_moves_coser_to_black_king " + MCTSSetup.HEURISTICS_white_KING_only_moves_coser_to_black_king
                + " (if set white king can't increase distance to white king).\r\n";

        rez += "HEURISTICS_check_for_urgent_moves " + MCTSSetup.HEURISTICS_check_for_urgent_moves
                + " (if set white checks if any of his figures are in dagner and tries to move them to safety).\r\n";

        rez += "HEURISTICS_only_safe_moves " + MCTSSetup.HEURISTICS_only_safe_moves + " (if set white tries to move figures to safe titles)\r\n";

        rez += "HEURISTICS_avoid_move_repetition " + MCTSSetup.HEURISTICS_avoid_move_repetition + "\r\n";

        if (MCTSSetup.ENDING.equalsIgnoreCase("KRK")) {
            rez += "KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition " + MCTSSetup.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition
                    + " (if set, when king are in opposition white tries to check).\r\n";
        }
        else if (MCTSSetup.ENDING.equalsIgnoreCase("KBBK")) {
            rez += "KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals " + MCTSSetup.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals
                    + " (if set white tries to put bishops on adjacent diagonals).\r\n";
        }

        return rez;
    }


    /**
     * Sets MCT parameters from configuration file.
     */
    public static void readConfigFile() {
        int currentLine = 0;
        try {
            File confFile = new File(IOSetup.CONFIG_FILENAME);
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

                if (currentLine == 1) {
                    MCTSSetup.parseTestParameterAndValues(line);
                    continue;
                }

                String[] words = line.split("[ \t]");
                if (words[0].equalsIgnoreCase("number_of_games") || words[0].equalsIgnoreCase("number_of_games_played")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'number_of_games' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfGames = -1;
                    try {
                        numOfGames = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'number_of_games' must be an integer (line: " + currentLine + ").");
                        System.exit(1);
                    }
                    MCTSSetup.NUMBER_OF_GAMES_PLAYED = numOfGames;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[0] = true;

                }
                else if (words[0].equalsIgnoreCase("c")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'c' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    double c = Double.NaN;
                    try {
                        c = Double.parseDouble(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'c' must be double (line: " + currentLine + ").");
                    }

                    MCTSSetup.C = c;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[1] = true;
                }
                else if (words[0].equalsIgnoreCase("goban")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'goban' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    int goban = -1;
                    try {
                        goban = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'goban' must be an integer (line: " + currentLine + " ).");
                        System.exit(1);
                    }

                    MCTSSetup.THRESHOLD_T = goban;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[2] = true;

                }
                else if (words[0].equalsIgnoreCase("number_of_simulations_per_evaluation")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'number_of_simulations_per_evaluation' (line: "
                                + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSim = -1;
                    try {
                        numOfSim = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'number_of_simulations_per_evaluation' must be an integer (line: "
                                + currentLine + " ).");
                        System.exit(1);
                    }

                    MCTSSetup.NUMBER_OF_SIMULATIONS_PER_EVALUATION = numOfSim;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[3] = true;
                }
                else if (words[0].equalsIgnoreCase("number_of_inital_steps") || words[0].equalsIgnoreCase("initial_steps")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'number_of_inital_steps' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSteps = -1;
                    try {
                        numOfSteps = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'number_of_inital_steps' must be an integer (line: " + currentLine + " ).");
                        System.exit(1);
                    }

                    MCTSSetup.NUMBER_OF_INITAL_STEPS = numOfSteps;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[4] = true;

                }
                else if (words[0].equalsIgnoreCase("number_of_running_steps") || words[0].equalsIgnoreCase("running_steps")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'number_of_running_steps' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    int numOfSteps = -1;
                    try {
                        numOfSteps = Integer.parseInt(words[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("*** ERROR: In config file token after 'number_of_running_steps' must be an integer (line: " + currentLine + " ).");
                        System.exit(1);
                    }

                    MCTSSetup.NUMBER_OF_RUNNING_STEPS = numOfSteps;
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[5] = true;

                }
                else if (words[0].equalsIgnoreCase("ending")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'ending' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    boolean notKRRK = !words[1].equalsIgnoreCase("krrk");
                    boolean notKQK = !words[1].equalsIgnoreCase("kqk");
                    boolean notKRK = !words[1].equalsIgnoreCase("krk");
                    boolean notKBBK = !words[1].equalsIgnoreCase("kbbk");
                    if (notKRRK && notKQK && notKRK && notKBBK) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid ending.");
                        System.exit(1);
                    }

                    MCTSSetup.ENDING = words[1];
                    MCTSSetup.FILE_MANDATORY_PARAMETERS[6] = true;
                }
                else if (words[0].equalsIgnoreCase("white_move_chooser_strategy") || words[0].equalsIgnoreCase("white_strat")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'white_move_chooser_strategy' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    boolean visitCount = words[1].equalsIgnoreCase("visitcount") || words[1].equalsIgnoreCase("visit_count");
                    boolean rating = words[1].equalsIgnoreCase("rating");
                    boolean random = words[1].equalsIgnoreCase("random");

                    if (!visitCount && !rating && !random) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid white move chooser strategy.");
                        System.exit(1);
                    }

                    if (visitCount) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.MAX_VISIT_COUNT;
                    }
                    else if (rating) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.MAX_UCT;
                    }
                    else if (random) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.RANDOM;
                    }

                    MCTSSetup.FILE_MANDATORY_PARAMETERS[7] = true;
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
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'file_log_level' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    Level level = null;
                    try {
                        level = Level.parse(words[1]);
                    }
                    catch (IllegalArgumentException e) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid level.");
                        System.exit(1);
                    }
                    catch (NullPointerException e) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid level.");
                        System.exit(1);
                    }

                    IOSetup.FILE_LOG_LEVEL = level;
                }
                else if (words[0].equalsIgnoreCase("console_log_level")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 1 token after 'console_log_level' (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    Level level = null;
                    try {
                        level = Level.parse(words[1]);
                    }
                    catch (IllegalArgumentException e) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid level.");
                        System.exit(1);
                    }
                    catch (NullPointerException e) {
                        System.err.println("Line " + currentLine + " in config file does not contain valid level.");
                        System.exit(1);
                    }

                    IOSetup.CONSOLE_LOG_LEVEL = level;
                }
                else if (words[0].equalsIgnoreCase("selection_evaluates")) {
                    if (words.length != 1) {
                        System.err.println("There must be no other tokens after selection_evaluates (line: " + currentLine + " ).");
                        System.exit(1);
                    }
                    MCTSSetup.SELECTION_EVALUATES_CHESSBOARD = true;
                }
                else if (words[0].equalsIgnoreCase("selection_uses_visti_count") || words[0].equalsIgnoreCase("suvc")) {
                    if (words.length != 1) {
                        System.err.println("There must be no other tokens after " + words[0] + " (line: " + currentLine + " ).");
                        System.exit(1);
                    }

                    MCTSSetup.SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING = true;
                }
                /* *************************************************************************************************
                 * *****************************HEURISTICS***********************
                 * ********************************
                 * *****************************
                 * *********************************
                 * ********************************
                 */
                else if (words[0].equalsIgnoreCase("GENERAL") || words[0].equalsIgnoreCase(MCTSSetup.ENDING)) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 2 token when setting heuristics (line: " + currentLine + ").");
                        System.exit(1);
                    }
                    if (words[1].equalsIgnoreCase("kingMovesCloserIfTooFar")) {
                        MCTSSetup.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = true;
                    }
                    else if (words[1].equalsIgnoreCase("kingMovesOnlyCloser")) {
                        MCTSSetup.HEURISTICS_white_KING_only_moves_coser_to_black_king = true;
                    }
                    else if (words[1].equalsIgnoreCase("checkForUrgentMoves")) {
                        MCTSSetup.HEURISTICS_check_for_urgent_moves = true;
                    }
                    else if (words[1].equalsIgnoreCase("safeMovesOnly")) {
                        MCTSSetup.HEURISTICS_only_safe_moves = true;
                    }
                    else if (words[1].equalsIgnoreCase("basic")) {
                        MCTSSetup.HEURISTICS_check_for_urgent_moves = true;
                        MCTSSetup.HEURISTICS_only_safe_moves = true;
                        MCTSSetup.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;
                        MCTSSetup.HEURISTICS_white_KING_only_moves_coser_to_black_king = false;
                    }
                    else if (words[1].equalsIgnoreCase("avoidMoveRepetition")) {
                        MCTSSetup.HEURISTICS_avoid_move_repetition = true;
                    }
                    else if (words[1].equalsIgnoreCase("checksIfKingsInOpposition") && MCTSSetup.ENDING.equalsIgnoreCase("KRK")) {
                        MCTSSetup.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition = true;
                    }
                    else if (words[1].equalsIgnoreCase("bishopsOnAdjacentDiagonals") && MCTSSetup.ENDING.equalsIgnoreCase("KBBK")) {
                        MCTSSetup.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals = true;
                    }
                    else {
                        System.err.println("*** ERROR: " + words[1] + " does not represent valid heuristic (line: " + currentLine + ").");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("BLACK")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 2 token when setting black move strategy (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    if (words[1].equalsIgnoreCase("random")) {
                        MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY = BlackFinderStrategy.RANDOM;
                    }
                    else if (words[1].equalsIgnoreCase("center") || words[1].equalsIgnoreCase("centre")) {
                        MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY = BlackFinderStrategy.CENTER;
                    }
                    else if (words[1].equalsIgnoreCase("normal")) {
                        MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY = BlackFinderStrategy.CUSTOM;
                    }
                    else if (words[1].equalsIgnoreCase("perfect")) {
                        MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY = BlackFinderStrategy.PERFECT;
                    }
                    else {
                        System.err.println(words[1] + " is not valid strategy (line: " + currentLine + " ).");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("white")) {
                    if (words.length != 2) {
                        System.err.println("*** ERROR: Config file must have exactly 2 token when setting white move strategy (line: " + currentLine + ").");
                        System.exit(1);
                    }
                    if (words[1].equalsIgnoreCase("random")) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.RANDOM;
                    }
                    else if (words[1].equalsIgnoreCase("vc") || words[1].equalsIgnoreCase("visit_count") || words[1].equalsIgnoreCase("visitCount")) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.MAX_VISIT_COUNT;
                    }
                    else if (words[1].equalsIgnoreCase("rating")) {
                        MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY = WhiteChooserStrategy.MAX_UCT;
                    }
                    else {
                        System.err.println(words[1] + " is not valid strategy (line: " + currentLine + " ).");
                        System.exit(1);
                    }
                }
                else if (words[0].equalsIgnoreCase("NWH") || words[0].equalsIgnoreCase("whiteNoHeuristics")) {
                    if (words.length != 1) {
                        System.err.println("There mustn't be any other token besides " + words[0] + " (line: " + currentLine + ").");
                        System.exit(1);
                    }

                    MCTSSetup.HEURISTICS_check_for_urgent_moves = false;
                    MCTSSetup.HEURISTICS_only_safe_moves = false;
                    MCTSSetup.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = false;
                    MCTSSetup.HEURISTICS_white_KING_only_moves_coser_to_black_king = false;
                    MCTSSetup.KRK_HEURISTICS_white_checkes_if_kings_are_in_opposition = false;
                    MCTSSetup.KBBK_HEURISTICS_white_tries_to_put_bishops_on_adjacent_diagonals = false;

                    MCTSSetup.WHITE_SIMULATION_STRATEGY = WhiteFinderStrategy.RANDOM;
                }
                else if (MCTSSetup.doesStringMatchValidEnding(words[0])) {
                    // we do nothing here, because, if token is valid ending,
                    // but not current ending then we ignore it
                }
                else {
                    System.err.println("Token " + words[0] + " is invalid (line: " + currentLine + ").");
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
        for (int x = 0; x < MCTSSetup.FILE_MANDATORY_PARAMETERS.length; x++) {
            if (!MCTSSetup.FILE_MANDATORY_PARAMETERS[x]) {
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
            String rez = "There are parameters missing in config file:\r\n" + sb.toString();
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


    /**
     * Parses tested parameter and its values from line
     * 
     * @param line
     *            first line of configuration file
     */
    private static void parseTestParameterAndValues(String line) {
        String[] lineArgs = line.split("[ \t]");

        // check if keyword is correct
        if (!lineArgs[0].equalsIgnoreCase("TP") && !lineArgs[0].equalsIgnoreCase("TESTED_PARAMETER")) {
            System.err.println("First line of configuration file must be tested parameter");
            System.exit(1);
        }

        // convert string representation of parameter to enum
        MCTSSetup.testParameter = ExperimentUtils.mcTestParameterStringToEnum(lineArgs[1]);

        if (MCTSSetup.testParameter == null) {
            System.err.println("Invalid test parameter");
        }

        // check if and values are specified
        if (lineArgs.length < 3) {
            System.err.println("Configuration file does not contain any values for test parameter");
        }

        // fill test parameter values
        for (int x = 2; x < lineArgs.length; x++) {
            MCTSSetup.testParameterValues.add(Double.parseDouble(lineArgs[x]));
        }

    }

}
