package systems.singularity.chatfx.client;

import systems.singularity.chatfx.util.RDT;

import java.util.HashMap;

/**
 * Created by pedro on 7/3/17.
 */
public class Singleton extends HashMap<String, Object> {
    private static Singleton ourInstance = new Singleton();

    public RDT.Receiver chatReceiver;
    public RDT.Receiver fileReceiver;
    public RDT.Receiver rttReceiver;

    private Singleton() {
    }

    public static Singleton getInstance() {
        return ourInstance;
    }
}
