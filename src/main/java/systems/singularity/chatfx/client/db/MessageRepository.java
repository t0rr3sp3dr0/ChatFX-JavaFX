package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Message;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by caesa on 02/07/2017.
 */
public class MessageRepository implements Repository<Message> {
    @Override
    public boolean exists(Message message) throws SQLException {
        return false;
    }

    @Override
    public void insert(Message message) throws SQLException {

    }

    @Override
    public void update(Message message) throws SQLException {

    }

    @Override
    public void remove(Message message) throws SQLException {

    }

    @Override
    public List<Message> getAll() throws SQLException {
        return null;
    }

    @Override
    public Message get(Message message) throws SQLException {
        return null;
    }
}
