package exec;

import static exec.Print.print;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

import utils.ChessboardUtils;
import exceptions.ChessboardException;
import exceptions.UtilsException;

public class Chessboard {

    /*
     * board hrani stevilko figure, ki je na doloceni lokaciji
     */
    private int[]   board;
    /*
     * piecePosition shrani lokacijo vsake figure na plosci
     */
    private int[]   piecePosition;

    private boolean isWhitesTurn      = true;
    private int     numberOfMovesMade = 0;
    private int     maxNumberOfMoves  = Constants.MAX_DEPTH;

    // private final boolean READLN = Constants.READLN;
    private String  name;


    public Chessboard(String name) {
        this.name = name;
        this.isWhitesTurn = true;
        this.numberOfMovesMade = 0;
        this.maxNumberOfMoves = 100;

        this.board = new int[128];
        for (int x = 0; x < 128; x++) {
            this.board[x] = -1;
        }

        // //////////////////////////////
        // tukaj pride zacetna posatvitev
        // /////////////////////////////

        if (Constants.ENDING.equalsIgnoreCase("KRK")) {
            board[0] = 0;
            board[4] = 4;
            board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KQK")) {
            board[0] = 3;
            board[4] = 4;
            board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KRRK")) {
            board[0] = 0;
            board[4] = 4;
            board[7] = 7;
            board[67] = 28;
        }
        else if (Constants.ENDING.equalsIgnoreCase("KBBK")) {
            board[2] = 2;
            board[4] = 4;
            board[5] = 5;
            board[67] = 28;
        }

        // /////////////////////////////
        // konec zacetne postavitve//
        // ////////////////////////////
        constructPiecePositionFromBoard();

    }


    /**
     * Constructor which transforms MCTNode into chessboard
     * 
     * @param name
     *            name which chess board will have
     * @param node
     *            node from which we get chess board state
     */
    public Chessboard(String name, MCTNode node) {
        this.name = name;
        this.numberOfMovesMade = node.moveDepth;
        this.maxNumberOfMoves = 100;

        this.board = new int[128];
        for (int x = 0; x < 128; x++) {
            this.board[x] = -1;
        }

        this.board = node.boardState.clone();
        this.isWhitesTurn = node.isWhitesMove;

        constructPiecePositionFromBoard();

    }


    public Chessboard(Chessboard cb, String name) {
        this.name = name;
        this.isWhitesTurn = cb.getIsWhitesTurn();
        this.board = cb.getBoard();
        this.constructPiecePositionFromBoard();
        this.numberOfMovesMade = cb.getNumberOfMovesMade();
        this.maxNumberOfMoves = 100;

    }


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


    /* ************************************************************************
     * ***************************JAVNE FUNKCIJE*******************************
     */

    /**
     * @return fen representation of board
     * @throws UtilsException
     */
    public String boardToFen() throws UtilsException {
        StringBuffer sb = new StringBuffer();
        for (int x = 112; x >= 0; x = x - 16) {
            int counter = 0;
            for (int y = 0; y < 8; y++) {
                int piece = board[x + y];

                if (piece == -1) {
                    counter++;
                    if (y == 7) {
                        sb.append(counter);
                    }
                }
                else {
                    if (counter == 0) {
                        sb.append(Utils.pieceToCharFEN(piece));
                    }
                    else {
                        sb.append(counter + "" + Utils.pieceToCharFEN(piece));
                        counter = 0;
                    }
                }
            }
            if (x > 0) {
                sb.append("/");
                counter = 0;
            }
            else {
                if (isWhitesTurn) {
                    sb.append(" w - - 0 " + (numberOfMovesMade / 2));
                }
                else {
                    sb.append(" b - - 0 " + (numberOfMovesMade / 2));
                }
            }
        }

        return sb.toString();
    }


    public void printChessboard() throws UtilsException {
        for (int x = 0; x < 24; x++) {
            System.out.print("*");
        }
        System.out.println();

        print("board: ");
        Utils.printBoardArray(board);
        print("piecePosition: ");
        Utils.printIntArray(piecePosition);

        if (this.isWhitesTurn) {
            System.out.println("beli je na potezi na plo��i " + this.name);
        }
        else {
            System.out.println("�rni je na potezi na " + this.name);
        }

        for (int x = 0; x < 24; x++) {
            System.out.print("*");
        }
        System.out.println("*");
        for (int x = 7; x >= 0; x--) {
            int baza = x * 16;
            for (int y = 0; y < 8; y++) {
                int t = board[baza + y];
                if (t == -1) {
                    System.out.print("*00");
                }
                else {
                    System.out.print("*" + Utils.pieceNumberToString(t));
                }
            }
            System.out.println("*");
        }
        for (int x = 0; x < 24; x++) {
            System.out.print("*");
        }
        System.out.println("*");
    }


    public String toString() {
        StringBuffer sb = new StringBuffer(50);
        for (int x = 0; x < 24; x++) {
            sb.append("*");
        }
        sb.append("\n");

        if (this.isWhitesTurn) {
            sb.append("Beli je na potezi na plosci " + this.name + "\n");
        }
        else {
            sb.append("crni je na potezi na plosci " + this.name + "\n");
        }

        for (int x = 0; x < 24; x++) {
            sb.append("*");
        }

        sb.append("*\n");
        for (int x = 7; x >= 0; x--) {
            int baza = x * 16;
            for (int y = 0; y < 8; y++) {
                int t = board[baza + y];
                if (t == -1) {
                    sb.append("*00");
                }
                else {
                    try {
                        sb.append("*" + Utils.pieceNumberToString(t));
                    }
                    catch (UtilsException e) {
                        throw new RuntimeErrorException(new Error(e));
                    }
                }
            }
            sb.append("*\n");
        }
        for (int x = 0; x < 24; x++) {
            sb.append("*");
        }
        sb.append("*\n");

        return sb.toString();
    }


    /**
     * @return int[] ki predstavlja plosco v X88 obliki
     */
    public int[] getBoard() {
        return this.board.clone();
    }


    /**
     * @return polje int[] v katerem je v x elementu shranjena pozicija za
     *         figuro x
     */
    public int[] getPiecesPosition() {
        return this.piecePosition.clone();
    }


    public boolean getIsWhitesTurn() {
        return this.isWhitesTurn;
    }


    public int getNumberOfMovesMade() {
        return numberOfMovesMade;
    }


