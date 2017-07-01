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
            connection = ConnectionFactory.createConnection(path);
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
        //ajustar o nome da tabela
        String sql = "CREATE TABLE IF NOT EXISTS nomeTabela (id integer PRIMARY KEY);";
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
