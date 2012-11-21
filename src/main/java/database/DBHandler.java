package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import mct.MCTNodeStatistics;
import mct.MCTSParameters;
import chess.chessgame.ChessGame;
import chess.chessgame.ChessGameStatistics;
import config.MCTSSetup;

/**
 * This handles working with database.
 * 
 * @author ak83
 */
public class DBHandler {

    /**
     * Name of the table that holds node statistics.
     */
    private static final String NODE_STATISTICS_TABLE       = "mcts_node_statistics";

    /**
     * Name of the table that holds chess game statistics.
     */
    private static final String CHESS_GAME_STATISTICS_TABLE = "chess_statistics";

    /**
     * Host. It must be in format which java understands.
     */
    private String              host;

    /**
     * Database user.
     */
    private String              username;

    /**
     * Password for database user of this instance.
     */
    private String              password;

    private Connection          connection;
    private Statement           statement;


    /**
     * Construct a SQL array from the hashmap.
     * 
     * @param hashmap
     *            hashmap where key represents a turn in the chess game.
     * @return SQL array representation of the <code>hashmap</code>
     */
    private static String converHashMaoToSQLArray(HashMap<Integer, Integer> hashmap) {

        String rez = "{";

        for (int i = 0; i < hashmap.size(); i++) {
            // add dtm to string. And until necessary also add the delimiter
            // (comma).
            rez += hashmap.get(i + 1) + (i == (hashmap.size() - 1) ? "" : ", ");
        }
        rez += "}";

        return rez;
    }


    /**
     * Calculates average from all values in the map.
     * 
     * @param map
     *            map
     * @return average of all values in <code>map</code>.
     */
    private static double getAverageFromHashMap(HashMap<Integer, Integer> map) {
        int sum = 0;
        for (Integer value : map.values()) {
            sum += value;
        }

        return sum / (double) map.size();
    }


    /**
     * @param host
     *            Database host in java format.
     * @param username
     *            db username
     * @param password
     *            db password
     */
    public DBHandler(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }


    /**
     * Initializes the connection to the database.
     * 
     * @throws SQLException
     */
    public void connect() throws SQLException {
        this.connection = DriverManager.getConnection(this.host, this.username, this.password);
        this.statement = this.connection.createStatement();

    }


    /**
     * Inserts an ended chess game into the database. All data is inserted in
     * single transaction. If connection to database cannot be established or
     * there is an error during transaction then this method doesn't do
     * anything.
     * 
     * @param chessGame
     *            statistics and other output from this chess game will be
     *            written into the database
     */
    public void insertChessGame(ChessGame chessGame) {

        MCTSParameters temp = new MCTSParameters();
        try {
            this.connect();
            this.connection.setAutoCommit(false);

            int psId = this.insertOrGetParameterSet(temp);
            int experimentId = this.insertOrGetExperiment("none", "different experiments not yet implemented");
            int statisticsId = this.insertChessGameStatistics(chessGame.getMatchStats());

            final String fen = chessGame.getFen().replaceAll("\n", "").replaceAll("\r", "");
            String sql = "INSERT INTO chess_game (endgame, fen, log, did_white_win, number_of_turns_made, statistic, experiment, mcts_parameters) VALUES ("
                    + "'" + MCTSSetup.ENDING + "', E'" + fen + "', NULL, " + chessGame.getMatchStats().didWhiteWin() + ", "
                    + chessGame.getMatchStats().getNumberOfTurnsMade() + ", " + statisticsId + ", " + experimentId + ", " + psId + ");";

            this.statement.executeUpdate(sql);
            this.connection.commit();
            this.statement.close();
            this.connection.close();
        }
        catch (SQLException e) {
            return;
        }
    }


    /**
     * Inserts {@link MCTNodeStatistics} into the database.
     * 
     * @param stats
     *            statistics to be inserted.
     * @return id of newly added statistics from database.
     * @throws SQLException
     */
    private int inserNodeStatistics(MCTNodeStatistics stats) throws SQLException {
        String sql = "INSERT INTO " + DBHandler.NODE_STATISTICS_TABLE + " (sum_checkmates, sum_visit_count, sum_uct, sum_max_subtree_depth, node_count) VALUES"
                + " (" + stats.sumOfCheckmatesPerNode + ", " + stats.sumOfVisitCountPerNode + ", " + stats.sumOfUCTRankingsPerNode + ", "
                + stats.sumOfMaxSubTreeDepthPerNode + ", " + stats.numberOfNodesChecked + ") RETURNING *";

        int rez = -1;
        ResultSet rs = this.statement.executeQuery(sql);
        rs.next();
        rez = rs.getInt("id");
        return rez;
    }


