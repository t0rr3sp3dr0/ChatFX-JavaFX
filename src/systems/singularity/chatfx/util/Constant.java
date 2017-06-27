package systems.singularity.chatfx.util;

/**
 * Created by pedro on 5/21/17.
 */
public final class Constant {
    public static final int MTU = 1024;
    public static final double SENDER_LOSS_PROBABILITY = 0.03125;
    public static final double RECEIVER_LOSS_PROBABILITY = 0.125;

    private Constant() {
        // Avoid class instantiation
    }

    public final class Port {
        public static final int MESSAGE = 49150;
        public static final int FILE = 49149;
        public static final int RTT = 49148;
        public static final int ECHO = 49147;
    }
}