package mct;

import moveChoosers.WhiteMoveChooser;

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
    public int        numberOfMatsInSimAddsOneNode = 0;
    /** Number of check mates appeared in simulations for current match. */
    public int        numberOfMatsInSimulation     = 0;

    /** Number of MC tree collapses in current match. */
    public int        numberOfMCTreeColapses       = 0;

    /** Statistics related to {@link MCTNode} */
    MCTNodeStatistics nodeStatistics               = new MCTNodeStatistics();

    /** Statistics related to nodes that were chosen by {@link WhiteMoveChooser} */
    MCTNodeStatistics nodesSelectedStatistics      = new MCTNodeStatistics();


    public MCTStats() {}


    /**
     * Updates node related statistics from nodes.
     * 
     * @param node
     *            parent of {@link MCTNode}s that will update statistics
     */
    public void updateNodeStats(MCTNode node) {
        this.nodeStatistics.updateNodeStats(node);
    }


    /**
     * Updates node related statistics from nodes.
     * 
     * @param node
     *            parent of {@link MCTNode}s that will update statistics
     */
    public void updateSingleNodeStats(MCTNode node) {
        this.nodeStatistics.updateSingleNodeStats(node);
    }


    /**
     * Gets node related statistics in human readable format.
     * 
     * @return node related summary
     */
    public String nodeStatsSummary() {
        return this.nodeStatistics.toString();
    }


    public MCTNodeStatistics getNodeStatistics() {
        return nodeStatistics;
    }


    
    public MCTNodeStatistics getNodesSelectedStatistics() {
        return nodesSelectedStatistics;
    }

}
