package dfki.mm.tracks;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.tracks.parser.OldJsonParser;
import dfki.mm.tracks.parser.SimpleTrack;
//import org.jdom2.Document;
//import org.jdom2.Element;
//import org.jdom2.JDOMException;
//import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GpsTrack {

    private static final Logger log = LoggerFactory.getLogger(GpsTrack.class);
    private static final AtomicInteger counter = new AtomicInteger();
    private static final DecimalFormat df = new DecimalFormat("0000");
    private static final DecimalFormat myFormatter = new DecimalFormat("#.000");

    HashMap<Field, FieldList> data = new HashMap<>();
    AtomicInteger size = new AtomicInteger();

    protected HashMap<Field, FieldList> getData() {
        return data;
    }

//    public void add(Field field, Double value) {
//        var f = data.computeIfAbsent(field, s -> new FieldList.FieldDouble(field));
//        f.add(value);
//    }
//
//    public void add(Field field, Long value) {
//        var f = data.computeIfAbsent(field, s -> new FieldList.FieldLong(field));
//        f.add(value);
//    }
//
//    public void add(Field field, String value) {
//        var f = data.computeIfAbsent(field, s -> new FieldList.FieldString(field));
//        f.add(value);
//    }
//
//    public void add(Field field, TravelMode value) {
//        var f = data.computeIfAbsent(field, s -> new FieldList.FieldTravelMode(field));
//        f.add(value);
//    }

//    public class GpsPoint implements Iterator<GpsPoint> {
//        private GpsPoint() {
//        }
//
//        HashMap<Field, FieldList.FieldValue> value = new HashMap<>();
//
////        HashMap<Field.DoubleField, Double> dv = new HashMap<>();
////        HashMap<Field.LongField, Long> lv = new HashMap<>();
////        HashMap<Field.StringField, String> sv = new HashMap<>();
////        HashMap<Field.ModeField, TravelMode> mv = new HashMap<>();
//
//        @Override
//        public boolean hasNext() {
////            return false;
//            return value.values().stream().findAny().get().hasNext();
//        }
//
//        public GpsPoint next() {
//            value.values().forEach(FieldList.FieldValue::next);
//            return this;
//        }
//
//        @Override
//        public void remove() {
//            value.values().forEach(FieldList.FieldValue::remove);
//        }
//
//        @Override
//        public void forEachRemaining(Consumer<? super GpsPoint> action) {
//            value.values().forEach(e -> e.forEachRemaining(action));
//        }
//
//    }

    public FieldList.PointIterator getGpsPoint() {
        FieldList.PointIterator p = new FieldList.PointIterator(this);
        return p;
    }


    public final int id = counter.getAndIncrement();
//    public final List<GpsTrackPoint> points = new ArrayList<>();
//    public final List<Extension> extensions = new ArrayList<>();
//    public final LinkedHashMap<String, Fie> extensions = new ArrayList<>();

//    public final List<Field<Long>> fieldsLong = new ArrayList<>();
//    public final List<Field<Double>> fieldsDouble = new ArrayList<>();
//    public final List<Field<String>> fieldsString = new ArrayList<>();
//    public final List<Field<TravelMode>> fieldsMode = new ArrayList<>();

    public final List<Field> fields = new ArrayList<>();
    public String name;
    public Map<TravelMode, Long> modes = Collections.emptyMap();
    public Exception exception = null;

    protected GpsTrack() {
        this.fields.addAll(ExtensionManager.INSTANCE.getAllFields());
//        for (Field field : this.fields) {
//            this.data.put(field, new FieldList(field, 0));
//        }
    }

    public static GpsTrack withException(Exception e) {
        GpsTrack ret = new GpsTrack();
        ret.exception = e;
        return ret;
    }

    public static GpsTrack fromBackup(String name) {
        GpsTrack ret = new GpsTrack();
        ret.name = name;
        return ret;
    }

    public static GpsTrack empty(String name) {
        return fromBackup(name);
    }

    public static GpsTrack fromBackup(String name, int size) {
        GpsTrack ret = new GpsTrack();
        ret.name = name;
        ret.size.set(size);
        return ret;
    }

