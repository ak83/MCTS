package experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import chess.chessgame.ChessGameStatistics;

/**
 * Class holds and calculates statistics data for experiments
 * 
 * @author Andraz
 */
public class ExperimentStatistics {

    private Vector<ChessGameStatistics> chessGameStatistics = new Vector<ChessGameStatistics>();


    /**
     * Add new statistics for chess game
     * 
     * @param statistics
     *            {@link ChessGameStatistics} to be added
     */
    public void addChessGameStatistics(ChessGameStatistics statistics) {
        this.chessGameStatistics.add(statistics);
    }


    /**
     * Gets average DTM difference for experiment that this statistics belong
     * to.
     * 
     * @return average DTM difference
     */
    public double getWhitesAverageDTMDiff() {
        int moveCounter = 0;
        int totalDTMDiff = 0;

        for (ChessGameStatistics stats : this.chessGameStatistics) {
            for (Integer dtmDiff : stats.getWhitesDiffFromOptimal().values()) {
                moveCounter++;
                totalDTMDiff += dtmDiff;
            }
        }

        return totalDTMDiff / (double) moveCounter;
    }


    /**
     * Writes average DTM difference to CSV file.
     * 
     * @param filePath
     *            file to which DTM differences will be saved
     */
    public void writeAverageDTMDiffToCVS(String filePath) {
        // names for columns in csv file (ChessGame1, ChessGame2,....)
        StringBuffer sbColumnNames = new StringBuffer();

        // data (only one row) for each column
        StringBuffer sbRow = new StringBuffer();

        int x = 1;
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            sbColumnNames.append("ChessGame" + x + "\t");
            sbRow.append(stats.getAverageWhitesDTMDiff() + "\t");
        }

        try {
            FileWriter fw = new FileWriter(new File(filePath));
            fw.write(sbColumnNames.toString() + "\r\n" + sbRow.toString());
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }


    /**
     * Writes average MC tree size to CSV file.
     * 
     * @param filePath
     *            file to which DTM differences will be saved
     */
    public void writeAverageTreeSizeToCVS(String filePath) {
        // names for columns in csv file (ChessGame1, ChessGame2,....)
        StringBuffer sbColumnNames = new StringBuffer();

        // data (only one row) for each column
        StringBuffer sbRow = new StringBuffer();

        int x = 1;
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            sbColumnNames.append("ChessGame" + x + "\t");
            sbRow.append(stats.getAverageTreeSize() + "\t");
        }

        try {
            FileWriter fw = new FileWriter(new File(filePath));
            fw.write(sbColumnNames.toString() + "\r\n" + sbRow.toString());
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }


    /**
     * Calculates average game length.
     * 
     * @return average game lenght
     */
    public double getAverageGameLength() {
        int totalLength = 0;

        for (ChessGameStatistics stats : this.chessGameStatistics) {
            totalLength += stats.getNumberOfPliesMade();
        }

        return (double) (totalLength / (double) this.chessGameStatistics.size()) / 2.0d;
    }

}
