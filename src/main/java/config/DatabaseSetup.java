package config;

import java.util.ResourceBundle;

/**
 * Class manages database settings.
 * 
 * @author Andraz
 */
public class DatabaseSetup {

    private DatabaseSetup() {};

    /** Resource bundle file where DB related settings are stored. */
    public static String  CONFIG_FILE = "database.db.properties";

    /** SQL server address */
    public static String  HOST;

    /** DB password */
    public static String  PASSWORD;

    /** DB username */
    public static String  USER;

    /** Flag that tells if program will use DB */
    public static boolean DB_ENABLED;


    /**
     * Reads database configuration from file
     * 
     * @param file
     *            configuration resource bundle
     */
    public static void readConfigFile(String file) {

        String propFile = file.replaceAll(".properties", "");
        ResourceBundle bundle = ResourceBundle.getBundle(propFile);

        DatabaseSetup.HOST = bundle.getString("host");
        DatabaseSetup.PASSWORD = bundle.getString("password");
        DatabaseSetup.USER = bundle.getString("username");
        DatabaseSetup.DB_ENABLED = Boolean.parseBoolean(bundle.getString("db.enabled"));
    }

}
