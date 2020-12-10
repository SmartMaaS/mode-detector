package dfki.mm.wui.page;

import dfki.mm.request.MyHttpRequest;
import dfki.mm.wui.MyWebUtils;
import dfki.mm.wui.ResourceLoader;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StaticPage extends PageTemplate {

    private static final Logger log = LoggerFactory.getLogger(StaticPage.class);

//    private final Map<String, > data = new HashMap<>();
    public static final String STATIC_PATH = "/static/";
    public static final String STATIC_LIST_FILE = STATIC_PATH + "aaa-list.txt";

    Map<String, byte[]> statics = new HashMap<>();

    public StaticPage(String filename, String pageName, String address) {
//        super(filename, pageName, address);
        super(filename);
        init();
    }

    public void init() {
        List<String> staticFileNames = ResourceLoader.read4(STATIC_LIST_FILE);
        for (String s : staticFileNames) {
            s = STATIC_PATH + s.strip();
            statics.put(s, ResourceLoader.readBytes(s));
        }
    }

    @Override
    public void handle(MyHttpRequest http) {
        log.debug("handle: {}", http.baseRequest);

        byte[] data = statics.get(http.baseRequest.getContextPath());
        if (data == null) {
            MyWebUtils.simpleError(http, HttpServletResponse.SC_NOT_FOUND, "");
//            handleError(target, baseRequest, request, response, HttpServletResponse.SC_NOT_FOUND, null);
            return;
        }

//        response.setContentType("text/html; charset=utf-8");
        http.response.setStatus(HttpServletResponse.SC_OK);
        try {
            http.response.getOutputStream().write(data);
        } catch (IOException e) {
            log.warn("Error serving static", e);
            http.response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        }
//        if (writeResponse(response, getPageString())) {
//            baseRequest.setHandled(true);
//        }
        http.baseRequest.setHandled(true);

//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        if (writeResponse(response, getPageString())) {
//            baseRequest.setHandled(true);
//        }
    }

//    public void handleError(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response,
//                            int status, String text) {
//        response.setStatus(status);
//        if (text != null && text.length() > 0) {
//            MyWebUtils.writeResponse(response, text);
//        }
//        baseRequest.setHandled(true);
//    }

}
