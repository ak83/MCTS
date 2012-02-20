package chessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exec.Constants;
import exec.Move;

public class TestChessBoard {

    private static Chessboard      cbKRK;
    private static Chessboard      cbKRRK;
    private static ArrayList<Move> cbKRKAllMoves;
    private static ArrayList<Move> cbKRRKALLMoves;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Constants.ENDING = "KRK";
        TreeMap<Integer, Integer> initialCbKRKState = new TreeMap<Integer, Integer>();
        initialCbKRKState.put(51, 4);
        initialCbKRKState.put(66, 0);
        initialCbKRKState.put(83, 28);

        TestChessBoard.cbKRK = new Chessboard("test board", initialCbKRKState);
        System.out.println(TestChessBoard.cbKRK);
        TestChessBoard.cbKRKAllMoves = TestChessBoard.cbKRK
                .getAllLegalWhitePlies();

        Constants.ENDING = "KRRK";
        TreeMap<Integer, Integer> initialCbKRRKState = new TreeMap<Integer, Integer>();
        initialCbKRRKState.put(52, 4);
        initialCbKRRKState.put(38, 0);
        initialCbKRRKState.put(6, 7);
        initialCbKRRKState.put(23, 28);

        TestChessBoard.cbKRRK = new Chessboard("test KRRK board",
                initialCbKRRKState);

        TestChessBoard.cbKRRKALLMoves = TestChessBoard.cbKRRK
                .getAllLegalWhitePlies();

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testKRKWhiteMovesWhereRookChecksIfKingsAreInOpposition()
            throws Exception {

        ArrayList<Move> returned = TestChessBoard.cbKRK
                .KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(TestChessBoard.cbKRK
                        .getAllLegalWhitePlies());
        

        // there are 2 possible moves where white checks
        Assert.assertEquals(1, returned.size());

        // we check if returned moves are correct
        Assert.assertEquals(1112670463, returned.get(0).moveNumber);
    }


    @Test
    public void testConstructorStringTreemap() {
        TreeMap<Integer, Integer> initialBoardState = new TreeMap<Integer, Integer>();
        initialBoardState.put(0, 1);
        initialBoardState.put(117, 28);
        initialBoardState.put(115, 4);
        initialBoardState.put(99, 19);

        Chessboard testBoard = new Chessboard("temp board", initialBoardState);

        for (int x = 0; x < 128; x++) {
            if (initialBoardState.containsKey(x)) {
                Assert.assertEquals((int) initialBoardState.get(x), testBoard
                        .cloneBoard()[x]);
            }
            else {
                Assert.assertEquals(-1, testBoard.cloneBoard()[x]);
            }
        }
    }


    @Test
    public void testKRKWhiteSafeMoves() throws Exception {
        ArrayList<Move> returned = TestChessBoard.cbKRK
                .whiteSafePlies(TestChessBoard.cbKRK.getAllLegalWhitePlies());

        for (int x = 0; x < TestChessBoard.cbKRKAllMoves.size(); x++) {
            if ((x < 14 && x != 11 && x != 12) || x == 17) {
                Assert.assertTrue(returned
                        .contains(TestChessBoard.cbKRKAllMoves.get(x)));
            }
            else {
                Assert.assertFalse(returned
                        .contains(TestChessBoard.cbKRKAllMoves.get(x)));
            }
        }

    }


    @Test
    public void testWhiteUrgentMoves() throws Exception {
        ArrayList<Move> returned = TestChessBoard.cbKRK
                .whiteUrgentPlies(TestChessBoard.cbKRK.getAllLegalWhitePlies());
        Assert.assertEquals(0, returned.size());

        returned = TestChessBoard.cbKRRK
                .whiteUrgentPlies(TestChessBoard.cbKRRKALLMoves);
        Integer[] expectedIndexesOfNotUrgentMoves = { 0, 7, 13, 14, 16, 18, 19,
                20, 27, 28 };
        ArrayList<Integer> listOfexpectedIndexesOfNotUrgentMoves = new ArrayList<Integer>(
                Arrays.asList(expectedIndexesOfNotUrgentMoves));
        for (int x = 0; x < TestChessBoard.cbKRRKALLMoves.size(); x++) {
            if (listOfexpectedIndexesOfNotUrgentMoves.contains(x)) {
                Assert.assertFalse(
                        "Move with index: " + x + " is urgent move.", returned
                                .contains(TestChessBoard.cbKRRKALLMoves.get(x)));
            }
            else {
                Assert.assertTrue("Move with index: " + x
                        + " isn't urgent move.", returned
                        .contains(TestChessBoard.cbKRRKALLMoves.get(x)));
            }
        }

    }


    @Test
    public void testPiecesNearPosition() {
        ArrayList<Integer> returned = TestChessBoard.cbKRK
                .piecesNearPosition(82);
        Assert.assertEquals(2, returned.size());
        Assert.assertEquals(28, (int) returned.get(0));
        Assert.assertEquals(0, (int) returned.get(1));

    }


    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(5452610, TestChessBoard.cbKRK.hashCode());

        for (Move move : TestChessBoard.cbKRKAllMoves) {
            Chessboard temp = new Chessboard(TestChessBoard.cbKRK, "clone");
            temp.makeAMove(move.moveNumber);
            Assert.assertNotSame(TestChessBoard.cbKRK.hashCode(), temp
                    .hashCode());
        }

    }

}
