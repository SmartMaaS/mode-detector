package dfki.mm;

//import java.util.EnumMap;
import java.util.EnumSet;
//import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum TravelMode {
    BICYCLE,
    BUS,
    CAR,
    TRAIN,
    WALK,
    UNDEF,
    ;

    public static final TravelMode[] meaningfull = {
            TravelMode.BICYCLE,
            TravelMode.BUS,
            TravelMode.CAR,
            TravelMode.TRAIN,
            TravelMode.WALK,
    };

    private static final Map<Integer, TravelMode> ordinals =
            EnumSet.allOf(TravelMode.class).stream().collect(Collectors.toMap(Enum::ordinal, e -> e));

    public static TravelMode parseString(String from) {
        if (from == null || from.trim().length() == 0) {
            return UNDEF;
        }
        int i = from.indexOf("_");
        if (i >= 0) {
            from = from.substring(i + 1);
        }
        return TravelMode.valueOf(from.trim().toUpperCase());
    }

    public static TravelMode parseOrDefault2(String from, TravelMode defaultValue) {
        if (from == null || from.trim().length() == 0) {
            return defaultValue;
        }
        switch (from.toUpperCase()) {
            case "BIKE":
            case "BICYCLE":
                return BICYCLE;
            case "BUS":
            case "TRAM":
                return BUS;
            case "CAR_DRIVER":
            case "CAR":
                return CAR;
            case "TRAIN":
                return TRAIN;
            case "WALK":
                return WALK;
            case "NULL":
            case "UNDEF":
                return UNDEF;
            default:
                System.out.println("!!!!!!!!!!!!!!!!!!!! WTF mode: " + from);
                throw new RuntimeException("!!!!!!!!!!!!!!!!!!!! WTF mode: " + from);
        }
    }

    public static TravelMode parseOrDefault(String from, TravelMode defaultValue) {
        try {
            if (from == null || from.trim().length() == 0) {
                return defaultValue;
            }
            int i = Math.min(from.indexOf("_"), from.indexOf(" "));
            if (i >= 0) {
                from = from.substring(i + 1);
            }
            return TravelMode.valueOf(from.trim().toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static TravelMode fromOrdinal(int ordinal) {
        return ordinals.get(ordinal);
    }
}
