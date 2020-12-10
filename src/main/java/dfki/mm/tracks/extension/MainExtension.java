package dfki.mm.tracks.extension;

import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.Field.DoubleField;
import dfki.mm.tracks.FieldList;
import dfki.mm.tracks.GpsTrack;

import java.util.*;

public class MainExtension extends Extension {

    private final List<Field<?>> fields;

    @Override
    public List<Extension> requires() {
        return Collections.emptyList();
    }

    @Override
    public String getInfo() {
        return "input data";
    }

    @Override
    public String getName() {
        return "gps";
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

    @Override
    public void compute(GpsTrack track) {
        FieldList.PointIterator p = track.getGpsPoint();
        FieldList<Long>.FieldValue time = p.get(ExtensionManager.INSTANCE.mainExtension.time);
        FieldList<Double>.FieldValue speed = p.get(ExtensionManager.INSTANCE.mainExtension.speed);
        FieldList<Double>.FieldValue heading = p.get(ExtensionManager.INSTANCE.mainExtension.heading);
        p.next();
        Set<Integer> toRemove = new HashSet<>();
        long prevTime = time.get();
        if (speed.get() == null) {
            speed.set(-1D);
        }
        if (heading.get() == null) {
            heading.set(-1D);
        }
        boolean hasMore = p.next();
        while (hasMore) {
            if (time.get() == null || prevTime >= time.get()) {
                toRemove.add(p.getPos());
//                hasMore = p.removeAndNext();
            } else {
                hasMore = p.next();
            }
        }
        track.removePoints(toRemove);

    }

    public final DoubleField lat;
    public final DoubleField lon;
    public final DoubleField ele;
    public final DoubleField speed;
    public final DoubleField heading;
    public final Field.LongField time;
    public final Field.ModeField mode;
    //    public final Field acc;
//    public final Field speedAcc;
//    public final Field hdop;


    public MainExtension() {
        super();
        this.fields = List.of(
                this.lat = Field.newDoubleField(this, Field.LATITUDE),
                this.lon = Field.newDoubleField(this, Field.LONGITUDE),
                this.ele = Field.newDoubleField(this, "ele"),
                this.speed = Field.newDoubleField(this, Field.SPEED),
                this.heading = Field.newDoubleField(this, "heading"),
//                this.acc = new Field(this, "accuracy", FieldType.DOUBLE),
//                this.speedAcc = new Field(this, "speedAccuracy", FieldType.DOUBLE),
//                this.hdop = new Field(this, "hdop", FieldType.DOUBLE),
                this.time = Field.newLongField(this, Field.TIME),
                this.mode = Field.newModeField(this, Field.MODE)
        );
    }


}
