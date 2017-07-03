package systems.singularity.chatfx.client.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.models.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvrma on 03/07/2017.
 */
public class MemberRepository implements Repository<Member> {
    @Override
    public boolean exists(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_members WHERE (member_group_id = ? " +
                "AND member_user_id = ?);");
        statement.setInt(1, member.getGroupId());
        statement.setInt(2, member.getUserId());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        if (!exists(member)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_members (member_group_id, member_user_id) VALUES (?, ?);");
            statement.setInt(1, member.getGroupId());
            statement.setInt(2, member.getUserId());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(member)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_members SET member_group_id = ?, member_user_id = ? WHERE member_id = ?;");
            statement.setInt(1, member.getGroupId());
            statement.setInt(2, member.getUserId());
            statement.setInt(3, member.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(member)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM cf_members WHERE member_id = ?;");
            statement.setInt(1, member.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Member> getAll() throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_members;");
        ResultSet rs = statement.executeQuery();
        ArrayList<Member> members = new ArrayList<>();
        while (rs.next())
            members.add(new Member(
                    rs.getInt("member_id"),
                    rs.getInt("member_group_id"),
                    rs.getInt("member_user_id")
            ));
        return members;
    }

    @Override
    public Member get(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_members WHERE member_id = ?;");
        statement.setInt(1, member.getId());
        ResultSet rs = statement.executeQuery();
        member = null;
        while (rs.next())
            member = new Member(
                    rs.getInt("member_id"),
                    rs.getInt("member_group_id"),
                    rs.getInt("member_user_id"));
        return member;
    }
}
