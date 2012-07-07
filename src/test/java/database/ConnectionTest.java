package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import config.DatabaseSetup;

/**
 * Test case that checks if program is properly connected to database
 * 
 * @author Andraz
 */
public class ConnectionTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DatabaseSetup.readConfigFile("database.db");
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception {}


    @After
    public void tearDown() throws Exception {}


    /**
     * Tests if program successfully connects to database
     */
    @Test
    public void testConnection() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(DatabaseSetup.HOST, DatabaseSetup.USER, DatabaseSetup.PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        }
        catch (SQLException ex) {
            Assert.fail(ex.getMessage());

        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            }
            catch (SQLException ex) {
                Assert.fail(ex.getMessage());
            }
        }
    }


    /**
     * Tests if writing to SQL DB was enabled.
     */
    @Test
    public void testEnabled() {
        Assert.assertTrue(DatabaseSetup.DB_ENABLED);
    }

}
