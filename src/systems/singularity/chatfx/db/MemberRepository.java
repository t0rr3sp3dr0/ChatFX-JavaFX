package systems.singularity.chatfx.db;

import systems.singularity.chatfx.interfaces.Repository;
import systems.singularity.chatfx.structs.Member;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by caesa on 02/07/2017.
 */
public class MemberRepository implements Repository<Member> {
    @Override
    public boolean exists(Member member) throws SQLException {
        return false;
    }

    @Override
    public void insert(Member member) throws SQLException {

    }

    @Override
    public void update(Member member) throws SQLException {

    }

    @Override
    public void remove(Member member) throws SQLException {

    }

    @Override
    public List<Member> getAll() throws SQLException {
        return null;
    }

    @Override
    public Member get(Member member) throws SQLException {
        return null;
    }
}
