package dfki.mm.tracks.extension;

import dfki.mm.relation2.GeoMath2;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldList;
import dfki.mm.tracks.GpsTrack;

import java.util.List;

public class MyExtension extends Extension {

//    public final int DEFAULT_DISTANCE = 88;

    private final List<Field<?>> fields;
    private final List<Extension> requires;

    private final MainExtension mainExtension;
    private final OsmExtension osmExtension;

    @Override
    public List<Extension> requires() {
        return requires;
    }

    @Override
    public String getInfo() {
        return "my data";
    }

    @Override
    public String getName() {
        return "my";
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

    public final Field.DoubleField dist;
    public final Field.DoubleField time;
    public final Field.DoubleField gSpeed;
    public final Field.DoubleField gSpeedMissing;
    public final Field.DoubleField gHead;
    public final Field.DoubleField gHeadX;
    public final Field.DoubleField gHeadY;
    public final Field.DoubleField gHeadMissing;

    public final Field.DoubleField head;
    public final Field.DoubleField headX;
    public final Field.DoubleField headY;

    public final Field.DoubleField speed;
//    public final Field.DoubleField accel;
    public final Field.DoubleField headChange;
    public final Field.DoubleField headChangeX;
    public final Field.DoubleField headChangeY;

    public final Field.DoubleField logDist;
    public final Field.DoubleField logTime;
    public final Field.DoubleField logSpeed;
    public final Field.DoubleField g_logSpeed;
    public final Field.DoubleField g_logHead;

    public final Field.DoubleField limitTrainLine;
    public final Field.DoubleField limitTrainStop;
    public final Field.DoubleField limitBusLine;
    public final Field.DoubleField limitBusStop;

    public MyExtension(MainExtension mainExtension, OsmExtension osmExtension) {
        super();
        this.requires = List.of(
                this.mainExtension = mainExtension,
                this.osmExtension = osmExtension
        );
//        updateFields(mainExtension);
        this.fields = List.of(
                this.dist = Field.newDoubleField(this, "dist"),
                this.time = Field.newDoubleField(this, "time"),

                this.gSpeed = Field.newDoubleField(this, "g_Speed"),
                this.gSpeedMissing = Field.newDoubleField(this, "g_SpeedMissing"),
                this.gHead = Field.newDoubleField(this, "g_Head"),
                this.gHeadX = Field.newDoubleField(this, "g_HeadX"),
                this.gHeadY = Field.newDoubleField(this, "g_HeadY"),
                this.gHeadMissing = Field.newDoubleField(this, "g_HeadMissing"),

                this.head = Field.newDoubleField(this, "head"),
                this.headX = Field.newDoubleField(this, "headX"),
                this.headY = Field.newDoubleField(this, "headY"),

                this.speed = Field.newDoubleField(this, "speed"),
//                this.accel = Field.newDoubleField(this, "accel"),
                this.headChange = Field.newDoubleField(this, "headChange"),
                this.headChangeX = Field.newDoubleField(this, "headChangeX"),
                this.headChangeY = Field.newDoubleField(this, "headChangeY"),

                this.logDist = Field.newDoubleField(this, "logDist"),
                this.logTime = Field.newDoubleField(this, "logTime"),
                this.logSpeed = Field.newDoubleField(this, "logSpeed"),
                this.g_logSpeed = Field.newDoubleField(this, "g_logSpeed"),
                this.g_logHead = Field.newDoubleField(this, "g_logHead"),

//                this.prevTrainLine = Field.newDoubleField(this, "prevTrainLine"),
//                this.prevTrainStop = Field.newDoubleField(this, "prevTrainStop"),
//                this.prevBusLine = Field.newDoubleField(this, "prevBusLine"),
//                this.prevBusStop = Field.newDoubleField(this, "prevBusStop")
                this.limitTrainLine = Field.newDoubleField(this, "limitTrainLine"),
                this.limitTrainStop = Field.newDoubleField(this, "limitTrainStop"),
                this.limitBusLine = Field.newDoubleField(this, "limitBusLine"),
                this.limitBusStop = Field.newDoubleField(this, "limitBusStop")
        );
    }

    @Override
    public void compute(GpsTrack track) {
        FieldList.PointIterator current = track.getGpsPoint();
        FieldList.PointIterator prev = track.getGpsPoint();
        Double ph;
        Double head = null;
        var c_time = current.get(this.mainExtension.time);
        var c_lat = current.get(this.mainExtension.lat);
        var c_lon = current.get(this.mainExtension.lon);
        var c_speed = current.get(this.mainExtension.speed);
        var c_heading = current.get(this.mainExtension.heading);

        var c_tl = current.get(this.osmExtension.distanceToTrainLine);
        var c_ts = current.get(this.osmExtension.distanceToTrainStation);
        var c_bl = current.get(this.osmExtension.distanceToBusLine);
        var c_bs = current.get(this.osmExtension.distanceToBusStop);

        var p_time = prev.get(this.mainExtension.time);
        var p_lat = prev.get(this.mainExtension.lat);
        var p_lon = prev.get(this.mainExtension.lon);
//        var c_time = p.get(this.mainExtension.time);
//        var p_time = prev.get(this.mainExtension.time);

        var mc_dist = current.get(this.dist);
        var mc_time = current.get(this.time);
        var mc_gSpeed = current.get(this.gSpeed);
        var mc_gSpeedMissing = current.get(this.gSpeedMissing);
        var mc_gHead = current.get(this.gHead);
        var mc_gHeadX = current.get(this.gHeadX);
        var mc_gHeadY = current.get(this.gHeadY);
        var mc_gHeadMissing = current.get(this.gHeadMissing);

        var mc_speed = current.get(this.speed);
//        var mc_accel = p.get(this.accel);
        var mc_head = current.get(this.head);
        var mc_headX = current.get(this.headX);
        var mc_headY = current.get(this.headY);

        var mc_headChange = current.get(this.headChange);
        var mc_headChangeX = current.get(this.headChangeX);
        var mc_headChangeY = current.get(this.headChangeY);
        var mc_logDist = current.get(this.logDist);
        var mc_logTime = current.get(this.logTime);
        var mc_logSpeed = current.get(this.logSpeed);
        var mc_g_logSpeed = current.get(this.g_logSpeed);
        var mc_g_logHead = current.get(this.g_logHead);
        var mc_cTrainLine = current.get(this.limitTrainLine);
        var mc_cTrainStop = current.get(this.limitTrainStop);
        var mc_cBusLine = current.get(this.limitBusLine);
        var mc_cBusStop = current.get(this.limitBusStop);
//        var p_dist = prev.get(this.dist);
//        var p_time = prev.get(this.time);
//        var p_speed = prev.get(this.speed);
//        var p_accel = prev.get(this.accel);
//        var p_head_change = prev.get(this.head_change);
//        var p_prevTrainLine = prev.get(this.prevTrainLine);
//        var p_prevTrainStop = prev.get(this.prevTrainStop);
//        var p_prevBusLine = prev.get(this.prevBusLine);
//        var p_prevBusStop = prev.get(this.prevBusStop);
        current.next();
        mc_dist.set(0D);
        mc_time.set(0D);
        mc_speed.set(0D);
//        mc_accel.set(-1D);

        fixMissing(c_speed, mc_gSpeed, mc_gSpeedMissing);
        fixMissing(c_heading, mc_gHead, mc_gHeadMissing);
        mc_gHead.set(mc_gHead.get() / 360);
        setXY(mc_gHead.get(), mc_gHeadX, mc_gHeadY);

//        mc_gHead.set(0D);
//        mc_gHeadX.set(1.);
//        mc_gHeadY.set(0D);
//        mc_gHeadMissing.set(1.);
        mc_head.set(0D);
        setXY(0, mc_headX, mc_headY);


        mc_headChange.set(0D);
        setXY(0, mc_headChangeX, mc_headChangeY);
//        mc_headChangeX.set(1.);
//        mc_headChangeY.set(0D);

        mc_logDist.set(1D);
        mc_logTime.set(1D);
        mc_logSpeed.set(1D);
//        mc_g_logSpeed.set(Math.exp(c_speed.get() / 100));
//        mc_g_logHead.set(Math.exp(c_heading.get() / 360));
//        mc_cTrainLine.set(0D);
//        mc_cTrainStop.set(0D);
//        mc_cBusLine.set(0D);
//        mc_cBusStop.set(0D);
//        mc_logDist.set(Math.exp(mc_dist.get() / 100));
//        mc_logTime.set(Math.exp(mc_time.get() / 10));
//        mc_logSpeed.set(Math.exp(mc_speed.get() / 100));
        mc_g_logSpeed.set(Math.exp(c_speed.get() / 100));
        mc_g_logHead.set(Math.exp(c_heading.get() / 360));
        mc_cTrainLine.set(1 - Math.min(1, Math.pow(c_tl.get(), 2)));
        mc_cTrainStop.set(1 - Math.min(1, Math.pow(c_ts.get(), 2)));
        mc_cBusLine.set(1 - Math.min(1, Math.pow(c_bl.get(), 2)));
        mc_cBusStop.set(1 - Math.min(1, Math.pow(c_bs.get(), 2)));

        while (current.next()) {
            prev.next();

            mc_time.set((c_time.get() - p_time.get()) / 1000.D);

            mc_dist.set(1000 * GeoMath2.computeDistance(
                    p_lat.get(),
                    p_lon.get(),
                    c_lat.get(),
                    c_lon.get()
            ));

            fixMissing(c_speed, mc_gSpeed, mc_gSpeedMissing);
            fixMissing(c_heading, mc_gHead, mc_gHeadMissing);
            mc_gHead.set(mc_gHead.get() / 360);
            setXY(mc_gHead.get(), mc_gHeadX, mc_gHeadY);

            mc_speed.set(mc_dist.get() / mc_time.get());
//            mc_accel.set(mc_dist.get() / mc_time.get());
            ph = head;
            head = GeoMath2.computeBearingRad(
                    p_lat.get(),
                    p_lon.get(),
                    c_lat.get(),
                    c_lon.get()
            ) / Math.PI / 2 + 0.5;
            mc_head.set(head);
            setXY(head, mc_headX, mc_headY);

            if (ph != null) {
                double res = ((head - ph) + 2) % 2;
                mc_headChange.set(res);
                setXY(res, mc_headChangeX, mc_headChangeY);
            } else {
                mc_headChange.set(0D);
                setXY(0, mc_headChangeX, mc_headChangeY);
            }
            mc_logDist.set(Math.exp(mc_dist.get() / 100));
            mc_logTime.set(Math.exp(mc_time.get() / 10));
            mc_logSpeed.set(Math.exp(mc_speed.get() / 100));
            mc_g_logSpeed.set(Math.exp(c_speed.get() / 100));
            mc_g_logHead.set(Math.exp(c_heading.get() / 360));
            mc_cTrainLine.set(Math.pow(1 - Math.min(1, c_tl.get()), 2));
            mc_cTrainStop.set(Math.pow(1 - Math.min(1, c_ts.get()), 2));
            mc_cBusLine.set(Math.pow(1 - Math.min(1, c_bl.get()), 2));
            mc_cBusStop.set(Math.pow(1 - Math.min(1, c_bs.get()), 2));
        }
    }

    private double cut(double value) {
        return Math.min(1, value);
    }

    private static void fixMissing(FieldList<Double>.FieldValue current,
                                   FieldList<Double>.FieldValue value,
                                   FieldList<Double>.FieldValue valueMissing) {
        Double s = current.get();
        if (s == null || s < 0) {
            value.set(0D);
            valueMissing.set(1.);
        } else {
            value.set(s);
            valueMissing.set(0D);
        }
    }

    private static void setXY(double current,
                              FieldList<Double>.FieldValue valueX,
                              FieldList<Double>.FieldValue valueY) {
        valueX.set((Math.cos(current * Math.PI * 2) + 1) / 2);
        valueY.set((Math.sin(current * Math.PI * 2) + 1) / 2);
    }

}
