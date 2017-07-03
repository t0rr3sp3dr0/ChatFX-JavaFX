package systems.singularity.chatfx.models;

/**
 * Created by caesa on 02/07/2017.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String address;
    private int portChat;
    private int portFile;
    private int portRtt;
    private boolean status;

    public User() {
    }

    public User(int id, String username, String password, String address, int portChat, int portFile, int portRtt, boolean status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.portChat = portChat;
        this.portFile = portFile;
        this.portRtt = portRtt;
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

    public int getPortChat() {
        return portChat;
    }

    public void setPortChat(int portChat) {
        this.portChat = portChat;
    }

    public int getPortFile() {
        return portFile;
    }

    public void setPortFile(int portFile) {
        this.portFile = portFile;
    }

    public int getPortRtt() {
        return portRtt;
    }

    public void setPortRtt(int portRtt) {
        this.portRtt = portRtt;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
