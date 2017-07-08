package systems.singularity.chatfx.client.db;

import org.joda.time.DateTime;
import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvrma on 02/07/2017.
 */
public class MessageRepository implements Repository<Message> {
    private static MessageRepository ourInstance = new MessageRepository();

    private MessageRepository() {
        // Avoid class instantiation
    }

    public static MessageRepository getInstance() {
        return MessageRepository.ourInstance;
    }

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
                "message_id, message_chat_id, message_content, message_status, message_timestamp, message_author_id) " +
                "VALUES (?, ?, ?, ?, ?, ?);");
        statement.setObject(1, message.getId());
        statement.setObject(2, message.getChatId());
        statement.setString(3, message.getContent());
        statement.setString(4, message.getStatus());
        statement.setTime(5, new Time(DateTime.parse(message.getTime()).getMillis()));
        statement.setObject(6, message.getAuthorId());
        statement.executeUpdate();
    }

    @Override
    public void update(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(message)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_messages SET message_chat_id = ?, " +
                    "message_content = ?, message_status = ?, message_timestamp = ?, message_author_id = ? " +
                    "WHERE message_id = ?;");
            statement.setInt(1, message.getChatId());
            statement.setString(2, message.getContent());
            statement.setString(3, message.getStatus());
            statement.setTime(4, new Time(DateTime.parse(message.getTime()).getMillis()));
            statement.setInt(5, message.getAuthorId());
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
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_messages ORDER BY message_timestamp ASC;");
        ResultSet rs = statement.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (rs.next())
            messages.add(
                    new Message()
                            .id(rs.getInt("message_id"))
                            .chatId(rs.getInt("message_chat_id"))
                            .content(rs.getString("message_content"))
                            .status(rs.getString("message_status"))
                            .time(new DateTime(rs.getTime("message_timestamp").getTime()).toString())
                            .authorId(rs.getInt("message_author_id"))
            );
        return messages;
    }

    @Override
    public Message get(Message message) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_messages WHERE message_id = ?;");
        statement.setInt(1, message.getId());
        ResultSet rs = statement.executeQuery();

        if (!rs.next())
            return null;

        return new Message()
                .id(rs.getInt("message_id"))
                .chatId(rs.getInt("message_chat_id"))
                .content(rs.getString("message_content"))
                .status(rs.getString("message_status"))
                .time(new DateTime(rs.getTime("message_timestamp").getTime()).toString())
                .authorId(rs.getInt("message_author_id"));
    }
}
