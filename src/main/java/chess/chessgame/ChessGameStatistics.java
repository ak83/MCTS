package chess.chessgame;

import java.util.HashMap;

/**
 * Class for statistics that are to be logged.
 * 
 * @author Andraz
 */
public class ChessGameStatistics {

    /** Number of plies made in chess game */
    int                       numberOfPliesMade     = 0;

    /** Holds whites DTM difference from optimal move per turn. */
    HashMap<Integer, Integer> whitesDiffFromOptimal = new HashMap<Integer, Integer>(
                                                            50);

    /** Holds blacks DTM difference from optimal move per turn. */
    HashMap<Integer, Integer> blacksDiffFromOptimal = new HashMap<Integer, Integer>(
                                                            50);

    /** Holds tree size per turn. */
    HashMap<Integer, Integer> treeSize              = new HashMap<Integer, Integer>(
                                                            50);


    /**
     * Get average difference of whites moves from optimal moves
     * 
     * @return whites average DTM difference
     */
    public double getAverageWhitesDTMDiff() {
        int sum = 0;
        for (Integer turn : this.whitesDiffFromOptimal.keySet()) {
            sum += this.whitesDiffFromOptimal.get(turn);
        }

        return sum / (double) this.whitesDiffFromOptimal.size();
    }


    /**
     * Get average difference of blacks moves from optimal moves
     * 
     * @return black average DTM difference
     */
    public double getAverageBlacksDTMDiff() {
        int sum = 0;
        for (Integer turn : this.blacksDiffFromOptimal.keySet()) {
            sum += this.blacksDiffFromOptimal.get(turn);
        }

        return (double) sum / (double) this.blacksDiffFromOptimal.size();
    }


    /**
     * Gets {@link HashMap} that contains DTM difference per turn for white
     * player.
     * 
     * @return whites DTM differences
     */
    public HashMap<Integer, Integer> getWhitesDiffFromOptimal() {
        return this.whitesDiffFromOptimal;
    }


    /**
     * Gets average MC tree size.
     * 
     * @return average MC tree size
     */
    public double getAverageTreeSize() {
        int totalTreeSize = 0;
        for (Integer size : this.treeSize.values()) {
            totalTreeSize += size;
        }

        return totalTreeSize / (double) this.treeSize.keySet().size();
    }


    
    public int getNumberOfPliesMade() {
        return this.numberOfPliesMade;
    }

}
