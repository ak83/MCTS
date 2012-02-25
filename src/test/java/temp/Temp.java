package temp;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import chessboard.Chessboard;

import utils.IOUtils;

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
    public void test() {
       String path = getClass().getResource("/fruit/Fruit-2-3-1.exe").toString().substring(6).replaceAll("%20", " ");
       System.out.println(path);
       
       Constants.FRUIT_FILEPATH = path;
       IOUtils.isFruitReady();
       
       Constants.ENDING = "KRK";
       Chessboard cb = new Chessboard("sss");
       
       System.out.println(IOUtils.getMoveFromFruit(cb.boardToFen()));
    }

}
