package systems.singularity.chatfx.util;

import systems.singularity.chatfx.util.java.Utilities;

import java.nio.file.Paths;

/**
 * Created by pedro on 5/21/17.
 */
public final class Constants {
    public static final int MTU = 56 * 1024;
    public static final String PERSISTENT_DIRECTORY = Paths.get(Utilities.defaultDirectory(), "ChatFX").toString();
    public static final String DOWNLOAD_DIRECTORY = Paths.get(System.getProperty("user.home"), "Downloads").toString();

    private Constants() {
        // Avoid class instantiation
    }
}