    /*
     * zgradi �tevilko poteze vhod je oblike xnxn (naprimer a2a5 - premik iz
     * a2 na a5)
     */
    public int constructMoveNumberFromString(String move) throws UtilsException {
        String fromS = move.substring(0, 2);
        String toS = move.substring(2);
        int from = Utils.positionFromString(fromS);
        int to = Utils.positionFromString(toS);
        return constructMoveNumber(from, to);
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhitePawnMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhitePawnMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece < 8 || piece > 15)
            throw new ChessboardException("na from je figura" + piece);
        if (!isWhitePawnMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeWhitePawnMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackPawnMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeBlackPawnMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece < 16 || piece > 23)
            throw new ChessboardException("na from je figura" + piece);
        if (!isBlackPawnMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeBlackPawnMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeBlackPawnMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhiteRookMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhiteRookMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 0 && piece != 7)
            throw new ChessboardException("na from je figura " + piece);
        if (!isWhiteRookMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeWhiteRookMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makwWhiteRookMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackRookMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeBlackRookMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 24 && piece != 31)
            throw new ChessboardException("na from je figura " + piece);
        if (!isBlackRookMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeBlackRookMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeBlackRookMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhiteKnightMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhiteKnightMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 1 && piece != 6)
            throw new ChessboardException("na from je figura " + piece);
        if (!isWhiteKnightMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makwWhiteKnightMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeWhiteKnightMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackKnightMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeBlackKnightMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 25 && piece != 30)
            throw new ChessboardException("na from je figura " + piece);
        if (!isBlackKnightMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeBlackKnightMove(int from, int to)");
            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeBlackKnightMove(int from, int to");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhiteBishopMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhiteBishopMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 2 && piece != 5)
            throw new ChessboardException("na from je figura " + piece);
        if (!isWhiteBishopMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeWhiteBishopMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeWhiteBishopMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackBishopMove(int from, int to)
            throws ChessboardException {

        int piece = board[from];
        int targetPiece = board[to];

        // if(DEBUG) println("Za�etek makeBlackBishopMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 26 && piece != 29)
            throw new ChessboardException("na from je figura " + piece);

        if (!isBlackBishopMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false;");
            // if(DEBUG) println("Konec makeBlackBishopMove(int from, int to)");

            return false;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        // if(DEBUG) println("Konec makeBlackBishopMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhiteQueenMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhiteQueenMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 3)
            throw new ChessboardException("na from je figura " + piece);

        if (!isWhiteQueenMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeWhiteQueenMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeWhiteQueenMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackQueenMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeBlackQueenMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 27)
            throw new ChessboardException("na from je figura " + piece);
        if (!isBlackQueenMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeBlackQueenMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeBlackQueenMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeWhiteKingMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeWhiteKingMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 4)
            throw new ChessboardException("na from je figura " + piece);
        if (!isWhiteKingMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeWhiteKingMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        // if(DEBUG) println("Konec makeWhiteKingMove(int from, int to)");

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeBlackKingMove(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek makeBlackKingMove(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);

        if (piece != 28)
            throw new ChessboardException("na from je figura " + piece);
        if (!isBlackKingMoveLegal(from, to)) {
            // if(DEBUG) println("Poteza ni dovoljena, vra�am false");
            // if(DEBUG) println("Konec makeBlackKingMove(int from, int to)");

            return false;
        }

        int targetPiece = board[to];
        if (targetPiece != -1) {
            piecePosition[targetPiece] = -1;
        }

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
        numberOfMovesMade++;

        return true;
    }


    /**
     * ce je poteza mozna naredi potezo
     * 
     * @param from
     *            pozicija iz katere premikamo
     * @param to
     *            pozicija na katero premikamo
     * @return true ce je bila poteza narejena, drugace vrne false
     */
    public boolean makeAMove(int from, int to) throws ChessboardException {
        // println(this);
        isWhitesTurn = !isWhitesTurn;
        int piece = board[from];

        if (piece == 0 || piece == 7) { return makeWhiteRookMove(from, to); }
        if (piece == 1 || piece == 6) { return makeWhiteKnightMove(from, to); }
        if (piece == 2 || piece == 5) { return makeWhiteBishopMove(from, to); }
        if (piece == 3) { return makeWhiteQueenMove(from, to); }
        if (piece == 4) { return makeWhiteKingMove(from, to); }
        if (piece > 7 && piece < 16) { return makeWhitePawnMove(from, to); }
        if (piece > 15 && piece < 24) { return makeBlackPawnMove(from, to); }
        if (piece == 24 || piece == 31) { return makeBlackRookMove(from, to); }
        if (piece == 25 || piece == 30) { return makeBlackKnightMove(from, to); }
        if (piece == 26 || piece == 29) { return makeBlackBishopMove(from, to); }
        if (piece == 27) { return makeBlackQueenMove(from, to); }
        if (piece == 28) { return makeBlackKingMove(from, to); }

        throw new ChessboardException();
    }


    /**
     * ce je poteza mozna naredi potezo, preveri tudi �e je �tevilka poteze
     * pravilna, druga�e
     * 
     * @param moveNumber
     *            - stevilka poteze, ki jo hocemo narediti
     * @throws ChessboardException
     * @throws UtilsException
     */
    public boolean makeAMove(int moveNumber) throws ChessboardException {
        if (moveNumber == -1) { throw new ChessboardException(
                "neveljavna poteza"); }

        int from = Utils.getFromFromMoveNumber(moveNumber);
        int to = Utils.getToFromMoveNumber(moveNumber);
        int movedPiece = Utils.getMovedPieceFromMoveNumber(moveNumber);
        int targetPiece = Utils.getTargetPieceFromMoveNumber(moveNumber);

        if (board[from] != movedPiece || piecePosition[movedPiece] != from) { throw new ChessboardException(
                "na mestu " + from + "ni figura " + movedPiece); }
        if (targetPiece != -1
                && (board[to] != targetPiece || piecePosition[targetPiece] != to)) {
            throw new ChessboardException("na mestu " + to + " ni figura "
                    + targetPiece + ", ampak je " + board[to]);
        }
        else if (targetPiece == -1 && board[to] != -1) { throw new ChessboardException(
                "Pozijia to: " + to + " ni prazna"); }

        return makeAMove(from, to);
    }


    /**
     * Naredi potezo, toda ne preverja ali je poteza pravilna ali ne
     * 
     * @param from
     *            - pozicija s katere premikamo
     * @param to
     *            - pozicija na kotero premikamo
     * @throws ChessboardException
     *             ce je na from poziciji prazen kvadrat
     */
    public void makeAMoveQuick(int from, int to) throws ChessboardException {
        isWhitesTurn = !isWhitesTurn;

        int piece = board[from];
        if (piece == -1)
            throw new ChessboardException();

        board[from] = -1;
        board[to] = piece;
        piecePosition[piece] = to;
    }


    /**
     * Naredi potezo, toda ne preverja ali je poteza pravilna ali ne
     * 
     * @param from
     *            - pozicija s katere premikamo
     * @param to
     *            - pozicija na kotero premikamo
     * @throws ChessboardException
     *             ce je na from poziciji prazen kvadrat
     */
    public void makeAMoveQuick(Move move) throws ChessboardException {
        int from = Utils.getFromFromMove(move);
        int to = Utils.getToFromMove(move);

        makeAMove(from, to);
    }


    /**
     * Naredi potezo, toda ne preverja ali je poteza pravilna ali ne
     * 
     * @param moveNumber
     *            - stevilka poteze, ki jo delamo
     * @throws ChessboardException
     */
    public void makeAQuickMove(int moveNumber) throws ChessboardException {
        int from = Utils.getFromFromMoveNumber(moveNumber);
        int to = Utils.getToFromMoveNumber(moveNumber);

        makeAMoveQuick(from, to);
    }


    /**
     * generira vse mozne poteze za vse bele figure
     * 
     * @return ArrayList<Move> vseh moznih belih potez
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteMoves() throws ChessboardException {

        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < 16; x++) {
            int from = piecePosition[x];

            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isWhiteMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }

        return rez;
    }


    /**
     * generira vse mozne poteze za vse crne figure
     * 
     * @return ArrayList<Move> vseh moznih crnih potez
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackMoves() throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 16; x < 32; x++) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isBlackMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }

        return rez;
    }


    /**
     * generira vse mozne poteze
     * 
     * @return ArrayList<Move> vseh moznih potez belih kmetov
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhitePawnMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 8; x < 16; x++) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isWhitePawnMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crnih kmetov
     * 
     * @return ArrayList<Move> vseh moznih potez crnih kmetov
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackPawnMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 16; x < 24; x++) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isBlackPawnMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze belih trdnjav
     * 
     * @return ArrayList<Move> vseh moznih potez belih trdnjav
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteRookMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] rooks = { 0, 7 };

        for (int x : rooks) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isWhiteRookMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crnih trdnjav
     * 
     * @return ArrayList<Move> vseh moznih potez crnih trdnjav
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackRookMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] rooks = { 24, 31 };

        for (int x : rooks) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);
                for (int y = 0; y < 128; y++) {
                    if (isBlackRookMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze belih konjev
     * 
     * @return ArrayList<Move> vseh moznih potez belih konjev
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteKnightMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] knights = { 1, 6 };

        for (int x : knights) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);

                for (int y = 0; y < 128; y++) {
                    if (isWhiteKnightMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crnih konjev
     * 
     * @return ArrayList<Move> vseh moznih potez crnih konjev
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackKnightMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] knights = { 25, 30 };

        for (int x : knights) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);

                for (int y = 0; y < 128; y++) {
                    if (isBlackKnightMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze belih tekacev
     * 
     * @return ArrayList<Move> vseh moznih potez belih tekacev
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteBishopMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] bishops = { 2, 5 };

        for (int x : bishops) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);

                for (int y = 0; y < 128; y++) {
                    if (isWhiteBishopMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crnih tekacev
     * 
     * @return ArrayList<Move> vseh moznih potez crnih tekacev
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackBishopMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int[] bishops = { 26, 29 };

        for (int x : bishops) {
            int from = piecePosition[x];
            if (from != -1) {
                if (!isPositionLegal(from))
                    throw new ChessboardException("figura " + x
                            + " je na poziciji " + from);

                for (int y = 0; y < 128; y++) {
                    if (isBlackBishopMoveLegal(from, y)) {
                        int t = this.constructMoveNumber(from, y);
                        rez.add(new Move(t));
                    }
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze bele kraljice
     * 
     * @return ArrayList<Move> vseh moznih potez bele kravljice
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteQueenMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int from = piecePosition[3];

        if (from != -1) {
            if (!isPositionLegal(from))
                throw new ChessboardException("figura " + 3
                        + " je na poziciji " + from);

            for (int y = 0; y < 128; y++) {
                if (isWhiteQueenMoveLegal(from, y)) {
                    int t = this.constructMoveNumber(from, y);
                    rez.add(new Move(t));
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crne kraljice
     * 
     * @return ArrayList<Move> vseh moznih potez crne kravljice
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackQueenMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int from = piecePosition[27];

        if (from != -1) {
            if (!isPositionLegal(from))
                throw new ChessboardException("figura " + 27
                        + " je na poziciji " + from);

            for (int y = 0; y < 128; y++) {
                if (isBlackQueenMoveLegal(from, y)) {
                    int t = this.constructMoveNumber(from, y);
                    rez.add(new Move(t));
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze belega kralja
     * 
     * @return ArrayList<Move> vseh moznih potez belega kralja
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalWhiteKingMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int from = piecePosition[4];

        if (from != -1) {
            if (!isPositionLegal(from))
                throw new ChessboardException("figura " + 4
                        + " je na poziciji " + from);

            for (int y = 0; y < 128; y++) {
                if (isWhiteKingMoveLegal(from, y)) {
                    int t = this.constructMoveNumber(from, y);
                    rez.add(new Move(t));
                }
            }
        }
        return rez;
    }


    /**
     * generira vse mozne poteze crnega kralja
     * 
     * @return ArrayList<Move> vseh moznih potez crnega kralja
     * @throws ChessboardException
     */
    public ArrayList<Move> getAllLegalBlackKingMoves()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int from = piecePosition[28];

        if (from != -1) {
            if (!isPositionLegal(from))
                throw new ChessboardException("figura " + 28
                        + " je na poziciji " + from);

            for (int y = 0; y < 128; y++) {
                if (isBlackKingMoveLegal(from, y)) {
                    int t = this.constructMoveNumber(from, y);
                    rez.add(new Move(t));
                }
            }
        }

        return rez;
    }


    /**
     * pomozna funkcija za simulacijo,
     * 
     * @return ce je kak�na bela figura napadena ali ce je pat vrne -1, ce je
     *         crni kralj matiran vrne 1, drugace pa vrne 0.
     */
    public int evaluateChessboardFromWhitesPerpective()
            throws ChessboardException {
        if (isBlackKingMated()) { return 1; }
        if (isBlackKingPatted()) { return -1; }
        if (isAnyWhiteFigureUnderAttackFromBlack() && !isWhitesTurn) { return -1; }
        if (numberOfMovesMade > maxNumberOfMoves) { return -1; }

        return 0;
    }


    /**
     * pomozna funkcija za Chessgame
     * 
     * @return vrednost trenutne pozicije
     * @throws ChessboardException
     */
    public int evaluateChessboard() throws ChessboardException {
        if (isBlackKingMated()) { return 1; }

        if (isBlackKingPatted()) { return -1; }
        if (numberOfMovesMade > maxNumberOfMoves) { return -1; }

        return 0;
    }


    public boolean isBlackKingChecked() throws ChessboardException {
        // ni stestirana

        int blackKingPos = piecePosition[28];

        if (this.isPositionUnderAttackByWhite(blackKingPos, false)) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isBlackKingMated() throws ChessboardException {
        int numberOfBlackKingPossibleMoves = this.getAllLegalBlackKingMoves()
                .size();

        if (numberOfBlackKingPossibleMoves == 0) {
            return isBlackKingChecked();
        }
        else {
            return false;
        }
    }


    public boolean isBlackKingPatted() throws ChessboardException {
        // ni stestirana
        int numberOfPossibleBlackKingMoves = getAllLegalBlackKingMoves().size();

        if (numberOfPossibleBlackKingMoves == 0) {
            return !isBlackKingChecked();
        }
        else {
            return false;
        }
    }


    public boolean isAnyWhiteFigureUnderAttackFromBlack()
            throws ChessboardException {
        // ni stestirana
        for (int x = 0; x < 16; x++) {
            int pos = piecePosition[x];
            if (pos == -1) {
                continue;
            }

            if (isPositionUnderAttackByBlack(pos, false)) { return true; }
        }

        return false;
    }


    /* ************************************************************************
     * *****************************PREMIKANJE FIGUR***************************
     */
    /*
     * ne preverja ce je na poziciji from beli kmet
     */
    public boolean isWhitePawnMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff == 16 && board[to] == -1)
            return true;
        if (diff == 15 || diff == 17) {
            if (board[to] != -1 && !isPieceWhite(board[to]))
                return true;
        }
        return false;
    }


    /*
     * ne preverja ce je na poziciji from beli kmet
     */
    public boolean isWhiteCannibalPawnMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff == 16 && board[to] == -1)
            return true;
        if (diff == 15 || diff == 17) {
            if (board[to] != -1)
                return true;
        }
        return false;
    }


    /*
     * ne preverja kaj je s figuro na poziciji from
     */
    public boolean isBlackPawnMoveLegal(int from, int to)
            throws ChessboardException {
        // ni stestirana, samo bi mogla delat
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff == -16 && board[to] == -1)
            return true;
        if (diff == -15 || diff == -17) {
            if (board[to] != -1 && isPieceWhite(board[to]))
                return true;
        }
        return false;
    }


    /*
     * ne preverja kaj je s figuro na poziciji from
     */
    public boolean isBlackCannibalPawnMoveLegal(int from, int to)
            throws ChessboardException {
        // ni stestirana, samo bi mogla delat
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff == -16 && board[to] == -1)
            return true;
        if (diff == -15 || diff == -17) {
            if (board[to] != -1)
                return true;
        }
        return false;
    }


