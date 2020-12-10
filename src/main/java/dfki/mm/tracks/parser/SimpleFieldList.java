package dfki.mm.tracks.parser;

import dfki.mm.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class SimpleFieldList<T> {
    String name;
    public List<T> data = new ArrayList<>();

    public void add(Double value) {
        data.add((T) value);
    }

    public void add(String value) {
        data.add((T) value);
    }

    public void add(Long value) {
        data.add((T) value);
    }

    public void add(TravelMode value) {
        data.add((T) value);
    }

//    public Object


    public SimpleFieldList(String name) {
        this.name = name;
    }

    public static class FieldDouble extends SimpleFieldList<Double> {
        public FieldDouble(String name) {
            super(name);
        }
    }
    public static class FieldString extends SimpleFieldList<String> {
        public FieldString(String name) {
            super(name);
        }
    }
    public static class FieldLong extends SimpleFieldList<Long> {
        public FieldLong(String name) {
            super(name);
        }
    }
    public static class FieldTravelMode extends SimpleFieldList<TravelMode> {
        public FieldTravelMode(String name) {
            super(name);
        }
    }



}