//    public <T> void getGetter(Field<T> field2, List<Field<T>> list) {
//        int p = list.indexOf(field2);
//        return (GpsTrackPoint p) ->
//    }

//    public <T> int getLongGetter(Field<T> field2) {
//        if (field2 instanceof Field.LongField) {
//            int p = fieldsLong.indexOf(field2);
//            if (p >= 0) {
//                return new Function<T>(){
//                    @Override
//                    public T get() {
//                        return null;
//                    }
//                };
//            }
//        }
//    }

//    public Function<GpsTrackPoint, Double> getGetter(Field field) {
//        int p = fields.indexOf(field);
//        switch (field.type) {
//            case DOUBLE: return new Function<GpsTrackPoint, Double>() {
//                @Override
//                public Double apply(GpsTrackPoint gpsPoint2) {
//                    return null;
//                }
//            };
//        }
//        return null;
//    }

//    public Function<GpsTrackPoint, Double> doubleGetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.DOUBLE || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return point -> (Double) point.fields.get(p);
//    }
//
//    public Function<GpsTrackPoint, Long> longGetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.LONG || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return point -> (Long) point.fields.get(p);
//    }
//
//    public Function<GpsTrackPoint, String> stringGetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.STRING || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return point -> (String) point.fields.get(p);
//    }
//
//    public Function<GpsTrackPoint, TravelMode> modeGetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.ENUM || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return point -> (TravelMode) point.fields.get(p);
//    }
//
//
//    public BiConsumer<GpsTrackPoint, Double> doubleSetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.DOUBLE || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return (point, v) -> point.fields.set(p, (Double) v);
//    }
//
//    public BiConsumer<GpsTrackPoint, Long> longSetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.LONG || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return (point, v) -> point.fields.set(p, (Long) v);
//    }
//
//    public BiConsumer<GpsTrackPoint, String> stringSetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.STRING || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return (point, v) -> point.fields.set(p, (String) v);
//    }
//
//    public BiConsumer<GpsTrackPoint, TravelMode> modeSetter(Field field) {
//        int p = fields.indexOf(field);
//        if (field.type != FieldType.ENUM || p < 0) {
//            throw new IllegalArgumentException();
//        }
//        return (point, v) -> point.fields.set(p, (TravelMode) v);
//    }

    private <T> FieldList<T> getList(Field<T> field) {
        return (FieldList<T>) data.get(field);
    }

    public <T> void setData(Field<T> field, List<T> data) {
        FieldList<T> t = this.data.computeIfAbsent(field, field1 -> new FieldList<T>(field1, size()));
        List<T> oldData = t.data;
        if (oldData.size() != data.size()) {
            throw new IllegalArgumentException("different length: " + oldData.size() + " != " + data.size());
        }
        oldData.clear();
        oldData.addAll(data);
    }

    public <T> List<T> getData(Field<T> field) {
        FieldList<T> t = this.data.computeIfAbsent(field, field1 -> new FieldList<T>(field1, size()));
        return Collections.unmodifiableList(t.data);
    }

    public void updateModes() {
//        modes = this.points.parallelStream().map(e -> e.mode).distinct().collect(Collectors.toList());
//        modes = new TreeMap<>();
//        EnumSet.allOf(TravelMode.class).forEach(e -> modes.put(e, 0));
//        points.forEach(e -> modes.pu.put(e.mode, modes.get(e.mode) + 1));
//        points.forEach(e -> modes.computeIfPresent(e.mode, modes.get(e.mode) + 1));
        Map<TravelMode, Long> map = getList(ExtensionManager.INSTANCE.mainExtension.mode).data.stream()
//                .map(p -> (TravelMode) ExtensionManager.INSTANCE.mainExtension.mode.get(p))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        modes = new TreeMap<>(map);
//        return modes.entrySet().stream().map(e -> e.getKey().toString() + ": " + e.getValue()).collect(Collectors.joining(",\n"));
    }

