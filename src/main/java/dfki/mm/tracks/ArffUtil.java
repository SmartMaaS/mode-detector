package dfki.mm.tracks;

import dfki.mm.TravelMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class ArffUtil {

    private static final Logger log = LoggerFactory.getLogger(ArffUtil.class);
    public static final String DELIMITER = ",";

//    public static final List<Field> fields = Arrays.asList(
//            Field.gps_longitude,
//            Field.gps_latitude,
//            Field.gps_speed,
//            Field.gps_bearing,
//            Field.gps_headingChange,
//            Field.gps_avgHeadingChange,
//            Field.activity_motorized,
//            Field.activity_cycle,
//            Field.activity_walk,
//            Field.distanceToTrainLine,
//            Field.distanceToTrainStation,
//            Field.distanceToBusLine,
//            Field.distanceToBusStop,
//            Field.speed,
//            Field.avgSpeed,
//            Field.maxSpeed,
//            Field.ninetyFifthPercentile,
//            Field.kurtosisSpeed,
//            Field.skewnessSpeed,
//            Field.standardDeviation,
//            Field.headingChange,
//            Field.avgHeadingChange,
//            Field.acceleration,
//            Field.avgAcceleration,
//            Field.avgAccelerationNoSign,
//            Field.tripId,
//            Field.time,
//            Field.timeString,
//            Field.kindOfTrain,
//            Field.kindOfBus,
//            Field.distanceCovered,
//            Field.distanceCoveredTotal,
//            Field.timePassed,
//            Field.timePassedTotal,
//            Field.mode
//    );

//    public static String header() {
//        return "@relation Modedetection\n" +
//                "\n" +
//                "@attribute gps_longitude numeric\n" +
//                "@attribute gps_latitude numeric\n" +
//                "@attribute gps_speed numeric\n" +
//                "@attribute gps_bearing numeric\n" +
//                "@attribute gps_headingChange numeric\n" +
//                "@attribute gps_avgHeadingChange numeric\n" +
//                "@attribute activity_motorized numeric\n" +
//                "@attribute activity_cycle numeric\n" +
//                "@attribute activity_walk numeric\n" +
//                "@attribute distanceToTrainLine numeric\n" +
//                "@attribute distanceToTrainStation numeric\n" +
//                "@attribute distanceToBusLine numeric\n" +
//                "@attribute distanceToBusStop numeric\n" +
//                "@attribute speed numeric\n" +
//                "@attribute avgSpeed numeric\n" +
//                "@attribute maxSpeed numeric\n" +
//                "@attribute ninetyFifthPercentile numeric\n" +
//                "@attribute kurtosisSpeed numeric\n" +
//                "@attribute skewnessSpeed numeric\n" +
//                "@attribute standardDeviation numeric\n" +
//                "@attribute headingChange numeric\n" +
//                "@attribute avgHeadingChange numeric\n" +
//                "@attribute acceleration numeric\n" +
//                "@attribute avgAcceleration numeric\n" +
//                "@attribute avgAccelerationNoSign numeric\n" +
//                "@attribute tripId string\n" +
//                "@attribute time numeric\n" +
//                "@attribute timeString string\n" +
//                "@attribute kindOfTrain string\n" +
//                "@attribute kindOfBus string\n" +
//                "@attribute distanceCovered numeric\n" +
//                "@attribute distanceCoveredTotal numeric\n" +
//                "@attribute timePassed numeric\n" +
//                "@attribute timePassedTotal numeric\n" +
//                "@attribute mode {BICYCLE,TRAIN,WALK,CAR,BUS,UNDEF}\n" +
////                "@attribute mode {Bicycle,Train,Walk,Car,Bus,null}\n" +
//                "\n" +
//                "@data\n";
//    }

//    public static String headerCSV() {
//        return "gps_longitude" + DELIMITER +
//                "gps_latitude" + DELIMITER +
//                "gps_speed" + DELIMITER +
//                "gps_bearing" + DELIMITER +
//                "gps_headingChange" + DELIMITER +
//                "gps_avgHeadingChange" + DELIMITER +
//                "activity_motorized" + DELIMITER +
//                "activity_cycle" + DELIMITER +
//                "activity_walk" + DELIMITER +
//                "distanceToTrainLine" + DELIMITER +
//                "distanceToTrainStation" + DELIMITER +
//                "distanceToBusLine" + DELIMITER +
//                "distanceToBusStop" + DELIMITER +
//                "speed" + DELIMITER +
//                "avgSpeed" + DELIMITER +
//                "maxSpeed" + DELIMITER +
//                "ninetyFifthPercentile" + DELIMITER +
//                "kurtosisSpeed" + DELIMITER +
//                "skewnessSpeed" + DELIMITER +
//                "standardDeviation" + DELIMITER +
//                "headingChange" + DELIMITER +
//                "avgHeadingChange" + DELIMITER +
//                "acceleration" + DELIMITER +
//                "avgAcceleration" + DELIMITER +
//                "avgAccelerationNoSign" + DELIMITER +
//                "tripId" + DELIMITER +
//                "time" + DELIMITER +
//                "timeString" + DELIMITER +
//                "kindOfTrain" + DELIMITER +
//                "kindOfBus" + DELIMITER +
//                "distanceCovered" + DELIMITER +
//                "distanceCoveredTotal" + DELIMITER +
//                "timePassed" + DELIMITER +
//                "timePassedTotal" + DELIMITER +
//                "mode";
//    }

//    public static List<GPSPoint> readFile(List<String> lines) {
//        List<GPSPoint> ret = new ArrayList<>();
//        boolean data = false;
//        int attribute = 0;
//        for (String l : lines) {
//            if (!data) {
//                if (l.startsWith("@attribute ")) {
//                    attribute++;
//                } else if (l.startsWith("@data ")) {
//                    data = true;
//                    log.debug("attribute = {} (should be 34)", attribute);
//                    if (attribute != 35) {
//                        throw new RuntimeException("attribute=" + attribute);
//                    }
//                }
//            } else {
//                String[] s = smartSplit(l, attribute);
//                GPSPoint point = new GPSPoint();
//                point.gps_longitude = Float.parseFloat(s[0]);
//                point.gps_latitude = Float.parseFloat(s[1]);
//                point.gps_speed = Float.parseFloat(s[2]);
//                point.gps_bearing = Float.parseFloat(s[3]);
//                point.gps_headingChange = Float.parseFloat(s[4]);
//                point.gps_avgHeadingChange = Float.parseFloat(s[5]);
//                point.activity_motorized = Float.parseFloat(s[6]);
//                point.activity_cycle = Float.parseFloat(s[7]);
//                point.activity_walk = Float.parseFloat(s[8]);
//                point.distanceToTrainLine = Float.parseFloat(s[9]);
//                point.distanceToTrainStation = Float.parseFloat(s[10]);
//                point.distanceToBusLine = Float.parseFloat(s[11]);
//                point.distanceToBusStop = Float.parseFloat(s[12]);
//                point.speed = Float.parseFloat(s[13]);
//                point.avgSpeed = Float.parseFloat(s[14]);
//                point.maxSpeed = Float.parseFloat(s[15]);
//                point.ninetyFifthPercentile = Float.parseFloat(s[16]);
//                point.kurtosisSpeed = Float.parseFloat(s[17]);
//                point.skewnessSpeed = Float.parseFloat(s[18]);
//                point.standardDeviation = Float.parseFloat(s[19]);
//                point.headingChange = Float.parseFloat(s[20]);
//                point.avgHeadingChange = Float.parseFloat(s[21]);
//                point.acceleration = Float.parseFloat(s[22]);
//                point.avgAcceleration = Float.parseFloat(s[23]);
//                point.avgAccelerationNoSign = Float.parseFloat(s[24]);
//                point.tripId = s[25];
//                point.time = Long.parseLong(s[26]);
//                point.timeString = s[27];
//                point.kindOfTrain = s[28];
//                point.kindOfBus = s[29];
//                point.distanceCovered = Float.parseFloat(s[30]);
//                point.distanceCoveredTotal = Float.parseFloat(s[31]);
//                point.timePassed = Float.parseFloat(s[32]);
//                point.timePassedTotal = Float.parseFloat(s[33]);
//                point.mode = TravelMode.parseString(s[34]);
//                ret.add(point);
//            }
//        }
//        return ret;
//    }

//    public static String writeFile(List<GPSPoint> points) {
//        StringBuilder sb = new StringBuilder(header());
//        points.forEach(p -> sb.append(fields.stream().map(f -> wrapIfNeeded(getField(p, f))).
//                collect(Collectors.joining(","))).append("\n"));
//        return sb.toString();
////        throw new RuntimeException();
//    }
//
//    public static String writeCSV(List<GPSPoint> points) {
//        StringBuilder sb = new StringBuilder(headerCSV());
//        points.forEach(p -> sb.append(fields.stream().map(f -> wrapIfNeeded(getField(p, f))).
//                collect(Collectors.joining(","))).append("\n"));
//        return sb.toString();
////        throw new RuntimeException();
//    }

    public static String[] smartSplit(String line, int len) {
        String[] ret = new String[len];
        int retPos = 0;
        int pos = 0;
        boolean inside = false;
        boolean wasInside = false;
        for (int i = 0; i < line.length(); i++) {
            boolean add = i == line.length() - 1;
            switch (line.charAt(i)) {
                case '\'':
                    inside ^= true;
                    wasInside = true;
                    break;
                case ',':
                    add = true;
                    break;
                default:
                    break;
            }
            if (add) {
                if (i == line.length() - 1) {
                    i++;
                }
                if (!inside) {
                    ret[retPos] = wasInside ? line.substring(pos + 1, i - 2) : line.substring(pos, i - 1);
                    wasInside = false;
                    pos = i + 1;
                    retPos++;
                }

            }
        }
        if (retPos != len - 1) {
            throw new RuntimeException("Bad line" + line);
        }

        return ret;
    }

//    private static String wrapIfNeeded(String value) {
//        return value.contains(" ") ? '\'' + value + '\'' : value;
//    }

//    public static String getField(GPSPoint point, Field field) {
//        switch (field) {
//            case  gps_longitude: return String.valueOf(point.gps_longitude);
//            case  gps_latitude: return String.valueOf(point.gps_latitude);
//            case  gps_speed: return String.valueOf(point.gps_speed);
//            case  gps_bearing: return String.valueOf(point.gps_bearing);
//            case  gps_headingChange: return String.valueOf(point.gps_headingChange);
//            case  gps_avgHeadingChange: return String.valueOf(point.gps_avgHeadingChange);
//            case  activity_motorized: return String.valueOf(point.activity_motorized);
//            case  activity_cycle: return String.valueOf(point.activity_cycle);
//            case  activity_walk: return String.valueOf(point.activity_walk);
//            case  distanceToTrainLine: return String.valueOf(point.distanceToTrainLine);
//            case  distanceToTrainStation: return String.valueOf(point.distanceToTrainStation);
//            case  distanceToBusLine: return String.valueOf(point.distanceToBusLine);
//            case  distanceToBusStop: return String.valueOf(point.distanceToBusStop);
//            case  speed: return String.valueOf(point.speed);
//            case  avgSpeed: return String.valueOf(point.avgSpeed);
//            case  maxSpeed: return String.valueOf(point.maxSpeed);
//            case  ninetyFifthPercentile: return String.valueOf(point.ninetyFifthPercentile);
//            case  kurtosisSpeed: return String.valueOf(point.kurtosisSpeed);
//            case  skewnessSpeed: return String.valueOf(point.skewnessSpeed);
//            case  standardDeviation: return String.valueOf(point.standardDeviation);
//            case  headingChange: return String.valueOf(point.headingChange);
//            case  avgHeadingChange: return String.valueOf(point.avgHeadingChange);
//            case  acceleration: return String.valueOf(point.acceleration);
//            case  avgAcceleration: return String.valueOf(point.avgAcceleration);
//            case  avgAccelerationNoSign: return String.valueOf(point.avgAccelerationNoSign);
//            case  tripId: return String.valueOf(point.tripId);
//            case  time: return String.valueOf(point.time);
//            case  timeString: return String.valueOf(point.timeString);
//            case  kindOfTrain: return String.valueOf(point.kindOfTrain);
//            case  kindOfBus: return String.valueOf(point.kindOfBus);
//            case  distanceCovered: return String.valueOf(point.distanceCovered);
//            case  distanceCoveredTotal: return String.valueOf(point.distanceCoveredTotal);
//            case  timePassed: return String.valueOf(point.timePassed);
//            case  timePassedTotal: return String.valueOf(point.timePassedTotal);
//            case  mode: return String.valueOf(point.mode);
//            default:
//                throw new RuntimeException("f=" + field);
//        }
//    }
//
//    public static void setField(GPSPoint point, Field field, String value) {
//        switch (field) {
//            case  gps_longitude: point.gps_longitude = Float.parseFloat(value); break;
//            case  gps_latitude: point.gps_latitude = Float.parseFloat(value); break;
//            case  gps_speed: point.gps_speed = Float.parseFloat(value); break;
//            case  gps_bearing: point.gps_bearing = Float.parseFloat(value); break;
//            case  gps_headingChange: point.gps_headingChange = Float.parseFloat(value); break;
//            case  gps_avgHeadingChange: point.gps_avgHeadingChange = Float.parseFloat(value); break;
//            case  activity_motorized: point.activity_motorized = Float.parseFloat(value); break;
//            case  activity_cycle: point.activity_cycle = Float.parseFloat(value); break;
//            case  activity_walk: point.activity_walk = Float.parseFloat(value); break;
//            case  distanceToTrainLine: point.distanceToTrainLine = Float.parseFloat(value); break;
//            case  distanceToTrainStation: point.distanceToTrainStation = Float.parseFloat(value); break;
//            case  distanceToBusLine: point.distanceToBusLine = Float.parseFloat(value); break;
//            case  distanceToBusStop: point.distanceToBusStop = Float.parseFloat(value); break;
//            case  speed: point.speed = Float.parseFloat(value); break;
//            case  avgSpeed: point.avgSpeed = Float.parseFloat(value); break;
//            case  maxSpeed: point.maxSpeed = Float.parseFloat(value); break;
//            case  ninetyFifthPercentile: point.ninetyFifthPercentile = Float.parseFloat(value); break;
//            case  kurtosisSpeed: point.kurtosisSpeed = Float.parseFloat(value); break;
//            case  skewnessSpeed: point.skewnessSpeed = Float.parseFloat(value); break;
//            case  standardDeviation: point.standardDeviation = Float.parseFloat(value); break;
//            case  headingChange: point.headingChange = Float.parseFloat(value); break;
//            case  avgHeadingChange: point.avgHeadingChange = Float.parseFloat(value); break;
//            case  acceleration: point.acceleration = Float.parseFloat(value); break;
//            case  avgAcceleration: point.avgAcceleration = Float.parseFloat(value); break;
//            case  avgAccelerationNoSign: point.avgAccelerationNoSign = Float.parseFloat(value); break;
//            case  tripId: point.tripId = value; break;
//            case  time: point.time = Long.parseLong(value); break;
//            case  timeString: point.timeString = value; break;
//            case  kindOfTrain: point.kindOfTrain = value; break;
//            case  kindOfBus: point.kindOfBus = value; break;
//            case  distanceCovered: point.distanceCovered = Float.parseFloat(value); break;
//            case  distanceCoveredTotal: point.distanceCoveredTotal = Float.parseFloat(value); break;
//            case  timePassed: point.timePassed = Float.parseFloat(value); break;
//            case  timePassedTotal: point.timePassedTotal = Float.parseFloat(value); break;
//            case  mode: point.mode = TravelMode.parseString(value); break;
//            default:
//                throw new RuntimeException("f=" + field);
//        }
//    }

//    public static String pointToCSV(GPSPoint point) {
//        return point.toCSV(fields);
//    }
//
//    public static GPSPoint pointFromCSV(String csv) {
//        String[] vals = csv.split(DELIMITER, -1);
//        if (vals.length != fields.size()) {
//            log.warn("wrong lengths csv={} fields=", vals.length, fields.size());
//            throw new RuntimeException("wrong lengths");
//        }
//        GPSPoint ret = new GPSPoint();
//        Iterator<Field> fi = fields.iterator();
//        for (String val : vals) {
//            setField(ret, fi.next(), val);
//        }
//        return ret;
//    }
//
//    public static String arffForTracks(List<GPSTrack> tracks) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(header());
//        for (GPSTrack track : tracks) {
//            for (GPSPoint point : track.points) {
//                sb.append(fields.stream().map(f -> wrapIfNeeded(getField(point, f))).
//                        collect(Collectors.joining(","))).append("\n");
//            }
//        }
//        return sb.toString();
//    }

    /**
     * The only enum supported is MODE.
     * @param tracks tracks to output
     * @param fields fields to be present
     * @return full dataset
     */
    public static Instances trackToDataset(List<GpsTrack> tracks, List<Field<?>> fields) {
        return trackToDataset(tracks, fields, false);
    }

    private static final List<TravelMode> allowed = List.of(
            TravelMode.BICYCLE,
            TravelMode.BUS,
            TravelMode.CAR,
            TravelMode.TRAIN,
            TravelMode.WALK
    );

    public static Instances trackToDataset(List<GpsTrack> tracks, List<Field<?>> fields, boolean dummyMode) {
        FastVector atts = new FastVector(fields.size() + (dummyMode ? 1 : 0));
        for (Field field : fields) {
            switch (field.type) {
                case LONG:
                case DOUBLE:
                    atts.addElement(new Attribute(field.getFullName()));
                    break;
                case STRING:
                    atts.addElement(new Attribute(field.getFullName(), (FastVector) null));
                    break;
                case ENUM:
                    FastVector fastVector = new FastVector(allowed.size());
                    for (TravelMode value : allowed) {
                        fastVector.addElement(value.toString());
                    }
                    atts.addElement(new Attribute(field.getFullName(), fastVector));
                    break;
                default:
                    throw new IllegalArgumentException("unsupported field type: " + field + "=" + field.type);
            }
        }
//        for (int i = 0; i < dummyExtra; i++) {
        if (dummyMode) {
//            atts.addElement(new Attribute("dummy"));
            FastVector fastVector = new FastVector(allowed.size());
            for (TravelMode value : allowed) {
                fastVector.addElement(value.toString());
            }
            atts.addElement(new Attribute("dummy", fastVector));
        }
        int len = tracks.parallelStream().mapToInt(e -> e.size()).sum();
        Instances dataset = new Instances("tracks", atts, len);
        for (GpsTrack track : tracks) {
            FieldList.PointIterator point = track.getGpsPoint();
            List<FieldList.FieldValue> fv = point.getAll(fields);
//            for (GpsTrackPoint point : track.points) {
            while (point.next()) {
                double [] vals = new double[dataset.numAttributes()];
                for (int i = 0; i < fields.size(); i++) {
//                    vals[i]
                    var field = fields.get(i);
//                    Object tmp = field.get(point);
                    Object tmp = fv.get(i).get();
                    if (tmp == null) {
                        vals[i] = Instance.missingValue();
                    } else {
                        switch (field.type) {
                            case DOUBLE:
                                vals[i] = (Double) tmp;
                                break;
                            case LONG:
                                vals[i] = (Long) tmp;
                                break;
                            case STRING:
                                vals[i] = dataset.attribute(i).addStringValue((String) tmp);
                                break;
                            case ENUM:
                                vals[i] = allowed.indexOf(tmp);
                                break;
                            default:
                                vals[i] = Instance.missingValue();
                        }
                    }
                }
                if (dummyMode) {
                    vals[fields.size()] = Instance.missingValue();
                }
//                for (int i = 0; i < dummyExtra; i++) {
//                    vals[i + fields.size()] = Instance.missingValue();
//                }
                dataset.add(new Instance(1.0, vals));
            }
        }
        return dataset;
    }

    public static String writeFile(Instances instances) {
        return instances.toString();
    }

}
