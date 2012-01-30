package exec;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import moveChoosers.BlackMoveChooser;
import moveChoosers.WhiteMoveChooser;
import moveFinders.BlackMoveFinder;
import moveFinders.WhiteMoveFinder;
import utils.MCTUtils;
import chessboard.Chessboard;
import exceptions.BlackMoveFinderException;
import exceptions.ChessboardException;
import exceptions.MCTException;
import exceptions.MCTNodeException;
import exceptions.MCTUtilsException;
import exceptions.UtilsException;
import exceptions.WhiteMoveChooserException;
import exceptions.WhiteMoveFinderException;

public class MCT {

    /*
     * root predstavlja, koren MCT drevesa in je edino vozlisce s stevilko
     * poteze 0
     */
    private MCTNode          root;

    private Chessboard       mainChessboard;
    private Chessboard       simulationChessboard;

    private int              whiteNodeRatingComputationMethod = 1;
    private int              blackNodeRatingComputationMethod = 2;

    private Random           r                                = new Random();

    private WhiteMoveChooser whiteMoveChooser                 = new WhiteMoveChooser();
    private BlackMoveChooser blackMoveChooser                 = new BlackMoveChooser();

    private MCTStats         stats                            = new MCTStats();
    private Logger           log                              = Logger.getLogger("MCTS.MCT");
    private int              currentTreeSize                  = 0;


    public MCT() {
        this.setParameters();
    }


    public MCTNode selection(MCTNode node) throws UtilsException,
            ChessboardException, MCTUtilsException {
        if (Constants.SELECTION_EVALUATES_CHESSBOARD) {
            if (node.getEvalFromWhitesPerspective() != 0) { return node; }
        }

        boolean maxDepthReached = node.plyDepth > Constants.MAX_DEPTH;
        boolean gobanStrategy = node.visitCount < Constants.GOBAN;
        boolean nerazvitiNasledniki = node.nextMoves == null;

        if (maxDepthReached || gobanStrategy || nerazvitiNasledniki
                || node.nextMoves.size() == 0) { return node; }

        ArrayList<Integer> maxRatingIndexes = MCTUtils
                .getInedexesWithMaxRating(node,
                        this.whiteNodeRatingComputationMethod,
                        this.blackNodeRatingComputationMethod);

        int selectedIndex = this.r.nextInt(maxRatingIndexes.size());
        selectedIndex = maxRatingIndexes.get(selectedIndex);

        int moveNo = node.nextMoves.get(selectedIndex).moveNumber;

        this.simulationChessboard.makeAMove(moveNo);
        return this.selection(node.nextMoves.get(selectedIndex));
    }


    /**
     * updates tree from end node to root
     * 
     * @param node
     *            node from which we start backpropagation
     * @param numberOfMats
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
                && (addedNodeDepth < node.minimumDepthOfDescendadWhoRepresentsMat)) {
            node.minimumDepthOfDescendadWhoRepresentsMat = addedNodeDepth
                    - node.mcDepth;
        }

        if (node.parent != null) {
            this.backPropagation(node.parent, numberOfMats,
                    numberOfSimulationsPerNode, addedNodeDepth,
                    doesAddedNodeRepresentsMat);
        }
    }


    /**
     * this method add one node to current true, with MCTS algorithm
     * 
     * @param node
     *            to which subtree we add node
     * @return added node or current node if no node was added
     * @throws ChessboardException
     * @throws MCTException
     * @throws MCTNodeException
     */
    public MCTNode simulationAddsOneNode(MCTNode node)
            throws ChessboardException, WhiteMoveFinderException,
            BlackMoveFinderException, MCTException, MCTNodeException {
        MCTNode currNode = node;

        if (node.nextMoves == null) {
            int moveNo = MCTUtils.findNextMove(currNode,
                    Constants.WHITE_SIMULATION_STRATEGY,
                    Constants.BLACK_SIMULATION_STRATEGY);

            if (moveNo == -1) { return currNode; }

            this.currentTreeSize++;
            this.simulationChessboard.makeAMove(moveNo);
            node.addNextMove(moveNo, false);

            return node.nextMoves.get(0);
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

            if (currNode.nextMoves == null) {
                this.currentTreeSize++;
                currNode.nextMoves = new ArrayList<MCTNode>();
                currNode.nextMoves.add(new MCTNode(currNode, moveNo));
                this.simulationChessboard.makeAMove(moveNo);

                return currNode.nextMoves.get(0);
            }

            int moveIndex = Utils
                    .indexOfMoveNumberInNextMoves(moveNo, currNode);

            if (moveIndex == -1) {
                this.currentTreeSize++;
                currNode.addNextMove(moveNo, false);
                this.simulationChessboard.makeAMove(moveNo);

                int temp = currNode.nextMoves.size() - 1;
                return currNode.nextMoves.get(temp);
            }
            else {
                this.simulationChessboard.makeAMove(moveNo);

                currNode = currNode.nextMoves.get(moveIndex);
            }

            if (currNode.plyDepth > Constants.MAX_DEPTH) { return currNode; }
        }
    }


