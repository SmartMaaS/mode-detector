package dfki.mm.relation2;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public abstract class IgnorantSink implements Sink {
    @Override
    public void process(EntityContainer entityContainer) {

    }

    @Override
    public void initialize(Map<String, Object> metaData) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void close() {

    }
}
