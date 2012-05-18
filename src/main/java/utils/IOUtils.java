package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * Class with methods for input/output
 * 
 * @author Andraz
 */
public class IOUtils {

    private IOUtils() {}

    /**
     * prefix of files that contain whites average DTM difference from optimal
     * move.
     */
    public static final String WHITE_DTM_DIFFERENCE_FILE_NAME          = "whiteDTMDiff";

    /**
     * Prefix of files that contain number MCTS tree collapses.
     */
    public static final String NUMBER_OF_MCTS_TREE_COLLAPSES_FILE_NAME = "numberOfMCTSCollapses";

    /** Prefix of files that contain all output parameters */
    public static final String ULTIMATE_FILE_NAME                      = "all";

    /**
     * Prefix of files that contain (average) MCTS tree size
     */
    public static final String TREE_SIZE_FILE_NAME                     = "treeSize";

    /** Prefix of files that represent data connected to game length. */
    public static final String GAME_LENGTH_FILE_NAME                   = "gameLength";

    /** Prefix of files that represent white players success rate. */
    public static final String WHITE_SUCCESS_RATE_FILE_NAME            = "whiteSuccessRate";

    /** Default height for saving graphics */
    public static final int    DEFAULT_GRAPH_HEIGHT                    = 500;

    /** Default width for saving graphics */
    public static final int    DEFAULT_GRAPH_WIDTH                     = 1200;


    /**
     * Writes string into file
     * 
     * @param fileName
     *            file in which string will be written
     * @param input
     *            string to be written in file
     */
    public static void writeToFile(String fileName, String input) {
        try {

            FileWriter fw = new FileWriter(new File(fileName));
            fw.write(input);
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes CSV file
     * 
     * @param filePath
     *            file path
     * @param columnNames
     *            column names of CSV file (first line)
     * @param data
     *            data that will be written to CSV
     */
    public static void writeCSV(String filePath, Vector<String> columnNames, Vector<Vector<Object>> data) {
        StringBuffer sb = new StringBuffer();

        // generate first line in file
        for (String columnName : columnNames) {
            sb.append(columnName + "\t");
        }

        // generate CSV data
        for (Vector<Object> row : data) {
            sb.append("\r\n");
            for (Object columnData : row) {
                sb.append(columnData.toString() + "\t");
            }
        }

        // write to a file
        IOUtils.writeToFile(filePath, sb.toString());
    }


    /**
     * Saves {@link JFreeChart} as jpg picture to desired location
     * 
     * @param filePath
     *            file to which generated picture will be saved
     * @param chart
     *            {@link JFreeChart} that we want to save
     */
    public static void saveChart(String filePath, JFreeChart chart) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(filePath), chart, DEFAULT_GRAPH_WIDTH, DEFAULT_GRAPH_HEIGHT);
        }
        catch (IOException e) {
            System.err.println("Could not save " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
