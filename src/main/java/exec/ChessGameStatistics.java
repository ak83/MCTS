package exec;

import java.util.HashMap;

public class ChessGameStatistics {

    /** Holds whites DTM difference from optimal move per turn. */
    public HashMap<Integer, Integer> whitesDiffFromOptimal = new HashMap<Integer, Integer>(
                                                                   50);

    /** Holds blacks DTM difference from optimal move per turn. */
    public HashMap<Integer, Integer> blacksDiffFromOptimal = new HashMap<Integer, Integer>(
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

}
