package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phts on 06/07/17.
 */
public class ChatRepository implements Repository<Chat> {
    private static ChatRepository ourInstance = new ChatRepository();

    private ChatRepository() {
        // Avoid class instantiation
    }

    public static ChatRepository getInstance() {
        return ChatRepository.ourInstance;
    }

    @Override
    public boolean exists(Chat chat) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_chats WHERE chat_id = ?;");
        statement.setInt(1, chat.getId());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(Chat chat) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_chats (chat_name, chat_isGroup) VALUES (?, ?);");
        statement.setString(1, chat.getName());
        statement.setBoolean(2, chat.isGroup());
        statement.executeUpdate();
    }

    @Override
    public void update(Chat chat) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(chat)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_chats SET chat_name = ?, chat_isGroup = ? WHERE chat_id = ?;");
            statement.setString(1, chat.getName());
            statement.setBoolean(2, chat.isGroup());
            statement.setInt(3, chat.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(Chat chat) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(chat)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM cf_chats WHERE chat_id = ?;");
            statement.setInt(1, chat.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Chat> getAll() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_chats;");
        ResultSet rs = statement.executeQuery();
        ArrayList<Chat> chats = new ArrayList<>();
        while (rs.next())
            chats.add(
                    new Chat()
                            .id(rs.getInt("chat_id"))
                            .name(rs.getString("chat_name"))
                            .isGroup(rs.getBoolean("chat_isGroup"))
            );
        return chats;
    }

    @Override
    public Chat get(Chat chat) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_chats WHERE chat_id = ?;");
        statement.setInt(1, chat.getId());
        ResultSet rs = statement.executeQuery();

        if (!rs.next())
            return null;

        return new Chat()
                .id(rs.getInt("chat_id"))
                .name(rs.getString("chat_name"))
                .isGroup(rs.getBoolean("chat_isGroup"));
    }
}
