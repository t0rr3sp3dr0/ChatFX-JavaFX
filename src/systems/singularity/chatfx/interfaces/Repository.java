package systems.singularity.chatfx.interfaces;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by caesa on 02/07/2017.
 */
public interface Repository<E> {
    boolean exists(E e) throws SQLException;

    void insert(E e) throws SQLException;

    void update(E e) throws SQLException;

    void remove(E e) throws SQLException;

    List<E> getAll() throws SQLException;

    E get(int id) throws SQLException;
}
