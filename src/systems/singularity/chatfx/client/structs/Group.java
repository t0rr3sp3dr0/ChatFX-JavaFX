package systems.singularity.chatfx.client.structs;

/**
 * Created by caesa on 02/07/2017.
 */
public class Group {

    private int id;
    private int group_id;
    private int user_id;

    public Group(int id, int group_id, int user_id) {
        this.id = id;
        this.group_id = group_id;
        this.user_id = user_id;
    }

    public Group() { }

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
