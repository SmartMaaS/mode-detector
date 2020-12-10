package dfki.mm.wui;


import dfki.mm.Constants;
import dfki.mm.DataHolder;
import dfki.mm.relation2.MyNodeBucket;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class HandlerAPI {

    private static final Logger log = LoggerFactory.getLogger(HandlerAPI.class);

//    private enum RequestMethod {
//        GET,
//    }

//    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//
////        final String MODEL = DataHolder.INSTANCE.modelPage.getPageName() + "-";
//
//        String p = baseRequest.getParameter("page");
//        String r = baseRequest.getParameter("request");
//
//        String m = baseRequest.getParameter("model");
//        String t = baseRequest.getParameter("track");
//
//        boolean isGet = "get".equalsIgnoreCase(request.getMethod());
//
//        switch (p + "-" + r) {
//            case "null-pos":
//                handlePosition(target, baseRequest, request, response);
//                return;
//            case Constants.PAGE_MODEL + Constants.REQUEST_DELETE:
//                if (m != null) {
//                    int hid = Integer.parseInt(m);
//                    DataHolder.INSTANCE.modelData.remove(DataHolder.INSTANCE.modelData.getModelByHid(hid));
//                    if (isGet) {
//                        MyWebUtils.sendRedirect(response, DataHolder.INSTANCE.webData.modelistPage.getUrl());
//                    } else {
//                        MyWebUtils.writeResponse(response, "ok");
//                    }
//                }
//                return;
//            case Constants.PAGE_MODEL + Constants.REQUEST_DELETE_PREDICTIONS:
//                if (m != null) {
//                    int hid = Integer.parseInt(m);
//                    DataHolder.INSTANCE.predictionData.removeForModel(DataHolder.INSTANCE.modelData.getModelByHid(hid));
//                    if (isGet) {
//                        MyWebUtils.sendRedirect(response, DataHolder.INSTANCE.webData.modelPage.getUrl() + "&model=" + m);
//                    } else {
//                        MyWebUtils.writeResponse(response, "ok");
//                    }
//                }
//                return;
//            default:
//                log.warn("Wrong request");
//        }
//    }
//
//    private void handlePosition(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        String lat = baseRequest.getParameter("lat");
//        String lon = baseRequest.getParameter("lon");
//
//        response.setContentType("application/json; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
////        response.getWriter().print("{a: 1, gps_latitude: "+gps_latitude+"}");
//        JSONObject o = new JSONObject();
//        o.put("gps_latitude", lat);
//        o.put("gps_longitude", lon);
//        JSONObject o2;
////        JSONArray a;
//
//        float la = Float.parseFloat(lat);
//        float lo = Float.parseFloat(lon);
//
//        EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> res =
//                DataHolder.INSTANCE.mapData.findNearest(la, lo, 5);
//
////        Map<Integer, MyNodeBucket> map = DataHolder.INSTANCE.mapData.getMap();
//
////        if (map != null) {
//
////            EnumMap<MyNodeBucket.NodeType, MyNodeBucket.NodeWithDistance> res = MyNodeBucket.getNearestNodes(map, la, lo, 5);
////            MyNodeBucket.NodeWithDistance defaultX = new MyNodeBucket.NodeWithDistance()
////                    .set(new MyNode(-1).setLatLon(0d,0d), 111);
////            defaultX.node.setLatLon(0d,0d);
//            MyNodeBucket.NodeWithDistance x;
//            o2 = new JSONObject();
//
//            x = res.get(MyNodeBucket.NodeType.BUS_LINE);
//            o2.put("distanceToBusLine", x.distance);
//            o2.put("nearestPointOnBusLine", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));
//
//            x = res.get(MyNodeBucket.NodeType.BUS_STOP);
//            o2.put("distanceToBusStop", x.distance);
//            o2.put("nearestBusStop", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));
//            o2.put("busRouteString", "");
//
//            o.put("bus", o2);
//            o2 = new JSONObject();
//
//            x = res.get(MyNodeBucket.NodeType.RAIL_LINE);
//            o2.put("distanceToTrainLine", x.distance);
//            o2.put("nearestPointOnRailwayTrack", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));
//
//            x = res.get(MyNodeBucket.NodeType.RAIL_STOP);
//            o2.put("distanceToTrainStation", x.distance);
//            o2.put("nearestTrainStation", new JSONArray().put(x.getNodeOrDefault().lat).put(x.getNodeOrDefault().lon));
//
//            o.put("train", o2);
////        } else {
////            o.put("osm", "still loading");
////        }
//
//        response.getWriter().print(o.toString());
//        baseRequest.setHandled(true);
//    }
}
