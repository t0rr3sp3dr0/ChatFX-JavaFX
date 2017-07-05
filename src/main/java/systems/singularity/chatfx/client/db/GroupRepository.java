package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvrma on 03/07/2017.
 */
public class GroupRepository implements Repository<Group> {
    @Override
    public boolean exists(Group group) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_groups WHERE group_id = ?;");
        statement.setInt(1, group.getId());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(Group group) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_groups (group_name) VALUES (?);");
        statement.setString(1, group.getName());
        statement.executeUpdate();
    }

    @Override
    public void update(Group group) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(group)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_groups SET group_name = ? WHERE group_id = ?;");
            statement.setString(1, group.getName());
            statement.setInt(2, group.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(Group group) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(group)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM cf_groups WHERE group_id = ?;");
            statement.setInt(1, group.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Group> getAll() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_groups;");
        ResultSet rs = statement.executeQuery();
        ArrayList<Group> groups = new ArrayList<>();
        while (rs.next())
            groups.add(
                    new Group()
                            .id(rs.getInt("group_id"))
                            .name(rs.getString("group_name"))
            );
        return groups;
    }

    @Override
    public Group get(Group group) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_groups WHERE group_id = ?;");
        statement.setInt(1, group.getId());
        ResultSet rs = statement.executeQuery();
        rs.next();

        return new Group()
                .id(rs.getInt("group_id"))
                .name(rs.getString("group_name"));
    }
}
