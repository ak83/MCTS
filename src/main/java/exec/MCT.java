package exec;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import moveChoosers.BlackMoveChooser;
import moveChoosers.WhiteChooserStrategy;
import moveChoosers.WhiteMoveChooser;
import moveFinders.BlackMoveFinder;
import moveFinders.BlackFinderStrategy;
import moveFinders.WhitePlyFinder;
import utils.MCTUtils;
import utils.Utils;
import chessboard.Chessboard;
import exceptions.ChessboardException;

/**
 * Monte Carlo tree search implementation.
 * 
 * @author Andraz Kohne
 */
public class MCT {

    /** Chess board on which match is played on */
    private Chessboard mainChessboard       = new Chessboard("Main board");

    /** Chess board used for simulations */
    private Chessboard simulationChessboard = new Chessboard(
                                                    this.mainChessboard,
                                                    "Simulation board");

    /** MCT root node */
    private MCTNode    root                 = new MCTNode(this.mainChessboard);

    /** random used by this class */
    private Random     random               = new Random();

    /** Statistics produced by MCT algortihm */
    private MCTStats   stats                = new MCTStats();

    /** Logger */
    private Logger     log                  = Logger.getLogger("MCTS.MCT");

    /** Current MC tree size */
    private int        currentTreeSize      = 0;


    /** Consturctor */
    public MCT() {}


    /**
     * Selection phase of MCT algorithm.
     * 
     * @param node
     *            node to be evaluated
     * @return last node where selection stopped
     * @throws ChessboardException
     */
    public MCTNode selection(MCTNode node) throws ChessboardException {
        if (Constants.SELECTION_EVALUATES_CHESSBOARD) {
            if (node.getEvalFromWhitesPerspective() != 0) { return node; }
        }

        boolean maxDepthReached = node.plyDepth > Constants.MAX_DEPTH;
        boolean gobanStrategy = node.visitCount < Constants.GOBAN;
        boolean nerazvitiNasledniki = node.nextPlies == null;

        if (maxDepthReached || gobanStrategy || nerazvitiNasledniki
                || node.nextPlies.size() == 0) { return node; }

        ArrayList<Integer> maxRatingIndexes = MCTUtils
                .getInedexesWithMaxRating(node);

        int selectedIndex = this.random.nextInt(maxRatingIndexes.size());
        selectedIndex = maxRatingIndexes.get(selectedIndex);

        int moveNo = node.nextPlies.get(selectedIndex).plyNumber;

        this.simulationChessboard.makeAMove(moveNo);
        return this.selection(node.nextPlies.get(selectedIndex));
    }


    /**
     * Backpropagation phase of MCT algorithm
     * 
     * @param node
     *            node from which we start backpropagation
     * @param numberOfMats
     *            number of check mates that in simulations evaluating node
     * @param numberOfSimulationsPerNode
     *            number of simulation used to evaluate node
     * @param addedNodeDepth
     *            depth of node from which backpropagation starts
     */
    public void backPropagation(MCTNode node, int numberOfMats,
            int numberOfSimulationsPerNode, int addedNodeDepth,
            boolean doesAddedNodeRepresentsMat) {
        node.numberOfMatsInNode += numberOfMats;
        node.visitCount += numberOfSimulationsPerNode;

        if (addedNodeDepth > node.maximumSubTreeDepth) {
            node.maximumSubTreeDepth = addedNodeDepth - node.mcDepth;
        }

        if (doesAddedNodeRepresentsMat
                && (addedNodeDepth < node.minimumDepthOfDescendadWhoRepresentsCheckMate)) {
            node.minimumDepthOfDescendadWhoRepresentsCheckMate = addedNodeDepth
                    - node.mcDepth;
        }

        if (node.parent != null) {
            this.backPropagation(node.parent, numberOfMats,
                    numberOfSimulationsPerNode, addedNodeDepth,
                    doesAddedNodeRepresentsMat);
        }
    }


