package exec;

import java.util.ArrayList;

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
    public int                numberOfMatsInSimAddsOneNode = 0;
    /** Number of check mates appeared in simulations for current match. */
    public int                numberOfMatsInSimulation     = 0;

    /** Number of MC tree collapses in current match. */
    public int                numberOfMCTreeColapses       = 0;

    /**
     * List of that tell in which plies tree collapses occurred for current
     * match
     */
    public ArrayList<Integer> movesWhereMCTreeCollapsed    = new ArrayList<Integer>();

    /** List that holds size of MT tree before collapses for current match */
    public ArrayList<Integer> sizeOfTreeBeforeCollapses    = new ArrayList<Integer>();

    /** Holds description of moves that white was choosing from in current match */
    public ArrayList<String>  whiteMoveChoices             = new ArrayList<String>();

    /** Moves that white player has chosen in current match */
    public ArrayList<Integer> whiteMovesChosen             = new ArrayList<Integer>();


    public MCTStats() {}


    /**
     * Copy constructor.
     * 
     * @param stats
     *            MCTStats that will be copied
     */
    @SuppressWarnings("unchecked")
    public MCTStats(MCTStats stats) {
        this.numberOfMatsInSimAddsOneNode = stats.numberOfMatsInSimAddsOneNode;
        this.numberOfMatsInSimulation = stats.numberOfMatsInSimulation;
        this.numberOfMCTreeColapses = stats.numberOfMCTreeColapses;
        this.movesWhereMCTreeCollapsed = (ArrayList<Integer>) stats.movesWhereMCTreeCollapsed
                .clone();
        this.sizeOfTreeBeforeCollapses = (ArrayList<Integer>) stats.sizeOfTreeBeforeCollapses
                .clone();
        this.whiteMovesChosen = (ArrayList<Integer>) stats.whiteMovesChosen
                .clone();
        this.whiteMoveChoices = stats.whiteMoveChoices;
    }

}
