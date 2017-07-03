package systems.singularity.chatfx.util;

/**
 * Created by pedro on 7/3/17.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();

    private Singleton() {
    }

    public static Singleton getInstance() {
        return ourInstance;
    }
}
