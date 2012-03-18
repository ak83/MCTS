package chessboard;

import java.util.ArrayList;
import java.util.TreeMap;

import mct.MCTNode;
import utils.ChessboardUtils;
import utils.Utils;
import exceptions.ChessboardException;
import exec.Constants;
import exec.Move;

public class Chessboard extends SimpleChessboard implements Cloneable {

    /**
     * Constructor that should be used with clone method.
     */
    public Chessboard() {}


    /**
     * @param name
     *            chessboard name
     */
    public Chessboard(String name) {
        this.name = name;
        this.isWhitesTurn = true;
        this.numberOfMovesMade = 0;

        this.board = new int[128];
        for (int x = 0; x < 128; x++) {
            this.board[x] = -1;
        }

        // //////////////////////////////
        // tukaj pride zacetna posatvitev
        // /////////////////////////////

        if (Constants.ENDING.equalsIgnoreCase("KRK")) {
            // this.board[0] = 0;
            // this.board[4] = 4;
            // this.board[67] = 28;

            this.board[17] = 0;
            this.board[2] = 4;
            this.board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KQK")) {
            this.board[0] = 3;
            this.board[4] = 4;
            this.board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KRRK")) {
            this.board[0] = 0;
            this.board[4] = 4;
            this.board[7] = 7;
            this.board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KBBK")) {
            this.board[2] = 2;
            this.board[4] = 4;
            this.board[5] = 5;
            this.board[67] = 28;
        }

        // /////////////////////////////
        // konec zacetne postavitve//
        // ////////////////////////////
        this.constructPiecePositionFromBoard();

        this.numberOfTimesBoardStateHasOccured.put(this.hashCode(), 1);

    }


    /**
     * Constructor which transforms MCTNode into chessboard
     * 
     * @param name
     *            name which chess board will have
     * @param node
     *            node which we transform into chess board
     */
    public Chessboard(String name, MCTNode node) {

        this.name = name;
        this.board = node.chessboard.cloneBoard();
        this.isWhitesTurn = node.chessboard.isWhitesTurn;
        this.log = node.chessboard.log;
        this.numberOfMovesMade = node.chessboard.numberOfMovesMade;
        this.numberOfTimesBoardStateHasOccured = node.chessboard
                .cloneNumberOfTimesBoardStateHasOccured();
        this.piecePosition = node.chessboard.clonePiecePosition();
        this.previousHashes = node.chessboard.clonePreviousHashes();
        this.wasBoardStateRepeatedThreeTimes = node.chessboard.wasBoardStateRepeatedThreeTimes;
    }


    /**
     * Constructor that creates clone of other chessboard.
     * 
     * @param cb
     *            chess board that we want to clone.
     * @param name
     *            chess board name.
     */
    public Chessboard(SimpleChessboard cb, String name) {
        this.name = name;
        this.isWhitesTurn = cb.getIsWhitesTurn();
        this.board = cb.cloneBoard();
        this.numberOfMovesMade = cb.getNumberOfPliesMade();
        this.numberOfTimesBoardStateHasOccured = cb
                .cloneNumberOfTimesBoardStateHasOccured();
        this.previousHashes = cb.clonePreviousHashes();
        this.piecePosition = cb.piecePosition.clone();
        this.wasBoardStateRepeatedThreeTimes = cb.wasBoardStateRepeatedThreeTimes;

    }


    /**
     * Constructor that sets board state from map
     * 
     * @param name
     *            chess board name
     * @param startingPosition
     *            map that sets pieces on board. Keys are positions and values
     *            are pieces that go on corresponding position. All positions
     *            that are not in the map are empty.
     */
    public Chessboard(String name, TreeMap<Integer, Integer> startingPosition) {
        this.name = name;
        this.board = new int[128];
        // we empty entire board
        for (int x = 0; x < 128; x++) {
            this.board[x] = -1;
        }

        // we set those board according to map
        for (int position : startingPosition.keySet()) {
            this.board[position] = startingPosition.get(position);
        }

        // we update piecesPosition
        this.constructPiecePositionFromBoard();

    }


