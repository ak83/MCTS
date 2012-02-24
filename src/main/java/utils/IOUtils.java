package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import exec.Constants;

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


    public static String getMoveFromFruit(String fen) {
	String rez = null;
	try {
	    Runtime rt = Runtime.getRuntime();
	    Process pr = rt.exec(Constants.FRUIT_FILEPATH);
	    IOUtils.writeToProcess(pr, "ucinewgame");
	    IOUtils.writeToProcess(pr, "setoption name Hash value 128");
	    IOUtils.writeToProcess(pr, "setoption name MultiPV value 1");
	    IOUtils.writeToProcess(pr, "setoption name NalimovPath value "
		    + Constants.EMD_DIR);
	    IOUtils.writeToProcess(pr, "setoption name NalimovCache value 32");
	    IOUtils.writeToProcess(pr, "setoption name EGBB Path value "
		    + Constants.EMD_DIR);
	    IOUtils.writeToProcess(pr, "setoption name EGBB Cache value 32");
	    IOUtils.writeToProcess(pr, "position fen " + fen);
	    IOUtils.writeToProcess(pr, "go depth 2");
	    pr.getOutputStream().close();

	    BufferedReader input = new BufferedReader(new InputStreamReader(
		    pr.getInputStream()));

	    String line = null;

	    while ((line = input.readLine()) != null) {
		rez = line;
	    }
	    input.close();
	    rez = rez.substring(9);
	    pr.destroy();
	}
	catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	return rez;
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


    


    /**
     * Checks if external program fruit is ready.
     * 
     * @return <code>true</code> if fruit is ready, otherwise <code>false</code>
     *         .
     */
    public static boolean isFruitReady() {
	String h = null;
	try {
	    Runtime rt = Runtime.getRuntime();
	    Process pr = rt.exec(Constants.FRUIT_FILEPATH);
	    writeToProcess(pr, "isready");

	    pr.getOutputStream().close();
	    BufferedReader input = new BufferedReader(new InputStreamReader(
		    pr.getInputStream()));

	    String line = null;
	    while ((line = input.readLine()) != null) {
		h = line;
	    }
	    input.close();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return h.equals("readyok");
    }
    
    private IOUtils() {}

}
