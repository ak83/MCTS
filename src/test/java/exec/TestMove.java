package exec;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMove {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testToString() {
        int moveNumber = Utils.constructMoveNumber(0, 7, 0, -1);
        Move move = new Move(moveNumber);
        assertEquals(
                "move: Ra1-h1\tfrom: 0\tto: 7\tmovedPiece: 0\ttargetPiece: -1",
                move.toString());
    }

}
