package dfki.mm.tracks;

import dfki.mm.TravelMode;
import dfki.mm.tracks.extension.Extension;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Field<T> implements Comparable<Field> {
    private static final AtomicInteger counter = new AtomicInteger();

    public final int id = counter.getAndIncrement();

    public final String name;
    public final FieldType type;
    public final Extension extension;
    public int position;

    public Field(Extension extension, String name, FieldType type) {
        this.extension = extension;
        this.name = name;
        this.type = type;
    }

//    public Double getDouble(GpsTrackPoint point) {
//        throw new RuntimeException("Wrong field type: " + type);
//    }

//    public abstract Double asDouble(GpsTrackPoint point);

//    public String asString(GpsTrackPoint point) {
//        return String.valueOf(point.fields.get(position));
//    }

//    public Object asJson(GpsTrackPoint point) {
//        Object ret = point.fields.get(position);
//        return ret == null ? JSONObject.NULL : this.type == FieldType.ENUM ? ret.toString() : ret;
//    }

//    public T get(GpsTrackPoint point) {
//        return (T) point.fields.get(position);
//    }

//    public void set(GpsTrackPoint point, T value) {
//        point.fields.set(this.position, value);
//    }

    public String getFullName() {
        return extension.getName() + "." + this.name;
    }

    @Override
    public String toString() {
        return String.format("Field[%d] %s.%s (%s)", id, extension.getName(), name, type);
    }

    /**
     * Field of data type Long
     */
    public static class LongField extends Field<Long> {
        private LongField(Extension extension, String name) {
            super(extension, name, FieldType.LONG);
        }

//        @Override
//        public Double asDouble(GpsTrackPoint point) {
//            Long ret = (Long) point.fields.get(position);
//            return ret == null ? null : ret.doubleValue();
//        }
    }

    /**
     * Field of data type Double
     */
    public static class DoubleField extends Field<Double> {
        private DoubleField(Extension extension, String name) {
            super(extension, name, FieldType.DOUBLE);
        }

//        @Override
//        public Double getDouble(GpsTrackPoint point) {
//            return (Double) point.fields.get(position);
//        }
//
//        @Override
//        public Double asDouble(GpsTrackPoint point) {
//            return (Double) point.fields.get(position);
//        }
    }

    /**
     * Field of data type String
     */
    public static class StringField extends Field<String> {
        private StringField(Extension extension, String name) {
            super(extension, name, FieldType.STRING);
        }

//        @Override
//        public Double asDouble(GpsTrackPoint point) {
//            throw new IllegalArgumentException("not for string");
//        }
    }

    /**
     * Field of data type TravelMode
     */

    public static class ModeField extends Field<TravelMode> {
        private ModeField(Extension extension, String name) {
            super(extension, name, FieldType.ENUM);
        }

//        @Override
//        public Double asDouble(GpsTrackPoint point) {
//            TravelMode ret = (TravelMode) point.fields.get(position);
//            return ret == null ? null : (double) ret.ordinal();
//        }
    }

//    /**
//     * Dummy field (for predictions)
//     */
//    public static class ZeroField extends Field<Double> {
//        private ZeroField(Extension extension, String name) {
//            super(extension, name, FieldType.DOUBLE);
//        }
//
//        @Override
//        public Double getDouble(GpsTrackPoint point) {
//            return -1;
//        }
//
//        @Override
//        public Double asDouble(GpsTrackPoint point) {
//            return (Double) point.fields.get(position);
//        }
//    }

    public static LongField newLongField(Extension extension, String name) {
        return new LongField(extension, name);
    }

    public static DoubleField newDoubleField(Extension extension, String name) {
        return new DoubleField(extension, name);
    }

    public static StringField newStringField(Extension extension, String name) {
        return new StringField(extension, name);
    }

    public static ModeField newModeField(Extension extension, String name) {
        return new ModeField(extension, name);
    }

//    public Field newField(Extension extension, String name, FieldType type) {
//        switch (type) {
//            case DOUBLE: return new LongField(extension, name);
//            case LONG: return new DoubleField(extension, name);
//            case STRING: return new StringField(extension, name);
//            case ENUM: return new ModeField(extension, name);
//        }
//        throw new IllegalArgumentException(type.toString());
//    }



    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String SPEED = "speed";
    public static final String TIME = "time";
    public static final String HEADING = "heading";
    public static final String MODE = "mode";

    /**
     *
     * @param extension nullable
     * @param name exact name
     * @param fieldList where to search
     * @return index or -1 if not found
     */
    public static int indexOf(Extension extension, String name, Collection<Field> fieldList) {
        Objects.requireNonNull(name);
        int i = 0;
        for (Field field : fieldList) {
            if (name.equals(field.name) && (extension == null || extension == field.extension)) {
                return i;
            }
        }
        return -1;
    }


//    public float gps_longitude;
//    public float gps_latitude;
//    public float gps_speed;
//    public float gps_bearing;
//    public float gps_headingChange;
//    public float gps_avgHeadingChange;
//    public float activity_motorized;
//    public float activity_cycle;
//    public float activity_walk;
//    public float distanceToTrainLine;
//    public float distanceToTrainStation;
//    public float distanceToBusLine;
//    public float distanceToBusStop;
//    public float speed;
//    public float avgSpeed;
//    public float maxSpeed;
//    public float ninetyFifthPercentile;
//    public float kurtosisSpeed;
//    public float skewnessSpeed;
//    public float standardDeviation;
//    public float headingChange;
//    public float avgHeadingChange;
//    public float acceleration;
//    public float avgAcceleration;
//    public float avgAccelerationNoSign;
//    public String tripId;
//    public long time;
//    public String timeString;
//    public String kindOfTrain;
//    public String kindOfBus;
//    public float distanceCovered;
//    public float distanceCoveredTotal;
//    public float timePassed;
//    public float timePassedTotal;
//    public TravelMode mode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field<?> field = (Field<?>) o;
        return id == field.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Field o) {
        Objects.requireNonNull(o);
        return this.id - o.id;
    }
}
