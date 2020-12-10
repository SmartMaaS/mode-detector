package dfki.mm.tracks.stat;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldList;
import dfki.mm.tracks.GpsTrack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldStatistics {
    private static final int arraySize = TravelMode.values().length;
    public final Field.ModeField original = ExtensionManager.INSTANCE.mainExtension.mode;
    public final Field.ModeField prediction;
    public final Map<GpsTrack, TrackFieldStatistics> results = new HashMap<>();

    public FieldStatistics(Field.ModeField prediction) {
        this.prediction = prediction;
    }

    public List<TrackFieldStatistics> forTracks(List<GpsTrack> tracks) {
        List<TrackFieldStatistics> ret = new ArrayList<>();
        for (GpsTrack track : tracks) {
            ret.add(results.get(track));
        }
        return ret;
    }

    public TrackFieldStatistics compute(GpsTrack track) {
        TrackFieldStatistics ret = new TrackFieldStatistics(track);
        results.put(track, ret);
        return ret;
    }

    public class TrackFieldStatistics {
        private TrackFieldStatistics(GpsTrack track) {
            FieldList.PointIterator p = track.getGpsPoint();
            FieldList<TravelMode>.FieldValue tf = p.get(original);
            FieldList<TravelMode>.FieldValue pf = p.get(prediction);
//            fromTo = new int[][]
            int correct = 0;
            int total = 0;
            while (p.next()) {
                final int a = tf.get().ordinal();
                final int b = pf.get().ordinal();
                fromTo[a][b] += 1;
                totalPerMode[a] += 1;
                if (a == b) {
                    correct++;
                }
                total++;
            }
            this.correct = correct;
            this.total = total;
        }

        /**
         * true / predicted
         */
        public final int[][] fromTo = new int[arraySize][arraySize];
        public final int[] totalPerMode = new int[arraySize];
        public final long correct;
        public final long total;
    }

//    private static final NumberFormat nf =  NumberFormat.();

    public static String precentageString(long correct, long total) {
        if (total == 0) {
            return "-";
        }
        return String.format("%2.1f", (double)(correct * 1000 / total) / 10);
    }

    public static String precentageString(double total) {
        return String.format("%2.1f", total * 100);
    }

}
