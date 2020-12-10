package dfki.mm.tracks.extension;

import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Extension {
//    int getId();

    private static final AtomicInteger counter = new AtomicInteger();
    public final int id = counter.getAndIncrement();

//    protected final List<Field> fields;

//    protected Extension(List<Field> fields) {
//        this.fields = fields;
//    }

    public abstract List<Extension> requires();
    public abstract String getInfo();
    public abstract String getName();
    public abstract List<Field<?>> getFields();
    public abstract void compute(GpsTrack track);

    public Field getField(String name) {
        return getFields().stream().filter(e -> e.name.equals(name)).findAny().orElse(null);
    }

}
