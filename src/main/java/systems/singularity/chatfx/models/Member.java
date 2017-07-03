package systems.singularity.chatfx.models;

/**
 * Created by lvrma on 03/07/2017.
 */
public class Member {

    private int id;
    private int group_id;
    private int user_id;

    public Member(int id, int group_id, int user_id) {
        this.id = id;
        this.group_id = group_id;
        this.user_id = user_id;
    }

    public Member() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
