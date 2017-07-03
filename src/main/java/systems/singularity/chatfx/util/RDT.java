package systems.singularity.chatfx.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import systems.singularity.chatfx.util.java.OnEventListener;
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
        assert port > 0;
        System.out.println("\t\t\t\tgetSender(InetAddress, Integer)\t" + port);

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
        assert port > 0;
        System.out.println("\t\t\t\tgetReceiver(Integer)\t" + port);

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

    @NotNull
    public static Receiver getReceiver(@NotNull Sender sender) throws SocketException {
        assert sender.socket.getPort() > 0;
        System.out.println("\t\t\t\tgetReceiver(Sender)\t" + sender.socket.getLocalPort());

        synchronized (RDT.receivers) {
            Receiver receiver = receivers.get(sender.socket.getLocalPort());
            if (receiver == null) {
                receiver = new Receiver(sender);
                receiver.start();
                receivers.put(sender.socket.getLocalPort(), receiver);
            }
            return receiver;
        }
    }

    public static boolean dispose(double probability) {
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

        public Integer seq = -1;
        public Integer ack = -1;
        public Integer fin = -1;
        public Integer repeatedCount = 0;

        public Connection() {
            this.packets = new PriorityQueue<>();
            this.window = new ResizableBlockingQueue<>(4);
        }
    }

    public static final class Sender extends Thread {
        private final InetAddress address;
        private final int port;

        private final DatagramSocket socket;
        private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(Short.MAX_VALUE);
        private final RDT.Connection connection = new Connection();
        private final Timer timer = new Timer();
        private final RDT.RTT.Probe probe;

        private Sender(InetAddress address, int port) throws SocketException, UnknownHostException {
            super();

            this.address = address;
            this.port = port;

            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(0);

            this.probe = new RTT.Probe(address, 49150);
            this.probe.setOnTimeoutChanged(objects -> Sender.this.timer.setTimeout((Integer) objects[0]));
            this.probe.start();

            System.out.printf("DEBUG: Sender\t\tADDRESS\t%s\tPORT\t%s\tCONNECTION\t%s\n", this.address, this.port, this.connection);
            System.out.printf("RDT.getReceiver\t%s\tPORT\t%s\n", RDT.getReceiver(Sender.this), this.socket.getLocalPort());
        }

        public void sendMessage(byte[] message) throws InterruptedException {
            System.out.printf("DEBUG: sendMessage\t\tADDRESS\t%s\tPORT\t%s\tCONNECTION\t%s\n", this.address, this.port, this.connection);

            this.queue.put(message);

            //noinspection StatementWithEmptyBody
            while (this.queue.contains(message)) ;
        }

        public void sendACK(Integer seq, Integer port) throws IOException {
            System.out.printf("DEBUG: sendACK\t\tADDRESS\t%s\tPORT\t%s\tCONNECTION\t%sSEQ\t%s\n", this.address, this.port, this.connection, seq);

            byte[] payload = new byte[8 + Constants.MTU];
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

                    for (int i = 0; i < Math.ceil((double) message.length / Constants.MTU); i++) {
                        synchronized (this.connection.window) {
                            this.connection.window.add(seq = ++this.connection.seq);
                        }

                        byte[] payload = new byte[8 + Constants.MTU];

                        fin = i + 1 == Math.ceil((double) message.length / Constants.MTU);

                        payload[0] = (byte) ((fin ? 0b01000000 : 0b00000000) | ((len >> 24) & 0b00111111));
                        payload[1] = (byte) (len >> 16);
                        payload[2] = (byte) (len >> 8);
                        payload[3] = len.byteValue();
                        payload[4] = (byte) (seq >> 8);
                        payload[5] = seq.byteValue();
                        payload[6] = (byte) (port >> 8);
                        payload[7] = port.byteValue();

                        if (!fin)
                            System.arraycopy(message, i * Constants.MTU, payload, 8, Constants.MTU);
                        else
                            System.arraycopy(message, i * Constants.MTU, payload, 8, ((message.length - 1) % Constants.MTU) + 1);

                        if (!RDT.dispose(Constants.SENDER_LOSS_PROBABILITY)) {
                            DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.port);
                            socket.send(packet);

                            System.err.printf("%d\tFIN(%b)\tSEQ(%d)\n", Arrays.hashCode(message), fin, seq);
                        } else
                            System.err.printf("%d\tDISPOSED\tSEQ(%d)\n", Arrays.hashCode(message), seq);


                        this.timer.watch(new Packet(seq, payload));
                    }

                    //noinspection StatementWithEmptyBody
                    while (this.connection.window.size() > 0) ;

                    System.err.printf("%d\tFIN\n", Arrays.hashCode(message));
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    break;
                }
        }

        public void onACK(final int seq) {
            if (!this.connection.window.contains(seq)) {
                System.err.printf("Repeated\tACK(%d)\n", seq);
                if (this.connection.repeatedCount == 3) {
                    this.connection.repeatedCount = 0;
                    this.connection.window.resize(0.875, 0);
                }
            } else {
                this.connection.ack = Math.max(this.connection.ack, seq);
                this.connection.repeatedCount = 0;
                this.connection.window.removeIf(integer -> integer <= seq);
                this.connection.window.resize(1, 1);
                System.err.printf("Received\tACK(%d)\n", seq);
            }
        }

        private final class Timer extends Thread {
            private final PriorityQueue<Pair<Long, Packet>> heap = new PriorityQueue<>();
            private int timeout = 1;

            private Timer() {
                // Avoid class instantiation
            }

            @Override
            public void run() {
                super.run();

                //noinspection InfiniteLoopStatement
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

            assert this.port > 0;
            System.out.println("\t\t\t\tReceiver(int)\t" + port);
        }

        private Receiver(Sender sender) {
            super();

            this.port = sender.socket.getLocalPort();
            this.socket = sender.socket;

            assert this.port > 0;
            System.out.println("\t\t\t\tReceiver(sender)\t" + port);
        }

        @Override
        public void run() {
            super.run();

            while (true) {
                try {
                    byte[] payload = new byte[8 + Constants.MTU];
                    DatagramPacket packet = new DatagramPacket(payload, payload.length);
                    this.socket.receive(packet);

                    final Boolean ack = ((payload[0] >> 7) & 0b1) != 0;
                    final Boolean fin = ((payload[0] >> 6) & 0b1) != 0;
                    final Integer len = (((payload[0] & 0b00111111) << 24) & 0xFF000000) | ((payload[1] << 16) & 0x00FF0000) | ((payload[2] << 8) & 0x0000FF00) | (payload[3] & 0x000000FF);
                    final Integer seq = ((payload[4] << 8) & 0xFF00) | (payload[5] & 0x00FF);
                    final Integer port = ((payload[6] << 8) & 0xFF00) | (payload[7] & 0x00FF);

                    if (RDT.dispose(Constants.RECEIVER_LOSS_PROBABILITY)) {
                        System.out.printf("%d\tDISPOSED\tSEQ(%d)\n", packet.getAddress().hashCode(), seq);
                        continue;
                    }

                    if (ack) {
                        RDT.getSender(packet.getAddress(), port).onACK(seq);

                        System.out.printf("DEBUG: onACK\t\tADDRESS\t%s\tPORT\t%s\n", packet.getAddress(), port);
                    } else {
                        Connection connection;
                        synchronized (this.connections) {
                            connection = this.connections.get(packet.getAddress());
                            if (connection == null) {
                                connection = new Connection();
                                this.connections.put(packet.getAddress(), connection);
                            }
                        }

                        System.out.printf("DEBUG: onReceive\t\tADDRESS\t%s\tPORT\t%s\tCONNECTION\t%s\n", packet.getAddress(), port, connection);

                        //noinspection SynchronizationOnLocalVariableOrMethodParameter
                        synchronized (connection) {
                            Packet pkt = new Packet(seq, Arrays.copyOfRange(payload, 8, 8 + Constants.MTU));

                            if (seq <= connection.fin || seq < connection.seq || connection.packets.contains(pkt)) {
                                System.out.printf("\nUnexpected SEQ(%d)\t%d(%d)\n%b\t%b\t%b\n\n", seq, connection.hashCode(), connection.seq, seq <= connection.fin, seq < connection.seq, connection.packets.contains(pkt));
                                RDT.getSender(packet.getAddress(), packet.getPort()).sendACK(connection.seq, this.port);
                                continue;
                            }

                            if (seq - 1 >= connection.seq) {
                                connection.packets.add(pkt);
                                System.out.printf("%d\tFIN(%b)\tSEQ(%d)\n", connection.hashCode(), fin, seq);
                            }

                            int packetsCount = (int) Math.ceil((double) len / Constants.MTU);
                            if (connection.packets.size() == packetsCount) {
                                byte[] bytes = new byte[len];
                                for (int i = 0; i < packetsCount - 1; i++)
                                    System.arraycopy(connection.packets.poll().bytes, 0, bytes, i * Constants.MTU, Constants.MTU);

                                Packet finPacket = connection.packets.poll();
                                connection.seq = finPacket.seq;
                                RDT.getSender(packet.getAddress(), packet.getPort()).sendACK(finPacket.seq, this.port);
                                System.arraycopy(finPacket.bytes, 0, bytes, (connection.seq - (connection.fin + 1)) * Constants.MTU, ((len - 1) % Constants.MTU) + 1);
                                connection.fin = connection.seq;

                                OnReceiveListener listener;
                                if ((listener = this.onReceiveListeners.get(null)) != null)
                                    listener.onReceive(packet.getAddress(), packet.getPort(), bytes);
                                if ((listener = this.onReceiveListeners.get(packet.getAddress())) != null)
                                    listener.onReceive(packet.getAddress(), packet.getPort(), bytes);
                            }

                            if (seq - 1 == connection.seq) {
                                for (int i = seq; connection.packets.contains(new Packet(i, null)); i++)
                                    connection.seq++;

                                RDT.getSender(packet.getAddress(), packet.getPort()).sendACK(connection.seq - 1, this.port);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void clearOnReceiveListeners() {
            this.onReceiveListeners.clear();
        }

        public void setOnReceiveListener(@Nullable InetAddress address, @Nullable OnReceiveListener onReceiveListener) {
            this.onReceiveListeners.put(address, onReceiveListener);
        }

        public interface OnReceiveListener {
            void onReceive(InetAddress address, int port, byte[] bytes);
        }
    }

    public static final class RTT {
        private RTT() {
            // Avoid class instantiation
        }

        public static final class Echo extends Thread {
            private final DatagramSocket datagramSocket;

            public Echo(int port) throws SocketException {
                this.datagramSocket = new DatagramSocket(port);
            }

            public Echo(Receiver receiver) {
                this.datagramSocket = receiver.socket;
            }

            public Echo(Sender sender) {
                this.datagramSocket = sender.socket;
            }

            @Override
            public void run() {
                super.run();

                try {
                    byte[] probeData = new byte[1];

                    //noinspection InfiniteLoopStatement
                    while (true) {
                        DatagramPacket probePacket = new DatagramPacket(probeData, probeData.length);
                        datagramSocket.receive(probePacket);
                        datagramSocket.send(new DatagramPacket(probeData, probeData.length, probePacket.getAddress(), probePacket.getPort()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static final class Probe extends Thread {
            private static final int MIN_TIMEOUT = 1;
            private static final double ALPHA = 0.125;
            private static final double BETA = 0.250;

            private final InetAddress address;
            private final int port;

            private int timeout = 500;
            private double sample = 0;
            private double estimated = 0;
            private double deviation = 0;

            private OnEventListener onRTTFailed = null;
            private OnEventListener onTimeoutChanged = null;

            public Probe(InetAddress address, int port) {
                this.address = address;
                this.port = port;
            }

            @Override
            public void run() {
                super.run();

                try {
                    DatagramSocket socket = new DatagramSocket();

                    byte seq = Byte.MIN_VALUE;
                    byte[] bytes = {seq};

                    //noinspection InfiniteLoopStatement
                    while (true)
                        try {
                            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, this.port);
                            socket.setSoTimeout(250);

                            this.sample = System.nanoTime();
                            socket.send(packet);
                            do {
                                socket.receive(packet);
                            } while (bytes[0] != seq);
                            this.sample = System.nanoTime() - this.sample;

                            this.estimated = (1 - Probe.ALPHA) * this.estimated + Probe.ALPHA * this.sample;
                            this.deviation = (1 - Probe.BETA) * this.deviation + Probe.BETA * Math.abs(this.sample - this.estimated);

                            int timeout = Math.max((int) (Math.round(this.estimated + 4 * this.deviation) / 1e6), Probe.MIN_TIMEOUT);
                            if (this.onTimeoutChanged != null && timeout != this.timeout)
                                this.onTimeoutChanged.onEvent(timeout);
                            this.timeout = timeout;
                        } catch (IOException e) {
                            if (this.onRTTFailed != null)
                                this.onRTTFailed.onEvent();
                        } finally {
                            bytes[0] = ++seq;

                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void setOnRTTFailed(OnEventListener onRTTFailed) {
                this.onRTTFailed = onRTTFailed;
            }

            public void setOnTimeoutChanged(OnEventListener onTimeoutChanged) {
                this.onTimeoutChanged = onTimeoutChanged;
            }
        }
    }
}
