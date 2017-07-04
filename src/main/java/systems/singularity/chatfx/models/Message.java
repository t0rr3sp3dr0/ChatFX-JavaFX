package systems.singularity.chatfx.models;

import java.sql.Time;

/**
 * Created by caesa on 02/07/2017.
 */
public class Message {

    private int id;
    private int groupId;
    private String content;
    private String status;
    private Time time;
    private int authorId;

    public Message(int id, int groupId, String content, String status, Time time, int authorId) {
        this.id = id;
        this.groupId = groupId;
        this.content = content;
        this.status = status;
        this.time = time;
        this.authorId = authorId;
    }

    public Message() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}
