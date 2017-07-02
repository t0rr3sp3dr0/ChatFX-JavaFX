package systems.singularity.chatfx.util;

import systems.singularity.chatfx.util.java.Utilities;

import java.io.File;

/**
 * Created by pedro on 5/21/17.
 */
public final class Constants {
    public static final int MTU = 56 * 1024;
    public static final double SENDER_LOSS_PROBABILITY = 0;
    public static final double RECEIVER_LOSS_PROBABILITY = 0;
    public static final String chatFX = Utilities.defaultDirectory() + File.separator + "ChatFX" + File.separator;

    private Constants() {
        // Avoid class instantiation
    }

    public final class Port {
        public static final int MESSAGE = 49150;
        public static final int FILE = 49149;
        public static final int RTT = 49148;
        public static final int ECHO = 49147;
    }
}
