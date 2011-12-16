package utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(1, 0, 2));
        assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(50, 55, 49));
        assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(68, 84, 36));
        assertTrue(ChessboardUtils.isPositionBetweenPositionsOnLine(55, 7, 119));

        assertFalse(ChessboardUtils.isPositionBetweenPositionsOnLine(0, 3, 8));
        assertFalse(ChessboardUtils.isPositionBetweenPositionsOnLine(39, 2, 5));
    }

}