    /**
     * This method adds one node to current true, with MCTS algorithm.
     * 
     * @param node
     *            to which subtree we add node
     * @return added node or current node if no node was added
     * @throws ChessboardException
     */
    public MCTNode simulationAddsOneNode(MCTNode node)
            throws ChessboardException {
        MCTNode currNode = node;

        if (node.nextPlies == null) {
            int moveNo = MCTUtils.findNextMove(currNode,
                    Constants.WHITE_SIMULATION_STRATEGY,
                    Constants.BLACK_SIMULATION_STRATEGY);

            if (moveNo == -1) { return currNode; }

            this.currentTreeSize++;
            this.simulationChessboard.makeAMove(moveNo);
            node.addNextMove(moveNo);

            return node.nextPlies.get(0);
        }

        while (true) {

            if (currNode.getEvalFromWhitesPerspective() != 0) {
                if (currNode.getEvalFromWhitesPerspective() == 1) {
                    this.stats.numberOfMatsInSimAddsOneNode++;
                }
                return node;
            }

            int moveNo = MCTUtils.findNextMove(currNode,
                    Constants.WHITE_SIMULATION_STRATEGY,
                    Constants.BLACK_SIMULATION_STRATEGY);

            if (moveNo == -1) {
                // ni vec moznih naslednjih potez
                return currNode;
            }

            if (currNode.nextPlies == null) {
                this.currentTreeSize++;
                currNode.nextPlies = new ArrayList<MCTNode>();
                currNode.nextPlies.add(new MCTNode(currNode, moveNo));
                this.simulationChessboard.makeAMove(moveNo);

                return currNode.nextPlies.get(0);
            }

            int moveIndex = Utils
                    .indexOfMoveNumberInNextMoves(moveNo, currNode);

            if (moveIndex == -1) {
                this.currentTreeSize++;
                currNode.addNextMove(moveNo);
                this.simulationChessboard.makeAMove(moveNo);

                int temp = currNode.nextPlies.size() - 1;
                return currNode.nextPlies.get(temp);
            }
            else {
                this.simulationChessboard.makeAMove(moveNo);

                currNode = currNode.nextPlies.get(moveIndex);
            }

            if (currNode.plyDepth > Constants.MAX_DEPTH) { return currNode; }
        }
    }


    /**
     * Runs simulations to evaluate node
     * 
     * @param node
     *            node from which we run simulations
     * @return number of mats that happened in simulations
     * @throws ChessboardException
     */
    public int simulation(MCTNode node) throws ChessboardException {
        int rez = 0;
        Chessboard temp = new Chessboard("resetBoard", node);

        for (int x = 0; x < Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION; x++) {
            int currDepth = node.plyDepth;
            boolean itsWhitesTurn = Utils.isWhitesTurn(node);
            this.simulationChessboard = new Chessboard(temp,
                    "simulation Chessboard");

            while (true) {
                int eval = this.simulationChessboard
                        .evaluateChessboardFromWhitesPerpective();

                if (eval != 0) {
                    if (eval == 1) {
                        this.stats.numberOfMatsInSimulation++;
                        rez++;
                    }
                    break;
                }
                else {
                    if (itsWhitesTurn) {
                        int moveNo = WhitePlyFinder.findWhiteMove(
                                this.simulationChessboard,
                                Constants.WHITE_SIMULATION_STRATEGY);
                        this.simulationChessboard.makeAMove(moveNo);
                        itsWhitesTurn = !itsWhitesTurn;
                    }
                    else {
                        int moveNo = BlackMoveFinder.findBlackKingMove(
                                this.simulationChessboard,
                                Constants.BLACK_SIMULATION_STRATEGY);
                        this.simulationChessboard.makeAMove(moveNo);
                        itsWhitesTurn = !itsWhitesTurn;
                    }
                }
                currDepth++;
                if (currDepth > Constants.MAX_DEPTH) {
                    break;
                }
            }
        }

        return rez;
    }