    /**
     * runs simulations to evaluate node
     * 
     * @param node
     *            node from which we run simulations
     * @return number of mats that happened in simulations
     * @throws ChessboardException
     * @throws WhiteMoveFinderException
     * @throws BlackMoveFinderException
     */
    public int simulation(MCTNode node) throws ChessboardException,
            WhiteMoveFinderException, BlackMoveFinderException, MCTException {
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
                        int moveNo = WhiteMoveFinder.findWhiteMove(
                                this.simulationChessboard,
                                Constants.WHITE_SIMULATION_STRATEGY);
                        if (moveNo == -1)
                            throw new MCTException("to se ne bi smelo zgoditi");
                        this.simulationChessboard.makeAMove(moveNo);
                        itsWhitesTurn = !itsWhitesTurn;
                    }
                    else {
                        int moveNo = BlackMoveFinder.findBlackKingMove(
                                this.simulationChessboard,
                                Constants.BLACK_SIMULATION_STRATEGY);
                        if (moveNo == -1)
                            throw new MCTException("to se ne bio smelo zgoditi");
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


    public void resetSimulationChessboard() {
        this.simulationChessboard = new Chessboard(this.mainChessboard,
                "Simulation board");
    }


    // //////////////////////////////////////////////////////////////////
    // ////////////METODE ZA CHESSGAME/////////////////////////////
    // /////////////////////////////////////////////////////////////

    // ni stesitrana
    public void oneMCTStep() throws ChessboardException, MCTException,
            WhiteMoveFinderException, BlackMoveFinderException, UtilsException,
            MCTUtilsException, MCTNodeException {
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
                    + " je pri�lo do zru�itve drevesa");
            this.stats.numberOfMCTreeColapses++;
            this.stats.movesWhereMCTreeCollapsed.add(this.root.plyDepth + 1);
            this.stats.sizeOfTreeBeforeCollapses.add(this.currentTreeSize);
            this.root = new MCTNode(moveNumber, this.root.plyDepth + 1,
                    this.mainChessboard);
            this.currentTreeSize = 0;
        }
        else {
            this.root = this.root.nextMoves.get(index);
        }

        this.mainChessboard.makeAMove(moveNumber);

        this.log.fine("Stanje sahovnice je:\r\n" + this.mainChessboard
                + "To stanje se je pojavilo "
                + this.mainChessboard.howManyTimeHasCurrentStateAppeared()
                + "-krat.\r\n");
    }


    /**
     * @param whiteChoosingStrategy
     *            strategija po kateri beli izbira poteze
     * @param blackChoosingStrategy
     *            strategija po kateri crni izbira poteze
     * @return stevilko izbrane poteze
     * @throws WhiteMoveChooserException
     * @throws UtilsException
     * @throws BlackMoveFinderException
     * @throws ChessboardException
     * @throws MCTUtilsException
     */
    public int chooseAPlyNumber(int whiteChoosingStrategy,
            int blackChoosingStrategy) throws WhiteMoveChooserException,
            UtilsException, ChessboardException, BlackMoveFinderException,
            MCTUtilsException {
        int rez = -1;

        if (Utils.isWhitesTurn(this.root)) {
            this.stats.whiteMoveChoices.add(this.root.nexMovesToString());
            rez = this.whiteMoveChooser.chooseAMove(this.root,
                    whiteChoosingStrategy, 1);
            this.stats.whiteMovesChosen.add(rez);
            rez = this.root.nextMoves.get(rez).moveNumber;
        }
        else {
            rez = this.blackMoveChooser.chooseBlackKingMove(
                    this.mainChessboard, blackChoosingStrategy);
        }

        return rez;
    }


    public int evaluateMainChessBoardState() throws ChessboardException {
        if (this.root.plyDepth > Constants.MAX_DEPTH) { return -1; }
        return this.mainChessboard.evaluateChessboard();
    }


    public String getFEN() throws UtilsException {
        return this.mainChessboard.boardToFen();
    }


    public MCTStats getMCTStatistics() {
        MCTStats rez = new MCTStats(this.stats);
        return rez;
    }


    public int getCurrentTreeSize() {
        return this.currentTreeSize;
    }


    private void setParameters() {
        this.mainChessboard = new Chessboard("Main board");
        this.simulationChessboard = new Chessboard(this.mainChessboard,
                "Simulation board");

        this.root = new MCTNode(this.mainChessboard);

    }

}
