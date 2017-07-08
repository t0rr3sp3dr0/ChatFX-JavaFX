package systems.singularity.chatfx.client.db;

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

            Database.initializeTables();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Database() {
        // Avoid class instantiation
    }

    private static void initializeDatabase() throws SQLException {
        Database.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", Paths.get(Constants.PERSISTENT_DIRECTORY, "client.db").toString()));
    }

    private static void initializeTables() throws SQLException {
        Statement statement = Database.getConnection().createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS cf_messages (message_id INTEGER, message_chat_id INTEGER NULL, message_content VARCHAR, message_status VARCHAR(8), message_timestamp DATETIME, message_author_id INTEGER NOT NULL);");
        statement.execute("CREATE TABLE IF NOT EXISTS cf_groups (group_id INTEGER PRIMARY KEY, group_name VARCHAR(25));");
        statement.execute("CREATE TABLE IF NOT EXISTS cf_members (member_id INTEGER PRIMARY KEY, member_group_id INTEGER NOT NULL, member_user_id INTEGER NOT NULL);");
    }

    public static Connection getConnection() throws SQLException {
        if (connection.isClosed())
            Database.initializeDatabase();

        return connection;
    }
}
