package dfki.mm.map;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldList;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.extension.MainExtension;
import dfki.mm.wui.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OSMMap {

    public static final String TEMPLATE_KEYWORD = "MYMAP_INIT";
//    public static final String TEMPLATE_FILE = "/osm-template.html";
//    public static final String TEMPLATE_POS_FILE = "osm-clicker-template.html";

    private static final Logger log = LoggerFactory.getLogger(OSMMap.class);
    private String template;
    private final Map<TravelMode, String> colorMap;

    public OSMMap(String templateFile) {
        this();
        loadTemplate(templateFile);

    }

    public OSMMap() {
        colorMap = new HashMap<>();
        colorMap.put(TravelMode.BICYCLE, "red");
        colorMap.put(TravelMode.BUS, "cyan");
        colorMap.put(TravelMode.CAR, "purple");
        colorMap.put(TravelMode.TRAIN, "blue");
        colorMap.put(TravelMode.WALK, "pink");
        colorMap.put(TravelMode.UNDEF, "black");
//        this(TEMPLATE_FILE);
    }

    public void loadTemplate(String templateFile) {
        this.template = ResourceLoader.readString(templateFile);
    }

//    Function<GPSPoint, String> toMapCoordinatesJson = e -> String.format("[%f, %f]", e.gps_latitude, e.gps_longitude);

    public String simpleTrack(int map, GpsTrack track) {
        MainExtension mainExtension = ExtensionManager.INSTANCE.mainExtension;
        StringBuilder sb = new StringBuilder();
        // var mymap = L.map('mapid').setView(CENTER_POSITION);
        // GpsTrack [51.505, -0.09], 13
        FieldList.PointIterator point = track.getGpsPoint();
//        Field.DoubleField lat = mainExtension.lat;
//        Field.DoubleField lon = mainExtension.lon;
        FieldList<Double>.FieldValue lat = point.get(mainExtension.lat);
        FieldList<Double>.FieldValue lon = point.get(mainExtension.lon);
        point.next();
        sb.append("var mymap = L.map('mapid').setView(")
                .append(toMapCoordinatesJson(lat.get(), lon.get()))
                .append(", 13").append(");\n");

        sb.append("var latlngs = [\n");
//        sb.append(String.join(", \n", ))
        while (point.next()) {
            sb.append(toMapCoordinatesJson(lat.get(), lon.get())).append(",\n");
        }
        sb.append("];");
        sb.append("var polyline = L.polyline(latlngs, {color: 'blue', weight: 8, opacity: 0.25}).addTo(mymap);\n" +
                "// zoom the map to the polyline\n" +
                "mymap.fitBounds(polyline.getBounds());");
//        for (GPSPoint p : track.points) {
//            sb.append(p.toMapCoordinatesJson()).append(",\n");
//        }

        return template.replace(TEMPLATE_KEYWORD, sb.toString());
    }

//    public String simpleTrackWithMode(int map, GpsTrack track) {
//        MainExtension mainExtension = ExtensionManager.INSTANCE.mainExtension;
//
//        class MWT {
//            TravelMode travelMode;
//            List<GpsTrackPoint> points = new ArrayList<>();
//        }
//
//        StringBuilder sb = new StringBuilder();
//        // var mymap = L.map('mapid').setView(CENTER_POSITION);
//        // GpsTrack [51.505, -0.09], 13
////        sb.append("var mymap = L.map('mapid').setView(").append(track.getStart().toMapCoordinatesJson()).append(", 13").append(");\n");
//        sb.append("var mymap = L.map('mapid').setView(")
//                .append(toMapCoordinatesJson(mainExtension, track.points.get(0)))
//                .append(", 13").append(");\n");
////        sb.append("var latlngs = [\n");
////        sb.append(String.join(", \n", ))
//
//        LinkedList<MWT> list = new LinkedList<>();
//        for (GpsTrackPoint point : track.points) {
//            if (list.size() == 0 || list.getLast().travelMode != mainExtension.mode.get(point)) {
//                if (list.size() > 0) {
//                    list.getLast().points.add(point);
//                }
//                MWT m = new MWT();
//                m.travelMode = mainExtension.mode.get(point);
//                list.add(m);
//            }
//            list.getLast().points.add(point);
//        }
//
//        for (MWT mwt : list) {
//            sb.append("var polyline = L.polyline([")
//                    .append(mwt.points.stream().map(e -> toMapCoordinatesJson(mainExtension, e))
//                            .collect(Collectors.joining(", \n")))
//                    .append("], {color: '")
//                    .append(colorMap.get(mwt.travelMode))
//                    .append("'}).addTo(mymap);\n");
//
//        }
//
////        sb.append(track.points.stream().map(GPSPoint::toMapCoordinatesJson).collect(Collectors.joining(", \n")));
////        sb.append("];");
//        sb.append("\nmymap.fitBounds(polyline.getBounds());");
////        for (GPSPoint p : track.points) {
////            sb.append(p.toMapCoordinatesJson()).append(",\n");
////        }
//
//        return template.replace(TEMPLATE_KEYWORD, sb.toString());
//    }

    public String simpleCenter(double lat, double lon, boolean handler) {
        StringBuilder sb = new StringBuilder();
        // var mymap = L.map('mapid').setView(CENTER_POSITION);
        // GpsTrack [51.505, -0.09], 13
        sb.append("var mymap = L.map('mapid'");
        if (handler) {
            sb.append(", {tilt: true}");
        }
        sb.append(").setView(").append(String.format("[%f, %f]", lat, lon)).append(", 13").append(");\n");

        return template.replace(TEMPLATE_KEYWORD, sb.toString());
    }

    private static String toMapCoordinatesJson(double lat, double lon) {
        return String.format("[%f, %f]", lat, lon);
    }
}
