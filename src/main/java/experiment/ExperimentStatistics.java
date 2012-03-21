package experiment;

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

}
