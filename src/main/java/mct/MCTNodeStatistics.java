package mct;

import moveChoosers.WhiteMoveChooser;
import utils.MCTUtils;
import chess.chessboard.ChessboardEvalState;

/**
 * Contains statistics related to {@link MCTNode}.
 * 
 * @author ak83
 */
public class MCTNodeStatistics {

    /** Sum of check mates of nodes that {@link WhiteMoveChooser} chooses from. */
    public long   sumOfCheckmatesPerNode      = 0;

    /** Sum of visit counts of nodes that {@link WhiteMoveChooser} chooses from. */
    public long   sumOfVisitCountPerNode      = 0;

    /**
     * Sum of maximum sub tree depths of nodes that {@link WhiteMoveChooser}
     * chooses from.
     */
    public long   sumOfMaxSubTreeDepthPerNode = 0;

    /** Sum of UCT rankings of nodes that {@link WhiteMoveChooser} chooses from. */
    public double sumOfUCTRankingsPerNode     = 0;

    /** Number of {@link MCTNode}s that updated statistics. */
    public int    numberOfNodesChecked        = 0;


    /**
     * Updates statistic counters from nodes.
     * 
     * @param node
     *            parent of {@link MCTNode}s that will update statistics
     */
    public void updateNodeStats(MCTNode node) {
        // if this is terminal node, we dont to add statistics from its children
        if (node.getEvalFromWhitesPerspective() != ChessboardEvalState.NORMAl) { return; }
        for (MCTNode son : node.children.values()) {
            this.sumOfCheckmatesPerNode += son.numberOfMatsInNode;
            this.sumOfVisitCountPerNode += son.visitCount;
            this.sumOfMaxSubTreeDepthPerNode += son.maximumSubTreeDepth;
            this.sumOfUCTRankingsPerNode += MCTUtils.computeNodeRating(son);
            this.numberOfNodesChecked++;
        }
    }


    /**
     * Updates statistic counters from node.
     * 
     * @param node
     *            counters will be updated from this node
     */
    public void updateSingleNodeStats(MCTNode node) {
        this.sumOfCheckmatesPerNode += node.numberOfMatsInNode;
        this.sumOfVisitCountPerNode += node.visitCount;
        this.sumOfMaxSubTreeDepthPerNode += node.maximumSubTreeDepth;
        this.sumOfUCTRankingsPerNode += MCTUtils.computeNodeRating(node);
        this.numberOfNodesChecked++;
    }


    /**
     * Updates node related statistics.
     * 
     * @param stats
     *            statistics form which this instance will be updated
     */
    public void updateNodeStats(MCTNodeStatistics stats) {
        this.numberOfNodesChecked += stats.numberOfNodesChecked;
        this.sumOfCheckmatesPerNode += stats.sumOfCheckmatesPerNode;
        this.sumOfVisitCountPerNode += stats.sumOfVisitCountPerNode;
        this.sumOfMaxSubTreeDepthPerNode += stats.sumOfMaxSubTreeDepthPerNode;
        this.sumOfUCTRankingsPerNode += stats.sumOfUCTRankingsPerNode;
    }


    /**
     * Gets node related statistics in human readable format.
     * 
     * @return node related summary
     */
    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");

        StringBuffer rez = new StringBuffer();
        rez.append("Average number of check mates per node is " + this.getAverageCheckmatesPerNode() + newLine);
        rez.append("Average sum of visit count per node is " + this.getAverageVisitCount() + newLine);
        rez.append("Average sum of max tree depth (ply depth) for each node is " + getAverageMaxSubTreeDepth() + newLine);
        rez.append("Average UCT ranking per node is " + getAverageUCTRank() + newLine);

        return rez.toString();

    }


    /**
     * Calculates average UCT rank per node
     * 
     * @return average UCT rank
     */
    public double getAverageUCTRank() {
        return this.sumOfUCTRankingsPerNode / (double) this.numberOfNodesChecked;
    }


    /**
     * Gets average max subtree depth per node
     * 
     * @return average max subtree depth
     */
    public double getAverageMaxSubTreeDepth() {
        return this.sumOfMaxSubTreeDepthPerNode / (double) this.numberOfNodesChecked;
    }


    /**
     * Gets average visit count per node
     * 
     * @return average visit count
     */
    public double getAverageVisitCount() {
        return this.sumOfVisitCountPerNode / (double) this.numberOfNodesChecked;
    }


    /**
     * Calculates average check mate ration per node
     * 
     * @return check mate ratio
     */
    public double getCheckmateRation() {
        return (this.sumOfCheckmatesPerNode / (double) this.sumOfVisitCountPerNode) / (double) this.numberOfNodesChecked;
    }


    public double getAverageCheckmatesPerNode() {
        return this.sumOfCheckmatesPerNode / (double) this.numberOfNodesChecked;
    }

}
