package moveChoosers;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import utils.MCTUtils;

import exec.MCTNode;

public class WhiteMoveChooser {

    private static Random random = new Random();
    private static Logger log    = Logger.getLogger("MCTS.WhiteMoveChooser");


    public WhiteMoveChooser() {}


    /**
     * Chooses black move from {@link MCTNode}
     * 
     * @param node
     *            root node
     * @param strategy
     *            strategy on which we choose move
     * @return chosen index of nodes child
     */
    public static int chooseAMove(MCTNode node, WhiteChooserStrategy strategy) {
        int rez = -1;
        switch (strategy) {
            case RANDOM:
                rez = WhiteMoveChooser.chooseRandomMove(node);
                break;
            case VISIT_COUNT:
                rez = WhiteMoveChooser.chooseMaxVisitCountMove(node);
                break;
            case RATING:
                rez = WhiteMoveChooser.chooseMaxRatingMove(node);
                break;
        }
        String logString = "V polpotezi " + (node.plyDepth + 1)
                + " je beli izbiral med potezami :\r\n"
                + node.nexMovesToString() + "Izbral si pa je potezo "
                + (rez + 1);
        WhiteMoveChooser.log.fine(logString);

        return rez;
    }


    /**
     * @param node
     *            stars sinov od katerih izbiramo poteze
     * @return
     */
    private static int chooseMaxVisitCountMove(MCTNode node) {
        ArrayList<Integer> rezCand = new ArrayList<Integer>();

        int maxVC = 0;
        for (int x = 0; x < node.nextMoves.size(); x++) {
            if (node.nextMoves.get(x).visitCount > maxVC) {
                maxVC = node.nextMoves.get(x).visitCount;
                rezCand = new ArrayList<Integer>();
            }

            if (node.nextMoves.get(x).visitCount == maxVC) {
                rezCand.add(x);
            }
        }

        int chosenMove = WhiteMoveChooser.random.nextInt(rezCand.size());
        chosenMove = rezCand.get(chosenMove);

        return chosenMove;
    }


    private static int chooseMaxRatingMove(MCTNode node) {
        ArrayList<Integer> rezCand = new ArrayList<Integer>();
        double maxRating = -Double.MAX_VALUE;

        for (int x = 0; x < node.nextMoves.size(); x++) {
            double currRating = MCTUtils.computeNodeRating(node.nextMoves
                    .get(x));
            if (currRating > maxRating) {
                rezCand = new ArrayList<Integer>();
                maxRating = currRating;
            }

            if (currRating == maxRating) {
                rezCand.add(x);
            }
        }

        int chosenMove = WhiteMoveChooser.random.nextInt(rezCand.size());
        chosenMove = rezCand.get(chosenMove);

        return chosenMove;
    }


    private static int chooseRandomMove(MCTNode node) {
        int rez = WhiteMoveChooser.random.nextInt(node.nextMoves.size());
        return rez;
    }

}
