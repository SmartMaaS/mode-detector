package dfki.mm.request;

import dfki.mm.DataHolder;
import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.predict.ModelMethod;
import dfki.mm.predict.PredictionModel;
import dfki.mm.relation2.MyNodeBucket;
import dfki.mm.tracks.*;
import dfki.mm.tracks.parser.GpxParser;
import dfki.mm.tracks.parser.OldJsonParser;
import dfki.mm.tracks.parser.SimpleTrack;
import dfki.mm.wui.JettyMain;
import dfki.mm.wui.MyWebUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class RequestUtil {

    private static final Logger log = LoggerFactory.getLogger(RequestUtil.class);

    static JSONObject trackInfoJson(GpsTrack t) {
        JSONObject ret = new JSONObject();
        ret.put("id", t.id);
        ret.put("name", t.name);
        ret.put("modes", t.modes);
        return ret;
    }

//    public static Map<String, List<Object>> trackData(GpsTrack t, List<Field> fields) {
//        Map<String,List<Object>> ret = new LinkedHashMap<>();
//        fields.forEach(e -> ret.put(e.name, new ArrayList<>(t.data.get(e))));
////        for (GpsTrackPoint point : t.points) {
////            fields.forEach(e -> ret.get(e.name).add(e.asJson(point)));
////        }
//        return ret;
//    }

    public static JSONObject modelInfo(PredictionModel model) {
        JSONObject ret = new JSONObject();
        ret.put("id", model.id);
        ret.put("fields", model.fields);
        ret.put("method", model.method);
        ret.put("info", model.getInfo());
        return ret;
    }

    public static List<ModelMethod> parseParamMethod(MyHttpRequest myHttpRequest) {
//        ModelMethod modelMethod = ModelMethod.valueOf(MyHttpRequest.postRequestReader.readString(myHttpRequest, MyApiField.method.name()));
        List<ModelMethod> ret = new ArrayList<>();
        List<String> method = MyHttpRequest.postRequestReader.readStrings(myHttpRequest, MyApiField.method.name());
        for (String modelMethodString : method) {
            ModelMethod modelMethod = ModelMethod.valueOf(modelMethodString);
            ret.add(modelMethod);
        }
        return ret;
    }

    public static List<Field<?>> parseParamFields(MyHttpRequest myHttpRequest) {
//        List<String> fieldStrings = MyHttpRequest.postRequestReader.readStrings(myHttpRequest, MyApiField.field.name());
        List<Number> fieldNumbers = MyHttpRequest.postRequestReader.readNumbers(myHttpRequest, MyApiField.field.name());

        if (fieldNumbers == null) {
            myHttpRequest.errors.add("fields not found");
            return null;
        }
        List<Field<?>> fieldList = new ArrayList<>();
        for (Number fieldNumber : fieldNumbers) {
            Field<?> field = ExtensionManager.INSTANCE.fieldById(fieldNumber.intValue());
            if (field == null) {
                myHttpRequest.errors.add("fields not found");
                return null;
            }
            fieldList.add(field);
        }
        return fieldList;
    }

    public static List<PredictionModel> parseParamModels(MyHttpRequest myHttpRequest) {
        List<Number> hids = MyHttpRequest.postRequestReader.readNumbers(myHttpRequest, MyApiField.model.name());
        List<PredictionModel> ret = new ArrayList<>();
        for (Number hid : hids) {

            PredictionModel pm = DataHolder.INSTANCE.modelData.getModelByHid(hid.intValue());
            ret.add(pm);
        }
        return ret;
    }

//    public static PostProcessor.Type parsePostType(MyHttpRequest httpRequest) {
//        String typeString = MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.post.name());
//        return PostProcessor.Type.valueOf(typeString);
//    }
//
//    public static PostProcessor parsePost(MyHttpRequest httpRequest) {
//        if (MyHttpRequest.postRequestReader.isNull(httpRequest, MyApiField.post.name())) {
//            return null;
//        }
//        return getOne(MyApiField.post.name(), parsePostList(httpRequest));
//
//    }
//
//    public static List<PostProcessor> parsePostList(MyHttpRequest httpRequest) {
//        List<Number> hids = MyHttpRequest.postRequestReader.readNumbers(httpRequest, MyApiField.post.name());
//        List<PostProcessor> ret = new ArrayList<>();
//        for (Number id : hids) {
//            PostProcessor pp = DataHolder.INSTANCE.predictionPostData.getPostProcessor(id.intValue());
//            ret.add(pp);
//        }
//        return ret;
//    }


    public static <T> T getOne(String name, List<T> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        if (list.size() > 1) {
            throw new IllegalArgumentException("Too many parameters: " + name);
        }
        return list.get(0);
    }



    enum TrackFormat {
        JSON,
        MY_JSON,
        CSV,
        GPX,
    }

    public static class TrackData {
        String name;
        TrackFormat format;
//        ByteArrayOutputStream data;
        String data;
    }

    private static Double parseStringDouble(String s) {
        if (s == null || s.length() == 0) {
            return null;
        } else {
            return Double.parseDouble(s);
        }
    }

    private static SimpleTrack parseCsv(TrackData trackData) throws IOException {
        CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new StringReader(trackData.data));
//        records.getHeaderNames();
        SimpleTrack ret = new SimpleTrack();
        for (CSVRecord record : records) {
            ret.add("lat", Double.parseDouble(record.get("gps.lat")));
            ret.add("lon", Double.parseDouble(record.get("gps.lon")));
            ret.add("ele", parseStringDouble(record.get("gps.ele")));
            ret.add("speed", parseStringDouble(record.get("gps.speed")));
            ret.add("heading", parseStringDouble(record.get("gps.heading")));
            ret.add("time", Long.parseLong(record.get("gps.time")));
            ret.add("mode", TravelMode.parseString(record.get("gps.mode")));
        }
        ret.verifyLength();
        return ret;
    }

    public static List<GpsTrack> parseTracks(List<TrackData> trackDataList) {
//        trackDataList.get(0).data
        List<GpsTrack> ret = new ArrayList<>();
//        List<GpsTrack> tracks = ;
        for (TrackData trackData : trackDataList) {
//            List<GpsTrack> tracks;
            switch (trackData.format) {
                case JSON: {
                    List<SimpleTrack> sl = OldJsonParser.parse(trackData.data);
                    List<GpsTrack> tracks = GpsTrack.fromSimpleTrack(
                            sl,
                            Collections.singletonList(ExtensionManager.INSTANCE.mainExtension),
                            trackData.name);
//                    List<GpsTrack> tracks = GpsTrack.fromOldJson(trackData.data);
//                    for (GpsTrack track : tracks) {
//                        track.updateModes();
//                        TrackUtil.removeWrongPoints(track.points);

//                    }
                    ret.addAll(tracks);
                    break;
                }
                case MY_JSON: {
                    List<GpsTrack> tracks;
                    try {
//                        GpsTrack g = GpsTrack.fromBackup(trackData.name);
//                        MyJsonParser.fromJson(new JSONArray(trackData.data));
                        //todo: test
                        tracks = MyJsonParser.fromJson(new JSONArray(trackData.data));
                        tracks.get(0).name = trackData.name;
//                                tracks = Collections.singletonList(g);
                    } catch (Exception e) {
                        log.warn("Cannot parse {}", trackData.name, e);
                        GpsTrack empty = GpsTrack.withException(e);
                        tracks = Collections.singletonList(empty);
                    }
                    ret.addAll(tracks);
                    break;
                }
                case GPX: {
//                    List<GpsTrack> tracks;
                    try {
                        SimpleTrack st = GpxParser.parse(trackData.data);
                        List<GpsTrack> tracks = GpsTrack.fromSimpleTrack(
                                Collections.singletonList(st),
                                Collections.singletonList(ExtensionManager.INSTANCE.mainExtension),
                                trackData.name);
//                        ret.add(GpsTrack.fromOSMT(trackData.data, trackData.name));
//                        track = GpsTrack.fromOSMT(MyWebUtils.toString(MyWebUtils.readPart(part)));
                        ret.addAll(tracks);

                    } catch (Exception e) {
                        log.warn("Cannot parse {}", trackData.name, e);
                        GpsTrack emptyTrack = GpsTrack.withException(e);
                        ret.add(emptyTrack);
//                        tracks = Collections.emptyList();
//                        throw new RuntimeException(e);
                    }

//                    ret.addAll(DataHolder.INSTANCE.trackManager.addTracks(tracks));

//                    track.name = filename;
                    break;
                }
                case CSV: {
                    try {
                        SimpleTrack st = parseCsv(trackData);
                        List<GpsTrack> tracks = GpsTrack.fromSimpleTrack(
                                Collections.singletonList(st),
                                Collections.singletonList(ExtensionManager.INSTANCE.mainExtension),
                                trackData.name);
                        ret.addAll(tracks);
                    } catch (Exception e) {
                        log.warn("Cannot parse {}", trackData.name, e);
                        GpsTrack emptyTrack = GpsTrack.withException(e);
                        ret.add(emptyTrack);
                    }
                    break;
                }
                default:
//                    ret.addAll Collections.emptyList();
            }
//            for (GpsTrack track : tracks) {
//                if (track.name != null && track.name.length() > 0) {
//                    track.name = trackData.name + "-" + track.name;
//                } else {
//                    track.name = trackData.name;
//                }
//                TrackUtil.removeWrongPoints(track.points);
//                if (track.points.size() == 0) {
//                    track.exception = new RuntimeException("Empty track");
//                }
//                ret.add(track);
//            }
        }
        ret = DataHolder.INSTANCE.trackManager.addTracks(ret);

        return ret;
    }

