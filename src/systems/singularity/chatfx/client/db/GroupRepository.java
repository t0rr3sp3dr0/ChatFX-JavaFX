package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.client.structs.Group;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by caesa on 02/07/2017.
 */
public class GroupRepository implements Repository<Group> {
    @Override
    public boolean exists(Group group) throws SQLException {
        return false;
    }

    @Override
    public void insert(Group group) throws SQLException {

    }

    @Override
    public void update(Group group) throws SQLException {

    }

    @Override
    public void remove(Group group) throws SQLException {

    }

    @Override
    public List<Group> getAll() throws SQLException {
        return null;
    }

    @Override
    public Group get(Group group) throws SQLException {
        return null;
    }
}
