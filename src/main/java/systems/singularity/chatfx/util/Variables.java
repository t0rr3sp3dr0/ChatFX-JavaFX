package systems.singularity.chatfx.util;

import java.net.InetAddress;

/**
 * Created by phts on 05/07/17.
 */
public class Variables {
    private Variables() {
        // Avoid class instantiation
    }

    public static final class DiscardModule {
        public static double senderLossProbability = 0;
        public static double receiverLossProbability = 0;
        public static int senderLossCount = 0;
        public static int receiverLossCount = 0;
    }

    public static final class Server {
        public static InetAddress address = null;
        public static int port = -1;
    }

    public static final class Port {
        public static int chat = 49150;
        public static int file = 49149;
        public static int rtt = 49148;
    }
}