//    static void preprocessTracks(List<GpsTrack> trackList) {
//        ExecutorService es = Executors.newCachedThreadPool();
//        for (GpsTrack track : trackList) {
//            if (track.exception != null) {
//                continue;
//            }
//            es.submit(() -> {
//                try {
////                    DataHolder.INSTANCE.preprocessUtilOriginal.updatePoints(track.points);
//                    log.info("handleUploadGps: processed {}", track.name);
//                } catch (Exception e) {
//                    track.exception = e;
////                    tracksToAdd.remove(track);
//                    log.warn("handleUploadGps: process failed {}", track.name, e);
//                }
//            });
//        }
//        es.shutdown();
//        try {
//            es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            log.error("Processing tracks interrupted", e);
//        }
//
//    }

//    public static List<GpsTrack> addGoodTracks(List<GpsTrack> trackList, MyHttpRequest request) {
//        List<GpsTrack> goodTracks = new ArrayList<>();
//        for (GpsTrack gpsTrack : trackList) {
//            if (gpsTrack.exception == null) {
//                goodTracks.add(gpsTrack);
//            } else {
//                request.errors.add(gpsTrack.exception.getMessage());
//            }
//        }
//        DataHolder.INSTANCE.trackManager.addTracks(goodTracks);
//        return goodTracks;
//    }

