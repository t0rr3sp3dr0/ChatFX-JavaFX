package systems.singularity.chatfx.util.java;

import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.io.IOException;

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
}
