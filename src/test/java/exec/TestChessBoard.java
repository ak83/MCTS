package exec;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exceptions.ChessboardException;

public class TestChessBoard {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testKRKWhiteMovesWhereRookChecksIfKingsAreInOpposition()
            throws Exception {
        Constants.ENDING = "KRK";
        TreeMap<Integer, Integer> initislBoardState = new TreeMap<Integer, Integer>();
        initislBoardState.put(51, 4);
        initislBoardState.put(66, 0);
        initislBoardState.put(83, 28);

        Chessboard cb = new Chessboard("test board", initislBoardState);
        ArrayList<Move> returned = cb
                .KRKWhiteMovesWhereRookChecksIfKingsAreInOpposition(cb
                        .getAllLegalWhiteMoves());

        // there are 2 possible moves where white checks
        assertEquals(2, returned.size());

        // we check if returned moves are
        assertEquals(1111687423, returned.get(0).moveNumber);
        assertEquals(1112670463, returned.get(1).moveNumber);
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
                assertEquals((int) initialBoardState.get(x),
                        testBoard.getBoard()[x]);
            }
            else {
                assertEquals(-1, testBoard.getBoard()[x]);
            }
        }
    }

}
