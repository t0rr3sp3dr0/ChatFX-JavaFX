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

    public static String MD5(String password) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(password.getBytes(), 0, password.length());
        return stringHexa(m.digest());
    }

    private static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) s.append('0');
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }
}
