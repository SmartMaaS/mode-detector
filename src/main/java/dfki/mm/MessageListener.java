package dfki.mm;

import java.util.Collection;

public interface MessageListener {
    void onMessage(InternalMessage message, Collection<String> data);
}