//    public static List<GpsTrack> fromMyJson(String data) {
//        return null;
//    }

    public static List<GpsTrack> fromSimpleTrack(List<SimpleTrack> simpleTracks, List<Extension> extensions, String name) {
        int i = 0;
        List<GpsTrack> ret = new ArrayList<>();
        for (SimpleTrack track : simpleTracks) {
            GpsTrack t = new GpsTrack();
            for (Extension extension : extensions) {
                for (Field field : extension.getFields()) {
                    t.data.put(field, new FieldList(field, track.data.get(field.name).data));
                }
            }
            t.size.set(track.length);
            t.name = simpleTracks.size() > 1 ? name + "-" + df.format(i++) : name;
            t.removeBadTimes();
            t.updateModes();
            ret.add(t);
        }
        return ret;
    }

    private void removeBadTimes() {
        long lt = Long.MAX_VALUE;
        var time = this.data.get(ExtensionManager.INSTANCE.mainExtension.time);
        Set<Integer> list = new TreeSet<>();
//        int i = 0;
        ListIterator<?> iterator = time.data.listIterator(time.data.size());
        while (iterator.hasPrevious()) {
//        for (Object datum : time.data) {
            int i = iterator.previousIndex();
            Long t = (Long) iterator.previous();
            if (t == null || lt <= t) {
                list.add(i);
            } else {
                lt = t;
            }
//            i++;
        }
        removePoints(list);
    }

//    public static List<GpsTrack> fromOldJson(String jsonString) {
//        List<GpsTrack> ret = new ArrayList<>();
//        JSONArray array = new JSONArray(jsonString);
//        for (int tripCounter = 0; tripCounter < array.length(); tripCounter++) {
//            DecimalFormat df = new DecimalFormat("0000");
//            JSONArray tripTrack = array.getJSONObject(tripCounter).getJSONArray("tripTrack");
//            GpsTrack track = new GpsTrack();
//            long lastTime = 0;
////            GpsTrackPoint prev = null;
//            FieldList.PointIterator point = new FieldList.PointIterator(track);
////            var time = point.get(ExtensionManager.INSTANCE.mainExtension.time);
//            for (int j = 0; j < tripTrack.length(); j++) {
//                long time = OldJsonParser.parsePoint(tripTrack.getJSONObject(j), point);
//                if (lastTime < time) {
//                    lastTime = time;
//                    point.addAndNext();
//                } else {
//                    System.out.print(".");
//                }
////                point.setRelTime(prev);
////                prev = point;
////                track.points.add(point);
//            }
//            track.name = df.format(tripCounter);
//            track.updateModes();
//            ret.add(track);
//            log.info("fromJson: added track [{}/{}]: {}", tripCounter, array.length(), track.name);
//        }
//        return ret;
//    }

//    public static GpsTrack fromOSMT(InputStream inputStream) throws JDOMException, IOException {
//        SAXBuilder builder = new SAXBuilder();
//        Document doc = builder.build(inputStream);
//        Element rootElement = doc.getRootElement();
//        GpsTrack ret = new GpsTrack();
//        GpxUtil.fromOSMT(rootElement, ret);
//        if (ret.points.size() < 1) {
//            //fixme
////            throw new RuntimeException("Empty track");
//            return GpsTrack.withException(new RuntimeException("Empty track"));
//
//        }
////        setRelTimes(ret.points);
//        ret.updateModes();
//        return ret;
//    }

    public int size() {
        return size.get();
    }

//    public static GpsTrack fromOSMT(String text, String name) throws JDOMException, IOException {
//        SAXBuilder builder = new SAXBuilder();
//        Document doc = builder.build(new StringReader(text));
//        Element rootElement = doc.getRootElement();
//        GpsTrack ret = new GpsTrack();
//        ret.name = name;
//        GpxUtil.fromOSMT(rootElement, ret);
//        if (ret.size() < 1) {
////            fixme
////            throw new RuntimeException("Empty track");
//            return GpsTrack.withException(new RuntimeException("Empty track"));
//        }
////        setRelTimes(ret.points);
//        ret.updateModes();
//        return ret;
//    }



//    public GpsTrackPoint getStart() {
//        return points.get(0);
//    }

