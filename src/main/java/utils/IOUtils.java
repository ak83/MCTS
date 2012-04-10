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
    public static final String WHITE_DTM_DIFFERENCE_FILE_NAME = "whiteDTMDiff";

    /** Default height for saving graphics */
    public static final int    DEFAULT_GRAPH_HEIGHT           = 500;

    /** Default width for saving graphics */
    public static final int    DEFAULT_GRAPH_WIDTH            = 500;


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
    public static void writeCSV(String filePath, Vector<String> columnNames,
            Vector<Vector<Object>> data) {
        StringBuffer sb = new StringBuffer();

        // generate first line in file
        for (String columnName : columnNames) {
            sb.append(columnName + "\t");
        }

        // generate CSV data
        for (Vector<Object> row : data) {
            sb.append("\n");
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
            ChartUtilities.saveChartAsJPEG(new File(filePath), chart,
                    DEFAULT_GRAPH_WIDTH, DEFAULT_GRAPH_HEIGHT);
        }
        catch (IOException e) {
            System.err.println("Could not save " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