    /*
     * ne preverja ce je na from bela trdnjava
     */
    public boolean isWhiteRookMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (getRankFromPosition(from) == getRankFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -1;
            else
                diff = 1;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            if (board[to] == -1 || !isPieceWhite(board[to]))
                return true;
            if (isPieceWhite(board[to]))
                return false;
        }
        if (getFileFromPosition(from) == getFileFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -16;
            else
                diff = 16;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }
            if (board[to] == -1 || !isPieceWhite(board[to]))
                return true;
            if (isPieceWhite(board[to]))
                return false;
        }

        return false;
    }


    /*
     * ne preverja ce je na from bela trdnjava
     */
    public boolean isWhiteCannibalRookMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (getRankFromPosition(from) == getRankFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -1;
            else
                diff = 1;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            return true;
        }
        if (getFileFromPosition(from) == getFileFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -16;
            else
                diff = 16;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }
            return true;
        }

        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isBlackRookMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (getRankFromPosition(from) == getRankFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -1;
            else
                diff = 1;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            if (board[to] == -1 || isPieceWhite(board[to]))
                return true;
            if (!isPieceWhite(board[to]))
                return false;
        }
        if (getFileFromPosition(from) == getFileFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -16;
            else
                diff = 16;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }
            if (board[to] == -1 || isPieceWhite(board[to]))
                return true;
            if (!isPieceWhite(board[to]))
                return false;
        }

        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isBlackCannibalRookMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (getRankFromPosition(from) == getRankFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -1;
            else
                diff = 1;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            return true;
        }
        if (getFileFromPosition(from) == getFileFromPosition(to)) {
            int diff = to - from;

            if (diff < 0)
                diff = -16;
            else
                diff = 16;

            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }
            return true;
        }

        return false;
    }


    /*
     * ne preverja kaj je z s figuro na from
     */
    public boolean isWhiteBishopMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff % 15 == 0) {
            if ((diff / 15) < 0)
                diff = -15;
            else
                diff = 15;
        }
        if (diff % 17 == 0) {
            if ((diff / 17) < 0)
                diff = -17;
            else
                diff = 17;
        }

        if (Math.abs(diff) == 17 || Math.abs(diff) == 15) {
            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            if (board[to] == -1 || !isPieceWhite(board[to]))
                return true;
            else
                return false;
        }
        return false;
    }


    /*
     * ne preverja kaj je z s figuro na from
     */
    public boolean isWhiteCannibalBishopMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff % 15 == 0) {
            if ((diff / 15) < 0)
                diff = -15;
            else
                diff = 15;
        }
        if (diff % 17 == 0) {
            if ((diff / 17) < 0)
                diff = -17;
            else
                diff = 17;
        }

        if (Math.abs(diff) == 17 || Math.abs(diff) == 15) {
            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            return true;
        }
        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isBlackCannibalBishopMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff % 15 == 0) {
            if ((diff / 15) < 0)
                diff = -15;
            else
                diff = 15;
        }
        if (diff % 17 == 0) {
            if ((diff / 17) < 0)
                diff = -17;
            else
                diff = 17;
        }

        if (Math.abs(diff) == 17 || Math.abs(diff) == 15) {
            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            return true;
        }
        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isBlackBishopMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        int diff = to - from;
        if (diff % 15 == 0) {
            if ((diff / 15) < 0)
                diff = -15;
            else
                diff = 15;
        }
        if (diff % 17 == 0) {
            if ((diff / 17) < 0)
                diff = -17;
            else
                diff = 17;
        }

        if (Math.abs(diff) == 17 || Math.abs(diff) == 15) {
            int temp = from + diff;
            while (temp != to) {
                if (board[temp] != -1)
                    return false;
                temp += diff;
            }

            if (board[to] == -1 || isPieceWhite(board[to]))
                return true;
            else
                return false;
        }
        return false;
    }


    /*
     * ne ugotavlja, kaj je s figuro na from poziciji
     */
    public boolean isWhiteKnightMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (board[to] == -1 || !isPieceWhite(board[to])) {
            int raz = Math.abs(from - to);
            switch (raz) {
            case 14:
                return true;
            case 31:
                return true;
            case 33:
                return true;
            case 18:
                return true;
            }
        }
        return false;
    }


    /*
     * ne ugotavlja, kaj je s figuro na from poziciji
     */
    public boolean isWhiteCannibalKnightMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (true) {
            int raz = Math.abs(from - to);
            switch (raz) {
            case 14:
                return true;
            case 31:
                return true;
            case 33:
                return true;
            case 18:
                return true;
            }
        }
        return false;
    }


    /*
     * ne ugotavlja, kaj je s figuro na from poziciji
     */
    public boolean isBlackKnightMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (board[to] == -1 || isPieceWhite(board[to])) {
            int raz = Math.abs(from - to);
            switch (raz) {
            case 14:
                return true;
            case 31:
                return true;
            case 33:
                return true;
            case 18:
                return true;
            }
        }
        return false;
    }


    /*
     * ne ugotavlja, kaj je s figuro na from poziciji
     */
    public boolean isBlackCannibalKnightMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (true) {
            int raz = Math.abs(from - to);
            switch (raz) {
            case 14:
                return true;
            case 31:
                return true;
            case 33:
                return true;
            case 18:
                return true;
            }
        }
        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isWhiteQueenMoveLegal(int from, int to)
            throws ChessboardException {
        if (isWhiteBishopMoveLegal(from, to))
            return true;
        if (isWhiteRookMoveLegal(from, to))
            return true;

        return false;
    }


    /*
     * ne preverja kaj je s figuro na from
     */
    public boolean isWhiteCannibalQueenMoveLegal(int from, int to)
            throws ChessboardException {
        if (isWhiteCannibalBishopMoveLegal(from, to))
            return true;
        if (isWhiteCannibalRookMoveLegal(from, to))
            return true;

        return false;
    }


    /*
     * ne preverja, kaj je s figuro na from
     */
    public boolean isBlackQueenMoveLegal(int from, int to)
            throws ChessboardException {
        if (isBlackBishopMoveLegal(from, to))
            return true;
        if (isBlackRookMoveLegal(from, to))
            return true;

        return false;
    }


    /*
     * ne preverja, kaj je s figuro na from
     */
    public boolean isBlackCannibalQueenMoveLegal(int from, int to)
            throws ChessboardException {
        if (isBlackCannibalBishopMoveLegal(from, to))
            return true;
        if (isBlackCannibalRookMoveLegal(from, to))
            return true;

        return false;
    }


    /*
     * ne ugotavlja kaj je s figuro na from poziciji
     */
    public boolean isWhiteKingMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (isPositionAdjacentToBlackKing(to)) { return false; }

        int targetPiece = board[to];
        if (targetPiece != -1 && isPieceWhite(targetPiece)) { return false; }

        int whiteKingPos = piecePosition[4];
        piecePosition[4] = -1;
        board[whiteKingPos] = -1;

        if (isPositionUnderAttackByBlack(to, true)) {
            piecePosition[4] = whiteKingPos;
            board[whiteKingPos] = 4;

            return false;
        }

        piecePosition[4] = whiteKingPos;
        board[whiteKingPos] = 4;

        if (isPositionAdjacentToWhiteKing(to)) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
     * ne ugotavlja kaj je s figuro na from poziciji
     */
    public boolean isWhiteCannibalKingMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to))
            return false;
        if (from == to)
            return false;

        if (isPositionAdjacentToBlackKing(to)) { return false; }

        int whiteKingPos = piecePosition[4];
        piecePosition[4] = -1;
        board[whiteKingPos] = -1;

        if (isPositionUnderAttackByBlack(to, true)) {
            piecePosition[4] = whiteKingPos;
            board[whiteKingPos] = 4;

            return false;
        }
        piecePosition[4] = whiteKingPos;
        board[whiteKingPos] = 4;

        if (isPositionAdjacentToWhiteKing(to)) {
            return true;
        }
        else {
            return false;
        }

    }


    /*
     * ne ugotavlja kaj je s figuro na from poziciji
     */
    // public boolean isWhiteInvincibleKingMoveLegal(int from, int to) throws
    // ChessboardException
    // {
    // if(from < 0 || from > 127) throw new ChessboardException("from = " +
    // from);
    //
    // if(!isPositionLegal(to)) return false;
    // if(from == to) return false;
    //
    // int diff = Math.abs(to - from);
    // if(diff == 1 || diff == 15 || diff == 16 || diff == 17)
    // {
    // if(board[to] == -1 || !isPieceWhite(board[to])) return true;
    // else return false;
    // }
    // else
    // {
    // return false;
    // }
    // }

    /*
     * ne ugotavlja kaj je s figuro na from poziciji
     */
    public boolean isBlackKingMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to)) { return false; }
        if (from == to)
            return false;

        if (isPositionAdjacentToWhiteKing(to)) { return false; }

        int targetPiece = board[to];

        if (targetPiece != -1 && isPieceBlack(targetPiece)) { return false; }

        int blackKingPos = piecePosition[28];
        piecePosition[28] = -1;
        board[blackKingPos] = -1;

        if (isPositionUnderAttackByWhite(to, true)) {
            piecePosition[28] = blackKingPos;
            board[blackKingPos] = 28;

            return false;
        }

        piecePosition[28] = blackKingPos;
        board[blackKingPos] = 28;

        if (isPositionAdjacentToBlackKing(to)) {
            return true;
        }
        else {
            return false;
        }

    }


    /*
     * ne ugotavlja kaj je s figuro na from poziciji
     */
    public boolean isBlackCannibalKingMoveLegal(int from, int to)
            throws ChessboardException {
        if (from < 0 || from > 127)
            throw new ChessboardException("from = " + from);

        if (!isPositionLegal(to)) { return false; }
        if (from == to)
            return false;

        if (isPositionAdjacentToWhiteKing(to)) { return false; }

        int blackKingPos = piecePosition[28];
        piecePosition[28] = -1;
        board[blackKingPos] = -1;

        if (isPositionUnderAttackByWhite(to, true)) {
            piecePosition[28] = blackKingPos;
            board[blackKingPos] = 28;

            return false;
        }

        piecePosition[28] = blackKingPos;
        board[blackKingPos] = 28;

        if (isPositionAdjacentToBlackKing(to)) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
     * preveri ce je poteza mozna
     */
    public boolean isWhiteMoveLegal(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        // if(DEBUG) println("Za�etek isWhiteMoveLegal(int from, int to)");
        // if(DEBUG) println("from: " + from + "\tto: " + to + "\tpiece: " +
        // piece);
        // if(DEBUG) println("Kli�em in vra�am isWhiteXXXMoveLegal");

        if (piece == 0 || piece == 7)
            return isWhiteRookMoveLegal(from, to);
        if (piece == 1 || piece == 6)
            return isWhiteKnightMoveLegal(from, to);
        if (piece == 2 || piece == 5)
            return isWhiteBishopMoveLegal(from, to);
        if (piece == 3)
            return isWhiteQueenMoveLegal(from, to);
        if (piece == 4)
            return isWhiteKingMoveLegal(from, to);
        if (piece > 7 && piece < 16)
            return isWhitePawnMoveLegal(from, to);

        // if(DEBUG)
        // {
        // println("///////////////////////////////////");
        // println("Napaka: Na from=" + from +" je figura " + piece);
        // try
        // {
        // printChessboard();
        // }
        // catch(Exception e)
        // {
        // e.printStackTrace();
        // }
        // println("///////////////////////////////////");
        // }

        throw new ChessboardException("na from=" + from + " je figura " + piece);
    }


    /*
     * preveri ce je poteza mozna
     */
    public boolean isBlackMoveLegal(int from, int to)
            throws ChessboardException {
        int piece = board[from];

        if (piece > 15 && piece < 24)
            return isBlackPawnMoveLegal(from, to);
        if (piece == 24 || piece == 31)
            return isBlackRookMoveLegal(from, to);
        if (piece == 25 || piece == 30)
            return isBlackKnightMoveLegal(from, to);
        if (piece == 26 || piece == 29)
            return isBlackBishopMoveLegal(from, to);
        if (piece == 27)
            return isBlackQueenMoveLegal(from, to);
        if (piece == 28)
            return isBlackKingMoveLegal(from, to);

        throw new ChessboardException("na from je figura " + piece);
    }


    /* *************************************************************************
     * *******************POMOZNE FUNKCIJE**************************************
     */

    private int constructMoveNumber(int from, int to) {
        /*
         * stevilka je sestavljena: from sestevlja prvih 8 bitov poteze (najbolj
         * levih) to je drugih 8 bitov tretjih 8 bitov je figura, ki jo
         * premaknemo zadnjih 8 bitov je pa figura, ki na mestu kamor se
         * premikamo
         */
        int rez = board[to] & 0xFF;
        rez |= (board[from] & 0xFF) << 8;
        rez |= (to & 0xFF) << 16;
        rez |= (from & 0xFF) << 24;
        return rez;
    }


    private static boolean isPositionLegal(int position) {
        if (position < 0 || position > 127)
            return false;
        if ((position & 0x88) != 0)
            return false;
        else
            return true;
    }


    private static int getRankFromPosition(int position) {
        return (position / 16) + 1;
    }


    private static int getFileFromPosition(int position) {
        return (position % 16) + 1;
    }


    private boolean isPieceWhite(int pieceNumber) throws ChessboardException {
        // ce vrne true, je figure bela, drugace je crna
        if (pieceNumber < 0 || pieceNumber > 31)
            throw new ChessboardException("piecenumber = " + pieceNumber);
        if (pieceNumber >= 0 && pieceNumber < 16)
            return true;
        else
            return false;
    }


    private boolean isPieceBlack(int pieceNumber) throws ChessboardException {
        if (pieceNumber < 0 || pieceNumber > 31)
            throw new ChessboardException("piecenumber = " + pieceNumber);
        if (pieceNumber > 15 && pieceNumber < 32)
            return true;
        else
            return false;
    }


    private void constructPiecePositionFromBoard() {
        piecePosition = new int[32];
        for (int x = 0; x < piecePosition.length; x++) {
            piecePosition[x] = -1;
        }
        for (int x = 0; x < board.length; x++) {
            if (board[x] != -1) {
                piecePosition[board[x]] = x;
            }
        }
    }


    public int distanceBewteenKings() {
        int positionA = this.piecePosition[4];
        int positionB = this.piecePosition[28];
        return Utils.distanceBetweenPositions(positionA, positionB);
    }


    /**
     * Checks if king are in opposition if black king moves to certain position
     * 
     * @param position
     *            position where if black king would be there we check if kings
     *            are in opposition
     * @return <code>true</code> if kings would be in oppostion if black king
     *         moves to postiion, otherwise <code>false</code>.
     */
    public boolean willBlackKingBeInOppositionIfItMovesTo(int position) {
        int whiteKingPos = piecePosition[4];
        return Utils.distanceBetweenPositions(whiteKingPos, position) == 2;
    }


    /* ***********************************************************************************
     * ***********************************************************************************
     * ***********************************************************************************
     */

    /*
     * ce je pozicija na imaginarnem delu plosce vrne true
     */
    public boolean isPositionUnderAttackByBlack(int position,
            boolean ignoreBlackKing) throws ChessboardException {
        if (!isPositionLegal(position))
            return true;

        for (int x = 16; x < 32; x++) {
            // trdnjavi
            if ((x == 24 || x == 31) && piecePosition[x] != -1) {
                if (isBlackCannibalRookMoveLegal(piecePosition[x], position)) { return true; }
            }
            // konja
            if ((x == 25 || x == 30) && piecePosition[x] != -1) {
                if (isBlackCannibalKnightMoveLegal(piecePosition[x], position)) { return true; }
            }
            // tekaca
            if ((x == 26 || x == 29) && piecePosition[x] != -1) {
                if (isBlackCannibalBishopMoveLegal(piecePosition[x], position)) { return true; }
            }
            // kraljica
            if (x == 27 && piecePosition[x] != -1) {
                if (isBlackCannibalQueenMoveLegal(piecePosition[x], position)) { return true; }
            }
            // kralj
            if (x == 28 && !ignoreBlackKing) {
                if (isBlackKingMoveLegal(piecePosition[x], position)) { return true; }
            }
        }

        for (int x = 16; x < 24; x++) {
            int from = piecePosition[x];
            if (from != -1) {
                int diff = position - from;
                if (diff == -17 || diff == -15)
                    return true;
            }
        }

        return false;
    }


    public boolean isPositionUnderAttackByWhite(int position,
            boolean ignoreWhiteKing) throws ChessboardException {
        if (position < 0 || position > 127)
            throw new ChessboardException("position = " + position);

        if (!isPositionLegal(position))
            return true;

        for (int x = 0; x < 8; x++) {
            // trdnjavi
            if ((x == 0 || x == 7) && piecePosition[x] != -1) {
                if (isWhiteCannibalRookMoveLegal(piecePosition[x], position)) { return true; }
            }
            // konja
            if ((x == 1 || x == 6) && piecePosition[x] != -1) {
                if (isWhiteCannibalKnightMoveLegal(piecePosition[x], position)) { return true; }
            }
            // tekaca
            if ((x == 2 || x == 5) && piecePosition[x] != -1) {
                if (isWhiteCannibalBishopMoveLegal(piecePosition[x], position)) { return true; }
            }
            // kraljica
            if (x == 3 && piecePosition[x] != -1) {
                if (isWhiteCannibalQueenMoveLegal(piecePosition[x], position)) { return true; }
            }
            // kralj
            if (x == 4 && !ignoreWhiteKing) {
                if (isWhiteKingMoveLegal(piecePosition[x], position)) { return true; }
            }
        }

        for (int x = 8; x < 16; x++) {
            int from = piecePosition[x];
            if (from != -1) {
                int diff = position - from;
                if (diff == 17 || diff == 15)
                    return true;
            }
        }

        return false;
    }


    public boolean isPositionAdjacentToWhiteKing(int position) {
        int kingPos = piecePosition[4];
        int diff = Math.abs(kingPos - position);

        if (diff == 1 || diff == 15 || diff == 16 || diff == 17) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isPositionAdjacentToBlackKing(int position) {
        int kingPos = piecePosition[28];
        int diff = Math.abs(kingPos - position);

        if (diff == 1 || diff == 15 || diff == 16 || diff == 17) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isPositionProtectedByWhiteKing(int position) {
        if (isPositionAdjacentToWhiteKing(position)) { return true; }

        int whiteKingPos = piecePosition[4];
        if (Utils.distanceBetweenPositions(whiteKingPos, position) == 2) {
            int whiteKingRank = Utils.getRankFromPosition(whiteKingPos);
            int whiteKingFile = Utils.getFileFromPosition(whiteKingPos);
            int posRank = Utils.getRankFromPosition(position);
            int posFile = Utils.getFileFromPosition(position);
            boolean differentRanks = whiteKingRank != posRank;
            boolean differentFiles = whiteKingFile != posFile;
            if (differentFiles && differentRanks) { return true; }
        }

        return false;
    }


    public boolean isPositionProtectedByBlackKing(int position) {
        if (isPositionAdjacentToBlackKing(position)) { return true; }

        int whiteKingPos = piecePosition[28];
        if (Utils.distanceBetweenPositions(whiteKingPos, position) == 2) {
            int whiteKingRank = Utils.getRankFromPosition(whiteKingPos);
            int whiteKingFile = Utils.getFileFromPosition(whiteKingPos);
            int posRank = Utils.getRankFromPosition(position);
            int posFile = Utils.getFileFromPosition(position);
            boolean differentRanks = whiteKingRank != posRank;
            boolean differentFiles = whiteKingFile != posFile;
            if (differentFiles && differentRanks) { return true; }
        }

        return false;
    }


    public boolean isPositionProtectedByKingIfKingIsMovedTo(int position,
            int kingPos) {
        if (Utils.distanceBetweenPositions(position, kingPos) == 1) { return true; }

        int whiteKingPos = kingPos;
        if (Utils.distanceBetweenPositions(whiteKingPos, position) == 2) {
            int whiteKingRank = Utils.getRankFromPosition(whiteKingPos);
            int whiteKingFile = Utils.getFileFromPosition(whiteKingPos);
            int posRank = Utils.getRankFromPosition(position);
            int posFile = Utils.getFileFromPosition(position);
            boolean differentRanks = whiteKingRank != posRank;
            boolean differentFiles = whiteKingFile != posFile;
            if (differentFiles && differentRanks) { return true; }
        }

        return false;
    }


    /* *********************************************************************
     * ***********************FUNKCIJE ZA POMOC ISKANJA POTEZ**************
     * *********************************************************************
     */

    public ArrayList<Move> movesWhereBlackKingEatsWhite()
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < 16; x++) {
            int piecePosition = this.piecePosition[x];
            if (piecePosition != -1
                    && isPositionAdjacentToBlackKing(piecePosition)) {
                int from = this.piecePosition[28];
                int to = piecePosition;
                if (isBlackKingMoveLegal(from, to)) {
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


    public ArrayList<Move> movesWhereBlackKingEvadesOposition(
            ArrayList<Move> blackKingPossibleMoves) throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < blackKingPossibleMoves.size(); x++) {
            int to = Utils
                    .getToFromMoveNumber(blackKingPossibleMoves.get(x).moveNumber);
            if (!this.willBlackKingBeInOppositionIfItMovesTo(to)) {
                Move copyMove = new Move(
                        blackKingPossibleMoves.get(x).moveNumber);
                rez.add(copyMove);
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereBlackKingTriesToEatRook(
            ArrayList<Move> possibleBlakKingMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int distance = 17;
        int rookPosition = -1;
        if (piecePosition[0] != -1 && piecePosition[7] != -1) {
            int dis0 = Utils.distanceBetweenPositions(piecePosition[28],
                    piecePosition[0]);
            int dis7 = Utils.distanceBetweenPositions(piecePosition[28],
                    piecePosition[7]);
            if (dis0 < dis7) {
                rookPosition = piecePosition[0];
            }
            else if (dis7 < dis0) {
                rookPosition = piecePosition[7];
            }
            else {
                Random rand = new Random();
                int i = rand.nextInt(2);
                if (i == 0) {
                    rookPosition = piecePosition[0];
                }
                else {
                    rookPosition = piecePosition[7];
                }
            }
        }
        else if (this.piecePosition[0] != -1) {
            rookPosition = this.piecePosition[0];
        }
        else if (this.piecePosition[7] != -1) {
            rookPosition = this.piecePosition[7];
        }
        else {
            return rez;
        }
        for (int x = 0; x < possibleBlakKingMoves.size(); x++) {
            int from = Utils
                    .getToFromMoveNumber(possibleBlakKingMoves.get(x).moveNumber);
            int currDistance = Utils.distanceBetweenPositions(from,
                    rookPosition);
            if (currDistance < distance) {
                rez = new ArrayList<Move>();
                distance = currDistance;
            }
            if (currDistance == distance) {
                rez.add(possibleBlakKingMoves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteIsntNearBlackKing(
            ArrayList<Move> moves) throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < moves.size(); x++) {
            int to = Utils.getToFromMoveNumber(moves.get(x).moveNumber);
            if (!isPositionAdjacentToBlackKing(to)) {
                rez.add(new Move(moves.get(x).moveNumber));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteRooksArentPlaceOnSameLine(
            ArrayList<Move> possibleRookMoves) throws ChessboardException {
        int rook0Pos = this.piecePosition[0];
        int rook7Pos = this.piecePosition[7];
        if (rook0Pos == -1 || rook7Pos == -1) { return possibleRookMoves; }
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < possibleRookMoves.size(); x++) {
            int from = Utils
                    .getFromFromMoveNumber(possibleRookMoves.get(x).moveNumber);
            int badRank = -1;
            int badFile = -1;
            if (from == rook0Pos) {
                badRank = Utils.getRankFromPosition(rook7Pos);
                badFile = Utils.getFileFromPosition(rook7Pos);
            }
            else if (from == rook7Pos) {
                badRank = Utils.getRankFromPosition(rook0Pos);
                badFile = Utils.getFileFromPosition(rook0Pos);
            }
            else {
                throw new ChessboardException("poteza ne pripada trdnjavi");
            }
            int to = Utils
                    .getToFromMoveNumber(possibleRookMoves.get(x).moveNumber);
            int currRank = Utils.getRankFromPosition(to);
            int currFile = Utils.getFileFromPosition(to);
            if (currFile != badFile && currRank != badRank) {
                rez.add(possibleRookMoves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWherePieceIsNotInCorner(ArrayList<Move> moves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < moves.size(); x++) {
            int to = Utils.getToFromMoveNumber(moves.get(x).moveNumber);
            if (to != 0 && to != 7 && to != 112 && to != 119) {
                rez.add(moves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteRookTryToStayOnNearestLines(
            ArrayList<Move> possibleRookMoves) throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int rook0Pos = this.piecePosition[0];
        int rook7Pos = this.piecePosition[7];
        if (rook0Pos == -1 || rook7Pos == -1) { return possibleRookMoves; }
        int minDistance = 20;
        for (int x = 0; x < possibleRookMoves.size(); x++) {
            Move currMove = possibleRookMoves.get(x);
            int from = Utils.getFromFromMoveNumber(currMove.moveNumber);
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int targetPosition = -1;
            if (from == rook0Pos) {
                targetPosition = rook7Pos;
            }
            else if (from == rook7Pos) {
                targetPosition = rook0Pos;
            }
            else {
                throw new ChessboardException("Poteza ni od trdnjave");
            }
            int currDistance = Utils.distanceBetweenPositions(to,
                    targetPosition);
            if (currDistance < minDistance) {
                minDistance = currDistance;
                rez = new ArrayList<Move>();
            }
            if (currDistance == minDistance) {
                rez.add(currMove);
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteAvoidBeingEatenByBlackKing(
            ArrayList<Move> moves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < moves.size(); x++) {
            int from = Utils.getFromFromMoveNumber(moves.get(x).moveNumber);
            int to = Utils.getToFromMoveNumber(moves.get(x).moveNumber);
            if (isPositionAdjacentToBlackKing(from)
                    && !isPositionAdjacentToBlackKing(to)) {
                rez.add(moves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteKingMovesCloserToBlackKind(
            ArrayList<Move> posKingMoves) {
        int distance = distanceBewteenKings();
        int blackKingPosition = this.piecePosition[28];
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < posKingMoves.size(); x++) {
            int to = Utils.getToFromMoveNumber(posKingMoves.get(x).moveNumber);
            int currDistance = Utils.distanceBetweenPositions(to,
                    blackKingPosition);
            if (currDistance < distance) {
                distance = currDistance;
                rez = new ArrayList<Move>();
            }
            if (currDistance == distance) {
                rez.add(posKingMoves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteKingMovesCloserOrEqualToBlackKind(
            ArrayList<Move> posMoves) {
        int distance = distanceBewteenKings();
        int blackKingPosition = this.piecePosition[28];
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < posMoves.size(); x++) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(posMoves.get(x).moveNumber);

            if (movedPiece == 4) {
                int to = Utils.getToFromMoveNumber(posMoves.get(x).moveNumber);
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


    public ArrayList<Move> movesWhereWhiteRookLikePieceBoundsBlackKing(
            ArrayList<Move> posQueenMoves) {
        int blackKingPosition = this.piecePosition[28];
        int blackKingRank = Utils.getRankFromPosition(blackKingPosition);
        int blackKingFile = Utils.getFileFromPosition(blackKingPosition);
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < posQueenMoves.size(); x++) {
            int to = Utils.getToFromMoveNumber(posQueenMoves.get(x).moveNumber);
            int currRank = Utils.getRankFromPosition(to);
            int currFile = Utils.getFileFromPosition(to);
            int rankDiff = Math.abs(currRank - blackKingRank);
            int fileDiff = Math.abs(currFile - blackKingFile);
            if (rankDiff == 1 || fileDiff == 1) {
                rez.add(posQueenMoves.get(x));
            }
        }
        return rez;
    }


    public ArrayList<Move> movesWhereWhiteKQKIsSafe(ArrayList<Move> moves)
            throws ChessboardException {
        ArrayList<Move> rez = new ArrayList<Move>();
        int queenPosition = this.piecePosition[3];
        if (isPositionAdjacentToBlackKing(queenPosition)) {
            for (int x = 0; x < moves.size(); x++) {
                int movedPiece = Utils
                        .getMovedPieceFromMoveNumber(moves.get(x).moveNumber);
                int to = Utils.getToFromMoveNumber(moves.get(x).moveNumber);
                if (movedPiece == 3) {
                    if (!isPositionAdjacentToBlackKing(to)) {
                        rez.add(moves.get(x));
                    }
                    else if (isPositionAdjacentToBlackKing(to)
                            && isPositionAdjacentToWhiteKing(to)) {
                        rez.add(moves.get(x));
                    }
                }
                else if (movedPiece == 4) {
                    if (Utils.arePositionsAdjacent(queenPosition, to)) {
                        rez.add(moves.get(x));
                    }
                }
                else {
                    throw new ChessboardException(
                            "poteza ne pripada beli kraljici ali belemu kralju");
                }
            }
        }
        else {
            for (int x = 0; x < moves.size(); x++) {
                int movedPiece = Utils
                        .getMovedPieceFromMoveNumber(moves.get(x).moveNumber);
                if (movedPiece == 4) {
                    rez.add(moves.get(x));
                }
                else if (movedPiece == 3) {
                    int to = Utils.getToFromMoveNumber(moves.get(x).moveNumber);
                    if (isPositionAdjacentToBlackKing(to)) {
                        if (isPositionAdjacentToWhiteKing(to)) {
                            rez.add(moves.get(x));
                        }
                    }
                    else {
                        rez.add(moves.get(x));
                    }
                }
                else {
                    throw new ChessboardException(
                            "poteza ne pripada beli kraljici ali belemu kralju");
                }
            }
        }
        return rez;
    }


    /*
     * ne preverja �e je dejansko ena figura, gleda pa samo bli�ino kraljev,
     * tako da �e je ve� figur, lahko odre�e kak�no potezo preve�
     */
    public ArrayList<Move> movesWhereWhiteOnePieceEndingIsSafe(
            ArrayList<Move> moves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (int x = 0; x < moves.size(); x++) {
            Move temp = moves.get(x);
            int to = Utils.getToFromMoveNumber(temp.moveNumber);
            int movedPiece = Utils.getMovedPieceFromMoveNumber(temp.moveNumber);
            if (movedPiece != 4) {
                if (isPositionAdjacentToBlackKing(to)
                        && isPositionAdjacentToWhiteKing(to)) {
                    rez.add(temp);
                }
                if (!isPositionAdjacentToBlackKing(to)) {
                    rez.add(temp);
                }
            }
            else {
                // poteze belega kralja so varne
                rez.add(temp);
            }
        }
        return rez;
    }


    /**
     * filter moves to those that need to be made (so that white doesn't loose a
     * piece).
     * 
     * @param allWhiteMoves
     * @return list of moves that white must do to avoid loosing a piece
     */
    public ArrayList<Move> KRKWhiteUrgentMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int rookPos = -1;
        if (piecePosition[0] != -1) {
            rookPos = piecePosition[0];
        }
        else if (piecePosition[7] != -1) {
            rookPos = piecePosition[7];
        }

        //if rook is not near black king then it's not in danger
        if (!isPositionAdjacentToBlackKing(rookPos)) { return rez; }

        for (int x = 0; x < allWhiteMoves.size(); x++) {
            Move currMove = new Move(allWhiteMoves.get(x).moveNumber);
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);

            if ((movedPiece == 0 || movedPiece == 7)
                    && !isPositionAdjacentToBlackKing(to)) {
                rez.add(currMove);
            }

            if (movedPiece == 4
                    && isPositionProtectedByKingIfKingIsMovedTo(rookPos, to)) {
                rez.add(currMove);
            }
        }

        return rez;
    }


    /**
     * heuristic that filters moves so that white wont give black king and
     * pieces (not true in all cases).
     * 
     * @param allWhiteMoves
     * @return list of safe moves
     */
    public ArrayList<Move> KRKWhiteSafeMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int rookPos = piecePosition[0];
        if (rookPos == -1) {
            rookPos = piecePosition[7];
        }
        if (rookPos == -1) { return allWhiteMoves; }

        for (Move currMove : allWhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                if (isPositionAdjacentToBlackKing(rookPos)) {
                    if (isPositionProtectedByKingIfKingIsMovedTo(rookPos, to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                else {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
            else {
                if (isPositionAdjacentToBlackKing(to)) {
                    if (isPositionAdjacentToWhiteKing(to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                else {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
        }
        return rez;
    }


    public ArrayList<Move> KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(
            ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int blackKingPos = piecePosition[28];

        // there are two rooks and only one should be on board
        int rookPos = piecePosition[0];
        if (rookPos == -1) {
            rookPos = piecePosition[7];
        }

        // if kings are on oppossition we find those in which white checks
        // otherwise all moves are valid
        if (willBlackKingBeInOppositionIfItMovesTo(blackKingPos)) {
            int blackKingRank = Utils.getRankFromPosition(blackKingPos);
            int blackKingFile = Utils.getFileFromPosition(blackKingPos);

            for (Move currMove : allWhiteMoves) {
                int movedPiece = Utils
                        .getMovedPieceFromMoveNumber(currMove.moveNumber);
                int to = Utils.getToFromMoveNumber(currMove.moveNumber);

                if (movedPiece == 0 || movedPiece == 7) {
                    // poteze trdnjave
                    int rank = Utils.getRankFromPosition(to);
                    int file = Utils.getFileFromPosition(to);
                    int from = Utils.getFromFromMoveNumber(currMove.moveNumber);

                    boolean sameRank = rank == blackKingRank;
                    boolean sameFile = file == blackKingFile;
                    if ((sameFile || sameRank)
                            && !ChessboardUtils
                                    .isPositionBetweenPositionsOnLine(
                                            this.piecePosition[4],
                                            blackKingPos, from)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                else {
                    // poteze kralja
                    int whiteKingPos = piecePosition[4];
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


    public ArrayList<Move> KRRKWhiteUrgentMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int rook0Position = piecePosition[0];
        int rook7Position = piecePosition[7];
        boolean isRook0NearBlackKing = false;
        boolean isRook7NearBlackKing = false;
        if (rook0Position != -1) {
            isRook0NearBlackKing = isPositionAdjacentToBlackKing(rook0Position);
        }
        if (rook7Position != -1) {
            isRook7NearBlackKing = isPositionAdjacentToBlackKing(rook7Position);
        }

        if (!isRook0NearBlackKing && !isRook7NearBlackKing) { return rez; }

        for (Move currMove : allWhiteMoves) {
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                if (isRook0NearBlackKing) {
                    if (isPositionProtectedByKingIfKingIsMovedTo(rook0Position,
                            to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                if (isRook7NearBlackKing) {
                    if (isPositionProtectedByKingIfKingIsMovedTo(rook7Position,
                            to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
            }
            else {
                if (isRook0NearBlackKing && movedPiece == 0
                        && !isPositionAdjacentToWhiteKing(rook0Position)
                        && !isPositionAdjacentToBlackKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
                else if (isRook7NearBlackKing && movedPiece == 7
                        && !isPositionAdjacentToWhiteKing(rook7Position)
                        && !isPositionAdjacentToBlackKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
        }
        return rez;
    }


    public ArrayList<Move> KBBKWhiteUrgentMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        int bishop2Position = piecePosition[2];
        int bishop5Position = piecePosition[5];
        boolean isBishop2NearBlackKing = false;
        boolean isBishop5NearBlackKing = false;
        if (bishop2Position != -1) {
            isBishop2NearBlackKing = isPositionAdjacentToBlackKing(bishop2Position);
        }
        if (bishop5Position != -1) {
            isBishop5NearBlackKing = isPositionAdjacentToBlackKing(bishop5Position);
        }
        if (!isBishop2NearBlackKing && !isBishop5NearBlackKing) { return rez; }

        for (Move currMove : allWhiteMoves) {
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                if (isBishop2NearBlackKing) {
                    if (isPositionProtectedByKingIfKingIsMovedTo(
                            bishop2Position, to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
                if (isBishop5NearBlackKing) {
                    if (isPositionProtectedByKingIfKingIsMovedTo(
                            bishop5Position, to)) {
                        rez.add(new Move(currMove.moveNumber));
                    }
                }
            }
            else {
                if (isBishop2NearBlackKing && movedPiece == 2
                        && !isPositionAdjacentToBlackKing(to)
                        && !isPositionAdjacentToWhiteKing(bishop2Position)) {
                    rez.add(new Move(currMove.moveNumber));
                }
                else if (isBishop5NearBlackKing && movedPiece == 5
                        && !isPositionAdjacentToBlackKing(to)
                        && !isPositionAdjacentToWhiteKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
        }

        return rez;
    }


    public ArrayList<Move> KRRKWhiteSafeMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allWhiteMoves) {
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                rez.add(new Move(currMove.moveNumber));
            }
            else {
                if (!isPositionAdjacentToBlackKing(to)
                        || isPositionAdjacentToWhiteKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
        }
        return rez;
    }


    public ArrayList<Move> KBBKWhiteSafeMoves(ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allWhiteMoves) {
            int to = Utils.getToFromMoveNumber(currMove.moveNumber);
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 4) {
                rez.add(new Move(currMove.moveNumber));
            }
            else {
                if (!isPositionAdjacentToBlackKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
                if (isPositionAdjacentToBlackKing(to)
                        && isPositionAdjacentToWhiteKing(to)) {
                    rez.add(new Move(currMove.moveNumber));
                }
            }
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
                int otherBishopPosition = piecePosition[otherBishop];
                int to = Utils.getToFromMoveNumber(currMove.moveNumber);
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


    public ArrayList<Move> filterMovesToWhiteRookMoves(
            ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allWhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 0 || movedPiece == 7) {
                rez.add(new Move(currMove.moveNumber));
            }
        }
        return rez;
    }


    public ArrayList<Move> filterMovesToWhiteBishopMoves(
            ArrayList<Move> allWhiteMoves) {
        ArrayList<Move> rez = new ArrayList<Move>();
        for (Move currMove : allWhiteMoves) {
            int movedPiece = Utils
                    .getMovedPieceFromMoveNumber(currMove.moveNumber);
            if (movedPiece == 2 || movedPiece == 5) {
                rez.add(new Move(currMove.moveNumber));
            }
        }
        return rez;
    }

    /* ********************************************************************
     * **************************DEBUG FUNKCIJE***************************
     */

    /* *******************************************************************
     * **********************KONSTANTE************************************
     */

}
