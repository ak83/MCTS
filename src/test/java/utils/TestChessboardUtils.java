package utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import chessboard.ChessboardUtils;

public class TestChessboardUtils {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testIsPositionBetweenPositionsOnLine() {
        Assert.assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(1,
                0, 2));
        Assert.assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(50,
                55, 49));
        Assert.assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(68,
                84, 36));
        Assert.assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(55,
                7, 119));

        Assert.assertFalse(ChessboardUtils.isPositionBetweenPositionsOnLine(0,
                3, 8));
        Assert.assertFalse(ChessboardUtils.isPositionBetweenPositionsOnLine(39,
                2, 5));
    }


    @Test
    public void testArePositionsAdjacent() {
        Assert.assertTrue(ChessboardUtils.arePositionsAdjacent(67, 84));
        Assert.assertTrue(ChessboardUtils.arePositionsAdjacent(22, 7));
        Assert.assertFalse(ChessboardUtils.arePositionsAdjacent(5, 7));
        Assert.assertFalse(ChessboardUtils.arePositionsAdjacent(99, 3));
    }

}