//    public static final String FILES = "files";

    public static  List<TrackData> loadTracks(MyHttpRequest request) {
        if (request.baseRequest.getContentType() == null || !request.baseRequest.getContentType().startsWith("multipart/form-data")) {
            request.errors.add("no data");
            return null;
        }
        try {
            request.baseRequest.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, JettyMain.MULTI_PART_CONFIG);
//            baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
            log.info("{}={}", MyApiField.file.name(), request.baseRequest.getPart(MyApiField.file.name()));

//            Collection<Part> parts = request.getParts();
//            ExecutorService es = Executors.newCachedThreadPool();
            List<TrackData> ret = new ArrayList<>();
//            var parts = request.baseRequest.getParts()
            for (Part part : request.baseRequest.getParts()) {
                if (!MyApiField.file.name().equals(part.getName())) {
                    continue;
                }
                log.info("Part {}-{}-{} -- {}", part.getName(), part.getSubmittedFileName(), part.getContentType(), part);
                String filename = part.getSubmittedFileName();

                TrackData track = new TrackData();
                track.name = filename;
                track.data = MyWebUtils.toString(MyWebUtils.readPart(part));

                if (part.getContentType().startsWith("application/json")) {
                    track.format = TrackFormat.JSON;
                } else if (part.getContentType().startsWith("application/gpx+xml")) {
                    track.format = TrackFormat.GPX;
                } else if (part.getContentType().startsWith("text/csv")) {
                    track.format = TrackFormat.CSV;
                } else {
                    log.info("File type info not provided or unknown, guessing for {}", part.getContentType());
                    if (track.data != null) {
                        if (track.data.startsWith("<")) {
                            track.format = TrackFormat.GPX;
                        } else if (track.data.startsWith("[")) {
                            track.format = TrackFormat.JSON;
                        } else if (track.data.startsWith("{")) {
                            track.format = TrackFormat.MY_JSON;
                        }
                    }
                }


                if (track.format != null) {
                    ret.add(track);
                } else {
                    log.warn("{}", part.getContentType());
                    request.errors.add("unknown format: " + part.getContentType());

                }
            }
            return ret;
        } catch (IOException | ServletException exception) {
            request.errors.add("Error reading parts: " + exception.getMessage());
            return null;
        }
    }

    public static List<GpsTrack> parseParamTracks(MyHttpRequest myHttpRequest) {
        return parseParamTracks(myHttpRequest, MyApiField.track.name(), true);
    }

    public static List<GpsTrack> parseParamTracks(MyHttpRequest myHttpRequest, String param, boolean fail) {
        if (myHttpRequest.baseRequest.getParameter(param) == null) {
            if (!fail) {
                return null;
            }
        }
        List<String> trackStrings = MyHttpRequest.postRequestReader.readStrings(myHttpRequest, param);
        if (trackStrings == null) {
            if (fail) {
                myHttpRequest.errors.add("Track not specified");
            }
            return null;
        }
        return parseParamTracks(myHttpRequest, trackStrings);
    }

    public static List<GpsTrack> parseParamTracks(MyHttpRequest myHttpRequest, List<String> trackStringList) {
        Objects.requireNonNull(trackStringList);
        List<GpsTrack> ret = new ArrayList<>();
        for (String s : trackStringList) {
            ret.addAll(parseParamTrackString(myHttpRequest, s));
        }
        return ret;
    }

    public static List<GpsTrack> parseParamTrackString(MyHttpRequest myHttpRequest, String trackString) {
        Objects.requireNonNull(trackString);
        if (trackString.contains(",")) {
            List<GpsTrack> ret = new ArrayList<>();
            for (String s : trackString.split(",")) {
                ret.addAll(parseParamTrackString(myHttpRequest, s));
            }
            return ret;
        } else {
            GpsTrack t = TrackUtil.parseTrackNumber(TrackUtil.parseInteger(trackString));
            if (t == null) {
                myHttpRequest.errors.add("Track not found: " + trackString);
                return Collections.emptyList();
            } else {
                return Collections.singletonList(t);
            }
        }
    }

    static JSONObject position(double lat, double lon, int radius) {
        if (radius < 1) {
            radius = 5;
        }
        JSONObject o = new JSONObject();
        o.put("gps_latitude", lat);
        o.put("gps_longitude", lon);
        JSONObject o2;
//        JSONArray a;

//        float la = Float.parseFloat(lat);
//        float lo = Float.parseFloat(lon);
        float la = (float) lat;
        float lo = (float) lon;

        EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> res =
                DataHolder.INSTANCE.mapData.findNearest(la, lo, radius);

//        Map<Integer, MyNodeBucket> map = DataHolder.INSTANCE.mapData.getMap();

//        if (map != null) {

//            EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> res = MyNodeBucket.getNearestNodes(map, la, lo, 5);
//            MyNodeBucket.NodeWithDistance defaultX = new MyNodeBucket.NodeWithDistance()
//                    .set(new MyNode(-1).setLatLon(0d,0d), 111);
//            defaultX.node.setLatLon(0d,0d);
        MyNodeBucket.NodeWithDistance x;
        o2 = new JSONObject();

        x = res.get(MyNodeBucket.NodeType.BUS_LINE);
        o2.put("distanceToBusLine", x.distance);
        o2.put("nearestPointOnBusLine", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));

        x = res.get(MyNodeBucket.NodeType.BUS_STOP);
        o2.put("distanceToBusStop", x.distance);
        o2.put("nearestBusStop", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));
        o2.put("busRouteString", "");

        o.put("bus", o2);
        o2 = new JSONObject();

        x = res.get(MyNodeBucket.NodeType.RAIL_LINE);
        o2.put("distanceToTrainLine", x.distance);
        o2.put("nearestPointOnRailwayTrack", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));

        x = res.get(MyNodeBucket.NodeType.RAIL_STOP);
        o2.put("distanceToTrainStation", x.distance);
        o2.put("nearestTrainStation", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));

        o.put("train", o2);
        return o;
    }

}
