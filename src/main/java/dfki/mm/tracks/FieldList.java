package dfki.mm.tracks;

import java.util.*;
import java.util.function.Function;

public class FieldList<T> {
    Field<T> name;
    List<T> data = new ArrayList<>();
//
//    public void add(Double value) {
//        data.add((T) value);
//    }
//
//    public void add(String value) {
//        data.add((T) value);
//    }
//
//    public void add(Long value) {
//        data.add((T) value);
//    }
//
//    public void add(TravelMode value) {
//        data.add((T) value);
//    }

//    public Object


    protected FieldList(Field<T> name, int len) {
        this.name = name;
        if (len > 0) {
            data.addAll(Collections.nCopies(len, null));
        }
    }

    public FieldList(Field<T> name, List<T> data) {
        this.name = name;
        this.data.addAll(data);
    }

//    public static class FieldListDouble extends FieldList<Double> {
//        public FieldListDouble(Field<Double> name) {
//            super(name);
//        }
//    }
//    public static class FieldListString extends FieldList<String> {
//        public FieldListString(Field name) {
//            super(name);
//        }
//    }
//    public static class FieldListLong extends FieldList<Long> {
//        public FieldListLong(Field name) {
//            super(name);
//        }
//    }
//    public static class FieldListTravelMode extends FieldList<TravelMode> {
//        public FieldListTravelMode(Field name) {
//            super(name);
//        }
//    }

    public FieldValue newFieldValue() {
        return new FieldValue();
    }

    @Override
    public String toString() {
        return "FieldList{" +
                "name=" + name +
                ", data=" + data +
                '}';
    }

    public class FieldValue {
        private ListIterator<T> listIterator;
        private T value;
        private boolean changed;

        private FieldValue() {
            listIterator = data.listIterator();
        }

        public Field<T> getParentField() {
            return FieldList.this.name;
        }

        private T next() {
            return value = listIterator.next();
        }

        public void set(T value) {
            this.value = value;
            changed = true;
        }

        public T get() {
            return this.value;
        }

        private void add() {
            listIterator.add(value);
            changed = false;
            value = null;
        }



        private void set() {
            if (changed) {
                listIterator.set(value);
            }
            changed = false;
        }


        private void setAndNext() {
            set();
            next();
        }

        @Override
        public String toString() {
            return "FieldValue{" +
                    ", changed=" + changed +
                    ", value=" + value +
                    ", listIterator=" + listIterator +
                    '}';
        }
    }

    public static class PointIterator {

        private final GpsTrack track;
//        List<FieldList.FieldValue> fieldValueList = new ArrayList<>();
        Set<FieldList.FieldValue> fieldValueSet = new HashSet<>();
        int len;
        int pos;

        public PointIterator(GpsTrack track) {
            this.track = track;
            len = track.size.get();
//            for (Map.Entry<Field, FieldList> entry : track.data.entrySet()) {
////                if (len == -1) {
////                    len = entry.getValue().data.size();
////                }
//                if (len != entry.getValue().data.size()) {
//                    throw new IllegalStateException(len + "!=" + entry.getValue().data.size());
//                }
//                fieldValueList.add(entry.getValue().newFieldValue());
//            }
            pos =  -1;
        }

        public <T> FieldList<T>.FieldValue get(Field<T> field) {
            if (pos > -1) {
                throw new IllegalStateException(String.valueOf(pos));
            }
            var ret = track.data.computeIfAbsent(field, field1 -> new FieldList(field1, len));
//            track.data.get(field).newFieldValue();
//            for (FieldList.FieldValue fieldValue : ) {
//                if (fieldValue.getParentField() == field) {
//                    return fieldValue;
//                }
//            }
//            FieldList<T> ret;
//            track.data.put(field, ret = new FieldList(field, len));
            var ret2 = ret.newFieldValue();
//            fieldValueList.add(ret2);
            fieldValueSet.add(ret2);
            return ret2;
        }

        public List<FieldList.FieldValue> getAll(List<Field<?>> fields) {
            List<FieldList.FieldValue> ret = new ArrayList<>();
            for (Field field : fields) {
                ret.add(get(field));
            }
            return ret;
        }

//        public boolean removeAndNext() {
//            for (FieldList.FieldValue fieldValue : fieldValueSet) {
//                fieldValue.listIterator.remove();
//            }
//            len = track.size.decrementAndGet();
//            return next();
//        }

        public boolean next() {
            pos++;
            if (pos >= len) {
                for (FieldList.FieldValue fieldValue : fieldValueSet) {
                    fieldValue.set();
                }
                return false;
            } else {
                for (FieldList.FieldValue fieldValue : fieldValueSet) {
                    fieldValue.setAndNext();
                }
                return true;
            }
        }

        public void addAndNext() {
            for (FieldList.FieldValue fieldValue : fieldValueSet) {
                fieldValue.add();
            }
            len = track.size.incrementAndGet();
//                len++;
            pos++;
        }

//        public void addLast() {
//            for (FieldList.FieldValue fieldValue : fieldValueSet) {
//                fieldValue.add();
//            }
//            len = track.size.incrementAndGet();
////                len++;
//            pos++;
//        }



        @Override
        public String toString() {
            return "PointIterator{" +
                    "track=" + track +
                    ", @ " + pos +
                    " / " + len +
                    '}';
        }

        public int getPos() {
            return pos;
        }
    }


}
