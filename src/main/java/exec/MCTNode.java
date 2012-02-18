package exec;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

import chessboard.Chessboard;
import exceptions.ChessboardException;

public class MCTNode {

    public MCTNode            parent;                                                     // oce
    // trenutne
    // poteze
    // public MCTNode[] nextMoves; //sinovi trenutnega vozlisca
    public ArrayList<MCTNode> nextMoves;                                                  // sinovi
    // trenutnega
    /**
     * consecutive ply number vozlisca
     */
    public int                plyDepth;                                                   // katera
    // je
    // trenutna
    // poteza
    // po
    // vrsti, v koncnicah je max
    // 100, po potrebi skenslaj

    // node depth in MC tree
    public int                mcDepth                                 = 1;
    public int                moveNumber;                                                 // stevilka
    // poteze
    public int                visitCount;                                                 // kolikokrat
    // je
    // bila
    // poteza
    public int                numberOfMatsInNode                      = 0;
    // obiskana
    public double             c;                                                          // ce
    // je
    // c
    // nizek
    // -
    // exploitation,
    // ce
    // je c visok -
    // exploration
    public boolean            isWhitesMove;
    public Chessboard         chessboard;

    // fields for statistics
    // WARNING: minimumDepthOfDescendadWhoRepresentsMat and maximumSubTreeDepth
    // represent depth from "imaginary root" (depth doesn't update when players
    // make moves, but it does reset when at tree collapse).

    /**
     * tells depth difference between this node and it's deepest descendant
     */
    public int                maximumSubTreeDepth                     = -1;
    /**
     * tells depth difference between this node and it's highest descendant that
     * represents mat
     */
    public int                minimumDepthOfDescendadWhoRepresentsMat = Integer.MAX_VALUE;

    /**
     * value of evaluateChessboardFromWhitesPerspective from chess board that is
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
        this.moveNumber = 0;
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
     * @param moveNumber
     *            numerical move representation
     * @throws ChessboardException
     */
    public MCTNode(MCTNode parent, int moveNumber) throws ChessboardException {
        this.parent = parent;
        this.plyDepth = this.parent.plyDepth + 1;
        this.moveNumber = moveNumber;
        this.visitCount = 0;
        this.c = Constants.C;
        this.isWhitesMove = !parent.isWhitesMove;
        this.mcDepth = parent.mcDepth + 1;

        Chessboard temp = new Chessboard("temp", parent);
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp
                .evaluateChessboardFromWhitesPerpective();
    }


    /**
     * this constructor is used for setting up new root after MCT tree has
     * collapsed
     * 
     * @param parent
     * @param moveNumber
     * @param depth
     * @param boardState
     * @throws ChessboardException
     */
    public MCTNode(int moveNumber, int depth, Chessboard boardState)
            throws ChessboardException {
        this.parent = null;
        this.plyDepth = depth;
        this.moveNumber = moveNumber;
        this.visitCount = 1;
        this.c = Constants.C;
        this.isWhitesMove = Utils.isWhitesMoveAtDepth(depth);

        Chessboard temp = new Chessboard(boardState, "temp");
        temp.makeAMove(moveNumber);
        this.chessboard = temp;
        this.evalFromWhitesPerspective = temp
                .evaluateChessboardFromWhitesPerpective();
    }


    /*
     * /////////////////////////////////////////////////////////////////////////
     */// //////////////////////JAVNE FUKNCIJE/////////////////////////////////
       // ////////////////////////////////////////////////////////////////////////*/

    public void addNextMove(int moveNumber) throws ChessboardException {
        if (this.nextMoves == null) {
            this.nextMoves = new ArrayList<MCTNode>();
        }

        this.nextMoves.add(new MCTNode(this, moveNumber));
    }


    public ArrayList<Integer> getNextMovesMoveNumbers() {
        if (this.nextMoves == null) { return null; }

        ArrayList<Integer> rez = new ArrayList<Integer>();
        for (int x = 0; x < this.nextMoves.size(); x++) {
            rez.add(this.nextMoves.get(x).moveNumber);
        }

        return rez;
    }


    @SuppressWarnings("unchecked")
    public ArrayList<MCTNode> getNextMovesCopy() {
        return (ArrayList<MCTNode>) this.nextMoves.clone();
    }


    @Override
    public String toString() {
        String s = "Globina: " + this.plyDepth + ", poteza: "
                + Utils.singleMoveNumberToString(this.moveNumber)
                + ", stevilo matov: " + this.numberOfMatsInNode
                + ", visitCount: " + this.visitCount + ", isWhitesMove: "
                + this.isWhitesMove + ", maximumSubTreeDepth: "
                + this.maximumSubTreeDepth
                + ", minumumDepthOfDescendWhoRepresentsMat: "
                + this.minimumDepthOfDescendadWhoRepresentsMat;
        if (this.nextMoves == null) {
            s = s + ", stevilo naslednikov: nerazvito";
        }
        else {
            s = s + ", stevilo naslednikov: " + this.nextMoves.size();
        }
        return s;
    }


    /**
     * @return summary of nodes children
     */
    public String nexMovesToString() {
        StringBuffer sb = new StringBuffer(50);
        sb.append("\tid,\tdepth,\tmove,\tnumberOfCheckmates,\tvisitcount,\tmaximumSubTreeDepth,\tminimumDepthOfDescendadThatRepresentCheckmate\r\n");
        if (this.nextMoves != null) {
            for (int x = 0; x < this.nextMoves.size(); x++) {
                MCTNode n = this.nextMoves.get(x);
                sb.append("\t"
                        + (x + 1)
                        + ",\t"
                        + n.plyDepth
                        + ",\t"
                        + Utils.singleMoveNumberToString(n.moveNumber)
                        + ",\t"
                        + n.numberOfMatsInNode
                        + ",\t"
                        + n.visitCount
                        + ",\t"
                        + n.maximumSubTreeDepth
                        + ",\t"
                        + (n.minimumDepthOfDescendadWhoRepresentsMat != Integer.MAX_VALUE ? n.minimumDepthOfDescendadWhoRepresentsMat
                                : "-1") + "\r\n");;
            }
        }
        else {
            return "This node hasn't been expanded";
        }
        return sb.toString();
    }


    public int getEvalFromWhitesPerspective() {
        return this.evalFromWhitesPerspective;
    }

}
