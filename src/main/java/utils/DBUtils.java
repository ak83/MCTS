package utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains various static methods for working with database
 * 
 * @author ak83
 */
public class DBUtils {

    private DBUtils() {};


    /**
     * Transforms {@link ResultSet} to human CSV form.
     * 
     * @param rs
     *            result set
     * @return CSV form of <code>rs</code>
     */
    public static String seeResults(ResultSet rs) {
        String rez = null;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            rez = "";
            
            //get column names out of result set
            ArrayList<String> columnNames = new ArrayList<String>();
            for(int i = 0; i < rsmd.getColumnCount(); i++)
            {
                final String columnName = rsmd.getColumnName(i+1);
                columnNames.add(columnName);
                rez += columnName + Utils.CSV_DELIMITER;
            }
            rez += IOUtils.NEW_LINE;
            
            while(rs.next())
            {
                for(String columnName : columnNames)
                {
                    rez += rs.getString(columnName) + Utils.CSV_DELIMITER;
                }
                rez += IOUtils.NEW_LINE;
            }
            
            
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return rez;
    }

}
