package systems.singularity.chatfx.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.util.java.Pair;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by pedro on 6/8/17.
 */
public final class RDT {
    private static final int MTU = 1024;
    private static final int WINDOW_SIZE = 4;
    private static final Map<Integer, Receiver> receivers = new HashMap<>();
    private static final Map<Pair<InetAddress, Integer>, Sender> senders = new HashMap<>();

    private RDT() {
        // Avoid class instantiation
    }

    @NotNull
    public static Sender getSender(@NotNull InetAddress address, @NotNull Integer port) throws SocketException, UnknownHostException {
        Pair<InetAddress, Integer> key = new Pair<>(address, port);
        synchronized (RDT.senders) {
            Sender sender = senders.get(key);
            if (sender == null) {
                sender = new Sender(key.first, key.second);
                sender.start();
                senders.put(key, sender);
            }
            return sender;
        }
    }

    @NotNull
    public static Receiver getReceiver(@NotNull Integer port) throws SocketException {
        synchronized (RDT.receivers) {
            Receiver receiver = receivers.get(port);
            if (receiver == null) {
                receiver = new Receiver(port);
                receiver.start();
                receivers.put(port, receiver);
            }
            return receiver;
        }
    }

    private static final class Packet implements Comparable<Packet> {
        public final int seq;
        public final byte[] bytes;

        public Packet(int seq, byte[] bytes) {
            this.seq = seq;
            this.bytes = bytes;
        }

        @Override
        public int compareTo(Packet o) {
            return (this.seq < o.seq) ? -1 : ((this.seq == o.seq) ? 0 : 1);
        }
    }

    private static final class Connection {
        public final PriorityQueue<Packet> packets;
        public final BlockingQueue<Integer> window;

        public Boolean fin = false;
        public Integer seq = -1;
        public Integer ack = -1;

        public Connection() throws UnknownHostException {
            this.packets = new PriorityQueue<>();
            this.window = new ArrayBlockingQueue<>(RDT.WINDOW_SIZE, true);
        }
    }

    public static final class Sender extends Thread {
        private final int port;
        private final InetAddress address;
        private final DatagramSocket socket = new DatagramSocket();
        private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(Short.MAX_VALUE);
        private final RDT.Connection connection = new Connection();

        private Sender(InetAddress address, int port) throws SocketException, UnknownHostException {
            super();

            this.port = port;
            this.address = address;

            this.socket.setSoTimeout(60);
        }

        public void sendMessage(String message) throws InterruptedException {
            this.queue.put(message);
        }

        public void sendACK(Integer seq) throws IOException {
            Integer port = this.port;

            byte[] payload = new byte[8 + RDT.MTU];
            payload[0] = (byte) 0b10000000;
            payload[4] = (byte) (seq >> 8);
            payload[5] = seq.byteValue();
            payload[6] = (byte) (port >> 8);
            payload[7] = port.byteValue();

            DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.port);
            socket.send(packet);
        }

