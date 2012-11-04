package mct;

import java.util.ArrayList;
import java.util.HashMap;

import javax.management.RuntimeErrorException;

import moveFinders.WhiteFinderStrategy;
import utils.MCTUtils;
import utils.Utils;
import chess.Move;
import chess.chessboard.Chessboard;
import chess.chessboard.ChessboardEvalState;
import chess.chessboard.SimpleChessboard;
import config.MCTSSetup;
import exceptions.ChessboardException;

/**
 * Class that represents node in Monte Carlo tree.
 * 
 * @author Andraz Kohne
 */
public class MCTNode {

    /** Parent of current node */
    public MCTNode                parent;

    /** This nodes descendants */
    public HashMap<Move, MCTNode> children;

    /**
     * consecutive ply number of node (consecutive ply made in chess game).
     */
    public int                    moveDepth;

    /** Nodes depth in MCT tree. */
    public int                    mcDepth                                       = 1;

    /** Ply number. */
    public int                    moveNumber;

    /** How many times has this node been visited. */
    public int                    visitCount;

    /**
     * Number of times check mate has been achieved when NCT algorithm visited
     * this node
     */
    public int                    numberOfMatsInNode                            = 0;

    /**
     * C constant in MCT algorithm. Represents exploration/exploitation ration.
     * Higher that C value is more it favors exploration, lower the C value is
     * more it favors exploitation.
     */
    public double                 c;

    /** Tells if its whites turn in current ply */
    public boolean                isWhitesMove;

    /** Chess board state belonging to this ply */
    public Chessboard             chessboard;

    /**
     * Tells depth difference between this node and it's deepest descendant.
     */
    public int                    maximumSubTreeDepth                           = -1;
    /**
     * Tells depth difference between this node and it's highest descendant that
     * represents check mate.
     */
    public int                    minimumDepthOfDescendadWhoRepresentsCheckMate = Integer.MAX_VALUE;

    /**
     * Value of evaluateChessboardFromWhitesPerspective from chess board that is
     * represented with this node
     */
    private ChessboardEvalState   evalFromWhitesPerspective;

    /**
     * Number of nodes successors (subtree size)
     */
    private int                   numberOfSuccessors                            = 0;

    /**
     * All moves that are possible from this node according to
     * {@link WhiteFinderStrategy}
     */
    public ArrayList<Move>        validMoves;


    /**
     * Constructor that has receives board state from <code>board</code>.
     * 
     * @param board
     *            chess board representation from which we get chess board state
     */
    public MCTNode(SimpleChessboard board) {
        this.parent = null;
        this.moveDepth = 0;
        this.moveNumber = 0;
        this.visitCount = 1;
        this.c = MCTSSetup.C;
        this.isWhitesMove = true;
        this.chessboard = (Chessboard) board.clone();
        try {
            this.evalFromWhitesPerspective = board.evaluateChessboardFromWhitesPerpective();
            this.validMoves = this.chessboard.getLegalMoves();
        }
        catch (ChessboardException e) {
            throw new RuntimeErrorException(new Error(e));
        }
    }


    /**
     * Constructor which represents node that made a move from from its parent.
     * 
     * @param parent
     *            node parent
     * @param moveNumber
     *            numerical ply representation
     * @throws ChessboardException
     */
    public MCTNode(MCTNode parent, int moveNumber) throws ChessboardException {
        this.parent = parent;
        this.moveDepth = this.parent.moveDepth + 1;
        this.moveNumber = moveNumber;
        this.visitCount = 0;
        this.c = MCTSSetup.C;
        this.isWhitesMove = !parent.isWhitesMove;
        this.mcDepth = parent.mcDepth + 1;

        Chessboard temp = new Chessboard("temp", parent);
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp.evaluateChessboardFromWhitesPerpective();
        this.validMoves = this.chessboard.getLegalMoves();
    }


    /**
     * This constructor is used for setting up new root after MCT tree has
     * collapsed
     * 
     * @param moveNumber
     *            this nodes move number
     * @param depth
     *            this nodes depth
     * @param boardState
     *            chessboard that belong to this node
     * @throws ChessboardException
     */
    public MCTNode(int moveNumber, int depth, SimpleChessboard boardState) throws ChessboardException {
        this.parent = null;
        this.moveDepth = depth;
        this.moveNumber = moveNumber;
        this.visitCount = 1;
        this.c = MCTSSetup.C;
        this.isWhitesMove = Utils.isWhitesMoveAtDepth(depth);

        Chessboard temp = new Chessboard(boardState, "temp");
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp.evaluateChessboardFromWhitesPerpective();
        this.validMoves = this.chessboard.getLegalMoves();
    }


