package utils;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

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

}
