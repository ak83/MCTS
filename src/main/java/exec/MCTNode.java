package exec;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import utils.Utils;

import chessboard.Chessboard;
import chessboard.ChessboardEvalState;
import chessboard.SimpleChessboard;
import exceptions.ChessboardException;

/**
 * Class that represents node in Monte Carlo tree.
 * 
 * @author Andraz Kohne
 */
public class MCTNode {

    /** Parent of current node */
    public MCTNode              parent;

    /** This nodes descendants */
    public ArrayList<MCTNode>   nextMoves;

    /**
     * consecutive ply number of node (consecutive ply made in chess game).
     */
    public int                  moveDepth;

    /** Nodes depth in MCT tree. */
    public int                  mcDepth                                       = 1;

    /** Ply number. */
    public int                  moveNumber;

    /** How many times has this node been visited. */
    public int                  visitCount;

    /**
     * Number of times check mate has been achieved when NCT algorithm visited
     * this node
     */
    public int                  numberOfMatsInNode                            = 0;

    /**
     * C constant in MCT algorithm. Represents exploration/exploitation ration.
     * Higher that C value is more it favors exploration, lower the C value is
     * more it favors exploitation.
     */
    public double               c;

    /** Tells if its whites turn in current ply */
    public boolean              isWhitesMove;

    /** Chess board state belonging to this ply */
    public SimpleChessboard           chessboard;

    /**
     * Tells depth difference between this node and it's deepest descendant.
     */
    public int                  maximumSubTreeDepth                           = -1;
    /**
     * Tells depth difference between this node and it's highest descendant that
     * represents check mate.
     */
    public int                  minimumDepthOfDescendadWhoRepresentsCheckMate = Integer.MAX_VALUE;

    /**
     * Value of evaluateChessboardFromWhitesPerspective from chess board that is
     * represented with this node
     */
    private ChessboardEvalState evalFromWhitesPerspective;


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
        this.c = Constants.C;
        this.isWhitesMove = true;
        this.chessboard = (SimpleChessboard) board.clone();
        try {
            this.evalFromWhitesPerspective = board
                    .evaluateChessboardFromWhitesPerpective();
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
        this.c = Constants.C;
        this.isWhitesMove = !parent.isWhitesMove;
        this.mcDepth = parent.mcDepth + 1;

        SimpleChessboard temp = new Chessboard("temp", parent);
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp
                .evaluateChessboardFromWhitesPerpective();
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
    public MCTNode(int moveNumber, int depth, SimpleChessboard boardState)
            throws ChessboardException {
        this.parent = null;
        this.moveDepth = depth;
        this.moveNumber = moveNumber;
        this.visitCount = 1;
        this.c = Constants.C;
        this.isWhitesMove = Utils.isWhitesMoveAtDepth(depth);

        SimpleChessboard temp = new Chessboard(boardState, "temp");
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp
                .evaluateChessboardFromWhitesPerpective();
    }


    /**
     * Creates child node that represents chess board state that we get by
     * making a ply from current instances chess board state.
     * 
     * @param plyNumber
     *            ply number
     * @throws ChessboardException
     */
    public void addNextMove(int plyNumber) throws ChessboardException {
        if (this.nextMoves == null) {
            this.nextMoves = new ArrayList<MCTNode>();
        }

        this.nextMoves.add(new MCTNode(this, plyNumber));
    }


    /**
     * Gets this nodes string representation.
     * 
     * @Override
     */
    public String toString() {
        String s = "Globina: " + this.moveDepth + ", poteza: "
                + Utils.singleMoveNumberToString(this.moveNumber)
                + ", stevilo matov: " + this.numberOfMatsInNode
                + ", visitCount: " + this.visitCount + ", isWhitesMove: "
                + this.isWhitesMove + ", maximumSubTreeDepth: "
                + this.maximumSubTreeDepth
                + ", minumumDepthOfDescendWhoRepresentsMat: "
                + this.minimumDepthOfDescendadWhoRepresentsCheckMate;
        if (this.nextMoves == null) {
            s = s + ", stevilo naslednikov: nerazvito";
        }
        else {
            s = s + ", stevilo naslednikov: " + this.nextMoves.size();
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
        sb.append("\tid,\tdepth,\tmove,\tnumberOfCheckmates,\tvisitcount,\tmaximumSubTreeDepth,\tminimumDepthOfDescendadThatRepresentCheckmate\r\n");
        if (this.nextMoves != null) {
            for (int x = 0; x < this.nextMoves.size(); x++) {
                MCTNode n = this.nextMoves.get(x);
                sb.append("\t"
                        + (x + 1)
                        + ",\t"
                        + n.moveDepth
                        + ",\t"
                        + Utils.singleMoveNumberToString(n.moveNumber)
                        + ",\t"
                        + n.numberOfMatsInNode
                        + ",\t"
                        + n.visitCount
                        + ",\t"
                        + n.maximumSubTreeDepth
                        + ",\t"
                        + (n.minimumDepthOfDescendadWhoRepresentsCheckMate != Integer.MAX_VALUE ? n.minimumDepthOfDescendadWhoRepresentsCheckMate
                                : "-1") + "\r\n");;
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

}
