package mct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import moveChoosers.BlackMoveChooser;
import moveChoosers.WhiteChooserStrategy;
import moveChoosers.WhiteMoveChooser;
import moveFinders.BlackFinderStrategy;
import moveFinders.WhiteFinderStrategy;
import utils.ChessboardUtils;
import utils.MCTUtils;
import chess.Move;
import chess.chessboard.Chessboard;
import chess.chessboard.ChessboardEvalState;
import chess.chessboard.SimpleChessboard;
import config.MCTSSetup;
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
    private Chessboard simulationChessboard = new Chessboard(this.mainChessboard, "Simulation board");

    /** MCT root node */
    private MCTNode    root                 = new MCTNode(this.mainChessboard);

    /** random used by this class */
    private Random     random               = new Random();

    /** Statistics produced by MCT algorithm */
    private MCTStats   stats                = new MCTStats();

    /** Logger */
    private Logger     log;


    /**
     * Constructor
     * 
     * @param log
     *            where details are logged
     */
    public MCT(Logger log) {
        this.log = log;
    }


    /**
     * Selection phase of MCT algorithm. It traverses the tree from the root
     * node. Along the way it chooses most promising nodes. <br/>
     * It adds a new child if it reaches a node which does not have all legal
     * children added or has been visited fewer times than the value of the
     * threshold T.<br/>
     * If it reaches a child that represent a terminal game state, it just
     * returns that node.
     * 
     * @param node
     *            node to be evaluated
     * @return last node where selection stopped or the node that has been added
     *         to the tree.
     * @throws ChessboardException
     */
    private MCTNode selection(MCTNode node) throws ChessboardException {
        if (node.getEvalFromWhitesPerspective() != ChessboardEvalState.NORMAl) { return this.expansion(node); }

        boolean terminaStateReached = node.getEvalFromWhitesPerspective() != ChessboardEvalState.NORMAl;
        boolean gobanStrategy = node.visitCount < MCTSSetup.THRESHOLD_T;
        boolean nerazvitiNasledniki = node.children == null;

        if (terminaStateReached || nerazvitiNasledniki || node.children.size() == 0 || !node.areAllChildrenAdded()) { return this.expansion(node); }
        if (gobanStrategy) { return this.simulationAddsOneNode(node); }

        // get children with highest UCT value
        ArrayList<MCTNode> maxRatingNodes = MCTUtils.getNodesWithMaxRating(node);

        // select random node
        int selectedMove = this.random.nextInt(maxRatingNodes.size());
        MCTNode selectedNode = maxRatingNodes.get(selectedMove);

        this.simulationChessboard.makeAMove(selectedNode.moveNumber);
        return this.selection(selectedNode);
    }


    /**
     * Expands given node. If node represents terminal chessboard state then
     * this method just returns given node. Otherwise it adds a new child to the
     * node.
     * 
     * @param node
     * @return node if the node represent the terminal game state, otherwise it
     *         returns the newly added node.
     * @throws ChessboardException
     *             if a new child cannot be added.
     */
    private MCTNode expansion(MCTNode node) throws ChessboardException {
        if (node.getEvalFromWhitesPerspective() != ChessboardEvalState.NORMAl) { return node; }

        if (node.children == null) {
            node.children = new HashMap<Move, MCTNode>();
        }

        if (!node.areAllChildrenAdded()) {
            ArrayList<Move> unexpandedMoves = new ArrayList<Move>();
            for (Move move : node.validMoves) {
                if (node.children.get(move) == null) {
                    unexpandedMoves.add(move);
                }
            }

            Move addedMove = ChessboardUtils.getRandomMoveFromList(unexpandedMoves);
            this.simulationChessboard.makeAMove(addedMove.moveNumber);
            return node.addNextMove(addedMove);
        }

        throw new ChessboardException("All children are allready added");
    }


    /**
     * Backpropagation phase of MCTS algorithm. It updates visitcount, value and
     * other variables from the given node to the root node.
     * 
     * @param node
     *            node from which we start backpropagation
     * @param numberOfMats
     *            number of check mates that appeared in the simulations
     * @param numberOfSimulationsPerNode
     *            number of simulation used to evaluate node
     * @param addedNodeDepth
     *            depth of node from which backpropagation starts
     */
    private void backPropagation(MCTNode node, int numberOfMats, int addedNodeDepth, boolean doesAddedNodeRepresentsMat) {

        // update subtree size
        node.updateNumberOfSuccessors();

        node.numberOfMatsInNode += numberOfMats;
        node.visitCount += MCTSSetup.NUMBER_OF_SIMULATIONS_PER_EVALUATION;

        if (addedNodeDepth > node.maximumSubTreeDepth) {
            node.maximumSubTreeDepth = addedNodeDepth - node.mcDepth;
        }

        if (doesAddedNodeRepresentsMat && (addedNodeDepth < node.minimumDepthOfDescendadWhoRepresentsCheckMate)) {
            node.minimumDepthOfDescendadWhoRepresentsCheckMate = addedNodeDepth - node.mcDepth;
        }

        if (node.parent != null) {
            this.backPropagation(node.parent, numberOfMats, addedNodeDepth, doesAddedNodeRepresentsMat);
        }
    }


    /**
     * Performs a random simulation according to the {@link WhiteFinderStrategy}
     * and {@link BlackFinderStrategy}.<br/>
     * It plays random moves until a move is played that is not yet a part of
     * the tree or until a node that represent terminal game state is reached.
     * 
     * @param node
     *            added node or the node that represent a terminal game state.
     * @return added node or current node if no node was added
     * @throws ChessboardException
     */
    private MCTNode simulationAddsOneNode(MCTNode node) throws ChessboardException {

        MCTNode currNode = node;
        while (currNode.getEvalFromWhitesPerspective() == ChessboardEvalState.NORMAl) {
            if (currNode.children == null) { return this.expansion(currNode); }
            int moveNo = MCTUtils.findNextMove(currNode, MCTSSetup.WHITE_SIMULATION_STRATEGY, MCTSSetup.BLACK_SIMULATION_STRATEGY);

            // if the move is not in currNode's children
            if (currNode.children.get(new Move(moveNo)) == null) {
                this.simulationChessboard.makeAMove(moveNo);
                return currNode.addNextMove(new Move(moveNo));
            }
            else {
                this.simulationChessboard.makeAMove(moveNo);
                currNode = currNode.children.get(new Move(moveNo));
            }
        }

        if (currNode.getEvalFromWhitesPerspective() == ChessboardEvalState.BLACK_KING_MATED) {
            ++this.stats.numberOfMatsInSimAddsOneNode;
        }

        return currNode;

    }


    /**
     * Runs simulations to evaluate the node. Simulations start at the
     * chessboard state represented by the given node. It chooses moves at
     * random according to {@link WhiteFinderStrategy} and
     * {@link BlackFinderStrategy}.
     * 
     * @param node
     *            node from which we run simulations
     * @return number of mats that happened in simulations.
     * @throws ChessboardException
     */
    private int simulation(MCTNode node) throws ChessboardException {
        int rez = 0;
        Chessboard temp = new Chessboard("resetBoard", node);

        for (int x = 0; x < MCTSSetup.NUMBER_OF_SIMULATIONS_PER_EVALUATION; x++) {
            this.simulationChessboard = new Chessboard(temp, "simulation Chessboard");

            while (true) {
                ChessboardEvalState gameState = this.simulationChessboard.evaluateChessboardFromWhitesPerpective();
                if (gameState == ChessboardEvalState.NORMAl) {
                    ArrayList<Move> legalMoves = this.simulationChessboard.getLegalMoves();
                    Random random = new Random();
                    Move selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
                    this.simulationChessboard.makeAMove(selectedMove.moveNumber);
                }
                else {
                    if (gameState == ChessboardEvalState.BLACK_KING_MATED) {
                        ++this.stats.numberOfMatsInSimulation;
                        ++rez;
                    }
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
        int diff = this.simulation(node);

        SimpleChessboard newCB = new Chessboard("temp eval", node);

        boolean nodeIsMat = newCB.evaluateChessboard() == ChessboardEvalState.BLACK_KING_MATED;

        this.backPropagation(node, diff, node.mcDepth, nodeIsMat);
    }


    /**
     * Chooses a root child that represent move number. If such child does not
     * exist then a new root node is created (a tree collapse happens).
     * 
     * @param moveNumber
     *            move that will be made
     * @throws ChessboardException
     */
    public void makeMCMove(int moveNumber) throws ChessboardException {

        Move move = new Move(moveNumber);

        if (this.root.children.get(move) == null) {
            this.log.fine("V polpotezi " + (this.root.moveDepth + 1) + " je prišlo do zrušitve drevesa");
            this.stats.numberOfMCTreeColapses++;
            this.root = new MCTNode(moveNumber, this.root.moveDepth + 1, this.mainChessboard);
        }
        else {
            this.root = this.root.children.get(move);
        }

        this.mainChessboard.makeAMove(moveNumber);

    }


    /**
     * Finds a legal move that can be played.
     * 
     * @param whiteChoosingStrategy
     *            white player strategy
     * @param blackChoosingStrategy
     *            black player strategy.
     * @return random legal move number.
     * @throws ChessboardException
     */
    public int chooseAMoveNumber(WhiteChooserStrategy whiteChoosingStrategy, BlackFinderStrategy blackChoosingStrategy) throws ChessboardException {
        int rez = -1;

        if (this.root.isWhitesMove) {

            // update MCTS related statistics
            this.stats.updateNodeStats(this.root);

            // choose white move
            rez = WhiteMoveChooser.chooseAMove(this.root, whiteChoosingStrategy, this.log);

            // node that was chosen by move chooser
            MCTNode selectedNode = this.root.children.get(new Move(rez));

            // update selected node statistics
            this.stats.getNodesSelectedStatistics().updateSingleNodeStats(selectedNode);

            rez = selectedNode.moveNumber;
        }
        else {
            // choose black move
            rez = BlackMoveChooser.chooseBlackKingMove(this.mainChessboard, blackChoosingStrategy, this.log);
        }

        return rez;
    }


    /**
     * Evaluates main chess board.
     * 
     * @return main chess board evaluation value
     * @throws ChessboardException
     */
    public ChessboardEvalState evaluateMainChessBoardState() throws ChessboardException {
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
        return this.stats;
    }


    /**
     * Gets current MC tree size.
     * 
     * @return MC tree size
     */
    public int getCurrentTreeSize() {

        return this.root.getNumberOfSuccessors() + 1;
    }


    /**
     * Gets main chess board.
     * 
     * @return main chess board
     */
    public Chessboard getMainChessboard() {
        return this.mainChessboard;
    }


    /** Sets simulation chess board state to main chess board state */
    private void resetSimulationChessboard() {
        this.simulationChessboard = new Chessboard(this.mainChessboard, "Simulation board");
    }

}
