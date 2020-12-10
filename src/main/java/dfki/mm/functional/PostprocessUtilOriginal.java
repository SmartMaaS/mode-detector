package dfki.mm.functional;

import dfki.mm.TravelMode;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldList;
import dfki.mm.tracks.GpsTrack;

import java.util.*;

public class PostprocessUtilOriginal {

//    private final int SURROUNDING_SIZE = 28;

    public static List<TravelMode> process(List<TravelMode> prediction, int surroundingSize) {
//        Map<TravelMode, Integer> map = new HashMap<>();
//        EnumSet.allOf(TravelMode.class).forEach(e -> map.put(e, 0));
//        List<Integer> list = new ArrayList<>();
        List<TravelMode> ret = new ArrayList<>();
        int[] array = new int[TravelMode.values().length];
//        EnumSet.allOf(TravelMode.class).forEach(e -> list.add(0));
        var next = prediction.listIterator();
        var cur = prediction.listIterator();
        var prev = prediction.listIterator();
        int dn = 0;
        int dp = -1;
//        cur.next();
//        prev.next();

//        dn--;
//        dp++;
        while (cur.hasNext()) {
            cur.next();
            dp++;
            dn--;
            for (; dn < surroundingSize && next.hasNext(); dn++) {
                TravelMode m = next.next();
                array[m.ordinal()]++;
            }
            if (dp > surroundingSize) {
                array[prev.next().ordinal()]--;
                dp--;
            }
            int max = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] > array[max]) {
                    max = i;
                }
            }
            ret.add(TravelMode.fromOrdinal(max));
        }
        return ret;
//        cur.next();

    }


    public static void process(GpsTrack track,
                               Field.ModeField source,
                               Field.ModeField target,
                               int surroundingSize) {

//        Map<TravelMode, Integer> map = new HashMap<>();
//        EnumSet.allOf(TravelMode.class).forEach(e -> map.put(e, 0));
//        List<Integer> list = new ArrayList<>();
        List<TravelMode> ret = new ArrayList<>();
        int[] array = new int[TravelMode.values().length];
//        EnumSet.allOf(TravelMode.class).forEach(e -> list.add(0));
        var next = track.getGpsPoint();
        var cur = track.getGpsPoint();
        var prev = track.getGpsPoint();
        var nextValue = next.get(source);
        var prevValue = prev.get(source);
        var curValue = cur.get(target);
        int dn = 0;
        int dp = -1;
//        cur.next();
//        prev.next();

//        dn--;
//        dp++;
        while (cur.next()) {
//            cur.next();
            dp++;
            dn--;
            for (; dn < surroundingSize && next.next(); dn++) {
                TravelMode m = nextValue.get();
                array[m.ordinal()]++;
            }
            if (dp > surroundingSize) {
                prev.next();
                array[prevValue.get().ordinal()]--;
                dp--;
            }
            int max = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] > array[max]) {
                    max = i;
                }
            }
//            ret.add(TravelMode.fromOrdinal(max));
            curValue.set(TravelMode.fromOrdinal(max));
        }
//        return ret;
//        cur.next();

    }
}
