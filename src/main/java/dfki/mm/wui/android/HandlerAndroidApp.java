package dfki.mm.wui.android;


import dfki.mm.DataHolder;
import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.functional.RemoteModelManagerHttp;
import dfki.mm.functional.TrackManager;
import dfki.mm.predict.RemoteModelProxy;
import dfki.mm.tracks.GpsTrack;
import netscape.javascript.JSException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

/**
 * Handler for Android requests.
 * <p>
 * Android app sends coordinates, we save them and send a detected mode back
 */
public class HandlerAndroidApp {

    private static final Logger log = LoggerFactory.getLogger(HandlerAndroidApp.class);
//    private static final Map<String, UpdatableGpsTrack> tracks = new HashMap<>();
    /**
     * One track pre user (email)
     */
    private static final Map<String, LocalTrack> tracks = new HashMap<>();


//    /**
//     * Get new GPS point (from request body), send mode back.
//     *
//     * @param target
//     * @param baseRequest
//     * @param request
//     * @param response
//     * @throws IOException
//     * @throws ServletException
//     */
//    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
//            throws IOException, ServletException {
//
//        // parse request body
////     {"lat":37.4224,"lon":-122.0841183,"altitude":5.0,"bearing":0.0,"accuracy":12.455,"speed":0.0,"time":1605801580827,"uid":"eiucOYz7DrVGjEbxsXfBksgjPUi2","email":"a@a.com"}
//        JSONObject jo;
//        try (var x = baseRequest.getReader()) {
//            String s = x.lines().collect(Collectors.joining());
//            //TODO s is the message that app sends. I need to send rdf from app to here, then convert this rdf (s) into json, the rest will be same
//            jo = new JSONObject(s);
////            log.warn(s);
//            log.warn(jo.toString(2));
//        } catch (RuntimeException e) {
//            log.error("Bad request[{}]: {}", baseRequest, e.getMessage());
////            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
////            baseRequest.setHandled(true);
//            return;
//        }
//        String uid = jo.getString("uid");
//        String email = jo.getString("email");
//        double lat = jo.getDouble("lat");
//        double lon = jo.getDouble("lon");
//        long time = jo.getLong("time");
//        double altitude = jo.getDouble("altitude");
//        double bearing = jo.getDouble("bearing");
//        double speed = jo.getDouble("speed");
//        double accuracy = jo.getDouble("accuracy");
//
////                "uid": "eiucOYz7DrVGjEbxsXfBksgjPUi2",
////                "bearing": 0,
////                "accuracy": 12.413,
////                "lon": -122.0841183,
////                "time": 1605801721000,
////                "lat": 37.4224,
////                "speed": 1,
////                "email": "a@a.com"
//        LocalTrack track = tracks.get(uid);
//
//        // save the track after 1 minute without updates
//        int MAX_GAP = 60_000;
//        if (track != null && track.getLastTime() < time - MAX_GAP) {
//            GpsTrack newTrack = track.generateTrack(Integer.MAX_VALUE);
//            DataHolder.INSTANCE.trackManager.addTracks(List.of(newTrack));
//            track = null;
//        }
//
//        if (track == null) {
//            track = new LocalTrack(email + "-" + time);
//            tracks.put(uid, track);
//        }
//        TravelMode mode = track.extend(uid, email, lat, lon, time, altitude, bearing, speed, accuracy);
//
//        // send response
//        response.setContentType("text/html; charset=utf-8");
//        if (mode != null) {
//            log.info("Success for {} => {}", email, mode);
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.getWriter().print(mode);
//        } else {
//            log.info("Error handling request from {}", email);
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }
//
//        System.out.println("mode = " + mode.toString());
//        //TODO I need to send the mode with the username to feedback service
//        /*
//        1. So app sends rdf here
//        2. the input rdf is converted into json or directly extract the info from input to create travel mode
//        3. send the travel mode with the username or uid to feedback service to be stored
//        4. send the input rdf from app to feedback service separately but indicate that it is optional
//
//        */
//        ContentProvider contentProvider = new StringContentProvider("RDF Model");
//
//
//        baseRequest.setHandled(true);
//    }


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
            throws IOException, ServletException, InterruptedException, ExecutionException, TimeoutException {

        // parse request body
//     {"lat":37.4224,"lon":-122.0841183,"altitude":5.0,"bearing":0.0,"accuracy":12.455,"speed":0.0,"time":1605801580827,"uid":"eiucOYz7DrVGjEbxsXfBksgjPUi2","email":"a@a.com"}
        JSONObject jo;
        try (var x = baseRequest.getReader()) {
            String s = x.lines().collect(Collectors.joining());


            try {
                // If json, then will be handled as json
                jo = new JSONObject(s);
                handleJsonInput(jo, baseRequest, response);
            } catch (JSONException je) {
                System.out.println("The input is not a valid json");
                // If not json, then will be handled as rdf. To do so, 2 methods exist:
                // Either the RDF is converted into JSON and then handled as json
//                handleJsonInput(convertRdf2Json(convertString2RDF(s)), baseRequest, response);

                // or handled as rdf (extracting info directly from rdf)
                handleRdfInput(s, baseRequest, response);
            }


//            log.warn(s);
//            log.warn(jo.toString(2));
        } catch (RuntimeException | InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Bad request[{}]: {}", baseRequest, e.getMessage());
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            baseRequest.setHandled(true);
            return;
        }


    }

    private void handleJsonInput(JSONObject jo, Request baseRequest, HttpServletResponse response) throws InterruptedException, ExecutionException, TimeoutException, IOException {
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

        computeTravelModeAndSendToFeedbackService(uid, time, email, altitude, accuracy, lat, bearing, lon, speed, baseRequest, response);

    }

    private TravelMode computeTravelModeAndSendToFeedbackService(String uid, long time, String email, double altitude,
                                                                 double accuracy, double lat, double bearing, double lon, double speed,
                                                                 Request baseRequest, HttpServletResponse response) throws IOException, InterruptedException, ExecutionException, TimeoutException {

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
            sendTravelModeToFeedbackService(uid, time, mode);
        } else {
            log.info("Error handling request from {}", email);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        baseRequest.setHandled(true);
        return mode;
    }

    private void handleRdfInput(String input, Request baseRequest, HttpServletResponse response) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Model model = convertString2RDF(input);

        String email = null, uid = null;
        long time = 0;
        double lat = 0, lon = 0, altitude = 0, bearing = 0, accuracy = 0, speed = 0;

        for (Resource subj : model.subjects()) {
            Model subFiltMod = model.filter(subj, null, null);
            for (Statement stm : subFiltMod) {
                switch (stm.getPredicate().getLocalName()) {
                    case "uid":
                        uid = stm.getObject().stringValue();
                        break;
                    case "email":
                        email = stm.getObject().stringValue();
                        break;
                    case "lat":
                        lat = Double.parseDouble(stm.getObject().stringValue());
                        break;
                    case "lon":
                        lon = Double.parseDouble(stm.getObject().stringValue());
                        break;
                    case "time":
                        time = Long.parseLong(stm.getObject().stringValue());
                        break;
                    case "alltitude":
                        altitude = Double.parseDouble(stm.getObject().stringValue());
                        break;
                    case "bearing":
                        bearing = Double.parseDouble(stm.getObject().stringValue());
                        break;
                    case "speed":
                        speed = Double.parseDouble(stm.getObject().stringValue());
                        break;
                    case "accuracy":
                        accuracy = Double.parseDouble(stm.getObject().stringValue());
                        break;
                }
            }
        }
        computeTravelModeAndSendToFeedbackService(uid, time, email, altitude, accuracy, lat, bearing, lon, speed, baseRequest, response);
    }

    // This method converts a string turtle into RDF4J model
    private Model convertString2RDF(String s) throws IOException {
        // Convert input into RDF Model
        String base = "http://www.dfki.de/SmartMaaS/feedback#";
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        return Rio.parse(inputStream, base, RDFFormat.TURTLE);
    }

    // This method is designed specifically for the input which comes from the app.
    // In other words, the input model must have a single subject.
    // This method converts an RDF4J model into JSONObject
    public static JSONObject convertRdf2Json(Model model) {

        JSONObject mainJsonObject = new JSONObject();

        for (Resource subj : model.subjects()) {
            Model subFiltMod = model.filter(subj, null, null);
            for (Statement stm : subFiltMod) {
                mainJsonObject.accumulate(stm.getPredicate().getLocalName(), stm.getObject().stringValue());
            }
        }
        return mainJsonObject;
    }

    // This method builds the RDF representaion of Travel Mode and sends to Feedback Service
    // to be stored in the TravelMode repository
    private void sendTravelModeToFeedbackService(String uid, long time, TravelMode mode) throws InterruptedException, ExecutionException, TimeoutException {
        final String feedbackWebServiceUrl = "http://192.168.42.52:8803/feedback_service/travelmode";
        HttpClient httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
//            e.printStackTrace();
        }

        final String pTime = "http://www.w3.org/2006/time#";
        final String pFoaf = "http://xmlns.com/foaf/spec/";
        final String pBase = "http://www.dfki.de/SmartMaaS/feedback#";
        final String pExf = "http://www.example.fe/edback#";

        ValueFactory vf = SimpleValueFactory.getInstance();
        ModelBuilder builder = new ModelBuilder();
        builder.setNamespace("base", pBase)
                .setNamespace("exf", pExf)
                .setNamespace("foaf", pFoaf)
                .setNamespace("time", pTime);

        builder.defaultGraph()
                .subject(vf.createIRI(pBase, "travelmodedata"))
                .add(vf.createIRI(pFoaf, "accountName"), uid)
                .add(vf.createIRI(pBase, "travelsBy"), mode.name())
                .add(vf.createIRI(pTime, "xsdDateTime"), time);


        org.eclipse.jetty.client.api.Request request = httpClient.newRequest(feedbackWebServiceUrl)
                .method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "text/turtle")
                .content(new StringContentProvider(convertRdf4jModel2String(builder.build())));


        ContentResponse response = request.send();
        int status = response.getStatus();
        String ret = response.getContentAsString();
        if (status != 200) {
            log.warn("Request failed [{}] {}", status, ret);
        }
        log.info("request [{}] status={} response={}", feedbackWebServiceUrl, status, ret.length());
    }

    // This method converts an RDF4J model to a correctly represented String
    public static String convertRdf4jModel2String(Model model) {
        String modelAsString = null;
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            TurtleWriter turtleWriter = new TurtleWriter(outputStream);
            Rio.write(model, turtleWriter);
            modelAsString = outputStream.toString();
            outputStream.close();

        } catch (IOException ioException) {
            return "Couldn't convert RDF model to String. Exception message:" + ioException.getLocalizedMessage();
        }
        return modelAsString;
    }

}
