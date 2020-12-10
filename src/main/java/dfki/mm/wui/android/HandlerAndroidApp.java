package dfki.mm.wui.android;


import dfki.mm.DataHolder;
import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.functional.TrackManager;
import dfki.mm.tracks.GpsTrack;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

/**
 * Handler for Android requests.
 *
 * Android app sends coordinates, we save them and send a detected mode back
 */
public class HandlerAndroidApp {

    private static final Logger log = LoggerFactory.getLogger(HandlerAndroidApp.class);
//    private static final Map<String, UpdatableGpsTrack> tracks = new HashMap<>();
    /**
     * One track pre user (email)
     */
    private static final Map<String, LocalTrack> tracks = new HashMap<>();


    /**
     * Get new GPS point (from request body), send mode back.
     *
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // parse request body
//     {"lat":37.4224,"lon":-122.0841183,"altitude":5.0,"bearing":0.0,"accuracy":12.455,"speed":0.0,"time":1605801580827,"uid":"eiucOYz7DrVGjEbxsXfBksgjPUi2","email":"a@a.com"}
        JSONObject jo;
        try (var x = baseRequest.getReader()) {
            String s = x.lines().collect(Collectors.joining());
            jo = new JSONObject(s);
//            log.warn(s);
            log.warn(jo.toString(2));
        } catch (RuntimeException e) {
            log.error("Bad request[{}]: {}", baseRequest, e.getMessage());
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            baseRequest.setHandled(true);
            return;
        }
        String uid = jo.getString("uid");
        String email = jo.getString("email");
        double lat = jo.getDouble("lat");
        double lon = jo.getDouble("lon");
        long time = jo.getLong("time");
        double altitude = jo.getDouble("altitude");
        double bearing = jo.getDouble("bearing");
        double speed = jo.getDouble("speed");
        double accuracy = jo.getDouble("accuracy");

//                "uid": "eiucOYz7DrVGjEbxsXfBksgjPUi2",
//                "bearing": 0,
//                "accuracy": 12.413,
//                "lon": -122.0841183,
//                "time": 1605801721000,
//                "lat": 37.4224,
//                "speed": 1,
//                "email": "a@a.com"
        LocalTrack track = tracks.get(uid);

        // save the track after 1 minute without updates
        int MAX_GAP = 60_000;
        if (track != null && track.getLastTime() < time - MAX_GAP) {
            GpsTrack newTrack = track.generateTrack(Integer.MAX_VALUE);
            DataHolder.INSTANCE.trackManager.addTracks(List.of(newTrack));
            track = null;
        }

        if (track == null) {
            track = new LocalTrack(email + "-" + time);
            tracks.put(uid, track);
        }
        TravelMode mode = track.extend(uid, email, lat, lon, time, altitude, bearing, speed, accuracy);

        // send response
        response.setContentType("text/html; charset=utf-8");
        if (mode != null) {
            log.info("Success for {} => {}", email, mode);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(mode);
        } else {
            log.info("Error handling request from {}", email);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        baseRequest.setHandled(true);
    }

}
