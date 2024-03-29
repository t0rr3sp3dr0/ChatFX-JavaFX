package systems.singularity.chatfx.server.db;

import systems.singularity.chatfx.util.Constants;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by caesa on 01/07/2017.
 */
public class Database {
    private static Connection connection;

    static {
        //noinspection Duplicates
        try {
            Class.forName("org.sqlite.JDBC");

            Database.initializeDatabase();
            Database.initializeTables();

            Database.connection.prepareStatement("UPDATE cf_users SET user_status = 0;").executeUpdate();

            new Thread(() -> {
                //noinspection InfiniteLoopStatement
                while (true)
                    try {
                        Database.connection.prepareStatement("UPDATE cf_users SET user_status = 0 WHERE ((julianday(CURRENT_TIMESTAMP) - julianday(user_lastSeen)) * 86400) > 5;").executeUpdate();
                        Thread.sleep(1000);
                    } catch (SQLException | InterruptedException e) {
                        e.printStackTrace();
                    }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Database() {
        // Avoid class instantiation
    }

    private static void initializeDatabase() throws SQLException {
        Database.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", Paths.get(Constants.PERSISTENT_DIRECTORY, "server.db").toString()));
    }

    private static void initializeTables() throws SQLException {
        Statement statement = Database.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS cf_users (user_id INTEGER PRIMARY KEY, user_username VARCHAR(16), user_password VARCHAR(32), user_address VARCHAR(16), user_portChat SMALLINT, user_portFile SMALLINT, user_portRtt SMALLINT, user_status BOOLEAN, user_lastSeen DATETIME);");
    }

    public static Connection getConnection() throws SQLException {
        if (Database.connection.isClosed())
            Database.initializeDatabase();

        return Database.connection;
    }
}
