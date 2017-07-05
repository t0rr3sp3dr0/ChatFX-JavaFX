package systems.singularity.chatfx.util.java;

import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by phts on 14/06/17.
 */
public class Utilities {
    private Utilities() {
        // Avoid class instantiation
    }

    public static synchronized void closeCloseables(@NotNull Closeable... closeables) {
        for (Closeable closeable : closeables)
            if (closeable != null)
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
    }

    public static String defaultDirectory() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN"))
            return System.getenv("APPDATA");
        else if (OS.contains("MAC"))
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support").toString();
        else
            return Paths.get(System.getProperty("user.home"), ".local", "share").toString();
    }

    public static String md5(String s) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        messageDigest.update(s.getBytes(), 0, s.length());
        return Utilities.hexString(messageDigest.digest());
    }

    private static String hexString(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            int upperBound = ((aByte >> 4) & 0xf) << 4;
            int lowerBound = aByte & 0xf;
            if (upperBound == 0) s.append('0');
            s.append(Integer.toHexString(upperBound | lowerBound));
        }
        return s.toString();
    }
}
