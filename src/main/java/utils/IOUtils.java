package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for stugg like file writing and getting output from fruit
 * 
 * @author Andraz Kohne
 */
public class IOUtils {

    /**
     * Writes string into file
     * 
     * @param fileName
     *            file in which string will be written
     * @param input
     *            string to be written in file
     */
    public static void writePGN(String fileName, String input) {
        try {

            FileWriter fw = new FileWriter(new File(fileName));
            fw.write(input);
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getMoveFromFruit() {
        return null;
    }


    /**
     * Inputs string to external process.
     * 
     * @param process
     *            process
     * @param msg
     *            input message
     * @throws IOException
     */
    public static void writeToProcess(Process process, String msg)
            throws IOException {
        char[] chars = (msg + "\n").toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int x = 0; x < chars.length; x++) {
            bytes[x] = (byte) chars[x];
        }
        process.getOutputStream().write(bytes);
    }


    private IOUtils() {}

}
