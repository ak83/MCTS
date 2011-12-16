package exec;

import java.util.ArrayList;

public class MCTStats {

    public int		numberOfMatsInSimAddsOneNode = 0;
    public int		numberOfMatsInSimulation     = 0;
    public int		numberOfMCTreeColapses       = 0;
    public ArrayList<Integer> movesWhereMCTreeCollapsed    = new ArrayList<Integer>();
    public ArrayList<Integer> sizeOfTreeBeforeCollapses    = new ArrayList<Integer>();
    public ArrayList<String>  whiteMoveChoices	     = new ArrayList<String>();
    public ArrayList<Integer> whiteMovesChosen	     = new ArrayList<Integer>();


    public MCTStats() {}


    @SuppressWarnings("unchecked")
    public MCTStats(MCTStats stats) {
	this.numberOfMatsInSimAddsOneNode = stats.numberOfMatsInSimAddsOneNode;
	this.numberOfMatsInSimulation = stats.numberOfMatsInSimulation;
	this.numberOfMCTreeColapses = stats.numberOfMCTreeColapses;
	this.movesWhereMCTreeCollapsed = (ArrayList<Integer>) stats.movesWhereMCTreeCollapsed.clone();
	this.sizeOfTreeBeforeCollapses = (ArrayList<Integer>) stats.sizeOfTreeBeforeCollapses.clone();
	this.whiteMovesChosen = (ArrayList<Integer>) stats.whiteMovesChosen.clone();
	this.whiteMoveChoices = stats.whiteMoveChoices;
    }

}