    /**
     * Creates child node that represents chess board state that we get by
     * making a move from current instance's chess board state.
     * 
     * @param move
     *            the move made from this instance
     * @throws ChessboardException
     */
    public MCTNode addNextMove(Move move) throws ChessboardException {
        if (this.children == null) {
            this.children = new HashMap<Move, MCTNode>();
        }

        final MCTNode newNode = new MCTNode(this, move.moveNumber);
        this.children.put(move, newNode);
        return newNode;
    }


    /**
     * Gets this nodes string representation.
     * 
     * @Override
     */
    public String toString() {
        String s = "Depth: " + this.moveDepth + ", move: " + Utils.singleMoveNumberToString(this.moveNumber) + ", numberOfCheckmates: "
                + this.numberOfMatsInNode + ", visitCount: " + this.visitCount + ", isWhitesMove: " + this.isWhitesMove + ", maximumSubTreeDepth: "
                + this.maximumSubTreeDepth + ", minumumDepthOfDescendWhoRepresentsMat: " + this.minimumDepthOfDescendadWhoRepresentsCheckMate
                + ", checkmateRatio " + (this.numberOfMatsInNode / (double) this.visitCount);
        if (this.children == null) {
            s = s + ", stevilo naslednikov: nerazvito";
        }
        else {
            s = s + ", stevilo naslednikov: " + this.children.size();
        }
        return s;
    }


    /**
     * Return this nodes descendants string representation.
     * 
     * @return summary of nodes children
     */
    public String descendantsToString() {
        StringBuffer sb = new StringBuffer(50);
        String newLine = System.getProperty("line.separator");
        sb.append("\tid,\tdepth,\tmove,\tnumberOfCheckmates,\tvisitcount,\tUCTRank,\tmaximumSubTreeDepth,\tminimumDepthOfDescendadThatRepresentCheckmate,\tcheckmateRatio"
                + newLine);
        int x = 0;
        if (this.children != null) {
            for (MCTNode son : this.children.values()) {
                sb.append("\t" + (x + 1) + ",\t" + son.moveDepth + ",\t" + Utils.singleMoveNumberToString(son.moveNumber) + ",\t" + son.numberOfMatsInNode
                        + ",\t" + son.visitCount + ",\t" + MCTUtils.computeNodeRating(son) + ",\t" + son.maximumSubTreeDepth + ",\t"
                        + (son.minimumDepthOfDescendadWhoRepresentsCheckMate != Integer.MAX_VALUE ? son.minimumDepthOfDescendadWhoRepresentsCheckMate : "-1")
                        + ",\t" + (son.numberOfMatsInNode / (double) son.visitCount) + newLine);
                ++x;
            }
        }
        else {
            return "This node hasn't been expanded";
        }
        return sb.toString();
    }


    /**
     * Gets this nodes chess board state evaluation from white perspective
     * 
     * @return evaluation of chess board state represented by this node
     */
    public ChessboardEvalState getEvalFromWhitesPerspective() {
        return this.evalFromWhitesPerspective;
    }


    /**
     * Updates number of successor nodes. If the result is to be correct all
     * successor nodes must also be updated.
     */
    public void updateNumberOfSuccessors() {
        if (this.children != null) {
            // initialize to number of child nodes
            this.numberOfSuccessors = this.children.size();

            // add number of child nodes successors
            for (MCTNode subNode : this.children.values()) {
                this.numberOfSuccessors += subNode.getNumberOfSuccessors();
            }
        }
    }


    /**
     * Return number of all successors in MC tree
     * 
     * @return number of all successors in MC tree
     */
    public int getNumberOfSuccessors() {
        return this.numberOfSuccessors;
    }


    public ArrayList<Move> getLegalMoves() {
        return this.validMoves;
    }


    /**
     * Check is each possible move (according to {@link WhiteFinderStrategy} )
     * has been added as a child node.
     */
    public boolean areAllChildrenAdded() {
        return this.validMoves.size() == this.children.size();
    }

}
