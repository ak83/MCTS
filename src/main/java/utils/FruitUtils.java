package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import exec.Constants;

/**
 * Class for fruit related methods
 * 
 * @author Andraz Kohne
 */
public class FruitUtils {

    /**
     * Runs fruit and returns its output.
     * 
     * @param fen
     *            fen for which fruit will be run
     * @return fruits output
     */
    public static String getOutputFromFruit(String fen) {
        String rez = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(Constants.FRUIT_FILEPATH);
            FruitUtils.writeToProcess(pr, "ucinewgame");
            FruitUtils.writeToProcess(pr, "setoption name Hash value 128");
            FruitUtils.writeToProcess(pr, "setoption name MultiPV value 100");
            FruitUtils.writeToProcess(pr, "setoption name NalimovPath value "
                    + Constants.EMD_DIR);
            FruitUtils.writeToProcess(pr,
                    "setoption name NalimovCache value 32");
            FruitUtils.writeToProcess(pr, "setoption name EGBB Path value "
                    + Constants.EMD_DIR);
            FruitUtils.writeToProcess(pr, "setoption name EGBB Cache value 32");
            FruitUtils.writeToProcess(pr, "position fen " + fen);
            FruitUtils.writeToProcess(pr, "go depth 2");
            pr.getOutputStream().close();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr
                    .getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                rez += line + "\n";
            }

            input.close();
            pr.destroy();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return rez;
    }


    /**
     * Gets best move from fruit output.
     * 
     * @param fruitOutput
     * @return best move
     */
    public static String getMoveFromFruit(String fruitOutput) {
        String[] lines = fruitOutput.split("\n");

        return lines[lines.length - 1].substring(9, 13);

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
            BufferedReader input = new BufferedReader(new InputStreamReader(pr
                    .getInputStream()));

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


    /**
     * Gets DTM of move from fruit output
     * 
     * @param move
     *            move in format such as c1c2
     * @param fruitOutput
     *            output of fruit
     * @return DTM of move
     */
    public static int getDTMOfMoveFromFruitOutput(String move,
            String fruitOutput) {
        String[] lines = fruitOutput.split("\n");
        String rez = null;
        for (String line : lines) {
            if (line.contains(move)) {
                rez = line.split(" ")[7];

                break;
            }
        }

        return Integer.parseInt(rez);
    }


    private FruitUtils() {}

}
