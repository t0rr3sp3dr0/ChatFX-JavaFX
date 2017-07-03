package systems.singularity.chatfx.models;

/**
 * Created by caesa on 02/07/2017.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String address;
    private int port_chat;
    private int port_file;
    private int port_rtt;
    private boolean status;

    public User() {
    }

    public User(int id, String username, String password, String address, int port_chat, int port_file, int port_rtt, boolean status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.port_chat = port_chat;
        this.port_file = port_file;
        this.port_rtt = port_rtt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort_chat() {
        return port_chat;
    }

    public void setPort_chat(int port_chat) {
        this.port_chat = port_chat;
    }

    public int getPort_file() {
        return port_file;
    }

    public void setPort_file(int port_file) {
        this.port_file = port_file;
    }

    public int getPort_rtt() {
        return port_rtt;
    }

    public void setPort_rtt(int port_rtt) {
        this.port_rtt = port_rtt;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
