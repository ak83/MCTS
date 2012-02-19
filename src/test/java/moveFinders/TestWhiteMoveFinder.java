package moveFinders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import chessboard.Chessboard;

import exec.Constants;
import exec.Ply;

public class TestWhiteMoveFinder {

    private static Chessboard      cbKRK;
    private static ArrayList<Ply> allCbKRKMoves;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	Constants.HEURISTICS_check_for_urgent_moves = true;
	Constants.HEURISTICS_only_safe_moves = true;
	Constants.HEURISTICS_white_king_moves_closer_if_distance_from_black_king_is_larger_than_3 = true;
	Constants.HEURISTICS_white_KING_only_moves_coser_to_black_king = true;

	TreeMap<Integer, Integer> initialPostion = new TreeMap<Integer, Integer>();
	initialPostion.put(52, 0);
	initialPostion.put(37, 4);
	initialPostion.put(69, 28);
	TestWhiteMoveFinder.cbKRK = new Chessboard("KRK test board",
		initialPostion);

	System.out.println(TestWhiteMoveFinder.cbKRK);

	TestWhiteMoveFinder.allCbKRKMoves = TestWhiteMoveFinder.cbKRK
		.getAllLegalWhiteMoves();
	int x = 0;
	for (Ply move : TestWhiteMoveFinder.allCbKRKMoves) {
	    System.out.println(x + ": " + move);
	    x++;
	}

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void testGeneralHeuristics() {
	ArrayList<Ply> returned = WhiteMoveFinder
		.generalHeuristics(TestWhiteMoveFinder.cbKRK);

	Integer[] indexesNotInAllMoves = { 10, 11, 14, 15, 16, 17, 18 };
	ArrayList<Integer> listOfIndexesNotInAllMoves = new ArrayList<Integer>(
		Arrays.asList(indexesNotInAllMoves));
	for (int x = 0; x < TestWhiteMoveFinder.allCbKRKMoves.size(); x++) {
	    if (listOfIndexesNotInAllMoves.contains(x)) {
		assertFalse(returned.contains(TestWhiteMoveFinder.allCbKRKMoves
			.get(x)));
	    }
	    else {
		assertTrue(returned.contains(TestWhiteMoveFinder.allCbKRKMoves
			.get(x)));
	    }
	}
    }

}
