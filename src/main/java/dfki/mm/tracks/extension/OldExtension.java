package dfki.mm.tracks.extension;

import dfki.mm.tracks.Field;
import dfki.mm.relation2.GeoMath2;
import dfki.mm.tracks.GpsTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OldExtension extends Extension {

    private final List<Field<?>> fields;
    private final List<Extension> requires;

//    private Field lat;
//    private Field lon;
//    private Field time;
    private final MainExtension mainExtension;

    @Override
    public List<Extension> requires() {
        return requires;
    }

    @Override
    public String getInfo() {
        return "previous data";
    }

    @Override
    public String getName() {
        return "old";
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }

//    private void updateFields(MainExtension mainExtension) throws NoSuchElementException {
//        List<Field> mainFields = mainExtension.getFields();
//        this.lat = mainFields.stream().filter(e -> e.name.equals(Field.LATITUDE)).findAny().get();
//        this.lon = mainFields.stream().filter(e -> e.name.equals(Field.LONGITUDE)).findAny().get();
//        this.time = mainFields.stream().filter(e -> e.name.equals(Field.TIME)).findAny().get();
//    }

    public final Field<Double> gps_headingChange;
    public final Field<Double> gps_avgHeadingChange;
    public final Field<Double> speed;
    public final Field<Double> avgSpeed;
    public final Field<Double> maxSpeed;
    public final Field<Double> ninetyFifthPercentile;
    public final Field<Double> kurtosisSpeed;
    public final Field<Double> skewnessSpeed;
    public final Field<Double> standardDeviation;
    public final Field<Double> headingChange;
    public final Field<Double> avgHeadingChange;
    public final Field<Double> acceleration;
    public final Field<Double> avgAcceleration;
    public final Field<Double> avgAccelerationNoSign;

    public final Field<Double> distanceCovered;
    public final Field<Double> distanceCoveredTotal;
    public final Field<Long> timePassed;
    public final Field<Long> timePassedTotal;

//    public final Field xxxxxxxxxxxxx;


    public OldExtension(MainExtension mainExtension) {
        super();
        this.requires = List.of(this.mainExtension = mainExtension);
//        updateFields(mainExtension);
        this.fields = List.of(
//                new Field(this, "speed", FieldType.DOUBLE),
//                new Field(this, "acceleration", FieldType.DOUBLE),
                this.gps_headingChange = Field.newDoubleField(this, "gps_headingChange"),
                this.gps_avgHeadingChange = Field.newDoubleField(this, "gps_avgHeadingChange"),
                this.speed = Field.newDoubleField(this, "speed"),
                this.avgSpeed = Field.newDoubleField(this, "avgSpeed"),
                this.maxSpeed = Field.newDoubleField(this, "maxSpeed"),
                this.ninetyFifthPercentile = Field.newDoubleField(this, "ninetyFifthPercentile"),
                this.kurtosisSpeed = Field.newDoubleField(this, "kurtosisSpeed"),
                this.skewnessSpeed = Field.newDoubleField(this, "skewnessSpeed"),
                this.standardDeviation = Field.newDoubleField(this, "standardDeviation"),
                this.headingChange = Field.newDoubleField(this, "headingChange"),
                this.avgHeadingChange = Field.newDoubleField(this, "avgHeadingChange"),
                this.acceleration = Field.newDoubleField(this, "acceleration"),
                this.avgAcceleration = Field.newDoubleField(this, "avgAcceleration"),
                this.avgAccelerationNoSign = Field.newDoubleField(this, "avgAccelerationNoSign"),
                this.distanceCovered = Field.newDoubleField(this, "distanceCovered"),
                this.distanceCoveredTotal = Field.newDoubleField(this, "distanceCoveredTotal"),
                this.timePassed = Field.newLongField(this, "timePassed"),
                this.timePassedTotal = Field.newLongField(this, "timePassedTotal")
        );
    }

    @Override
    public void compute(GpsTrack track) {
        if (track.size() == 0) {
//            throw new IllegalArgumentException("no points");
            return;
        }
        var p = track.getGpsPoint();
//        var prev = track.getGpsPoint();
        var lat = p.get(this.mainExtension.lat);
        var lon = p.get(this.mainExtension.lon);
        var time = p.get(this.mainExtension.time);
        var gps_bearing =  p.get(mainExtension.heading);

        var gps_headingChange = p.get(this.gps_headingChange);
        var gps_avgHeadingChange = p.get(this.gps_avgHeadingChange);
        var headingChange = p.get(this.headingChange);
        var avgHeadingChange = p.get(this.avgHeadingChange);
        var distanceCovered = p.get(this.distanceCovered);
        var distanceCoveredTotal = p.get(this.distanceCoveredTotal);
        var speed = p.get(this.speed);
        var avgSpeed = p.get(this.avgSpeed);
        var maxSpeed = p.get(this.maxSpeed);
        var acceleration = p.get(this.acceleration);
        var avgAcceleration = p.get(this.avgAcceleration);
        var avgAccelerationNoSign = p.get(this.avgAccelerationNoSign);
        var ninetyFifthPercentile = p.get(this.ninetyFifthPercentile);
        var standardDeviation = p.get(this.standardDeviation);
        var kurtosisSpeed = p.get(this.kurtosisSpeed);
        var skewnessSpeed = p.get(this.skewnessSpeed);

//        var speedGetter = track.doubleGetter(speed);
//        var distanceCoveredTotalGetter = track.doubleGetter(distanceCoveredTotal);

//        var trainStopSetter = track.doubleGetter(distanceToTrainStation);

        var timePassed = p.get(this.timePassed);
        var timePassedTotal = p.get(this.timePassedTotal);
//        var timePassed = p.get(this.timePassed);
//        var timePassedTotal = p.get(this.timePassedTotal);

//        for (GpsTrackPoint point : track.points) {
//            EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> d =
//                    DataHolder.INSTANCE.mapData.findNearest(latGetter.apply(point), lonGetter.apply(point), 5);
//
//            busLineSetter.accept(point, d.get(MyNodeBucket.NodeType.BUS_LINE).getDistanceOrMax(DEFAULT_DISTANCE));
//            busStopSetter.accept(point, d.get(MyNodeBucket.NodeType.BUS_STOP).getDistanceOrMax(DEFAULT_DISTANCE));
//            trainLineSetter.accept(point, d.get(MyNodeBucket.NodeType.RAIL_LINE).getDistanceOrMax(DEFAULT_DISTANCE));
//            trainStopSetter.accept(point, d.get(MyNodeBucket.NodeType.RAIL_STOP).getDistanceOrMax(DEFAULT_DISTANCE));
//
//        }



//        LinkedList<GpsTrackPoint> lastPoints = new LinkedList<>();
        Smoother gpsHeadingSmoother = new Smoother(1, 5);
        Smoother headingSmoother = new Smoother(1, 5);
        TimedSmoother speedSmoother = new TimedSmoother(0, 5);
        TimedSmoother accelerationSmoother = new TimedSmoother(0, 5);
//        float maxSpeed = 0;
//        GpsTrackPoint prev = null;
//        for (GpsTrackPoint cur : track.points) {
        boolean first = true;
        double p_gps_bearing = 0;
        double p_lat = 0;
        double p_lon = 0;
        double pp_lat = 0;
        double pp_lon = 0;

        //todo !!!
        double p_distanceCoveredTotal = 0;
        double p_speed = 0;
        long p_time = 0;
        long p_timePassedTotal = 0;

        LinkedList<Double> speeds = new LinkedList<>();

        while (p.next()) {
//            if (lastPoints.size() == 0) {
//                timePassedSetter.accept(cur, 0L);
//                timePassedTotalSetter.accept(cur, 0L);
//            }
//            lastPoints.addFirst(cur);
//            if (lastPoints.size() > 5) {
//                lastPoints.removeLast();
//            }
            if (first) {
                first = false;
                // better use another getter, returning default instead of null
                timePassed.set(0L);
                timePassedTotal.set(0L);
                gps_headingChange.set(0D);
                gps_avgHeadingChange.set(0D);
                headingChange.set(0D);
                avgHeadingChange.set(0D);
                distanceCovered.set(0D);
                distanceCoveredTotal.set(0D);
                speed.set(0D);
                avgSpeed.set(0D);
                maxSpeed.set(0D);
                acceleration.set(0D);
                avgAcceleration.set(0D);
                avgAccelerationNoSign.set(0D);
                ninetyFifthPercentile.set(0D);
                standardDeviation.set(0D);
                kurtosisSpeed.set(0D);
                skewnessSpeed.set(0D);
                speeds.add(0D);
            } else {

//                double tmp;
                gps_headingChange.set(Math.abs(computeHeadingChange(
                        p_gps_bearing,
                        gps_bearing.get()
                )));
//                cur.gps_headingChange = Math.abs(computeHeadingChange(prev.gps_bearing, cur.gps_bearing));
//                update(prev, p);
//                gps_avgHeadingChangeSetter.accept(cur, tmp);
                gps_avgHeadingChange.set(
                        gpsHeadingSmoother.add(gps_headingChange.get())
                );
//                cur.gps_avgHeadingChange = gpsHeadingSmoother.add(cur.gps_headingChange);
//                        (float) (gps_headingChangeList.stream().mapToDouble(e -> e.gps_headingChange)
//                        .sum() / gps_headingChangeList.size());
                double cur_distanceCovered;
                distanceCovered.set(cur_distanceCovered = 1000 * GeoMath2.computeDistance(
                        lat.get(),
                        lon.get(),
                        p_lat,
                        p_lon
                ));
//                cur.distanceCovered = 1000 * (float) GeoMath2.computeDistance(cur.gps_latitude, cur.gps_longitude, prev.gps_latitude, prev.gps_longitude);
                distanceCoveredTotal.set(
                        p_distanceCoveredTotal + cur_distanceCovered / 1000
                        );
//                cur.distanceCoveredTotal = prev.distanceCoveredTotal + cur.distanceCovered / 1000;

                long cur_timePassed = time.get() - p_time;
                timePassed.set(cur_timePassed);
//                        = timeGetter.apply(cur) - timeGetter.apply(prev);
//                double cur_timePassed = cur.time - prev.time;
                timePassedTotal.set(cur_timePassed + p_timePassedTotal);
//                cur.timePassedTotal = prev.timePassedTotal + cur.timePassed;


                double cur_speed = cur_distanceCovered / cur_timePassed * 1000;

                speed.set(cur_speed);
                speeds.addFirst(cur_speed);
                if (speeds.size() > 5) {
                    speeds.removeLast();
                }
                //                cur.avgSpeed = (gps_headingChangeList.stream().mapToDouble(e -> e.gps_headingChange).sum()
                double cur_avgSpeed = (float) speedSmoother.add(cur_distanceCovered, cur_timePassed) * 1000;
//                cur.maxSpeed = maxSpeed = Math.max(maxSpeed, cur.speed);
                double cur_maxSpeed = speeds.stream().max(Double::compareTo).orElse(0D);

                double cur_acceleration = (cur_speed - p_speed) / cur_timePassed * 1000;
                double cur_avgAcceleration = (float) accelerationSmoother.add(cur_speed - p_speed, cur_timePassed) * 1000;
//                cur.avgAcceleration = accelerationSmoother.add(cur.speed - prev.speed, cur.timePassed);
                double cur_avgAccelerationNoSign = (float)
                        (accelerationSmoother.list.stream().mapToDouble(Math::abs).sum() / accelerationSmoother.sumTime) * 1000;

                avgSpeed.set(cur_avgSpeed);
                maxSpeed.set(cur_maxSpeed);
                acceleration.set(cur_acceleration);
                avgAcceleration.set(cur_avgAcceleration);
                avgAccelerationNoSign.set(cur_avgAccelerationNoSign);

                // previousPreviousPosition 235

                if (speeds.size() > 3) {
                    double cur_headingChange;
                    if (cur_speed < 1 || p_speed < 1) {
//                        headingChangeSetter.accept(cur, 0D);
                        cur_headingChange = 0;
                    } else {
                        cur_headingChange =
//                        headingChangeSetter.accept(cur,
                                Math.abs(computeHeadingChange(
                                        pp_lat,
                                        pp_lon,
                                        p_lat,
                                        p_lon,
                                        lat.get(),
                                        lon.get()));
                    }
                    headingChange.set(cur_headingChange);
                    avgHeadingChange.set(headingSmoother.add(cur_headingChange));
//                    cur.avgHeadingChange = headingSmoother.add(cur.headingChange);
                } else {
                    headingChange.set(0d);
                    avgHeadingChange.set(0d);
                }

                // speedList 260
//                List<Double> speeds = lastPoints.parallelStream().map(speedGetter).sorted().collect(Collectors.toList());
                List<Double> ss = new ArrayList<>(speeds);
                Collections.sort(ss);
                if (speeds.size() > 1) {
                    ninetyFifthPercentile.set(speeds.get((int) (speeds.size() * 0.95) - 1));
//                    cur.ninetyFifthPercentile = speeds.get((int) (speeds.size() * 0.95) - 1);
                }
//                Collections.sort(new ArrayList<>(speedSmoother.list));
                double totalDeviations = 0;
                // kurtosis: https://en.wikipedia.org/wiki/Kurtosis
                double kurtosisDivider = 0;
                double kurtosisDenominator = 0;

                // skewness: https://en.wikipedia.org/wiki/Skewness
                double skewnessDivider = 0;
                double skewnessDenominator = 0;

                for (Double sss : ss) {
                    totalDeviations += Math.pow(sss - cur_avgSpeed, 2);
                    // kurtosis calculation
                    kurtosisDivider += Math.pow((sss - cur_avgSpeed), 4);
                    kurtosisDenominator += Math.pow((sss - cur_avgSpeed), 2);

                    // skewness calculation
                    skewnessDivider += Math.pow((sss - cur_avgSpeed), 3);
                    skewnessDenominator += Math.pow((sss - cur_avgSpeed), 2);
                }
                double mean = totalDeviations / speeds.size();
                standardDeviation.set(mean > 0 ? Math.sqrt(mean) : 0);
//                if (mean > 0) {
//                    standardDeviationSetter.accept(cur, Math.sqrt(mean));
//                    cur.standardDeviation = (float) Math.sqrt(mean);
//                }

                kurtosisDenominator = Math.pow(kurtosisDenominator, 2);

                kurtosisSpeed.set(kurtosisDenominator > 0 ?
                        kurtosisDivider * speeds.size() / kurtosisDenominator : 0);
//                if (kurtosisDenominator > 0)
//                    cur.kurtosisSpeed = kurtosisDivider * speeds.size() / kurtosisDenominator;

                skewnessDivider = skewnessDivider / speeds.size();
                skewnessSpeed.set(0D);
                if (speeds.size() > 1) {
                    skewnessDenominator = skewnessDenominator / (speeds.size() - 1);
                    skewnessDenominator = Math.pow(skewnessDenominator, 1.5);

                    if (skewnessDenominator > 0) {
                        skewnessSpeed.set(skewnessDivider / skewnessDenominator);
//                        cur.skewnessSpeed = skewnessDivider / skewnessDenominator;
                    }
                }


            }
            p_gps_bearing = gps_bearing.get();
            pp_lat = p_lat;
            pp_lon = p_lon;
            p_lat = lat.get();
            p_lon = lon.get();

            p_distanceCoveredTotal = distanceCoveredTotal.get();
            p_speed = speed.get();
            p_time = time.get();
            p_timePassedTotal = timePassedTotal.get();
//            gps_headingChangeList.addLast(cur);
//            if (gps_headingChangeList.size() > SMOOTH) {
//                gps_headingChangeList.removeFirst();
//            }
//            prev = cur;
        }

    }

    private static class Smoother {
        final int min;
        final int max;
        double sum;
        LinkedList<Double> list;

        public Smoother(int min, int max) {
            this.min = min;
            this.max = max;
            list = new LinkedList<>();
        }

        public double add(double val) {
            list.addLast(val);
            sum += val;
            if (list.size() > max) {
                sum -= list.removeFirst();
            }
            if (list.size() > min) {
                return sum / list.size();
            }
            return 0;
        }
    }

    private static class TimedSmoother {
        final int min;
        final int max;
        double sum;
        double sumTime;
        LinkedList<Double> list;
        LinkedList<Double> times;

        public TimedSmoother(int min, int max) {
            this.min = min;
            this.max = max;
            list = new LinkedList<>();
            times = new LinkedList<>();
        }

        public double add(double val, double time) {
            list.addLast(val);
            times.addLast(time);
            sum += val;
            sumTime += time;
            if (list.size() > max) {
                sum -= list.removeFirst();
                sumTime -= times.removeFirst();
            }
            if (list.size() > min) {
                return sum / sumTime;
            }
            return 0;
        }
    }

    public static double computeHeadingChange(double bearing1, double bearing2) {
        double out = bearing2 - bearing1;
        out += 360;
        out %= 360;

        if (out > 180)
            out -= 360;

        return out;
    }

    private static double computeHeadingChange(
            double previousPreviousPositionLat,
            double previousPreviousPositionLon,
            double previousPositionLat,
            double previousPositionLon,
            double currentPositionLat,
            double currentPositionLon
    ) {
        double bearing1 = GeoMath2.computeBearingRad(
                previousPreviousPositionLat,
                previousPreviousPositionLon,
                previousPositionLat,
                previousPositionLon
        );
        double bearing2 = GeoMath2.computeBearingRad(
                previousPositionLat,
                previousPositionLon,
                currentPositionLat,
                currentPositionLon
        );

        double out = bearing2 - bearing1;
        out += 2 * Math.PI;
        out %= 2 * Math.PI;

        if (out > Math.PI)
            out -= 2 * Math.PI;

        return  out * 180 / Math.PI;
    }

//    private static double computeHeadingChange(
//            GpsTrackPoint previousPreviousPosition,
//            GpsTrackPoint previousPosition,
//            GpsTrackPoint currentPosition
//    ) {
//        double bearing1 = GeoMath2.computeBearingRad(previousPreviousPosition, previousPosition);
//        double bearing2 = GeoMath2.computeBearingRad(previousPosition, currentPosition);
//
//        double out = bearing2 - bearing1;
//        out += 2 * Math.PI;
//        out %= 2 * Math.PI;
//
//        if (out > Math.PI)
//            out -= 2 * Math.PI;
//
//        return  out * 180 / Math.PI;
//    }


}
