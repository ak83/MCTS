package utils;

import java.util.ArrayList;

import chessboard.Chessboard;

import moveFinders.BlackMoveFinder;
import moveFinders.BlackFinderStrategy;
import moveFinders.WhitePlyFinder;
import moveFinders.WhiteFinderStrategy;

import exceptions.ChessboardException;
import exec.Constants;
import exec.MCTNode;

/**
 * Class that holds method that are needed by Monte Carlo Tree.
 * 
 * @author Andraz Kohne
 */
public class MCTUtils {

    /**
     * computes MCT rating for current node
     * 
     * @param node
     *            node of which we want to compute rating of
     * @return node rating
     */
    public static double computeNodeRating(MCTNode node) {

        if (!node.isWhitesMove) {
            // poteze belega
            double value = (double) node.numberOfMatsInNode
                    / (double) node.visitCount;
            double temp = Math.log(node.parent.visitCount) / node.visitCount;
            return value + node.c * Math.sqrt(temp);
        }
        else {
            // poteze crnega
            double value = (double) node.numberOfMatsInNode
                    / (double) node.visitCount;
            double temp = Math.log(node.parent.visitCount) / node.visitCount;
            return (1 - value) + node.c * Math.sqrt(temp);
        }

    }


    /**
     * Searches children of current node and returns those with highest rating.
     * 
     * @param node
     *            parent of children we computing ratings from.
     * @return list of indexes, which tell us which children have highest
     *         rating.
     */
    public static ArrayList<Integer> getInedexesWithMaxRating(MCTNode node) {
        ArrayList<Integer> rez = new ArrayList<Integer>();

        if (node.nextPlies.size() == 0) { return rez; }

        double maxRating = -Double.MAX_VALUE;
        double currRating = -Double.MAX_VALUE;

        for (int x = 0; x < node.nextPlies.size(); x++) {
            currRating = MCTUtils.computeNodeRating(node.nextPlies.get(x));

            if (currRating > maxRating) {
                maxRating = currRating;
                rez = new ArrayList<Integer>();
            }

            if (currRating == maxRating) {
                rez.add(x);
            }
        }

        // if flag is on then we only use those with highest visit count
        if (Constants.SELECTION_ALSO_USES_VISIT_COUNT_FOR_NODE_CHOOSING) {
            int maxVisitCount = Integer.MIN_VALUE;
            ArrayList<Integer> filteredRez = new ArrayList<Integer>();
            for (Integer x : rez) {
                MCTNode currNode = node.nextPlies.get(x);
                if (currNode.visitCount > maxVisitCount) {
                    maxVisitCount = currNode.visitCount;
                    filteredRez = new ArrayList<Integer>();
                }
                if (currNode.visitCount == maxVisitCount) {
                    filteredRez.add(x);
                }
            }
            return filteredRez;
        }
        return rez;
    }


    /**
     * Chooses next ply for given strategy.
     * 
     * @param node
     *            node from where we search for next ply number
     * @param whiteSimuationStrategy
     *            white simulation strategy
     * @param blackSimulationStrategy
     *            black simulation strategy
     * @return choose ply number for given strategies
     * @throws ChessboardException
     */
    public static int findNextMove(MCTNode node,
            WhiteFinderStrategy whiteSimuationStrategy,
            BlackFinderStrategy blackSimulationStrategy)
            throws ChessboardException {
        if (node.isWhitesMove) {
            Chessboard temp = new Chessboard("temp", node);
            return WhitePlyFinder.findWhiteMove(temp, whiteSimuationStrategy);
        }
        else {
            Chessboard temp = new Chessboard("temp", node);
            return BlackMoveFinder.findBlackKingMove(temp,
                    blackSimulationStrategy);
        }
    }


    private MCTUtils() {}

}
