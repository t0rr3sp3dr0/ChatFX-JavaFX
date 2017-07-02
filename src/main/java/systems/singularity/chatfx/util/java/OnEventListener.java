package systems.singularity.chatfx.util.java;

import java.util.EventListener;

/**
 * Created by pedro on 6/27/17.
 */
public interface OnEventListener extends EventListener {
    void onEvent(Object... objects);
}
