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
    private static UserRepository ourInstance = new UserRepository();

    private UserRepository() {
        // Avoid class instantiation
    }

    public static UserRepository getInstance() {
        return UserRepository.ourInstance;
    }

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
            PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_users (user_username, user_password, user_address, user_portChat, user_portFile, user_portRtt, user_status, user_lastSeen) VALUES (?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP);");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setInt(4, user.getPortChat() != null ? user.getPortChat() : -1);
            statement.setInt(5, user.getPortFile() != null ? user.getPortFile() : -1);
            statement.setInt(6, user.getPortRtt() != null ? user.getPortRtt() : -1);
            statement.executeUpdate();
        }
    }
    public void add(User user) throws SQLException {
        Connection conn = Database.getConnection();
        if (!exists(user)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_users (user_id, user_username, user_password, user_address, user_portChat, user_portFile, user_portRtt, user_status, user_lastSeen) VALUES (?, ?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP);");
            statement.setInt(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getAddress());
            statement.setInt(5, user.getPortChat() != null ? user.getPortChat() : -1);
            statement.setInt(6, user.getPortFile() != null ? user.getPortFile() : -1);
            statement.setInt(7, user.getPortRtt() != null ? user.getPortRtt() : -1);
            statement.executeUpdate();
        }
    }

    @Override
    public void update(User user) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(user)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_users SET user_username = ?, user_password = ?, user_address = ?, user_portChat = ?, user_portFile = ?, user_portRtt = ?, user_status = 1, user_lastSeen = CURRENT_TIMESTAMP WHERE user_id = ?;");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setInt(4, user.getPortChat());
            statement.setInt(5, user.getPortFile());
            statement.setInt(6, user.getPortRtt());
            statement.setInt(7, user.getId());
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
            users.add(
                    new User()
                            .id(rs.getInt("user_id"))
                            .username(rs.getString("user_username"))
                            .password(rs.getString("user_password"))
                            .address(rs.getString("user_address"))
                            .portChat(rs.getInt("user_portChat"))
                            .portFile(rs.getInt("user_portFile"))
                            .portRtt(rs.getInt("user_portRtt"))
                            .status(rs.getBoolean("user_status"))
            );
        return users;
    }

    @Override
    public User get(User user) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_users WHERE user_username = ?;");
        statement.setString(1, user.getUsername());
        ResultSet rs = statement.executeQuery();

        if (!rs.next())
            return null;

        return new User()
                .id(rs.getInt(1))
                .username(rs.getString(2))
                .password(rs.getString(3))
                .address(rs.getString(4))
                .portChat(rs.getInt(5))
                .portFile(rs.getInt(6))
                .portRtt(rs.getInt(7))
                .status(rs.getBoolean(8));
    }

}
