package moveChoosers;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import utils.MCTUtils;

import exceptions.MCTUtilsException;
import exceptions.UtilsException;
import exceptions.WhiteMoveChooserException;
import exec.MCTNode;

public class WhiteMoveChooser {

    private Random r;
    private Logger log = Logger.getLogger("MCTS.WhiteMoveChooser");


    public WhiteMoveChooser() {
        this.r = new Random();
    }


    public WhiteMoveChooser(long seed) {
        this.r = new Random(seed);
    }


    /**
     * @param list
     * @param choosingStrategy
     *            strategija izbiranja potez 0 - max visit count 1- max rating
     * @return index poteze, ki je najboljsa za trenutno strategija
     * @throws WhiteMoveChooserException
     * @throws UtilsException
     * @throws MCTUtilsException
     */
    public int chooseAMove(MCTNode node, int choosingStrategy, int rankingMethod)
            throws WhiteMoveChooserException, UtilsException, MCTUtilsException {
        int rez = -1;
        switch (choosingStrategy) {
        case 0:
            rez = this.chooseRandomMove(node);
            break;
        case 1:
            rez = this.chooseMaxVisitCountMove(node);
            break;
        case 2:
            rez = this.chooseMaxRatingMove(node, rankingMethod);
            break;
        default:
            throw new WhiteMoveChooserException("neveljavna strategija");
        }
        String logString = "V polpotezi " + (node.plyDepth + 1)
                + " je beli izbiral med potezami :\r\n"
                + node.nexMovesToString() + "Izbral si pa je potezo "
                + (rez + 1);
        this.log.fine(logString);

        return rez;
    }


    /**
     * @param node
     *            stars sinov od katerih izbiramo poteze
     * @return
     */
    private int chooseMaxVisitCountMove(MCTNode node) {
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

        int chosenMove = this.r.nextInt(rezCand.size());
        chosenMove = rezCand.get(chosenMove);

        return chosenMove;
    }


    private int chooseMaxRatingMove(MCTNode node, int methodOfRating)
            throws UtilsException, MCTUtilsException {
        ArrayList<Integer> rezCand = new ArrayList<Integer>();
        double maxRating = MCTUtils.computeNodeRating(node.nextMoves.get(0),
                methodOfRating);

        for (int x = 0; x < node.nextMoves.size(); x++) {
            double currRating = MCTUtils.computeNodeRating(
                    node.nextMoves.get(x), methodOfRating);
            if (currRating > maxRating) {
                rezCand = new ArrayList<Integer>();
                maxRating = currRating;
            }

            if (currRating == maxRating) {
                rezCand.add(x);
            }
        }

        int chosenMove = this.r.nextInt(rezCand.size());
        chosenMove = rezCand.get(chosenMove);

        return chosenMove;
    }


    private int chooseRandomMove(MCTNode node) {
        int rez = this.r.nextInt(node.nextMoves.size());
        return rez;
    }

}