    /**
     * Makes one step in MCT algorithm (selection, expansion, simulation,
     * backpropagation ).
     * 
     * @throws ChessboardException
     */
    public void oneMCTStep() throws ChessboardException {
        this.resetSimulationChessboard();

        MCTNode node = this.selection(this.root);

        node = this.simulationAddsOneNode(node);

        int diff = this.simulation(node);

        Chessboard newCB = new Chessboard("temp eval", node);

        boolean nodeIsMat = newCB.evaluateChessboard() == 1;

        this.backPropagation(node, diff,
                Constants.NUMBER_OF_SIMULATIONS_PER_EVALUATION, node.mcDepth,
                nodeIsMat);
    }


    // ni stestirana
    public void makeMCPly(int moveNumber) throws ChessboardException {

        int index = Utils.indexOfMoveNumberInNextMoves(moveNumber, this.root);

        if (index == -1) {
            this.log.fine("V polpotezi " + (this.root.plyDepth + 1)
                    + " je prišlo do zrušitve drevesa");
            this.stats.numberOfMCTreeColapses++;
            this.stats.movesWhereMCTreeCollapsed.add(this.root.plyDepth + 1);
            this.stats.sizeOfTreeBeforeCollapses.add(this.currentTreeSize);
            this.root = new MCTNode(moveNumber, this.root.plyDepth + 1,
                    this.mainChessboard);
            this.currentTreeSize = 0;
        }
        else {
            this.root = this.root.nextPlies.get(index);
        }

        this.mainChessboard.makeAMove(moveNumber);

        this.log.fine("Stanje sahovnice je:\r\n" + this.mainChessboard
                + "To stanje se je pojavilo "
                + this.mainChessboard.howManyTimeHasCurrentStateAppeared()
                + "-krat.\r\n");
    }


    /**
     * Finds move number fir given strategies.
     * 
     * @param whiteChoosingStrategy
     *            white move choosing strategy
     * @param blackChoosingStrategy
     *            black move choosing strategy
     * @return move number
     * @throws ChessboardException
     */
    public int chooseAMoveNumber(WhiteChooserStrategy whiteChoosingStrategy,
            BlackFinderStrategy blackChoosingStrategy)
            throws ChessboardException {
        int rez = -1;

        if (this.root.isWhitesMove) {
            this.stats.whiteMoveChoices.add(this.root.descendantsToString());

            rez = WhiteMoveChooser.chooseAPly(this.root, whiteChoosingStrategy);

            this.stats.whiteMovesChosen.add(rez);
            rez = this.root.nextPlies.get(rez).plyNumber;
        }
        else {
            rez = BlackMoveChooser.chooseBlackKingMove(this.mainChessboard,
                    blackChoosingStrategy);
        }

        return rez;
    }


    /**
     * Evaluates main chess board.
     * 
     * @return main chess board evaluation value
     * @throws ChessboardException
     */
    public int evaluateMainChessBoardState() throws ChessboardException {
        if (this.root.plyDepth > Constants.MAX_DEPTH) { return -1; }
        return this.mainChessboard.evaluateChessboard();
    }


    /**
     * Get current matches fen.
     * 
     * @return fen
     */
    public String getFEN() {
        return this.mainChessboard.boardToFen();
    }


    /**
     * Gets current matches statistics.
     * 
     * @return statistics
     */
    public MCTStats getMCTStatistics() {
        MCTStats rez = new MCTStats(this.stats);
        return rez;
    }


    /**
     * Gets current MC tree size.
     * 
     * @return MC tree size
     */
    public int getCurrentTreeSize() {
        return this.currentTreeSize;
    }


    /** Sets simulation chess board state to main chess board state */
    private void resetSimulationChessboard() {
        this.simulationChessboard = new Chessboard(this.mainChessboard,
                "Simulation board");
    }

}
