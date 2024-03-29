package systems.singularity.chatfx.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.util.java.ResizableBlockingQueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedro on 5/21/17.
 */
public final class Protocol {
    private static final Map<String, Downloader> downloaders = new HashMap<>();

    /**
     * This protocol was based on HTTP protocol
     * The protocol is divided in tow sections: headers and body
     * Those two sections are delimited by a single blank line between them
     * Headers are treated just like in HTTP
     * Body differs from HTTP by accepting binary content
     */
    private Protocol() {
        // Avoid class instantiation
    }

    @NotNull
    public static byte[] extractData(byte[] bytes) {
        char c = '\0';
        for (int i = 0; i < bytes.length; i++)
            if (c == '\n' && bytes[i] == '\n' && i + 1 < bytes.length)
                return Arrays.copyOfRange(bytes, i + 1, bytes.length);
            else
                c = (char) bytes[i];

        return new byte[0];
    }

    @NotNull
    public static Map<String, String> extractHeaders(@NotNull byte[] bytes) {
        final Map<String, String> headers = new HashMap<>();

        StringBuilder headerBuffer = new StringBuilder();
        for (byte b : bytes)
            if (b == '\n' && headerBuffer.charAt(headerBuffer.length() - 1) == '\n')
                break;
            else
                headerBuffer.append((char) b);

        String rawHeaders = headerBuffer.toString().trim();
        for (String header : rawHeaders.split("\n")) {
            String[] strings = header.split(": ");
            if (strings.length == 2)
                headers.put(strings[0], strings[1]);
        }

        return headers;
    }

    @NotNull
    public static Downloader getDownloader(@NotNull Map<String, String> headers) {
        synchronized (Protocol.downloaders) {
            Downloader downloader = Protocol.downloaders.get(headers.get("Message-ID"));
            if (downloader == null) {
                downloader = new Downloader(headers, new File(Paths.get(Constants.DOWNLOAD_DIRECTORY, headers.get("Content-Disposition").split("\"")[1]).toString()));
                downloader.start();
                Protocol.downloaders.put(headers.get("Message-ID"), downloader);
            }
            return downloader;
        }
    }

    public interface Receiver extends RDT.Receiver.OnReceiveListener {
        @Override
        default void onReceive(InetAddress address, int port, byte[] bytes) {
            onReceive(address, port, Protocol.extractHeaders(bytes), new String(Protocol.extractData(bytes)));
        }

        void onReceive(InetAddress address, int port, Map<String, String> headers, String message);
    }

    /**
     * Sends file through connection
     * Firstly it sends, via headers, file's length and name
     * Then it streams the file in blocks of MTU size
     * Finally it verifies if the hole file has been transmitted
     */
    public static final class Uploader extends Thread {
        private final RDT.Sender sender;
        private final String pragma;
        private final File file;
        private final Callback callback;

        public Uploader(@NotNull RDT.Sender sender, @NotNull String pragma, @NotNull File file, @Nullable Callback callback) {
            this.sender = sender;
            this.pragma = pragma;
            this.file = file;
            this.callback = callback;
        }

        @Override
        public void run() {
            super.run();

            try (FileInputStream fileInputStream = new FileInputStream(this.file)) {
                long writtenSize = 0;
                int size;
                long startTime = System.nanoTime();
                while (true) {
                    byte[] bytes = new byte[2 * Constants.MTU];

                    byte[] header = String.format("Content-Length: %d\nContent-Disposition: attachment; filename=\"%s\"\nMessage-ID: %d\nPragma: %s\n\n", this.file.length(), this.file.getName(), this.file.hashCode(), this.pragma).getBytes();
                    System.arraycopy(header, 0, bytes, 0, header.length);

                    size = fileInputStream.read(bytes, header.length, bytes.length - header.length);
                    if (size <= 0)
                        break;

                    sender.sendMessage(bytes);
                    long elapsedTime = System.nanoTime() - startTime;
                    writtenSize += size;

                    if (callback != null)
                        callback.onCallback(this.file, writtenSize, elapsedTime);
                }

                if (writtenSize != this.file.length())
                    throw new RuntimeException("Content Length Mismatch");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        public interface Callback {
            void onCallback(File file, double bytesSent, long elapsedTime);
        }
    }

    /**
     * Receives file in connection
     * Firstly it parses the headers of the stream
     * Then it reads the file from stream with a buffer of MTU size
     * Finally it verifies if the hole file has been recovered
     */
    public static final class Downloader extends Thread {
        private final Map<String, String> headers;
        private final File file;
        private final ResizableBlockingQueue<byte[]> queue = new ResizableBlockingQueue<>(Integer.MAX_VALUE);
        private Callback callback;

        public Downloader(@NotNull Map<String, String> headers, @NotNull File file) {
            this.headers = headers;
            this.file = file;
        }

        @Override
        public void run() {
            super.run();

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                long contentLength = Long.parseLong(headers.get("Content-Length"));

                long readSize = 0;
                long startTime = System.nanoTime();
                while (readSize < contentLength) {
                    byte[] bytes = this.queue.remove();

                    int size = (int) Math.min(bytes.length, contentLength - readSize);
                    if (size == 0)
                        continue;

                    fileOutputStream.write(bytes, 0, size);
                    long elapsedTime = System.nanoTime() - startTime;
                    readSize += size;

                    if (callback != null)
                        callback.onCallback(this.file, readSize, elapsedTime);
                }

                if (readSize != file.length())
                    throw new RuntimeException("Content Length Mismatch");
            } catch (IOException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        public void setCallback(@Nullable Callback callback) {
            this.callback = callback;
        }

        public boolean add(byte[] bytes) {
            return this.queue.add(bytes);
        }

        public interface Callback {
            void onCallback(File file, double bytesReceived, long elapsedTime);
        }
    }

    public static final class Sender {
        public static void sendMessage(@NotNull RDT.Sender sender, @NotNull Map<String, String> headers, @NotNull String message) throws InterruptedException {
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : headers.keySet())
                stringBuilder.append(key).append(": ").append(headers.get(key)).append('\n');
            stringBuilder.append('\n');
            stringBuilder.append(message);

            sender.sendMessage(stringBuilder.toString().getBytes());
        }
    }
}
