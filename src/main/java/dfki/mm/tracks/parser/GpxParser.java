package dfki.mm.tracks.parser;

import dfki.mm.TravelMode;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.time.Instant;
import java.util.List;

public class GpxParser {

    public static final String GPX = "gpx";
    public static final String TRACK = "trk";
    public static final String TRACK_SEGMENT = "trkseg";
    public static final String TRACK_POINT = "trkpt";

    public static final String TRACK_NAME = "name";
    public static final String TRACK_COMMENT = "cmt";
    public static final String TRACK_DESCRIPTION = "desc";

    public static final String TRACK_POINT_NAME = "name";
    public static final String TRACK_POINT_COMMENT = "cmt";
    public static final String TRACK_POINT_DESCRIPTION = "desc";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String ELEVATION = "ele";
    public static final String EXTENSIONS = "extensions";
    public static final String SPEED = "speed";
    public static final String COMPASS = "compass";
    public static final String COMPASS_ACCURACY = "compass_accuracy";
    public static final String COMMENT_COMPASS = "compass:";
    public static final String COMMENT_COMPASS_ACCURACY = "compAccuracy:";


    public static SimpleTrack fromOSMT(Element gpx) {
        SimpleTrack ret = new SimpleTrack();
        List<Element> trks = gpx.getChildren("trk", null);
        for (Element trk : trks) {
            parseTrackElement(
                    trk,
                    ret
            );
        }
        ret.verifyLength();
        return ret;
    }


    public static void parseTrackElement(Element trk, SimpleTrack ret) {
//        TravelMode travelMode = TravelMode.UNDEF;
        TravelMode[] travelMode = {TravelMode.UNDEF};
        String stringValue;

        for (String field : new String[]{
                TRACK_NAME,
                TRACK_COMMENT,
                TRACK_DESCRIPTION,
        }) {
            if ((stringValue = trk.getChildText(field, null)) != null) {
                travelMode[0] = TravelMode.parseOrDefault(stringValue, travelMode[0]);
            }
        }

        for (Element segment : trk.getChildren(TRACK_SEGMENT, null)) {
            for (Element element: segment.getChildren(TRACK_POINT, null)) {
//                ret.add("mode", travelMode);
                smartParse(element, ret, travelMode);
            }
        }
    }

//    private static void fromOSMT(Element element, GPSTrack track, TravelMode mode) {
//        if (element.getName().equals(TRACK_POINT)) {
//            track.points.add(GPSPoint.fromOSMT(element));
//        } else {
//            element.getChildren().forEach(e -> fromOSMT(e, track));
//        }
//    }


    private static String getExtension(Element element, String name) {
        Element extensions = element.getChild(EXTENSIONS, null);
        return extensions == null ? null : extensions.getChildText(name, null);

    }

    public static void smartParse(Element trkpt, SimpleTrack ret, TravelMode[] travelMode) {
        String stringValue;

        double lat;
        double lon;
        Double ele = null;
        Double speed = null;
        Double heading = null;
        Double hdop = null;
        Long time = null;

        stringValue = trkpt.getChildText("time", null);
        if (stringValue != null) {
//            target.timeString = stringValue;
//            ZonedDateTime zdt = formatter.parse(stringValue, ZonedDateTime::from); // , ZonedDateTime:: from
//            target.time = zdt.toInstant().toEpochMilli();//.getEpochSecond();

            Instant i = Instant.parse(stringValue);
            time = i.toEpochMilli();
        }
        lat = Double.parseDouble(trkpt.getAttributeValue(LATITUDE));
        lon = Double.parseDouble(trkpt.getAttributeValue(LONGITUDE));

        stringValue = trkpt.getChildText(ELEVATION, null);
        if (stringValue != null) {
            ele = Double.parseDouble(stringValue);
        }

        for (String field : new String[]{
                TRACK_POINT_NAME,
                TRACK_POINT_COMMENT,
                TRACK_POINT_DESCRIPTION,
        }) {
            if ((stringValue = trkpt.getChildText(field, null)) != null) {
                travelMode[0] = TravelMode.parseOrDefault(stringValue, travelMode[0]);
            }
        }

        stringValue = trkpt.getChildText("hdop", null);
        if (stringValue != null) {
            hdop = Double.parseDouble(stringValue);
        }

        Element childElement = trkpt.getChild(EXTENSIONS, null);
        if (childElement != null) {
            stringValue = childElement.getChildText(SPEED, null);
            if (stringValue != null) {
                speed = Double.parseDouble(stringValue);
            }

            stringValue = childElement.getChildText(COMPASS, null);
            if (stringValue != null) {
                heading = Double.parseDouble(stringValue);
            }

//            stringValue = childElement.getChildText(COMPASS_ACCURACY, null);
//            if (stringValue != null) {
//                target.speed = Double.parseDouble(stringValue);//GpxUtil.getElevation(trkpt);
//            }
        }

        childElement = trkpt.getChild("cmt", null);
        if (childElement != null) {
            try {
                for (Content c : childElement.getContent()) {
                    if (c.getCType() == Content.CType.CDATA) {
                        CDATA cdata = (CDATA) c;
                        String[] data = cdata.getText().split("\n");
                        for (String dataString : data) {
                            dataString = dataString.trim();
                            if (dataString.startsWith(COMMENT_COMPASS)) {
                                heading = Double.parseDouble(dataString.substring(COMMENT_COMPASS.length()));
//                                target.gps_bearing = Double.parseDouble(dataString.substring(COMMENT_COMPASS.length()));
                            }
//                            if (dataString.startsWith(COMMENT_COMPASS_ACCURACY)) {
//                                target.gps_bearing_accuracy = Double.parseDouble(dataString.substring(COMMENT_COMPASS_ACCURACY.length() + 1));
//                            }
                        }
                    }
                }
            } catch (Exception ignore) {

            }
        }
//        ret.add("lat");
        ret.add("time", time);
        ret.add("lat", lat);
        ret.add("lon", lon);
        ret.add("ele", ele);
//        ret.add("heading", heading);// == null ? -1 : heading);
//        ret.add("speed", speed);// == null ? -1 : speed);
        ret.add("heading", heading == null ? -1 : heading);
        ret.add("speed", speed == null ? -1 : speed);
        ret.add("hdop", hdop);
        ret.add("mode", travelMode[0]);
//      ret         t.add("heading", jsonObject.has("gps_bearing") ? jsonObject.getDouble("gps_bearing") : null);
//        ret.add("mode", jsonObject.has("mode") ? TravelMode.parseString(jsonObject.getString("mode")) : TravelMode.UNDEF);

    }

    public static SimpleTrack parse(String data) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(data));
        Element rootElement = doc.getRootElement();
        return fromOSMT(rootElement);
    }
}
