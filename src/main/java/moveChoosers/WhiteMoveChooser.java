package moveChoosers;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import mct.MCTNode;
import utils.MCTUtils;

/**
 * Class that handles choosing moves for white from MC Tree.
 * 
 * @author Andraz
 */
public class WhiteMoveChooser {

    /** Random used by this class */
    private static Random random = new Random();


    public WhiteMoveChooser() {}


    /**
     * Chooses whites move from {@link MCTNode}.
     * 
     * @param node
     *            root node
     * @param strategy
     *            strategy on which we choose ply move
     * @param log
     *            {@link Logger} in which details of chosen move are saved
     * @return chosen move number of nodes child
     */
    public static int chooseAMove(MCTNode node, WhiteChooserStrategy strategy, Logger log) {
        MCTNode rez = null;
        switch (strategy) {
            case RANDOM:
                rez = WhiteMoveChooser.chooseRandomNode(node);
                break;
            case VISIT_COUNT:
                rez = WhiteMoveChooser.chooseMaxVisitCountMove(node);
                break;
            case RATING:
                rez = WhiteMoveChooser.chooseMaxRatingNode(node);
                break;
        }
        String logString = "Parent node info: " + node + System.getProperty("line.separator") + "V polpotezi " + (node.moveDepth + 1)
                + " je beli izbiral med potezami :\r\n" + node.descendantsToString() + "Izbral si pa je potezo:\t " + rez;

        log.fine(logString);

        return rez.moveNumber;
    }


    /**
     * Gets son nodes with highest visit count from <code>node</code>.
     * 
     * @param node
     *            parent of node from which we choose
     * @return move number from one of son nodes with highest visit count
     */
    private static MCTNode chooseMaxVisitCountMove(MCTNode node) {
        int maxVC = 0;
        ArrayList<MCTNode> rezCand = new ArrayList<MCTNode>();

        for (MCTNode sonNode : node.children.values()) {
            if (sonNode.visitCount > maxVC) {
                maxVC = sonNode.visitCount;
                rezCand = new ArrayList<MCTNode>();
            }

            if (sonNode.visitCount == maxVC) {
                rezCand.add(sonNode);
            }
        }

        int chosenMove = WhiteMoveChooser.random.nextInt(rezCand.size());

        return rezCand.get(chosenMove);
    }


    /**
     * Return one of node's children with highest rating
     * 
     * @param node
     *            parent of nodes from which we choose
     * @return chosen node
     */
    private static MCTNode chooseMaxRatingNode(MCTNode node) {

        ArrayList<MCTNode> bestMoves = MCTUtils.getNodesWithMaxRating(node);
        int selctedMove = WhiteMoveChooser.random.nextInt(bestMoves.size());
        return bestMoves.get(selctedMove);
    }


    /**
     * Return random son of the <code>node</code>
     * 
     * @param node
     *            parent of nodes from which we choose
     * @return move number that belongs to one of nodes children, chosen at
     *         random
     */
    private static MCTNode chooseRandomNode(MCTNode node) {
        ArrayList<MCTNode> sonNodes = new ArrayList<MCTNode>(node.children.values());
        int chosenNode = WhiteMoveChooser.random.nextInt(sonNodes.size());
        return sonNodes.get(chosenNode);
    }

}
