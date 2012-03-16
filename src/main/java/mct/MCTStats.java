package mct;

/**
 * Class that holds match statistics that are logged on the end of the match.
 * 
 * @author Andraz Kohne
 */
public class MCTStats {

    /**
     * Number of check mates that MCT.simulationAddsOneNode added to the tree in
     * current match.
     */
    public int numberOfMatsInSimAddsOneNode = 0;
    /** Number of check mates appeared in simulations for current match. */
    public int numberOfMatsInSimulation     = 0;

    /** Number of MC tree collapses in current match. */
    public int numberOfMCTreeColapses       = 0;


    public MCTStats() {}


    /**
     * Copy constructor.
     * 
     * @param stats
     *            MCTStats that will be copied
     */
    public MCTStats(MCTStats stats) {
        this.numberOfMatsInSimAddsOneNode = stats.numberOfMatsInSimAddsOneNode;
        this.numberOfMatsInSimulation = stats.numberOfMatsInSimulation;
        this.numberOfMCTreeColapses = stats.numberOfMCTreeColapses;
    }

}
