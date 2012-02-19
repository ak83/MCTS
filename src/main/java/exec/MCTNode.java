package exec;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import chessboard.Chessboard;
import exceptions.ChessboardException;

/**
 * Class that represents node in Monte Carlo tree.
 * 
 * @author Andraz Kohne
 */
public class MCTNode {

    /** Parent of current node */
    public MCTNode            parent;

    /** This nodes descendants */
    public ArrayList<MCTNode> nextPlies;

    /**
     * consecutive ply number of node (consecutive ply made in chess game).
     */
    public int                plyDepth;

    /** Nodes depth in MCT tree. */
    public int                mcDepth                                       = 1;

    /** Ply number. */
    public int                plyNumber;

    /** How many times has this node been visited. */
    public int                visitCount;

    /**
     * Number of times check mate has been achieved when NCT algorithm visited
     * this node
     */
    public int                numberOfMatsInNode                            = 0;

    /**
     * C constant in MCT algorithm. Represents exploration/exploitation ration.
     * Higher that C value is more it favors exploration, lower the C value is
     * more it favors exploitation.
     */
    public double             c;

    /** Tells if its whites turn in current ply */
    public boolean            isWhitesMove;

    /** Chess board state belonging to this ply */
    public Chessboard         chessboard;

    /**
     * Tells depth difference between this node and it's deepest descendant.
     */
    public int                maximumSubTreeDepth                           = -1;
    /**
     * Tells depth difference between this node and it's highest descendant that
     * represents check mate.
     */
    public int                minimumDepthOfDescendadWhoRepresentsCheckMate = Integer.MAX_VALUE;

    /**
     * Value of evaluateChessboardFromWhitesPerspective from chess board that is
     * represented with this node
     */
    private int               evalFromWhitesPerspective;


    /**
     * Constructor that has receives board state from <code>board</code>.
     * 
     * @param board
     *            chess board representation from which we get chess board state
     */
    public MCTNode(Chessboard board) {
        this.parent = null;
        this.plyDepth = 0;
        this.plyNumber = 0;
        this.visitCount = 1;
        this.c = Constants.C;
        this.isWhitesMove = true;
        this.chessboard = (Chessboard) board.clone();
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
     * @param plyNumber
     *            numerical ply representation
     * @throws ChessboardException
     */
    public MCTNode(MCTNode parent, int plyNumber) throws ChessboardException {
        this.parent = parent;
        this.plyDepth = this.parent.plyDepth + 1;
        this.plyNumber = plyNumber;
        this.visitCount = 0;
        this.c = Constants.C;
        this.isWhitesMove = !parent.isWhitesMove;
        this.mcDepth = parent.mcDepth + 1;

        Chessboard temp = new Chessboard("temp", parent);
        temp.makeAMove(plyNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp
                .evaluateChessboardFromWhitesPerpective();
    }


    /**
     * This constructor is used for setting up new root after MCT tree has
     * collapsed
     * 
     * @param plyNumber
     *            this nodes ply number
     * @param depth
     *            this nodes depth
     * @param boardState
     *            chessboard that belong to this node
     * @throws ChessboardException
     */
    public MCTNode(int plyNumber, int depth, Chessboard boardState)
            throws ChessboardException {
        this.parent = null;
        this.plyDepth = depth;
        this.plyNumber = plyNumber;
        this.visitCount = 1;
        this.c = Constants.C;
        this.isWhitesMove = Utils.isWhitesMoveAtDepth(depth);

        Chessboard temp = new Chessboard(boardState, "temp");
        temp.makeAMove(plyNumber);
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
        if (this.nextPlies == null) {
            this.nextPlies = new ArrayList<MCTNode>();
        }

        this.nextPlies.add(new MCTNode(this, plyNumber));
    }


    /**
     * Gets this nodes string representation.
     * 
     * @Override
     */
    public String toString() {
        String s = "Globina: " + this.plyDepth + ", poteza: "
                + Utils.singleMoveNumberToString(this.plyNumber)
                + ", stevilo matov: " + this.numberOfMatsInNode
                + ", visitCount: " + this.visitCount + ", isWhitesMove: "
                + this.isWhitesMove + ", maximumSubTreeDepth: "
                + this.maximumSubTreeDepth
                + ", minumumDepthOfDescendWhoRepresentsMat: "
                + this.minimumDepthOfDescendadWhoRepresentsCheckMate;
        if (this.nextPlies == null) {
            s = s + ", stevilo naslednikov: nerazvito";
        }
        else {
            s = s + ", stevilo naslednikov: " + this.nextPlies.size();
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
        if (this.nextPlies != null) {
            for (int x = 0; x < this.nextPlies.size(); x++) {
                MCTNode n = this.nextPlies.get(x);
                sb.append("\t"
                        + (x + 1)
                        + ",\t"
                        + n.plyDepth
                        + ",\t"
                        + Utils.singleMoveNumberToString(n.plyNumber)
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


    /** Gets this nodes evaluation value from white perspective */
    public int getEvalFromWhitesPerspective() {
        return this.evalFromWhitesPerspective;
    }

}
