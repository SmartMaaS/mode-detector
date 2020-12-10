package dfki.mm.wui;

import dfki.mm.DataHolder;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.tracks.GpsTrack;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.swing.text.html.HTML;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MyWebUtils {

    private static final Logger log = LoggerFactory.getLogger(MyWebUtils.class);

    public static ByteArrayOutputStream readPart(Part part) throws IOException {
//        if (part.getSize() > Integer.MAX_VALUE) {
//            throw new RuntimeException("too big");
//        }
        int len = 1024;//(int) part.getSize();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[len];
        int length;
        InputStream inputStream = part.getInputStream();
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result;
    }

    public static String toString(ByteArrayOutputStream stream) {
        return stream.toString(StandardCharsets.UTF_8);
    }

    public static float getJsonOr(JSONObject jsonObject, String key, float defaultValue) {
        if (jsonObject.has(key)) {
            try {
                return ((float) jsonObject.getDouble(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static long getJsonOr(JSONObject jsonObject, String key, long defaultValue) {
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getLong(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static String getJsonOr(JSONObject jsonObject, String key, String defaultValue) {
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }


    public static int getParam(Request baseRequest, String name, int defaultValue) {
        String q = baseRequest.getParameter(name);
        if (q == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(q);
        } catch (Exception e) {
            log.warn("{} is not int: {}", name, q);
            return defaultValue;
        }
    }

    public static float getParam(Request baseRequest, String name, float defaultValue) {
        String q = baseRequest.getParameter(name);
        if (q == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(q);
        } catch (Exception e) {
            log.warn("{} is not float: {}", name, q);
            return defaultValue;
        }
    }

    public static boolean writeResponse(HttpServletResponse response, String text) {
        try {
            response.getWriter().print(text);
            return true;
        } catch (IOException e) {
            log.error("Cannot write response", e);
            return false;
        }
    }

    public static boolean sendRedirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
            return true;
        } catch (IOException e) {
            log.error("Cannot send redirect", e);
            return false;
        }
    }

    public static boolean sendRedirect(MyHttpRequest myHttpRequest, String location) {
        myHttpRequest.response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        boolean ret = sendRedirect(myHttpRequest.response, location);
        myHttpRequest.baseRequest.setHandled(true);
        return ret;
    }

    public static boolean simpleResponse(Request baseRequest, HttpServletResponse response, String text) {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (writeResponse(response, text)) {
            baseRequest.setHandled(true);
            return true;
        }
        return false;
    }

    public static boolean simpleResponse(MyHttpRequest myHttpRequest, String text) {
        return simpleResponse(
                myHttpRequest.baseRequest,
                myHttpRequest.response,
                text + (myHttpRequest.errors.size() > 0 ? myHttpRequest.errors : ""));
    }

    public static boolean simpleResponse(MyHttpRequest myHttpRequest) {
        return simpleResponse(
                myHttpRequest.baseRequest,
                myHttpRequest.response,
                String.valueOf(myHttpRequest.errors));
    }

    public static boolean simpleError(MyHttpRequest myHttpRequest, int code, String text) {
        myHttpRequest.response.setContentType("text/html; charset=utf-8");
        myHttpRequest.response.setStatus(code);
        if (writeResponse(myHttpRequest.response, text)) {
            myHttpRequest.baseRequest.setHandled(true);
            return true;
        }
        return false;
    }

//    public static boolean simpleResponse(HttpServletResponse response, int status, String text) {
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(status);
//        if (writeResponse(response, text)) {
//            return true;
//        }
//        return false;
//    }

    public static List<Element> trackInfoTDs(GpsTrack track) {
        final String TD = HTML.Tag.TD.toString();

        List<Element> ret = new ArrayList<>();
        int i = track == null ? 0 : track.id;//tracks.indexOf(track);
//        tmp = new Element(TD);
//        tmp.appendText(String.valueOf(i));
//        child.appendChild(tmp);
        final String url = DataHolder.INSTANCE.webData.tracksPage.getUrl();


        ret.add(new Element(TD).appendText(track == null ? "name" : track.name));

        ret.add(new Element(TD).appendText(track == null ? "size" : " [" + track.size() + "] "));

        //.stream().map(Enum::toString).collect(Collectors.joining(","))

//        ret.add(new Element(TD).appendText(track == null ? "modes" : " [" + track.modes.keySet().toString() + "] "));
        ret.add(new Element(TD).appendText(track == null ? "modes" : track.modes.keySet().toString()));

        if (track != null) {
            for (String code : new String[]{
//                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_DELETE + "?track=" + i +"'>delete</a>] ",
//                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_OSM + "?map=2&track=" + i +"'>osm</a>] ",
//                    String.format(" [<a href='%s%s?track=%s&request=info'>info</a>] ", JettyMain.WEB_PATH_GPS, WEB_PATH_HTML, i),
                    " [<a href='" + url + "&track=" + i + "&request=delete'>delete</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=osm&map=0'>osm</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=info'>info</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=raw'>raw</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=raw4'>raw4</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=predictions'>predictions</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=arff'>arff</a>] ",
                    " [<a href='" + url + "&track=" + i + "&request=csv'>csv</a>] ",
            }) {
                ret.add(new Element(TD).append(code));
            }
        } else {
            ret.add(new Element(TD));
        }
        return ret;
    }

    public static String wrapInHtml(String title, String s) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + title + "</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"/static/mynav.css\"/>\n" +
                "</head>\n" +
                "<body>" +
                s +
                "</body>";
    }

}
