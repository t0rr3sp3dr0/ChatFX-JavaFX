package systems.singularity.chatfx.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.Variables;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 7/3/17.
 */
public class Singleton extends HashMap<String, Object> {
    private static Singleton ourInstance = new Singleton();
    private final Map<InetAddress, List<RDT.Receiver.OnReceiveListener>> chatOnReceiveListeners = new HashMap<>();
    private final Map<InetAddress, List<RDT.Receiver.OnReceiveListener>> fileOnReceiveListeners = new HashMap<>();
    private final List<RDT.Receiver.OnReceiveListener> serverOnReceiveListeners = new ArrayList<>();
    private RDT.Receiver chatReceiver = null;
    private RDT.Receiver fileReceiver = null;
    private RDT.Receiver serverReceiver = null;
    private RDT.Sender senderReceiver = null;
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

    public synchronized void setChatReceiver(@NotNull RDT.Receiver chatReceiver) {
        for (InetAddress key : this.chatOnReceiveListeners.keySet())
            chatReceiver.setOnReceiveListener(key, (address, port, bytes) -> {
                for (RDT.Receiver.OnReceiveListener receiveListener : this.chatOnReceiveListeners.get(key))
                    receiveListener.onReceive(address, port, bytes);
            });

        if (this.chatReceiver != null)
            this.chatReceiver.clearOnReceiveListeners();

        this.chatReceiver = chatReceiver;
    }

    public RDT.Receiver getFileReceiver() {
        return fileReceiver;
    }

    public synchronized void setFileReceiver(@NotNull RDT.Receiver fileReceiver) {
        for (InetAddress key : this.fileOnReceiveListeners.keySet())
            fileReceiver.setOnReceiveListener(key, (address, port, bytes) -> {
                for (RDT.Receiver.OnReceiveListener receiveListener : this.fileOnReceiveListeners.get(key))
                    receiveListener.onReceive(address, port, bytes);
            });

        if (this.fileReceiver != null)
            this.fileReceiver.clearOnReceiveListeners();

        this.fileReceiver = fileReceiver;
    }

    public RDT.Receiver getServerReceiver() {
        return serverReceiver;
    }

    public synchronized void setServerReceiver(@NotNull RDT.Receiver serverReceiver) {
        serverReceiver.setOnReceiveListener(Variables.Server.address, (address, port, bytes) -> {
            for (RDT.Receiver.OnReceiveListener receiveListener : this.serverOnReceiveListeners)
                receiveListener.onReceive(address, port, bytes);
        });

        if (this.serverReceiver != null)
            this.serverReceiver.clearOnReceiveListeners();

        this.serverReceiver = serverReceiver;
    }

    public synchronized void setChatOnReceiveListener(@Nullable InetAddress address, @Nullable RDT.Receiver.OnReceiveListener onReceiveListener) {
        this.chatOnReceiveListeners.computeIfAbsent(address, k -> new ArrayList<>()).add(onReceiveListener);

        if (this.chatReceiver != null)
            this.chatReceiver.setOnReceiveListener(address, (_address, port, bytes) -> {
                for (RDT.Receiver.OnReceiveListener receiveListener : this.chatOnReceiveListeners.get(address))
                    receiveListener.onReceive(_address, port, bytes);
            });
    }

    public synchronized void setFileOnReceiveListener(@Nullable InetAddress address, @Nullable RDT.Receiver.OnReceiveListener onReceiveListener) {
        this.fileOnReceiveListeners.computeIfAbsent(address, k -> new ArrayList<>()).add(onReceiveListener);

        if (this.fileReceiver != null)
            this.fileReceiver.setOnReceiveListener(address, (_address, port, bytes) -> {
                for (RDT.Receiver.OnReceiveListener receiveListener : this.fileOnReceiveListeners.get(address))
                    receiveListener.onReceive(_address, port, bytes);
            });
    }

    public synchronized void setServerOnReceiveListener(@Nullable RDT.Receiver.OnReceiveListener onReceiveListener) {
        this.serverOnReceiveListeners.add(onReceiveListener);
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
}
