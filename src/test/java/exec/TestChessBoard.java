package exec;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exceptions.ChessboardException;

public class TestChessBoard {
    
    private static Chessboard cb;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Constants.ENDING = "KRK";
        TreeMap<Integer, Integer> initislBoardState = new TreeMap<Integer, Integer>();
        initislBoardState.put(51, 4);
        initislBoardState.put(66, 0);
        initislBoardState.put(83, 28);

        TestChessBoard.cb = new Chessboard("test board", initislBoardState);
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

        ArrayList<Move> returned = TestChessBoard.cb
                .KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(TestChessBoard.cb
                        .getAllLegalWhiteMoves());

        // there are 2 possible moves where white checks
        Assert.assertEquals(2, returned.size());

        // we check if returned moves are
        Assert.assertEquals(1111687423, returned.get(0).moveNumber);
        Assert.assertEquals(1112670463, returned.get(1).moveNumber);
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
                Assert.assertEquals((int) initialBoardState.get(x),
                        testBoard.getBoard()[x]);
            }
            else {
                Assert.assertEquals(-1, testBoard.getBoard()[x]);
            }
        }
    }
    
    @Test
    public void testKRKWhiteSafeMoves() throws Exception{
        ArrayList<Move> returned = TestChessBoard.cb.KRKWhiteSafeMoves(cb.getAllLegalWhiteMoves());
        
        Assert.assertTrue(returned.contains(new Move(1111687423)));
        Assert.assertTrue(returned.contains(new Move(1111752959)));
        
        for(Move move : returned) {
            int to = Utils.getToFromMoveNumber(move.moveNumber);
            int movedPiece = Utils.getMovedPieceFromMoveNumber(move.moveNumber);
            
            Assert.assertFalse(to == 82);
            Assert.assertFalse(to == 98);
            
            Assert.assertFalse(movedPiece == 4 && to != 50);
        }
    }
    
    @Test
    public void testKRKWhiteUrgentMoves() throws Exception {
        ArrayList<Move> returned = TestChessBoard.cb.KRKWhiteUrgentMoves(cb.getAllLegalWhiteMoves());
//        Assert.assertEquals(0, returned.size());
        for(Move move : returned) {
            System.out.println(move);
        }
        
    }

}
