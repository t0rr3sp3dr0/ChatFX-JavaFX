package systems.singularity.chatfx.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.structs.User;
import systems.singularity.chatfx.util.Constants;

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
    public void insert(User user) throws SQLException {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        if(!exists(user)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO bw_users (user_id, user_username, user_password, user_portChat, user_portFile, user_portRtt, user_status) VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setInt(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setShort(4, user.getPort_chat());
            statement.setShort(5, user.getPort_file());
            statement.setShort(6, user.getPort_rtt());
            statement.setBoolean(7, user.getStatus());
            statement.executeUpdate();
        }

    }

    @Override
    public void update(User user) throws SQLException {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        if(exists(user)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE bw_users SET user_username = ?, user_password = ?, user_portChat = ?, user_portFile = ?, user_portRtt = ?, user_status = ? WHERE user_id = ?;");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setShort(3, user.getPort_chat());
            statement.setShort(4, user.getPort_file());
            statement.setShort(5, user.getPort_rtt());
            statement.setBoolean(6, user.getStatus());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        }

    }

    @Override
    public boolean exists(User user) throws SQLException {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM bw_users WHERE user_id = ?;");
        statement.setInt(1, user.getId());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void remove(User user) throws SQLException {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        if(exists(user)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM bw_users WHERE user_id = ?;");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM bw_users;");
        ResultSet rs = statement.executeQuery();
        ArrayList<User> users = new ArrayList<>();
        while (rs.next())
            users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("user_username"),
                    rs.getString("user_password"),
                    rs.getShort("user_portChat"),
                    rs.getShort("user_portFile"),
                    rs.getShort("user_portRtt"),
                    rs.getBoolean("user_status")
            ));
        return users;
    }

    @Override
    public User get(int id) {
        Connection conn = Database.getConnection(Constants.chatFX + "ChatFX.db");
        return null;
    }
}