    /**
     * Gets moves where black king eats white piece
     * 
     * @return list of moves where black king eats white piece
     * @throws ChessboardException
     */
    public ArrayList<Move> movesWhereBlackKingEatsWhite()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < 16; x++) {
            int piecePosition = this.piecePosition[x];
            if (piecePosition != -1
                    && this.isPositionAdjacentToBlackKing(piecePosition)) {
                int from = this.piecePosition[28];
                int to = piecePosition;
                if (this.isBlackKingMoveLegal(from, to)) {
                    int movedPiece = 28;
                    int targetPiece = x;
                    Move add = new Move(Utils.constructMoveNumber(from, to,
                            movedPiece, targetPiece));
                    rez.add(add);
                }
            }
        }
        return rez;
    }


    /**
     * Filters moves to those where black king is not in opposition to white
     * king
     * 
     * @param blackKingPossibleMoves
     *            posible black king moves
     * @return moves where black king avoid opposition
     */
    public ArrayList<Move> movesWhereBlackKingEvadesOposition(
            ArrayList<Move> blackKingPossibleMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < blackKingPossibleMoves.size(); x++) {
            int to = Utils
                    .getTargetPositionFromMoveNumber(blackKingPossibleMoves
                            .get(x).moveNumber);
            if (!this.willBlackKingBeInOppositionIfItMovesTo(to)) {
                Move copyMove = new Move(
                        blackKingPossibleMoves.get(x).moveNumber);
                rez.add(copyMove);
            }
        }
        return rez;
    }


    /**
     * Filters out those moves that void lead to repeated chessboard state.
     * 
     * @param whiteMoves
     *            moves that we want to filter.
     * @return filtered moves.
     * @throws Exception
     */
    public ArrayList<Move> movesWhereWhiteAvoidsMoveRepetition(
            ArrayList<Move> whiteMoves) throws Exception {
        ArrayList<Move> rez = new ArrayList<Move>();

        for (Move move : whiteMoves) {

            SimpleChessboard temp = (SimpleChessboard) this.clone();
            temp.makeAMove(move.moveNumber);
            Integer hash = temp.hashCode();
            if (!this.previousHashes.contains(hash)) {
                rez.add(move);
            }
        }

        return rez;
    }


    /**
     * Filters moves to those where white king doesn't further away from black
     * king
     * 
     * @param posMoves
     *            white moves
     * @return list moves where king doesn't further away from black king
     */
    public ArrayList<Move> movesWhereWhiteKingMovesCloserOrEqualToBlackKind(
            ArrayList<Move> posMoves) {
        int distance = this.distanceBewteenKings();
        int blackKingPosition = this.piecePosition[28];
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < posMoves.size(); x++) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(posMoves.get(x).moveNumber);

            if (movedPiece == 4) {
                int to = Utils
                        .getTargetPositionFromMoveNumber(posMoves.get(x).moveNumber);
                int currDistance = Utils.distanceBetweenPositions(to,
                        blackKingPosition);
                if (currDistance < distance) {
                    rez.add(posMoves.get(x));
                }
            }
            else {
                rez.add(posMoves.get(x));
            }
        }
        return rez;
    }


    /**
     * filter moves to those that need to be made (so that white doesn't loose a
     * piece), this method only checks protection between piece and white king
     * (it doesnt know that for instance two rooks can protect each other).
     * 
     * @param allWhiteMoves
     *            list of moves to filter
     * @return list of moves that white must do to avoid loosing a piece
     */
    public ArrayList<Move> whiteUrgentMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();

        // if black king isn't near any pieces, it can't eat them
        if (this.piecesNearPosition(this.piecePosition[28]).size() == 0) { return rez; }

        for (Move currMove : allWhiteMoves) {
            int from = Utils
                    .getStartingPositionFromMoveNumber(currMove.moveNumber);
            int to = Utils.getTargetPositionFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);

            if (movedPiece == 4) {
                for (int piecesAroundBlackKing : this
                        .piecesNearPosition(this.piecePosition[28])) {
                    if (!ChessboardUtils.arePositionsAdjacent(from,
                            this.piecePosition[piecesAroundBlackKing])
                            && ChessboardUtils.arePositionsAdjacent(to,
                                    this.piecePosition[piecesAroundBlackKing])) {
                        rez.add(currMove);
                    }
                }
            }
            else if (!ChessboardUtils.arePositionsAdjacent(from,
                    this.piecePosition[4])
                    && ChessboardUtils.arePositionsAdjacent(from,
                            this.piecePosition[28])
                    && !ChessboardUtils.arePositionsAdjacent(to,
                            this.piecePosition[28])) {
                rez.add(currMove);
            }
        }

        return rez;
    }


    /**
     * heuristic that filters moves so that white wont give black king any
     * pieces (however this method only consideres piece protected by other
     * piece if other piece is white king), but it doesn't check if any moves
     * need to be made to avoid being eaten
     * 
     * @param allWhiteMoves
     * @return list of safe moves
     */
    public ArrayList<Move> whiteSafeMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();

        for (Move currMove : allWhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            int to = Utils.getTargetPositionFromMoveNumber(currMove.moveNumber);

            // since black only has black king, white kings move is always safe
            if (movedPiece == 4) {
                // we get all pieces that could be eaten by black king
                ArrayList<Integer> piecesThatCantLooseProtection = this
                        .piecesNearPosition(this.piecePosition[28]);
                boolean addKingMove = true;
                for (int piece : piecesThatCantLooseProtection) {
                    int positionOfPiece = this.piecePosition[piece];

                    // if some piece is protected by king, then king shouldn't
                    // withdraw protection
                    if (this.isPositionAdjacentToWhiteKing(positionOfPiece)
                            && !ChessboardUtils.arePositionsAdjacent(
                                    positionOfPiece, to)) {
                        addKingMove = false;
                    }
                }
                if (addKingMove) {
                    rez.add(currMove);
                }
            }
            // if piece doesn't move near black king or is near white king, than
            // it's also safe move
            else if (!this.isPositionAdjacentToBlackKing(to)
                    || this.isPositionAdjacentToWhiteKing(to)) {
                rez.add(currMove);
            }
        }
        return rez;
    }


    public ArrayList<Move> KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(
            ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int blackKingPos = this.piecePosition[28];

        // there are two rooks and only one should be on board
        int rookPos = this.piecePosition[0];
        if (rookPos == -1) {
            rookPos = this.piecePosition[7];
        }

        // if kings are on oppossition we find those in which white checks
        // otherwise all moves are valid
        if (this.willBlackKingBeInOppositionIfItMovesTo(blackKingPos)) {
            int blackKingRank = Utils.getRankFromPosition(blackKingPos);
            int blackKingFile = Utils.getFileFromPosition(blackKingPos);

            for (Move currMove : allWhiteMoves) {
                int movedPiece = Utils
                        .getMovedPieceFromMoveNumber(currMove.moveNumber);
                int to = Utils
                        .getTargetPositionFromMoveNumber(currMove.moveNumber);

                if (movedPiece == 0 || movedPiece == 7) {
                    // poteze trdnjave
                    int rank = Utils.getRankFromPosition(to);
                    int file = Utils.getFileFromPosition(to);

                    boolean sameRank = rank == blackKingRank;
                    boolean sameFile = file == blackKingFile;
                    if ((sameFile || sameRank)
                            && !(ChessboardUtils
                                    .isPositionBetweenPositionsOnLine(
                                            this.piecePosition[4],
                                            blackKingPos, to) || ChessboardUtils
                                    .isPositionBetweenPositionsOnLine(to,
                                            this.piecePosition[4], blackKingPos))) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                else {
                    // poteze kralja
                    int whiteKingPos = this.piecePosition[4];
                    int whiteKingFile = Utils.getFileFromPosition(whiteKingPos);
                    int whiteKingRank = Utils.getRankFromPosition(whiteKingPos);
                    int rookRank = Utils.getRankFromPosition(rookPos);
                    int rookFile = Utils.getFileFromPosition(rookPos);

                    boolean areRanksSame = (whiteKingRank == blackKingRank)
                            && (whiteKingRank == rookRank);
                    boolean areFilesSame = (whiteKingFile == blackKingFile)
                            && (whiteKingFile == rookFile);

                    if (areRanksSame) {
                        int rank = Utils.getRankFromPosition(to);
                        if (rank != whiteKingRank) {
                            rez.add(new Move(currMove.moveNumber));
                        }
                    }
                    if (areFilesSame) {
                        int file = Utils.getFileFromPosition(to);
                        if (file != whiteKingFile) {
                            rez.add(new Move(currMove.moveNumber));
                        }
                    }
                }
            }
        }
        else {
            return allWhiteMoves;
        }
        return rez;
    }


    public ArrayList<Move> KBBKWhiteMovesWhereBishopsAreOnAdjacentDiagonals(
            ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allWhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                rez.add(new Move(currMove.moveNumber));
            }
            else {
                int otherBishop = movedPiece == 2 ? 5 : 2;
                int otherBishopPosition = this.piecePosition[otherBishop];
                int to = Utils
                        .getTargetPositionFromMoveNumber(currMove.moveNumber);
                if (otherBishopPosition != -1
                        && Utils.arePsotionsDiagonallyAdjacent(to,
                                otherBishopPosition)) {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
        }
        return rez;
    }


    public ArrayList<Move> filterMovesToWhiteKingMoves(
            ArrayList<Move> allwhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allwhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                rez.add(new Move(currMove.moveNumber));
            }
        }
        return rez;
    }

}