//    public GpsTrackPoint getEnd() {
//        return points.get(points.size() - 1);
//    }

    public String stat() {
//        Map<TravelMode, Integer> modes = new TreeMap<>();
//        EnumSet.allOf(TravelMode.class).forEach(e -> modes.put(e, 0));
//        points.forEach(e -> modes.put(e.mode, modes.get(e.mode) + 1));
        return modes.entrySet().stream().map(e -> e.getKey().toString() + ": " + e.getValue()).collect(Collectors.joining(",\n"));
    }

//    private static void setRelTimes(Collection<GPSPoint> points) {
//        GPSPoint prev = null;
//        for (GPSPoint p : points) {
//            p.setRelTime(prev);
//            prev = p;
//        }
//    }

    public String toTable(Collection<Field<?>> fields) {
        StringBuilder ret = new StringBuilder("<table><tr><td>")
                .append(fields.stream()
                        .map(e -> e.extension.getName() + "." + e.name)
                        .collect(Collectors.joining("</td><td>")))
                .append("</td></tr>");
//        points.forEach(e -> sb.append(e.toCSV()).append("\n"));
        var point = this.getGpsPoint();
        List<FieldList.FieldValue> ll = fields.stream()
                .map(e -> point.get(e))
                .collect(Collectors.toList());
        while (point.next()) {
            ret.append("<tr><td>")
                    .append(ll.stream()
                    .map(e -> String.valueOf(e.get()))
                    .collect(Collectors.joining("</td><td>")))
                    .append("<td></tr>");
        }
        ret.append("</table>");
//        ret += points.stream().map(e -> e.toCSV(fields)).collect(Collectors.joining("\n"));
        return ret.toString();
//        ret += points.stream().map(e -> "<tr>" + e.toTable(fields) + "</tr>").collect(Collectors.joining("\n"));
//        return ret + "</table>";
    }


    public String toTableNice(Collection<Field<?>> fields) {
        StringBuilder ret = new StringBuilder("<table><tr>");
        for (Field field : fields) {
            Element td = new Element(HTML.Tag.TD.toString()).addClass("tdv");
            td.text(field.getFullName());
            ret.append(td);
        }
        ret.append("</td></tr>");
        var point = this.getGpsPoint();
        List<FieldList.FieldValue> ll = fields.stream()
                .map(e -> point.get(e))
                .collect(Collectors.toList());
        while (point.next()) {
            ret.append("<tr>");
            TravelMode tm = null;
            for (FieldList.FieldValue fieldValue : ll) {
                final Element td = new Element(HTML.Tag.TD.toString());
                final Object fv = fieldValue.get();
                final Field f = fieldValue.getParentField();
                if (fv == null) {
                    td.text("--");
                } else if (f.extension == ExtensionManager.INSTANCE.osmExtension) {
                    double v = (double) fv;
                    if (v < 0.020) {
                        td.addClass("tdg");
                    }
                    td.text(myFormatter.format(v));
                } else if (f == ExtensionManager.INSTANCE.mainExtension.lat) {
                    td.text(String.valueOf(fv));
//                    td.text(myFormatter.format((double) fv));
                } else if (f == ExtensionManager.INSTANCE.mainExtension.lon) {
                    td.text(String.valueOf(fv));
//                    td.text(myFormatter.format((double) fv));
                } else if (f == ExtensionManager.INSTANCE.mainExtension.speed) {
                    td.addClass("tdh");
                    td.text(myFormatter.format((double) fv));
                } else if (f == ExtensionManager.INSTANCE.mainExtension.time) {
                    td.text(DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli((Long) fv)));
                } else if (f.type == FieldType.ENUM) {
                    if (tm == null) {
                        tm = (TravelMode) fv;
                        td.addClass(tm.name());
                    } else if (tm == fv) {
                        td.addClass("tdg");
                    } else {
                        td.addClass("tdr");
                    }
                    td.text(String.valueOf(fv));
                } else if (f.type == FieldType.DOUBLE) {
                    td.text(myFormatter.format((double) fv));
                } else {
                    td.text(String.valueOf(fv));
                }
                ret.append(td);
            }
            ret.append("</tr>");
//                    .append(ll.stream()
//                    .map(e -> String.valueOf(e.get()))
//                    .collect(Collectors.joining("</td><td>")))
//                    .append("<td></tr>");
        }
        ret.append("</table>");
