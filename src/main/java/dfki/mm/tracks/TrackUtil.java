package dfki.mm.tracks;

import dfki.mm.DataHolder;
import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TrackUtil {

    private static final Logger log = LoggerFactory.getLogger(TrackUtil.class);




//    public static void trackPredictionStat(GpsTrack track, Field.ModeField field1, Field.ModeField field2) {
//        for (GpsTrackPoint point : track.points) {
//
//        }
//        Map<TravelMode, Long> m1 = track.points.parallelStream().map(e -> e.mode).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//        Map<TravelMode, Long> m2 = prediction.parallelStream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
////        track.points.parallelStream().map(e -> e.mode).mapToInt(Collectors..groupingBy(Function.identity(), Collectors.counting()))
////        track.points.parallelStream().map(e -> e.mode).reduce(Function.identity(), )Collectors..groupingBy(Function.identity(), Collectors.counting()))
//        log.info("source : {}", m1.entrySet().stream().map(e -> e.getKey().name() + ": " + e.getValue()).collect(Collectors.joining(", ")));
//        log.info("predict: {}", m2.entrySet().stream().map(e -> e.getKey().name() + ": " + e.getValue()).collect(Collectors.joining(", ")));
//
//        if (track.points.size() != prediction.size()) {
//            log.error("different lengths: track={} prediction={}", track.points.size(), prediction.size());
//            return;
//        }
////        for (int i = 0; i < track.points.size(); i++) {
////
////        }
//    }

//    public static void trackPredictionStat(GpsTrack track, List<TravelMode> prediction) {
//        Map<TravelMode, Long> m1 = track.points.parallelStream().map(e -> e.mode).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//        Map<TravelMode, Long> m2 = prediction.parallelStream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
////        track.points.parallelStream().map(e -> e.mode).mapToInt(Collectors..groupingBy(Function.identity(), Collectors.counting()))
////        track.points.parallelStream().map(e -> e.mode).reduce(Function.identity(), )Collectors..groupingBy(Function.identity(), Collectors.counting()))
//        log.info("source : {}", m1.entrySet().stream().map(e -> e.getKey().name() + ": " + e.getValue()).collect(Collectors.joining(", ")));
//        log.info("predict: {}", m2.entrySet().stream().map(e -> e.getKey().name() + ": " + e.getValue()).collect(Collectors.joining(", ")));
//
//        if (track.points.size() != prediction.size()) {
//            log.error("different lengths: track={} prediction={}", track.points.size(), prediction.size());
//            return;
//        }
////        for (int i = 0; i < track.points.size(); i++) {
////
////        }
//    }
//
    public static class DS {
        public Double d;
        public String s;

        public DS(Double d, String s) {
            this.d = d;
            this.s = s;
        }

        public void setDS(Double d, String s) {
            this.d = d;
            this.s = s;
        }
    }

//    public static class TrackStatistics {
//        public List<GpsTrack> tracks;
//        public long points = 0;
//        public Map<TravelMode, Long> modes = new TreeMap<>();
//        public Map<Field, DS> min = new TreeMap<>();
//        public Map<Field, DS> max = new TreeMap<>();
//
////        public TrackStatistics(GpsTrack track) {
////            this(track, Field.floatFields);
////        }
//
//        public TrackStatistics(GpsTrack track, List<Field> fields) {
//            this.tracks = Collections.singletonList(track);
//            GPSPoint prev = null;
//            for (GPSPoint point : track.points) {
//                points++;
//                modes.compute(point.mode, (k, v) -> (v == null) ? 1 : v + 1);
//                for (Field field : fields) {
//                    double df = point.getDouble(field);
////                    var m = min.get(field);
////                    if (m == null) {
////                        min.put(field, new DS(df, track.name + "-" + points));
////                    } else if (m.d > df) {
////                        m.setDS(df, track.name + "-" + points);
////                    }
//
//                    var m = min.putIfAbsent(field, new DS(df, track.name + "-" + points));
//                    if (m != null && m.d > df) {
//                        m.setDS(df, track.name + "-" + points);
//                    }
//
//                    m = max.putIfAbsent(field, new DS(df, track.name + "-" + points));
//                    if (m != null && m.d < df) {
//                        m.setDS(df, track.name + "-" + points);
//                    }
////                    min.computeIfPresent(field, (k, v) -> v.d > df ? v.setDS(df, track.name + "-" + points) : null);
////                    min.compute(field, (k, v) -> (v == null) ? point.getDouble(field) : Double.min(v, df));
////                    max.compute(field, (k, v) -> (v == null) ? point.getDouble(field) : Double.max(v, point.getDouble(field)));
//                }
//            }
//        }
//
//
//        public TrackStatistics(List<GpsTrack> tracks) {
//            this.tracks = tracks;
//            List<TrackStatistics> ts = tracks.stream().map(TrackStatistics::new).collect(Collectors.toList());
//            for (TrackStatistics t : ts) {
//                points += t.points;
//                for (TravelMode travelMode : t.modes.keySet()) {
//                    modes.compute(travelMode, (k, v) -> (v == null) ? t.modes.get(travelMode) : v + t.modes.get(travelMode));
//                }
//                for (Field field : Field.floatFields) {
////                    DS m = min.get(field);
//                    DS m = t.min.get(field);
//                    DS x = min.get(field);
//                    if (x == null || (m != null && x.d > m.d)) {
//                        min.put(field, m);
//                    }
//
//                    m = t.max.get(field);
//                    x = max.get(field);
//                    if (x == null || (m != null && x.d < m.d)) {
//                        max.put(field, m);
//                    }
//
//
//                    //                    var m = min.putIfAbsent(field, t.min.get(field));
////                    if (m != null && m.d > df) {
////                        m.setDS(df, track.name + "-" + points);
////                    }
//
////                    min.compute(field, (k, v) -> (v == null) ? t.min.get(field) : Double.min(v, t.min.get(field)));
////                    max.compute(field, (k, v) -> (v == null) ? t.max.get(field) : Double.max(v, t.max.get(field)));
//                }
//
//            }
//        }
//    }

//    /**
//     * Remove points with time going back or the same
//     * @param points all track points
//     */
//    public static void removeWrongPoints(List<GpsTrackPoint> points) {
//        var timeField = ExtensionManager.INSTANCE.mainExtension.time;
//        GpsTrackPoint prev = null;
//        List<GpsTrackPoint> toRemove = new ArrayList<>();
//        for (GpsTrackPoint point : points) {
//            if (prev != null && timeField.get(point) <= timeField.get(prev)) {
//                toRemove.add(prev);
//            }
//            prev = point;
//        }
////        System.out.println(points.size());
//        points.removeAll(toRemove);
//    }

    public static GpsTrack parseTrackNumber(Integer i) {
        if (i == null) {
            return null;
        }
        return DataHolder.INSTANCE.trackManager.getTrack(i);
    }

    public static Integer parseInteger(String q) {
        if (q == null) {
            return null;
        }
        try {
            int i = Integer.parseInt(q);
            return i;
        } catch (Exception e) {
            log.warn("Cannot osm gps={}", q, e);
        }
        return null;
    }

    public static String modelCorrelation(List<GpsTrack> tracks) throws Exception {
        final PrincipalComponents pca = new PrincipalComponents();
        var fields = ExtensionManager.INSTANCE.getAllFields().stream()
                .filter(e -> e.type == FieldType.ENUM).collect(Collectors.toList());
        Instances trainingSet = ArffUtil.trackToDataset(tracks, fields);
        pca.buildEvaluator(trainingSet);

        double[][] correlation = null;
        java.lang.reflect.Field declaredField = PrincipalComponents.class.getDeclaredField("m_correlation");
        declaredField.setAccessible(true);
        correlation = (double[][]) declaredField.get(pca);
        declaredField = PrincipalComponents.class.getDeclaredField("m_trainInstances");
        declaredField.setAccessible(true);
        Instances m_trainInstances = (Instances) declaredField.get(pca);

        System.out.println(pca.toString());
        List<String> atts = new ArrayList<>();
//        List<Integer> keeps = new ArrayList<>();
        LinkedHashSet<String> keepsName = new LinkedHashSet<>();
        for (int i = 0; i < m_trainInstances.numAttributes(); i++) {
            String n = m_trainInstances.attribute(i).name();
            atts.add(n);
            if (n.startsWith("gps")) {
//                keeps.add(i);
                keepsName.add(n.split("=")[1]);
            }
        }
        StringBuilder sb = new StringBuilder();
        keepsName.forEach(e -> sb.append(",").append(e));
        String last = "";
//        int nKeeps = 0;
        for (int i = 0; i < correlation.length; i++) {
            String[] n = atts.get(i).split("=");
            if (!n[0].equals(last)) {
                sb.append("\n").append(n[0]);
                last = n[0];
//            if (i % 5 == 0) {
//                sb.append("\n").append(fields.get(i/5).extension.getName());
//            } else {
//
            }
//            for (TravelMode value : TravelMode.values()) {
//
//            }
            int pos = 0;
//            keepsName.stream().forEach(e -> e.equals());
            for (String s : keepsName) {
                if (s.equals(n[1])) {
                    sb.append(",");
                    sb.append(correlation[i][pos]);
                    break;
                }
                pos++;
            }
        }
        return sb.toString() + "\n";

    }

}
