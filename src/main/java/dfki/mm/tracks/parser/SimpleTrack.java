package dfki.mm.tracks.parser;

import dfki.mm.TravelMode;

import java.util.LinkedHashMap;

public class SimpleTrack {

//    public class Point {
//        List<Double>
//    }

    public int length;

    public LinkedHashMap<String, SimpleFieldList<?>> data = new LinkedHashMap<>();

    public void add(String field, Double value) {
        var f = data.computeIfAbsent(field, s -> new SimpleFieldList.FieldDouble(field));
        f.add(value);
    }

    public void add(String field, Long value) {
        var f = data.computeIfAbsent(field, s -> new SimpleFieldList.FieldLong(field));
        f.add(value);
    }

    public void add(String field, String value) {
        var f = data.computeIfAbsent(field, s -> new SimpleFieldList.FieldString(field));
        f.add(value);
    }

    public void add(String field, TravelMode value) {
        var f = data.computeIfAbsent(field, s -> new SimpleFieldList.FieldTravelMode(field));
        f.add(value);
    }

    public void verifyLength() {
        length = -1;
        for (SimpleFieldList<?> fieldList : data.values()) {
            if (length == -1) {
                length = fieldList.data.size();
            }
            if (length != fieldList.data.size()) {
                throw new IllegalArgumentException("different length: " + length + " " + fieldList.data.size());
            }
        }
    }

}
