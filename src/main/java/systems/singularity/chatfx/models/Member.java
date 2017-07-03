package systems.singularity.chatfx.models;

/**
 * Created by lvrma on 03/07/2017.
 */
public class Member {

    private int id;
    private int groupId;
    private int userId;

    public Member(int id, int groupId, int userId) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
    }

    public Member() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
