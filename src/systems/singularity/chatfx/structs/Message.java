package systems.singularity.chatfx.structs;

import java.sql.Time;

/**
 * Created by caesa on 02/07/2017.
 */
public class Message {

    private int id;
    private int group_id;
    private String content;
    private boolean status;
    private Time time;
    private int author_id;

    public Message(int id, int group_id, String content, boolean status, Time time, int author_id) {
        this.id = id;
        this.group_id = group_id;
        this.content = content;
        this.status = status;
        this.time = time;
        this.author_id = author_id;
    }

    public Message() { }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }
}
