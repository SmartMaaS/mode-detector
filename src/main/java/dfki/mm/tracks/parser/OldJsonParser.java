package dfki.mm.tracks.parser;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.FieldList;
import dfki.mm.wui.MyWebUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OldJsonParser {

    //    public static final String DATE_TIME_FORMAT_JSON = "yyyy-MM-dd HH:mm:ss";
    public static List<SimpleTrack> parse(String data) {
        List<SimpleTrack> ret = new ArrayList<>();
        JSONArray a = new JSONArray(data);
        for (Object o1 : a) {
            SimpleTrack t = new SimpleTrack();
            ret.add(t);
            JSONObject oo = (JSONObject) o1;
            JSONArray aa = (JSONArray) oo.get("tripTrack");
            for (Object o2 : aa) {
                JSONObject jsonObject = (JSONObject) o2;
                double gps_longitude;
                double gps_latitude;
                long time;
                try {
                    time = jsonObject.getLong("time");
                    gps_latitude = jsonObject.getDouble("gps_latitude");
                    gps_longitude = jsonObject.getDouble("gps_longitude");

                } catch (JSONException e) {
//                    System.err.println("Error in file: " + inputFile);
                    e.printStackTrace();
                    throw e;
                }
//                JSONObject o = (JSONObject) o2;
//                Instant i = Instant.ofEpochMilli(time);
//                String timeString = DateTimeFormatter.ISO_INSTANT.format(i);

                t.add("time", time);
                t.add("lat", gps_latitude);
                t.add("lon", gps_longitude);
                t.add("ele", jsonObject.has("ele") ? jsonObject.getDouble("ele") : null);
                t.add("heading", jsonObject.has("gps_bearing") ? jsonObject.getDouble("gps_bearing") : null);
                // https://www.topografix.com/gpx_manual.asp
                if (jsonObject.has("gps_speed")) {
                    var s = jsonObject.getDouble("gps_speed");
                    t.add("speed", s > 0 ? s / 3.6 : -1D);
                } else {
                    t.add("speed", (Double) null);
                }
//                t.add("heading", jsonObject.has("gps_bearing") ? jsonObject.getDouble("gps_bearing") : null);
                t.add("mode", jsonObject.has("mode") ? TravelMode.parseString(jsonObject.getString("mode")) : TravelMode.UNDEF);
            }
            t.verifyLength();
        }
        return ret;
    }

    @Deprecated
    public static long parsePoint(JSONObject jsonObject, FieldList.PointIterator point) {
        double gps_longitude;
        double gps_latitude;
        long time;
        try {
            time = jsonObject.getLong("time");
            gps_longitude = jsonObject.getDouble("gps_longitude");
            gps_latitude = jsonObject.getDouble("gps_latitude");

        } catch (JSONException e) {
//                    System.err.println("Error in file: " + inputFile);
            e.printStackTrace();
            throw e;
        }

//        Instant i = Instant.parse(stringValue);
//        target.time = i.toEpochMilli();
        Instant i = Instant.ofEpochMilli(time);
        String timeString = DateTimeFormatter.ISO_INSTANT.format(i);


//        Date date = new Date(time); // milliseconds
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_JSON);
//        String timeString = sdf.format(date);

        double gps_bearing = MyWebUtils.getJsonOr(jsonObject, "gps_bearing", -1f);
//        float activity_walk = MyWebUtils.getJsonOr(jsonObject, "activity_walk", -1f);
//        float activity_cycle = MyWebUtils.getJsonOr(jsonObject, "activity_cycle", -1f);
//        float activity_motorized = MyWebUtils.getJsonOr(jsonObject, "activity_motorized", -1f);
        double gps_speed = MyWebUtils.getJsonOr(jsonObject, "gps_speed", -1f);
        if (gps_speed > 0) {
            gps_speed /= 3.6;
        }
        String modeString = MyWebUtils.getJsonOr(jsonObject, "mode", null);
        TravelMode mode = TravelMode.valueOf(modeString.toUpperCase());

//        GPSPoint ret = new GPSPoint();
        point.get(ExtensionManager.INSTANCE.mainExtension.lat).set(gps_latitude);
        point.get(ExtensionManager.INSTANCE.mainExtension.lon).set(gps_longitude);
        point.get(ExtensionManager.INSTANCE.mainExtension.ele).set(0D);
        point.get(ExtensionManager.INSTANCE.mainExtension.heading).set(gps_bearing);
        point.get(ExtensionManager.INSTANCE.mainExtension.time).set(time);
        point.get(ExtensionManager.INSTANCE.mainExtension.speed).set(gps_speed);
        point.get(ExtensionManager.INSTANCE.mainExtension.mode).set(mode);
//        point.addAndNext();
//        ret.gps_latitude = gps_latitude;
//        ret.gps_longitude = gps_longitude;
//        ret.gps_bearing = gps_bearing;
//        ret.ele = 0;
//        ret.time = time;
//        ret.timeString = timeString;
//        ret.activity_walk = activity_walk;
//        ret.activity_cycle = activity_cycle;
//        ret.activity_motorized = activity_motorized;
//        ret.gps_speed = gps_speed;
//        ret.mode = mode;

//        return ret;
        return time;
    }
}
