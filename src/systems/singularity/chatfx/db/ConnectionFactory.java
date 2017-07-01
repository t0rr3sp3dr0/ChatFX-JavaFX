package systems.singularity.chatfx.db;

import systems.singularity.chatfx.Main;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

/**
 * Created by caesa on 01/07/2017.
 */
public class ConnectionFactory {

    static Connection createConnection(String url) throws SQLException {
        //Preferences prefs = Preferences.userNodeForPackage(Main.class);
        //String url = String.format("jdbc:mysql://%s:%s/%s", prefs.get("domainConnection", "localhost"), prefs.get("portConnection", "3306"), prefs.get("databaseConnection", "mysql"));
        //return DriverManager.getConnection(url, prefs.get("userConnection", "root"), prefs.get("passwordConnection", "root"));

        return DriverManager.getConnection(url);
    }

    public static boolean verifyConnection(String domain, String port, String database, String user, String password) {
        try {
            String url = String.format("jdbc:mysql://%s:%s/%s", domain, port, database);
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn.isValid(8);
        } catch (SQLException e) {
            return false;
        }
    }
}
