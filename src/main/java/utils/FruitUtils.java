package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import config.IOSetup;


/**
 * Class for fruit related methods
 * 
 * @author Andraz Kohne
 */
public class FruitUtils {

    private static Logger log = Logger.getLogger("MCTS.FruitUtils");


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
            Process pr = rt.exec(IOSetup.FRUIT_FILEPATH);
            FruitUtils.writeToProcess(pr, "ucinewgame");
            FruitUtils.writeToProcess(pr, "setoption name Hash value 128");
            FruitUtils.writeToProcess(pr, "setoption name MultiPV value 100");
            FruitUtils.writeToProcess(pr, "setoption name NalimovPath value " + IOSetup.EMD_DIR);
            FruitUtils.writeToProcess(pr, "setoption name NalimovCache value 32");
            FruitUtils.writeToProcess(pr, "setoption name EGBB Path value " + IOSetup.EMD_DIR);
            FruitUtils.writeToProcess(pr, "setoption name EGBB Cache value 32");
            FruitUtils.writeToProcess(pr, "position fen " + fen);
            FruitUtils.writeToProcess(pr, "go depth 2");
            pr.getOutputStream().close();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

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

        // this try-catch is here because sometimes fruit output is not in
        // expected format and program crashes
        try {
            return lines[lines.length - 1].substring(9, 13);
        }
        catch (Exception e) {
            e.printStackTrace();
            FruitUtils.log.severe("There has been an error in program. Fruit output is:\r\n" + fruitOutput);
            System.exit(3);
        }

        return "THIS SHOULD NOT HAPPEN";

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
    public static void writeToProcess(Process process, String msg) throws IOException {
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
            Process pr = rt.exec(IOSetup.FRUIT_FILEPATH);
            writeToProcess(pr, "isready");

            pr.getOutputStream().close();
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

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
    public static int getDTMOfMoveFromFruitOutput(String move, String fruitOutput) {
        String[] lines = fruitOutput.split("\n");
        String rez = null;
        for (String line : lines) {
            if (line.contains(move)) {
                try {
                    rez = line.split(" ")[7];
                }
                catch (Exception e) {
                    FruitUtils.log.severe("There has been an error in program. Fruit output is:\r\n" + fruitOutput);
                    e.printStackTrace();
                }
                break;
            }
        }

        return Integer.parseInt(rez);
    }


    /**
     * Converts moveNumber to fruit format
     * 
     * @param moveNumber
     *            move number
     * @return fruit format of move number (ie. b1b2)
     */
    public static String moveNumberToFruitString(int moveNumber) {
        int from = Utils.getStartingPositionFromMoveNumber(moveNumber);
        int to = Utils.getTargetPositionFromMoveNumber(moveNumber);

        return Utils.positionToString(from) + Utils.positionToString(to);
    }


    private FruitUtils() {}

}
