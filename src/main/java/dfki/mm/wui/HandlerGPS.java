package dfki.mm.wui;

import dfki.mm.DataHolder;
import org.eclipse.jetty.server.Request;
import org.jdom2.JDOMException;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.swing.text.html.HTML;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class HandlerGPS {




//    public HandlerGPS() throws IOException, URISyntaxException {
////        osm = new OSMMap();
//    }

//    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        if (target.equals(WEB_PATH_UPLOAD_GPS)) {
//            handleUploadGps(target, baseRequest, request, response);
////        } else if (target.startsWith(WEB_PATH_DELETE)) {
////            handleDelete(target, baseRequest, request, response);
////        } else if (target.startsWith(WEB_PATH_OSM)) {
////            handleOSM(target, baseRequest, request, response);
////        } else if (target.startsWith(WEB_PATH_INFO)) {
////            handleInfo(target, baseRequest, request, response);
//        } else if (target.startsWith(WEB_PATH_SVG)) {
//            handleSVG(target, baseRequest, request, response);
//        } else if (target.startsWith(WEB_PATH_CHART)) {
//            handleChart(target, baseRequest, request, response);
////        } else if (target.startsWith(WEB_PATH_RAW)) {
////            handleRaw(target, baseRequest, request, response);
////        } else if (target.startsWith(WEB_PATH_HTML)) {
//        } else  {
//            handleHtml(target, baseRequest, request, response);
//        }
//    }

//    private void handleDelete(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
////        request.getQueryString();
//        String q = baseRequest.getParameter("track");
////        List<String> q = baseRequest.getParameters().get("track");
////        q.forEach(System.out::println);
////        baseRequest.que
//        if (q != null) {
//            try {
//                GPSTrack t = tracks.remove(Integer.parseInt(q));
//                log.info("Deleted [{}] ", q, t.name);
//                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
//                response.sendRedirect(JettyMain.WEB_PATH_GPS);
//            } catch (Exception e) {
//                log.warn("Cannot delete gps={}", q, e);
//            }
//        }
//    }

//    private void handleOSM(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        int m = MyWebUtils.getParam(baseRequest, "map", 0);
////        String m = baseRequest.getParameter("map");
//        String q = baseRequest.getParameter("track");
//        GPSTrack t = parseTrackNumber(parseTrackNumber(q));
//        if (t == null) { // r == null ||
//            return;
//        }
//
//        String text = DataHolder.INSTANCE.osmMapTemplates.get(m).simpleTrack(m, t);
//        log.info("OSMed [{}] ", q, t.name);
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().print(text);
//        baseRequest.setHandled(true);
//    }

//    private void handleInfo(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Integer q = parseTrackNumber(baseRequest.getParameter("track"));
//        GPSTrack t = parseTrackNumber(q);
//        if (t == null) {
//            return;
//        }
//
//        String text = t.stat().replace("\n", "<br/>");
//        log.info("OSMed [{}] ", t.name);
//        text += "<br/><img src='/gps/svg/?track=" + q + "' style='width: 85vw; min-width: 330px;'/><br/>";
//        text += "<br/><img src='/gps/chart/?track=" + q + "'/><br/>";
//        text += "<br/><img src='/gps/raw/?track=" + q + "'/><br/>";
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().print(text);
//        baseRequest.setHandled(true);
//    }



    private void handleHtml(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        int m = MyWebUtils.getParam(baseRequest, "map", 0);
//        String r = baseRequest.getParameter("request");
//        String q = baseRequest.getParameter("track");
//        GPSTrack t = parseTrackNumber(parseTrackNumber(q));
//        if (r == null || t == null) {
//            return;
//        }
//        String text;
//        switch (r) {
//            case "delete":
//                try {
//                    t = tracks.remove(Integer.parseInt(q));
//                    log.info("Deleted [{}] ", q, t.name);
//                    response.setStatus(HttpServletResponse.SC_SEE_OTHER);
//                    response.sendRedirect(JettyMain.WEB_PATH_GPS);
//                    baseRequest.setHandled(true);
//                    return;
//                } catch (Exception e) {
//                    log.warn("Cannot delete gps={}", q, e);
//                }
////                break;
//            case "osm":
//                text = DataHolder.INSTANCE.osmMapTemplates.get(m).simpleTrack(m, t);
//                log.info("OSMed [{}] ", q, t.name);
//                break;
//            case "raw":
//                text = t.toTable(EnumSet.allOf(Field.class));
//                break;
//            case "info":
//                text = t.stat().replace("\n", "<br/>");
//                log.info("OSMed [{}] ", t.name);
//                text += "<br/><img src='/gps/svg/?track=" + q + "' style='width: 85vw; min-width: 330px;'/><br/>";
//                text += "<br/><img src='/gps/chart/?track=" + q + "'/><br/>";
//                text += "<br/><img src='/gps/raw/?track=" + q + "'/><br/>";
//                break;
//            case "arff":
//                text = "<pre>";
//                text += ArffUtil.writeFile(t.points);
//                log.info("arff [{}] ", t.name);
//                text += "</pre>";
//                break;
//            case "csv":
//                text = "<pre>";
//                text += ArffUtil.writeCSV(t.points);
//                log.info("csv [{}] ", t.name);
//                text += "</pre>";
//                break;
//            default:
//                return;
//        }
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().print(text);
//        baseRequest.setHandled(true);
    }

//    private void handleSVG(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String q = baseRequest.getParameter("track");
//        GPSTrack t;
//        if (q == null) {
//            return;
//        }
//        try {
//            t = tracks.get(Integer.parseInt(q));
//        } catch (Exception e) {
//            log.warn("Cannot osm gps={}", q, e);
//            return;
//        }
//
//        int s = t.points.size();
//        float[] x = new float[s];
//        float[] y = new float[s];
//        double[] minmax = new double[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
//        int i = 0;
//        float pt = 0;
//        for (GPSPoint p : t.points) {
//            x[i] = pt + p.relTime / 30f;
//            pt = x[i];
//            y[i] = p.gps_speed;
//            minmax[0] = Double.min(minmax[0], x[i]);
//            minmax[1] = Double.min(minmax[1], y[i]);
//            minmax[2] = Double.max(minmax[2], x[i]);
//            minmax[3] = Double.max(minmax[3], y[i]);
//            i++;
//        }
//        double mult = (minmax[2] - minmax[0] + 1) / (minmax[3] - minmax[1] + 1) * 0.5;
//        for (int j = 0; j < y.length; j++) {
//            y[j] *= mult;
//        }
////        t.points.stream().map(e -> new Map.Entry<Float,Float>(e.time, e.speed)).collect(Collectors.toCollection());
////        t.points.stream().map(e -> e.speed).toArray(IntFunc).collect(Collectors.toCollection()).toArray(new float[1]);
////        HandleSVG.generate(null);
//        response.setContentType("image/svg+xml; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        HandleSVG.generate2(response.getWriter(), x, y);
////        HandleSVG.generate(response.getWriter());
////        response.getOutputStream()
////    getWriter().print(text);
//        baseRequest.setHandled(true);
//    }

//    private void handleChart(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
////        HandleSVG.generate(null);
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        HandleChart.generate(response.getOutputStream());
////        response.getOutputStream()
////    getWriter().print(text);
//        baseRequest.setHandled(true);
//    }

//    private void handleRaw(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String q = baseRequest.getParameter("track");
//        GPSTrack t;
//        if (q == null) {
//            return;
//        }
//        try {
//            t = tracks.get(Integer.parseInt(q));
//        } catch (Exception e) {
//            log.warn("Cannot osm gps={}", q, e);
//            return;
//        }
//
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
////        HandleChart.generate(response.getOutputStream());
////        response.getOutputStream()
////    getWriter().print(text);
//        response.getWriter().print(t.toTable(EnumSet.allOf(Field.class)));
//        baseRequest.setHandled(true);
//    }



//    public void appendTrackInfo(Element element) {
//        element.children().remove();
//
//        int i = 0;
//        for (GPSTrack t : tracks) {
//            Element child = new Element(HTML.Tag.LI.toString());
//            child.appendText(i + " " + t.name + "  " );
//            child.appendText(" [" +
//                    t.points.parallelStream().map(e -> e.mode).distinct().map(Enum::toString).collect(Collectors.joining(",")) +
//                    "] "
//            );
//            child.appendText(" [" + t.points.size() + "] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_DELETE + "?track=" + i +"'>delete</a>] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_OSM + "?track=" + i +"'>osm</a>] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"&request=info'>info</a>] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"'&request=raw>raw</a>] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"&request=arff'>arff</a>] ");
////            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_SVG + "?track=" + i +"'>info</a>] ");
////            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_CHART + "?track=" + i +"'>info</a>] ");
//            element.appendChild(child);
//            i++;
//        }
//
//    }


}
