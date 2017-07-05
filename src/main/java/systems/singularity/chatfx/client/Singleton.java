package systems.singularity.chatfx.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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
    private RDT.Receiver chatReceiver = null;
    private RDT.Receiver fileReceiver = null;
    private String token = null;

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
}
