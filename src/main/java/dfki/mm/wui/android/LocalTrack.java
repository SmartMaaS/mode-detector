package dfki.mm.wui.android;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.tracks.extension.MainExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Android app track.
 * Track points are appended until the time gap is too big, then it is converted to a regular track
 */
public class LocalTrack {

    private static final Logger log = LoggerFactory.getLogger(LocalTrack.class);

    public final String id;
    private int length = 0;
    private long lastTime;

    private final ArrayList<String> uid = new ArrayList<>();
    private final ArrayList<String> email = new ArrayList<>();
    private final ArrayList<Double> lat = new ArrayList<>();
    private final ArrayList<Double> lon = new ArrayList<>();
    private final ArrayList<Long> time = new ArrayList<>();
    private final ArrayList<Double> altitude = new ArrayList<>();
    private final ArrayList<Double> bearing = new ArrayList<>();
    private final ArrayList<Double> speed = new ArrayList<>();
    private final ArrayList<Double> accuracy = new ArrayList<>();

    public LocalTrack(String id) {
        this.id = id;
    }

    /**
     * @return The time of last GPS point.
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * Convert LocalTrack to regular Track
     * @param maxLength Use at most maxLength last points
     */
    public GpsTrack generateTrack(int maxLength) {
        MainExtension mainExtension = ExtensionManager.INSTANCE.mainExtension;
        if (maxLength > length) {
            maxLength = length;
        }
        int startIndex = length > maxLength ? length - maxLength : 0;
        GpsTrack ret =  GpsTrack.fromBackup("tmp", maxLength);

//        ret.setData().data.put(field, new FieldList(field, tl));

        ret.setData(mainExtension.lat, this.lat.subList(startIndex, length));
        ret.setData(mainExtension.lon, this.lon.subList(startIndex, length));
        ret.setData(mainExtension.time, this.time.subList(startIndex, length));
        ret.setData(mainExtension.ele, this.altitude.subList(startIndex, length));
        ret.setData(mainExtension.heading, this.bearing.subList(startIndex, length));
        ret.setData(mainExtension.speed, this.speed.subList(startIndex, length));
//        ret.setData(mainExtension.accuracy, this.accuracy);
        ret.setData(mainExtension.mode, Collections.nCopies(length - startIndex, TravelMode.UNDEF));

        var needed = ret.fixMissingFields();
        for (Extension extension : needed) {
            extension.compute(ret);
        }
        return ret;
    }

    /**
     * Update the track and detect the mode
     * @return  mode detected
     */
    public synchronized TravelMode extend(String uid, String email, double lat, double lon, long time,
                                          double altitude, double bearing, double speed, double accuracy) {
        if (length == 0) {
            lastTime = time - 1;
        }
        if (time < lastTime) {
            log.info("time < lastTime, ignoring for {}", email);
            return TravelMode.UNDEF;
        }
        lastTime = time;

        this.uid.add(uid);
        this.email.add(email);
        this.lat.add(lat);
        this.lon.add(lon);
        this.time.add(time);
        this.altitude.add(altitude);
        this.bearing.add(bearing);
        this.speed.add(speed);
        this.accuracy.add(accuracy);
        this.length++;
        // may skip here if len too small
        MainExtension mainExtension = ExtensionManager.INSTANCE.mainExtension;
        List<Field<?>> allFields = ExtensionManager.INSTANCE.getAllFields();
        Field<TravelMode> ret = null;
        ListIterator<Field<?>> fieldListIterator = allFields.listIterator(allFields.size());
        while (fieldListIterator.hasPrevious()) {
            Field<?> t = fieldListIterator.previous();
            if (t instanceof Field.ModeField) {
                ret = (Field.ModeField) t;
                break;
            }
        }
        if (ret == null || ret.extension == mainExtension) {
            log.info("TravelMode field not found: {}", ret);
            return TravelMode.UNDEF;
        }

        GpsTrack tmp = generateTrack(10);
        var modes = tmp.getData(ret);
        if (modes == null || modes.size() == 0) {
            log.info("TravelMode field {} is {}", ret, modes == null ? null : "empty");
            return TravelMode.UNDEF;
        }
        return modes.get(modes.size() - 1);
    }
}
