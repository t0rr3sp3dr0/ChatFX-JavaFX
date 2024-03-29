package systems.singularity.chatfx.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.models.Message;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by phts on 04/07/17.
 */
public final class Networking {
    private Networking() {
        // Avoid class instantiation
    }

    public static void sendFile(@NotNull final File file, @NotNull final User user, @NotNull final String pragma, @Nullable final TransferCallback callback) throws UnknownHostException, SocketException {
        new Protocol.Uploader(RDT.getSender(InetAddress.getByName(user.getAddress()), user.getPortFile()), pragma, file, (($, bytesSent, elapsedTime) -> {
            final double progress = bytesSent / file.length();
            final double speed = bytesSent / (elapsedTime / 1e9);
            final double remainingTime = (file.length() - bytesSent) / speed;

            if (callback != null)
                callback.onCallback(file, progress, speed, remainingTime);
        })).start();
    }

    public static void receiveFile(@NotNull final User user, @Nullable final TransferCallback callback) throws UnknownHostException {
        Singleton.getInstance().setFileOnReceiveListener(InetAddress.getByName(user.getAddress()), (address, port, bytes) -> {
            Map<String, String> headers = Protocol.extractHeaders(bytes);
            final long contentLength = Long.parseLong(headers.get("Content-Length"));

            Protocol.Downloader downloader = Protocol.getDownloader(headers);
            downloader.setCallback(((file, bytesReceived, elapsedTime) -> {
                final double progress = bytesReceived / contentLength;
                final double speed = bytesReceived / (elapsedTime / 1e9);
                final double remainingTime = (contentLength - bytesReceived) / speed;

                if (callback != null)
                    callback.onCallback(file, progress, speed, remainingTime);
            }));
            downloader.add(Protocol.extractData(bytes));
        });
    }

    public static void sendMessage(@NotNull final Message message, @NotNull final User user) throws UnknownHostException, SocketException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Pragma", "message");

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(message);

        Protocol.Sender.sendMessage(RDT.getSender(InetAddress.getByName(user.getAddress()), user.getPortChat()), headers, json);
    }

    public static void sendACK(@NotNull final Message message, @NotNull final User user) throws UnknownHostException, SocketException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Pragma", "ack");
        headers.put("Message-ID", String.valueOf(message.getId()));

        Protocol.Sender.sendMessage(RDT.getSender(InetAddress.getByName(user.getAddress()), user.getPortChat()), headers, "");
    }

    public static void sendSeen(@NotNull final Message message, @NotNull final User user) throws UnknownHostException, SocketException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Pragma", "seen");
        headers.put("Message-ID", String.valueOf(message.getId()));

        Protocol.Sender.sendMessage(RDT.getSender(InetAddress.getByName(user.getAddress()), user.getPortChat()), headers, "");
    }

    public static void receiveMessage(@NotNull final User user, @Nullable final OnMessageListener onMessageListener) throws UnknownHostException {
        Singleton.getInstance().setChatOnReceiveListener(InetAddress.getByName(user.getAddress()), (address, port, bytes) -> {
            Map<String, String> headers = Protocol.extractHeaders(bytes);
            byte[] data = Protocol.extractData(bytes);

            Gson gson = new GsonBuilder().create();
            Message message = gson.fromJson(new String(data), Message.class);

            if (onMessageListener != null)
                onMessageListener.onMessage(headers, message);
        });
    }

    public interface TransferCallback {
        void onCallback(File file, double progress, double speed, double remainingTime);
    }

    public interface OnMessageListener {
        void onMessage(Map<String, String> headers, Message message);
    }
}
