package systems.singularity.chatfx.util.java;

import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;

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

    public static String defaultDirectory()
    {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN"))
            return System.getenv("APPDATA");
        else if (OS.contains("MAC"))
            return Paths.get(System.getProperty("user.home"),"Library", "Application Support").toString();
        else
            return Paths.get(System.getProperty("user.home"), ".local").toString();
    }
}
