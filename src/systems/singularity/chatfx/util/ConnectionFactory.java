package systems.singularity.chatfx.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by caesa on 01/07/2017.
 */
public class ConnectionFactory {

    public static Connection createConnection(String url) throws SQLException {
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