//        ret += points.stream().map(e -> e.toCSV(fields)).collect(Collectors.joining("\n"));
        return ret.toString();
//        ret += points.stream().map(e -> "<tr>" + e.toTable(fields) + "</tr>").collect(Collectors.joining("\n"));
//        return ret + "</table>";
    }



//    public String toTable(Collection<Field> fields, Collection<PredictionModel> models) {
//        StringBuilder ret = new StringBuilder("<table><tr><td>")
//                .append(fields.stream().map(e -> e.name).collect(Collectors.joining("</td><td>")))
////                .append(models.stream().map(PredictionModel::getName).collect(Collectors.joining("</td><td>")))
//                .append("</td></tr>");
////        points.forEach(e -> sb.append(e.toCSV()).append("\n"));
////        List<Iterator<TravelMode>> list = models.stream().map(m -> this.predictions.get(m).prediction.iterator()).collect(Collectors.toList());
////        int i = 0;
//        for (GpsTrackPoint point : points) {
//            ret.append("<tr>").append(point.toTable(fields));
////            for (Iterator<TravelMode> travelModeIterator : list) {
////                ret.append("<td>").append(travelModeIterator.next()).append("</td>");
////            }
//            ret.append("</tr>\n");
//        }
////        ret += points.stream().map(e -> e.toTable(fields)).collect(Collectors.joining("\n"));
//        return ret + "</table>";
//    }

//    public static String serialize(GpsTrack track) {
//        JSONObject root = new JSONObject();
//        root.put("name", track.name);
//        JSONArray points = new JSONArray();
//        for (GPSPoint p : track.points) {
//            points.put(ArffUtil.pointToCSV(p));
//        }
//        root.put("points", points);
//        return root.toString();
//    }
//
//    public static GpsTrack deserialize(String json) {
//        GpsTrack ret = new GpsTrack();
//        JSONObject root = new JSONObject(json);
//        ret.name = root.getString("name");
//        JSONArray points = root.getJSONArray("points");
//        for (int i = 0; i < points.length(); i++) {
//            ret.points.add(ArffUtil.pointFromCSV(points.getString(i)));
//        }
//        ret.updateModes();
//        return ret;
////        for (String p : ) {
////            points.put(p.serialize());
////        }
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        GpsTrack gpsTrack = (GpsTrack) o;
//        return name.equals(gpsTrack.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name);
//    }
//    public int hashCode() {
//        try {
//            throw new RuntimeException();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return super.hashCode();
//    }

    @Override
    public String toString() {
        return "GpsTrack{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", modes=" + modes +
                ", exception=" + exception +
                '}';
    }

    private FieldList newField(Field field) {
        return new FieldList(field, size());
    }

    public void addExtension(Extension extension) {
        var fields = extension.getFields();
        this.fields.addAll(fields);
        for (Field field : fields) {
//            FieldList f = new FieldList(field, size());
            this.data.put(field, newField(field));
        }
//        for (GpsTrackPoint point : this.points) {
//            point.fields.addAll(Collections.nCopies(fields.size(), null));
//        }
//        extension.compute(this);
    }

    /**
     * Order should matter
     * @return ordered list of Extension s that should be updated
     */
    public List<Extension> fixMissingFields() {
        List<Extension> ret = new ArrayList<>();
        for (Extension extension : ExtensionManager.INSTANCE.getAllExtensions()) {
            boolean added = false;
            for (Field field : extension.getFields()) {
                if (!data.containsKey(field)) {
                    if (!added) {
                        ret.add(extension);
                        added = true;
                    }
                    data.put(field, newField(field));
                }
            }
        }
        return ret;
    }

    public void removePoints(Collection<Integer> indexes) {
        List<Integer> ii = new ArrayList<>(indexes);
        Collections.sort(ii);
        Collections.reverse(ii);
        int newSize = this.size.get();;
        for (FieldList fieldList : data.values()) {
            for (Integer index : ii) {
                fieldList.data.remove(index.intValue());
            }
            newSize = fieldList.data.size();
        }
        this.size.set(newSize);
//        List<Integer> toRemove =
    }
}
