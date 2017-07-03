package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Message;
import systems.singularity.chatfx.server.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvrma on 02/07/2017.
 */
public class MessageRepository implements Repository<Message> {
    @Override
    public boolean exists(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_messages WHERE message_id = ?;");
        statement.setInt(1, message.getId());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_messages (" +
                "message_group_id, message_content, message_status, message_timestamp, message_author_id) " +
                "VALUES (?, ?, ?, ?, ?);");
        statement.setInt(1, message.getGroup_id());
        statement.setString(2, message.getContent());
        statement.setBoolean(3, message.isStatus());
        statement.setTime(4, message.getTime());
        statement.setInt(5, message.getAuthor_id());
        statement.executeUpdate();
    }

    @Override
    public void update(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(message)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_messages SET message_group_id = ?, " +
                    "message_content = ?, message_status = ?, message_timestamp = ?, message_author_id = ? " +
                    "WHERE message_id = ?;");
            statement.setInt(1, message.getGroup_id());
            statement.setString(2, message.getContent());
            statement.setBoolean(3, message.isStatus());
            statement.setTime(4, message.getTime());
            statement.setInt(5, message.getAuthor_id());
            statement.setInt(6, message.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(message)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM cf_messages WHERE message_id = ?;");
            statement.setInt(1, message.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Message> getAll() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_messages;");
        ResultSet rs = statement.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (rs.next())
            messages.add(new Message(
                    rs.getInt("message_id"),
                    rs.getInt("message_group_id"),
                    rs.getString("message_content"),
                    rs.getBoolean("message_status"),
                    rs.getTime("message_timestamp"),
                    rs.getInt("message_author_id")
            ));
        return messages;
    }

    @Override
    public Message get(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_messages WHERE message_id = ?;");
        statement.setInt(1, message.getId());
        ResultSet rs = statement.executeQuery();
        message = null;
        while (rs.next())
            message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("message_group_id"),
                    rs.getString("message_content"),
                    rs.getBoolean("message_status"),
                    rs.getTime("message_timestamp"),
                    rs.getInt("message_author_id"));
        return message;
    }
}
