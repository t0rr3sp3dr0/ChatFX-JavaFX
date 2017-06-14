package systems.singularity.chatfx.util;

/**
 * Created by pedro on 5/21/17.
 */
public final class Constants {
    private Constants() {
        // Avoid class instantiation
    }

    public static int[] getPorts() {
        return new int[]{49149, 49150};
    }

    public static int getMTU() {
        return 32 * 1024;
    }
}
