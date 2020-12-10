package dfki.mm.tracks;

import dfki.mm.TravelMode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CSVUtil {

    private static final Pattern tripPattern = Pattern.compile("Trip_(\\d+)_(\\d+)");

    private static final Logger log = LoggerFactory.getLogger(CSVUtil.class);


    public static String trackToCSV(GpsTrack track, List<Field<?>> fields) {
        StringBuilder ret = new StringBuilder();
        ret.append(fields.stream().map(Field::getFullName).collect(Collectors.joining(",")));
        var il = fields.stream().map(e -> track.getData(e).iterator()).collect(Collectors.toList());
        while (true) {
            var hn = il.stream().allMatch(Iterator::hasNext);
            if (!hn) {
                break;
            }
            ret.append('\n');
            ret.append(il.stream().map(iterator -> String.valueOf(iterator.next())).collect(Collectors.joining(",")));
        }
        return ret.toString();
    }


    public static String[] splitTripId(String tripString) {
        try {
            Matcher m = tripPattern.matcher(tripString);
            if (m.find()) {
                final String trip = m.group(1);
                final String id = m.group(2);
                log.debug("Trip split found {} -> {}.{}", tripString, trip, id);


                return new String[]{trip, id};
            } else {
                log.error("Trip cannot be splited: {}", tripString);

            }
        } catch (Exception e) {
            log.error("Trip cannot be split: {}", tripString, e);
            throw new RuntimeException("Trip cannot be split: {}" + tripString, e);
        }
        throw new RuntimeException("Trip cannot be split: " + tripString);
    }

//    public static void main(String[] args) {
//        System.out.println(Arrays.toString(splitTripId("Trip_0096_0190")));
//    }

    public String toCSV(GpsTrack track, Collection<Field> fields, boolean addHeader) {
        StringBuilder ret = new StringBuilder();
        if (addHeader) {
            ret.append(fields.stream().map(e -> e.extension.getName() + "." + e.name)
                    .collect(Collectors.joining(",")))
                    .append("\n");
        }
//        points.forEach(e -> sb.append(e.toCSV()).append("\n"));
        var point = track.getGpsPoint();
//        point.
        List<FieldList.FieldValue> ll = fields.stream()
                .map(e -> point.get(e))
                .collect(Collectors.toList());
        while (point.next()) {
            ret.append(ll.stream()
                    .map(e -> String.valueOf(e.get()))
                    .collect(Collectors.joining(",")));
        }
//        ret += points.stream().map(e -> e.toCSV(fields)).collect(Collectors.joining("\n"));
        return ret.toString();
    }
}
