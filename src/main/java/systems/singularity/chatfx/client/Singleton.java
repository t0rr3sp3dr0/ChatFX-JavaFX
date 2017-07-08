package systems.singularity.chatfx.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.client.controllers.ChatController;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.RDT;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedro on 7/3/17.
 */
public class Singleton extends HashMap<String, Object> {
    private static Singleton ourInstance = new Singleton();
    private final Map<InetAddress, RDT.Receiver.OnReceiveListener> chatOnReceiveListeners = new HashMap<>();
    private final Map<InetAddress, RDT.Receiver.OnReceiveListener> fileOnReceiveListeners = new HashMap<>();
    private final Map<ChatController, Boolean> downloadInProgress = new HashMap<>();
    private RDT.Receiver chatReceiver = null;
    private RDT.Receiver fileReceiver = null;
    private String token = null;
    private String username = null;
    private User user = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        return ourInstance;
    }

    public RDT.Receiver getChatReceiver() {
        return chatReceiver;
    }

    public void setChatReceiver(@NotNull RDT.Receiver chatReceiver) {
        for (InetAddress key : this.chatOnReceiveListeners.keySet())
            chatReceiver.setOnReceiveListener(key, this.chatOnReceiveListeners.get(key));

        if (this.chatReceiver != null)
            this.chatReceiver.clearOnReceiveListeners();

        this.chatReceiver = chatReceiver;
    }

    public RDT.Receiver getFileReceiver() {
        return fileReceiver;
    }

    public void setFileReceiver(@NotNull RDT.Receiver fileReceiver) {
        for (InetAddress key : this.fileOnReceiveListeners.keySet())
            fileReceiver.setOnReceiveListener(key, this.fileOnReceiveListeners.get(key));

        if (this.fileReceiver != null)
            this.fileReceiver.clearOnReceiveListeners();

        this.fileReceiver = fileReceiver;
    }

    public void setChatOnReceiveListener(@Nullable InetAddress address, @Nullable RDT.Receiver.OnReceiveListener onReceiveListener) {
        this.chatOnReceiveListeners.put(address, onReceiveListener);

        if (this.chatReceiver != null)
            this.chatReceiver.setOnReceiveListener(address, onReceiveListener);
    }

    public void setFileOnReceiveListener(@Nullable InetAddress address, @Nullable RDT.Receiver.OnReceiveListener onReceiveListener) {
        this.fileOnReceiveListeners.put(address, onReceiveListener);

        if (this.fileReceiver != null)
            this.fileReceiver.setOnReceiveListener(address, onReceiveListener);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void putDownloadInProgress(ChatController chatController, Boolean b) {
        this.downloadInProgress.put(chatController, b);
    }

    public boolean getDownloadInProgress(ChatController chatController) {
        return this.downloadInProgress.get(chatController);
    }
}
