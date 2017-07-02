package systems.singularity.chatfx.db;

import java.sql.*;

/**
 * Created by caesa on 01/07/2017.
 */
public class Database {
    private static Database ourInstance;
    private Connection connection;

    private Database(String path) {
        try {
            this.connection = ConnectionFactory.createConnection(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDatabase(String path) {
        String url =  "jdbc:sqlite:" + path;
        try (Connection connection = DriverManager.getConnection(url)) {
            if(connection != null) {
                DatabaseMetaData metaData = connection.getMetaData();
                ourInstance = new Database(url);
                //conexão criada
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String path) {
        String url =  "jdbc:sqlite:" + path;

        String sql = "CREATE TABLE IF NOT EXISTS cf_users (user_id integer PRIMARY KEY, user_username VARCHAR(16), user_password VARCHAR(32), user_address VARCHAR(16), user_portChat SMALLINT, user_portFile SMALLINT, user_portRtt SMALLINT, user_status BOOLEAN);";
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement sttm = connection.createStatement();
            sttm.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createClientTable(String path) {
        String url =  "jdbc:sqlite:" + path;

        String sql = "CREATE TABLE IF NOT EXISTS cf_messages (message_id integer PRIMARY KEY, message_group_id integer NULL, message_content VARCHAR, message_status BOOLEAN, message_timestamp DATETIME, message_author_id INTEGER NOT NULL);";
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement sttm = connection.createStatement();
            sttm.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "CREATE TABLE IF NOT EXISTS cf_groups (group_id integer PRIMARY KEY, group_name varchar(25));";
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement sttm = connection.createStatement();
            sttm.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "CREATE TABLE IF NOT EXISTS cf_members (member_id integer PRIMARY KEY, member_group_id integer NOT NULL, member_user_id integer NOT NULL);";
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement sttm = connection.createStatement();
            sttm.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(String path) {
        try {
            if (ourInstance.connection == null || !ourInstance.connection.isValid(4))
                ourInstance.connection = ConnectionFactory.createConnection("jdbc:sqlite:" + path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ourInstance.connection;
    }


}
