package systems.singularity.chatfx.server.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by caesa on 02/07/2017.
 */
public class UserRepository implements Repository<User> {

    @Override
    public boolean exists(User user) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_users WHERE user_username = ?;");
        statement.setString(1, user.getUsername());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(User user) throws SQLException {
        Connection conn = Database.getConnection();
        if (!exists(user)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_users (user_username, user_password, user_address, user_portChat, user_portFile, user_portRtt, user_status) VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setInt(4, user.getPort_chat());
            statement.setInt(5, user.getPort_file());
            statement.setInt(6, user.getPort_rtt());
            statement.setBoolean(7, user.getStatus());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(User user) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(user)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_users SET user_username = ?, user_password = ?, user_address = ?, user_portChat = ?, user_portFile = ?, user_portRtt = ?, user_status = ? WHERE user_id = ?;");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setInt(4, user.getPort_chat());
            statement.setInt(5, user.getPort_file());
            statement.setInt(6, user.getPort_rtt());
            statement.setBoolean(7, user.getStatus());
            statement.setInt(8, user.getId());
            statement.executeUpdate();
        }

    }

    @Override
    public void remove(User user) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(user)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM cf_users WHERE user_id = ?;");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_users;");
        ResultSet rs = statement.executeQuery();
        ArrayList<User> users = new ArrayList<>();
        while (rs.next())
            users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("user_username"),
                    rs.getString("user_password"),
                    rs.getString("user_address"),
                    rs.getInt("user_portChat"),
                    rs.getInt("user_portFile"),
                    rs.getInt("user_portRtt"),
                    rs.getBoolean("user_status")
            ));
        return users;
    }

    @Override
    public User get(User user) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_users WHERE user_username = ?;");
        statement.setString(1, user.getUsername());
        ResultSet rs = statement.executeQuery();
        user = null;
        while (rs.next())
            user = new User(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getBoolean(8));
        return user;
    }

}
