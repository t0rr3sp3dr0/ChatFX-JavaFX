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
    private static MemberRepository ourInstance = new MemberRepository();

    private MemberRepository() {
        // Avoid class instantiation
    }

    public static MemberRepository getInstance() {
        return MemberRepository.ourInstance;
    }

    @Override
    public boolean exists(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_members WHERE (member_chat_id = ? " +
                "AND member_user_username = ?);");
        statement.setInt(1, member.getChatId());
        statement.setString(2, member.getUserUsername());
        ResultSet rs = statement.executeQuery();
        return rs.next();
    }

    @Override
    public void insert(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        if (!exists(member)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO cf_members (member_chat_id, member_user_username) VALUES (?, ?);");
            statement.setInt(1, member.getChatId());
            statement.setString(2, member.getUserUsername());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        if (exists(member)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE cf_members SET member_chat_id = ?, member_user_id = ? WHERE member_id = ?;");
            statement.setInt(1, member.getChatId());
            statement.setString(2, member.getUserUsername());
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
            members.add(
                    new Member()
                            .id(rs.getInt("member_id"))
                            .chatId(rs.getInt("member_chat_id"))
                            .userUsername(rs.getString("member_user_username"))
            );
        return members;
    }

    @Override
    public Member get(Member member) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM cf_members WHERE member_id = ?;");
        statement.setInt(1, member.getId());
        ResultSet rs = statement.executeQuery();

        if (!rs.next())
            return null;

        return new Member()
                .id(rs.getInt("member_id"))
                .chatId(rs.getInt("member_chat_id"))
                .userUsername(rs.getString("member_user_username"));
    }
}