    /**
     * Inserts chess game statistics into the database.
     * 
     * @param statistics
     *            statistics that will be inserted.
     * @return index (id) of added statistics.
     * @throws SQLException
     */
    private int insertChessGameStatistics(ChessGameStatistics statistics) throws SQLException {

        int rez = -1;

        String whiteDTMPerTurn = DBHandler.converHashMaoToSQLArray(statistics.getWhitesDiffFromOptimal());
        double whiteAverageDTM = DBHandler.getAverageFromHashMap(statistics.getWhitesDiffFromOptimal());
        String blackDTMPerTurn = DBHandler.converHashMaoToSQLArray(statistics.getBlacksDiffFromOptimal());
        double blackAverageDTM = DBHandler.getAverageFromHashMap(statistics.getBlacksDiffFromOptimal());
        String treeSizePerTurn = DBHandler.converHashMaoToSQLArray(statistics.getTreeSize());
        double averageTreeSize = DBHandler.getAverageFromHashMap(statistics.getTreeSize());

        MCTNodeStatistics nodeStatistics = statistics.getStatisticsOfMCTS().getNodeStatistics();
        MCTNodeStatistics selectedNodeStatistics = statistics.getStatisticsOfMCTS().getNodesSelectedStatistics();
        int nodeStatisticsKey = this.inserNodeStatistics(nodeStatistics);
        int selectedNodeStatisticsKry = this.inserNodeStatistics(selectedNodeStatistics);

        String sql = "INSERT INTO "
                + DBHandler.CHESS_GAME_STATISTICS_TABLE
                + " (white_dtm_per_turn, white_avg_dtm, black_dtm_per_turn, black_avg_dtm, mcts_tree_size_per_turn, avg_tree_size, number_of_checkmates_in_simulations, number_of_checkmates_in_simaddonenode, number_of_tree_collapses, mcts_node_stats, mcts_selected_node_stats)"
                + " VALUES ('" + whiteDTMPerTurn + "', " + whiteAverageDTM + ", '" + blackDTMPerTurn + "', " + blackAverageDTM + ", '" + treeSizePerTurn
                + "', " + averageTreeSize + ", " + statistics.getStatisticsOfMCTS().numberOfMatsInSimulation + ", "
                + statistics.getStatisticsOfMCTS().numberOfMatsInSimAddsOneNode + ", " + statistics.getStatisticsOfMCTS().numberOfMCTreeColapses + ", "
                + nodeStatisticsKey + ", " + selectedNodeStatisticsKry + ") RETURNING *";

        ResultSet rs = this.statement.executeQuery(sql);
        rs.next();
        rez = rs.getInt("id");

        return rez;
    }


    /**
     * Insert experiment info into the database. If experiment with given name
     * already exists then instead of inserting this method returns id column of
     * that experiment.
     * 
     * @param experimentName
     *            name of the experiment.
     * @param experimentDescription
     *            experiment's description.
     * @return id of newly added experiment or id of experiment that was already
     *         in database and represent given experiment.
     * @throws SQLException
     */
    private int insertOrGetExperiment(String experimentName, String experimentDescription) throws SQLException {
        int rez = -1;
        String sql = "SELECT * FROM insert_or_select_experiment('" + experimentName + "', '" + experimentDescription
                + "') AS (id INTEGER, description TEXT, name CHARACTER VARYING(30))";

        ResultSet rs = this.statement.executeQuery(sql);
        rs.next();
        rez = rs.getInt("id");

        return rez;
    }


    /**
     * Inserts MCTS parameter values into the database if set with such values
     * does not already exists.
     * 
     * @param parameterSet
     *            values that will be written into the database.
     * @return id of newly added column, if values were not inserted into the
     *         database, then this method return id of column that represent
     *         <code>parameterSet</code>.
     * @throws SQLException
     */
    private int insertOrGetParameterSet(MCTSParameters parameterSet) throws SQLException {
        int rez = -1;
        String sql = "SELECT * FROM insert_or_select_mcts_parameter_set("
                + parameterSet.getC()
                + ", "
                + parameterSet.getThresholdT()
                + ", "
                + parameterSet.getNumberOfInitalIterations()
                + ", "
                + parameterSet.getNuberOfRunningSimulations()
                + ", '"
                + parameterSet.getWhiteMovePlayingStrategy().toString().toLowerCase()
                + "', '"
                + parameterSet.getBlackMovePlayingStrategy().toString().toLowerCase()
                + "') "
                + "AS (id INTEGER, c DOUBLE PRECISION,threshold_t INTEGER, initial_steps INTEGER, running_steps INTEGER, white_chooser_strategy white_move_chooser_strategy, black_chooser_strategy black_move_chooser_strategy);";

        ResultSet rs = this.statement.executeQuery(sql);
        rs.next();
        rez = rs.getInt("id");

        return rez;
    }

}
