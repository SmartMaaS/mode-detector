package dfki.mm.tracks;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.extension.Extension;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyJsonParser {

    public static JSONObject trackToJsonFlat(GpsTrack track, List<Field<?>> fields) {
//        FieldList.PointIterator point = track.getGpsPoint();
//        List<FieldList.FieldValue> ll = fields.stream()
//                .map(e -> point.get(e))
//                .collect(Collectors.toList());
        JSONObject ret = new JSONObject();
        for (Field<?> field : fields) {
            if (field.type == FieldType.ENUM) {
                ret.put(field.getFullName() + "-ord",
                        track.data.get(field).data.stream()
                                .map(e -> ((TravelMode)e).ordinal()).collect(Collectors.toList()));
//            } else {
            }
            ret.put(field.getFullName(), track.data.get(field).data);
        }
        return ret;
    }

    public static JSONObject trackToJson(GpsTrack track, List<Extension> extensions) {
//        FieldList.PointIterator point = track.getGpsPoint();
//        List<FieldList.FieldValue> ll = fields.stream()
//                .map(e -> point.get(e))
//                .collect(Collectors.toList());
        JSONObject ret = new JSONObject();
        for (Extension extension : extensions) {
            JSONObject ext = new JSONObject();
            ret.put(extension.getName(), ext);
            for (Field<?> field : extension.getFields()) {
                ext.put(field.name, track.data.get(field).data);
            }
        }
        return ret;
    }

    public static List<JSONObject> tracksToJson(List<GpsTrack> tracks, List<Extension> extensions) {
        List<JSONObject> ret = new ArrayList<>();
        for (GpsTrack track : tracks) {
            JSONObject o = new JSONObject();
            ret.add(o);
            o.put("name", track.name);
            o.put("points", MyJsonParser.trackToJson(track, extensions));
        }
        return ret;
    }

    public static List<GpsTrack> fromJson(JSONArray list) {
        List<GpsTrack> ret = new ArrayList<>();
        for (Object tmp : list) {
            JSONObject t = (JSONObject) tmp;
            GpsTrack track = GpsTrack.fromBackup(t.getString("name"));
            ret.add(track);
            JSONObject ps = t.getJSONObject("points");
            for (String s1 : ps.keySet()) {
                JSONObject es = ps.getJSONObject(s1);
                Extension extension = ExtensionManager.INSTANCE.extensionByName(s1);
                for (String s2 : es.keySet()) {
                    JSONArray fs = es.getJSONArray(s2);
                    final Field<?> field = ExtensionManager.INSTANCE.fieldByName(extension, s2);

                    switch (field.type) {
                        case DOUBLE: {
                            List<Double> tl = new ArrayList<>();
                            for (int i = 0; i < fs.length(); i++) {
                                if (fs.isNull(i)) {
                                    tl.add(null);
                                } else {
                                    tl.add(fs.getDouble(i));
                                }
                            }
                            track.data.put(field, new FieldList(field, tl));
                            break;
                        }
                        case LONG: {
                            List<Long> tl = new ArrayList<>();
                            for (int i = 0; i < fs.length(); i++) {
                                if (fs.isNull(i)) {
                                    tl.add(null);
                                } else {
                                    tl.add(fs.getLong(i));
                                }
                            }
                            track.data.put(field, new FieldList(field, tl));
                            break;
                        }
                        case STRING: {
                            List<String> tl = new ArrayList<>();
                            for (int i = 0; i < fs.length(); i++) {
                                if (fs.isNull(i)) {
                                    tl.add(null);
                                } else {
                                    tl.add(fs.getString(i));
                                }
                            }
                            track.data.put(field, new FieldList(field, tl));
                            break;
                        }
                        case ENUM: {
                            List<TravelMode> tl = new ArrayList<>();
                            for (int i = 0; i < fs.length(); i++) {
                                if (fs.isNull(i)) {
                                    tl.add(null);
                                } else {
                                    tl.add(TravelMode.parseString(fs.getString(i)));
                                }
                            }
                            track.data.put(field, new FieldList(field, tl));
                            break;
                        }
                    }
                }
                validateFields(track, extension.getFields());
            }
        }
        return ret;
    }

    public static void validateFields(GpsTrack track, List<Field<?>> fields) {
        int i = track.size() == 0 ? -1 : track.size();
        for (Field<?> field : fields) {
            FieldList fl = track.data.get(field);
            if (i == -1) {
                i = fl.data.size();
                track.size.set(i);
            }
            if (i != fl.data.size()) {
                throw new RuntimeException(String.format("Different lengths: %d != %d (%s)", i, fl.data.size(), fl.name));
            }
        }
    }


//    public static JSONObject trackFromJson(GpsTrack track) {
////        FieldList.PointIterator point = track.getGpsPoint();
////        List<FieldList.FieldValue> ll = fields.stream()
////                .map(e -> point.get(e))
////                .collect(Collectors.toList());
//        JSONObject ret = new JSONObject();
//        for (Field field : fields) {
//            ret.put(field.getFullName(), track.data.get(field).data);
//        }
//        return ret;
//    }



}
