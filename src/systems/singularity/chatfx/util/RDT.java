package systems.singularity.chatfx.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.util.java.Pair;
import systems.singularity.chatfx.util.java.ResizableBlockingQueue;

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

    public static boolean ignore(double probability) {
        return Math.random() <= probability;
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

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof Packet && seq == ((Packet) o).seq;

        }
    }

    private static final class Connection {
        public final PriorityQueue<Packet> packets;
        public final ResizableBlockingQueue<Integer> window;

        public Boolean fin = false;
        public Integer seq = -1;
        public Integer ack = -1;
        public Integer repeatedCount = 0;

        public Connection() throws UnknownHostException {
            this.packets = new PriorityQueue<>();
            this.window = new ResizableBlockingQueue<>(4);
        }
    }

    public static final class Sender extends Thread {
        private final int port;
        private final InetAddress address;
        private final DatagramSocket socket = new DatagramSocket();
        private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(Short.MAX_VALUE);
        private final RDT.Connection connection = new Connection();
        private final Timer timer = new Timer();

        private Sender(InetAddress address, int port) throws SocketException, UnknownHostException {
            super();

            this.port = port;
            this.address = address;

            this.socket.setSoTimeout(60);
        }

        public void sendMessage(byte[] message) throws InterruptedException {
            this.queue.put(message);
        }

        public void sendACK(Integer seq) throws IOException {
            Integer port = this.port;

            byte[] payload = new byte[8 + Constant.MTU];
            payload[0] = (byte) 0b10000000;
            payload[4] = (byte) (seq >> 8);
            payload[5] = seq.byteValue();
            payload[6] = (byte) (port >> 8);
            payload[7] = port.byteValue();

            DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.port);
            socket.send(packet);
        }

        @Override
        public synchronized void start() {
            super.start();

            this.timer.start();
        }

        @Override
        public void run() {
            super.run();

            while (true)
                try {
                    byte[] message = queue.take();

                    Boolean ack = false;
                    Boolean fin = false;
                    Integer len = message.length;
                    Integer seq = this.connection.seq;
                    Integer port = this.port;

                    for (int i = 0; i < Math.ceil((double) message.length / Constant.MTU); i++) {
                        synchronized (this.connection.window) {
                            this.connection.window.add(seq = ++this.connection.seq);
                        }

                        byte[] payload = new byte[8 + Constant.MTU];

                        fin = i + 1 == Math.ceil((double) message.length / Constant.MTU);

                        payload[0] = (byte) ((fin ? 0b01000000 : 0b00000000) | ((len >> 24) & 0b00111111));
                        payload[1] = (byte) (len >> 16);
                        payload[2] = (byte) (len >> 8);
                        payload[3] = len.byteValue();
                        payload[4] = (byte) (seq >> 8);
                        payload[5] = seq.byteValue();
                        payload[6] = (byte) (port >> 8);
                        payload[7] = port.byteValue();

                        if (!fin)
                            System.arraycopy(message, i * Constant.MTU, payload, 8, Constant.MTU);
                        else
                            System.arraycopy(message, i * Constant.MTU, payload, 8, message.length % Constant.MTU);

                        if (!RDT.ignore(Constant.SENDER_LOSS_PROBABILITY)) {
                            DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.port);
                            socket.send(packet);

                            System.err.printf("%d\tFIN(%b)\tSEQ(%d)\n", Arrays.hashCode(message), fin, seq);
                        } else
                            System.err.printf("%d\tLOST\tSEQ(%d)\n", Arrays.hashCode(message), seq);


                        this.timer.watch(new Packet(seq, payload));
                    }

                    System.err.printf("%d\tFIN\n", Arrays.hashCode(message));
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    break;
                }
        }

        public void onACK(final int seq) {
            if (!this.connection.window.contains(seq)) {
                System.err.printf("Repeated\tACK(%d)\n%d\n", seq, ++this.connection.repeatedCount);
                if (this.connection.repeatedCount == 3) {
                    this.connection.repeatedCount = 0;
                    this.connection.window.resize(-32);
                }
            } else {
                this.connection.ack = Math.max(this.connection.ack, seq);
                this.connection.repeatedCount = 0;
                this.connection.window.removeIf(integer -> integer <= seq);
                this.connection.window.resize(4);
                System.err.printf("Received\tACK(%d)\n", seq);
            }
        }

        private final class Timer extends Thread {
            private final PriorityQueue<Pair<Long, Packet>> heap = new PriorityQueue<>();
            private int timeout = 100;

            private Timer() {
                // Avoid class instantiation
            }

            @Override
            public void run() {
                super.run();

                while (true) {
                    synchronized (this.heap) {
                        while (this.heap.size() > 0 && this.heap.peek().first <= System.currentTimeMillis()) {
                            Packet packet = this.heap.poll().second;
                            if (Sender.this.connection.ack < packet.seq)
                                try {
                                    System.err.printf("Timeout\tSEQ(%d)\n", packet.seq);
                                    Sender.this.socket.send(new DatagramPacket(packet.bytes, packet.bytes.length, Sender.this.address, Sender.this.port));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    this.heap.add(new Pair<>(System.currentTimeMillis() + this.timeout, packet));
                                }
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void watch(Packet packet) {
                synchronized (this.heap) {
                    this.heap.add(new Pair<>(System.currentTimeMillis() + this.timeout, packet));
                }
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }
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
                    byte[] payload = new byte[8 + Constant.MTU];
                    DatagramPacket packet = new DatagramPacket(payload, payload.length);
                    socket.receive(packet);

                    if (RDT.ignore(Constant.RECEIVER_LOSS_PROBABILITY))
                        continue;

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
                            Packet pkt = new Packet(seq, Arrays.copyOfRange(payload, 8, 8 + Constant.MTU));

                            if (seq < connection.seq || connection.packets.contains(pkt)) {
                                System.out.printf("Unexpected SEQ(%d)\t%d(%d)\n", seq, connection.hashCode(), connection.seq);
                                RDT.getSender(packet.getAddress(), port).sendACK(connection.seq);
                                continue;
                            }

                            if (seq - 1 >= connection.seq) {
                                connection.packets.add(pkt);
                                System.out.printf("%d\tFIN(%b)\tSEQ(%d)\n", connection.hashCode(), fin, seq);
                            }

                            int packetsCount = (int) Math.ceil((double) len / Constant.MTU);
                            if (!connection.fin && connection.packets.size() == packetsCount) {
                                byte[] bytes = new byte[len];
                                for (int i = 0; i < packetsCount - 1; i++)
                                    System.arraycopy(connection.packets.poll().bytes, 0, bytes, i * Constant.MTU, Constant.MTU);

                                Packet finPacket = connection.packets.poll();
                                connection.seq = finPacket.seq;
                                connection.fin = true;
                                RDT.getSender(packet.getAddress(), port).sendACK(finPacket.seq);
                                System.arraycopy(finPacket.bytes, 0, bytes, connection.seq * Constant.MTU, len % Constant.MTU);

                                OnReceiveListener listener;
                                if ((listener = this.onReceiveListeners.get(null)) != null)
                                    listener.onReceive(packet.getAddress(), bytes);
                                if ((listener = this.onReceiveListeners.get(packet.getAddress())) != null)
                                    listener.onReceive(packet.getAddress(), bytes);
                            }

                            if (seq - 1 == connection.seq) {
                                connection.seq++;
                                connection.fin = fin;
                                RDT.getSender(packet.getAddress(), port).sendACK(seq);
                            }
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