        @Override
        public void run() {
            super.run();

            while (true)
                try {
                    String message = queue.take();

                    Boolean ack = false;
                    Boolean fin = false;
                    Integer len = message.length();
                    Integer seq = this.connection.seq;
                    Integer port = this.port;

                    byte[] bytes = message.getBytes();

                    for (int i = 0; i < Math.ceil((double) bytes.length / RDT.MTU); i++) {
                        this.connection.window.put(seq = ++this.connection.seq);

                        byte[] payload = new byte[8 + RDT.MTU];

                        fin = i + 1 == Math.ceil((double) bytes.length / RDT.MTU);

                        payload[0] = (byte) ((fin ? 0b01000000 : 0b00000000) | ((len >> 24) & 0b00111111));
                        payload[1] = (byte) (len >> 16);
                        payload[2] = (byte) (len >> 8);
                        payload[3] = len.byteValue();
                        payload[4] = (byte) (seq >> 8);
                        payload[5] = seq.byteValue();
                        payload[6] = (byte) (port >> 8);
                        payload[7] = port.byteValue();

                        if (!fin)
                            System.arraycopy(bytes, i * RDT.MTU, payload, 8, RDT.MTU);
                        else
                            System.arraycopy(bytes, i * RDT.MTU, payload, 8, bytes.length % RDT.MTU);

                        DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.port);
                        socket.send(packet);

                        System.err.printf("%d\tFIN(%b)\tSEQ(%d)\n", message.hashCode(), fin, seq);
                    }

                    System.err.printf("%d\tFIN\n", message.hashCode());
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    break;
                }
        }

        public void onACK(final int seq) {
            this.connection.window.removeIf(integer -> integer <= seq);
        }
    }

    public static final class Receiver extends Thread {
        private final int port;
        private final DatagramSocket socket;
        private final Map<InetAddress, OnReceiveListener> onReceiveListeners = new HashMap<>();
        private final Map<InetAddress, Connection> connections = new HashMap<>();

        private Receiver(int port) throws SocketException {
            super();

            this.port = port;
            this.socket = new DatagramSocket(port);

            this.socket.setSoTimeout(0);
        }

        @Override
        public void run() {
            super.run();

            while (true) {
                try {
                    byte[] payload = new byte[8 + RDT.MTU];
                    DatagramPacket packet = new DatagramPacket(payload, payload.length);
                    socket.receive(packet);

                    final Boolean ack = ((payload[0] >> 7) & 0b1) != 0;
                    final Boolean fin = ((payload[0] >> 6) & 0b1) != 0;
                    final Integer len = (((payload[0] & 0b00111111) << 24) & 0xFF000000) | ((payload[1] << 16) & 0x00FF0000) | ((payload[2] << 8) & 0x0000FF00) | (payload[3] & 0x000000FF);
                    final Integer seq = ((payload[4] << 8) & 0xFF00) | (payload[5] & 0x00FF);
                    final Integer port = ((payload[6] << 8) & 0xFF00) | (payload[7] & 0x00FF);

                    if (ack)
                        RDT.getSender(packet.getAddress(), port).onACK(seq);
                    else {
                        Connection connection;
                        synchronized (this.connections) {
                            connection = this.connections.get(packet.getAddress());
                            if (connection == null) {
                                connection = new Connection();
                                this.connections.put(packet.getAddress(), connection);
                            }
                        }

                        //noinspection SynchronizationOnLocalVariableOrMethodParameter
                        synchronized (connection) {
                            if (seq - 1 >= connection.seq) {
                                connection.packets.add(new Packet(seq, Arrays.copyOfRange(payload, 8, 8 + RDT.MTU)));
                                System.out.printf("%d\tFIN(%b)\tSEQ(%d)\n", connection.hashCode(), fin, seq);

                                connection.fin = connection.fin || fin;
                                connection.seq = Math.max(connection.seq, seq);

                                if (connection.packets.size() == Math.ceil((double) len / RDT.MTU)) {
                                    byte[] bytes = new byte[len];
                                    for (int i = 0; i < connection.seq; i++)
                                        System.arraycopy(connection.packets.poll().bytes, 0, bytes, i * RDT.MTU, RDT.MTU);
                                    System.arraycopy(connection.packets.poll().bytes, 0, bytes, connection.seq * RDT.MTU, len % RDT.MTU);

                                    OnReceiveListener listener;
                                    if ((listener = this.onReceiveListeners.get(null)) != null)
                                        listener.onReceive(packet.getAddress(), bytes);
                                    if ((listener = this.onReceiveListeners.get(packet.getAddress())) != null)
                                        listener.onReceive(packet.getAddress(), bytes);
                                }
                            } else
                                System.out.printf("Unexpected SEQ(%d)\t%d(%d)\n", seq, connection.hashCode(), connection.seq);
                            RDT.getSender(packet.getAddress(), port).sendACK(seq);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void setOnReceiveListener(@Nullable InetAddress address, @Nullable OnReceiveListener onReceiveListener) {
            this.onReceiveListeners.put(address, onReceiveListener);
        }

        public interface OnReceiveListener {
            void onReceive(InetAddress address, byte[] bytes);
        }
    }
}
