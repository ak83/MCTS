package temp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import chessboard.Chessboard;
import chessboard.SimpleChessboard;

import utils.IOUtils;

import exceptions.ChessboardException;
import exec.Constants;


public class Temp {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    @Test
    public void test() throws ChessboardException {
        Constants.ENDING = "KRRK";
        Chessboard sc = new Chessboard("sss");
        sc.makeAMove(0, 64);
        System.out.println(sc);
        Constants.EMD_DIR = "C:\\Documents and Settings\\Andraz\\Desktop\\matej\\emd";
        Constants.FRUIT_FILEPATH = "C:/Documents and Settings/Andraz/Desktop/matej/Fruit-2-3-1.exe";
       System.out.println(IOUtils.getMoveFromFruit(sc.boardToFen()));
    }

}
