package systems.singularity.chatfx.util;

/**
 * Created by phts on 05/07/17.
 */
public class Variables {
    public static double senderLossProbability = 0;
    public static double receiverLossProbability = 0;
    public static int senderLossCount = 0;
    public static int receiverLossCount = 0;

    private Variables() {
        // Avoid class instantiation
    }

    public static final class Port {
        public static int chat = 49150;
        public static int file = 49149;
        public static int rtt = 49148;
    }
}
