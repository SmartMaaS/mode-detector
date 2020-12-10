package dfki.mm.wui;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.request.JsonOut;
import dfki.mm.request.PostRequest;
import dfki.mm.wui.page.PageTemplate;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web UI server
 */
public class JettyMain extends AbstractHandler {

    public static final String WEB_PATH_STATIC = "/static/";
    public static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement("");

    private static final Logger log = LoggerFactory.getLogger(JettyMain.class);

    private final HandlerAPI handlerAPI;
    private final Map<String, String> statics = new HashMap<>();

    /**
     * Loads static resources to be available during runtime
     */
    public JettyMain() throws IOException, URISyntaxException {
        handlerAPI = new HandlerAPI();
        for (String s : new String[]{
                "mynav.css",
                "notifications.css",
                "rocket-icon-64-89272.png",
                "myinit.js",
        }) {
            statics.put(s, ResourceLoader.readString("/static/" + s));
        }
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {

        // Declare response encoding and types
//        log.info("{}, {}, {}", target, baseRequest.getMethod(), baseRequest);
        MyHttpRequest http = new MyHttpRequest(baseRequest, request, response);
        if (target.startsWith(WEB_PATH_STATIC)) {
            handleStatic(target, http);
            return;
//        }
        /*if (target.startsWith("/json/post")) {
            try {
                String json = baseRequest.getParameter("json");
                JsonRequest r = JsonRequest.fromString(json);
                r.process();
//                switch (r.myApiRequest) {
//                    case TRACK_ADD:
//                }
            } catch (IllegalArgumentException e) {
                JsonOut.fail(baseRequest, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
            return;*/
        } else if (target.startsWith("/j2/")) {
            SimpleGet.main(target.replace("/j2/", ""), http);
            baseRequest.setHandled(true);
            return;
        } else if (target.startsWith("/post")) {
            try {
//                String json = baseRequest.getParameter("json");

                PostRequest r = PostRequest.fromRequest(http);
                r.process();
//                switch (r.myApiRequest) {
//                    case TRACK_ADD:
//                }
//            } catch (IllegalArgumentException e) {
//                JsonOut.fail(baseRequest, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//                return;
            } catch (Exception e) {
                log.error("", e);
                JsonOut.fail(http, e.getMessage());
                return;
            }
            return;
        }


        log.debug("{}, {}, {}", target, baseRequest, request.getContentLengthLong());

        String pageString = baseRequest.getParameter("page");
        PageTemplate page = DataHolder.INSTANCE.webData.pages.get(pageString);
//        log.info("");

        if (page == null) {
            if (target.equals("/favicon.ico")) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                response.sendRedirect("/static/rocket-icon-64-89272.png");
                baseRequest.setHandled(true);

//            } else if (target.startsWith("/api")) {
//                handlerAPI.handle(target, baseRequest, request, response);
//            } else if (target.startsWith("/static")) {
//                DataHolder.INSTANCE.webData.staticPage.handle(target, baseRequest, request, response);
//            } else {
//                DataHolder.INSTANCE.webData.pages.get("main").handle(target, baseRequest, request, response);
            } else {
                DataHolder.INSTANCE.webData.pages.get("main").handle(http);
            }

//            if (target.startsWith(WEB_PATH_GPS)) {
//                handlerGPS.handle(target.replaceFirst(WEB_PATH_GPS, ""), baseRequest, request, response);
//            } else
//                if (target.startsWith(WEB_PATH_MAP)) {
//                handlerMap.handle(target, baseRequest, request, response);
//            } else if (target.startsWith(WEB_PATH_POS_2)) {
//                handlerAPI.handle(target, baseRequest, request, response);
//            }
        } else {
            page.handle(http);
//            page.handle(target, baseRequest, request, response);
//            switch (page) {
//                case "gps":
//                    handlerGPS.handle(target.replaceFirst(WEB_PATH_GPS, ""), baseRequest, request, response);
//                    break;
//                case "map":
//                    handlerMap.handle(target, baseRequest, request, response);
//                    break;
//                case "save":
//                    handlerMap.handle(target, baseRequest, request, response);
//                    break;
//            }
        }
//        switch (target) {
//            case WEB_PATH_STATIC:
//                handleStatic(target, baseRequest, request, response);
//                break;
//            case WEB_PATH_UPLOAD_GPS:
//                handleUploadGps(target, baseRequest, request, response);
//                break;
//            default:
//                handleMain(target, baseRequest, request, response);

//        }
//
        if (!baseRequest.isHandled()) {

            handleMain(target, baseRequest, request, response);
        }

//        if (target.startsWith(WEB_PATH_STATIC)) {
//            handleStatic(target, baseRequest, request, response);
//        } else {
//            handleMain(target, baseRequest, request, response);
//        }

    }



    private void handleMain(String target,
                            Request baseRequest,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException,
            ServletException {

        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);


//        Element element = resourceLoader.doc.getElementById("gps-list");

//        handlerGPS.appendTrackInfo(element);
//        handlerGPS.appendTrackInfoTable(element);


        // Write back response
//        response.getWriter().println("<h1>Hello World</h1>");
//        response.getWriter().println(resourceLoader.doc.toString());

        response.getWriter().println("Empty page");

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    private void handleStatic(String target, MyHttpRequest http) throws IOException,
            ServletException {
        target = target.replace(WEB_PATH_STATIC, "");
        log.info("handleStatic {}", target);

        String res = statics.get(target);
        if (res == null) {
            http.response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            http.baseRequest.setHandled(true);
            return;
        }
//        http.response.setContentType("text/html; charset=utf-8");
        http.response.setStatus(HttpServletResponse.SC_OK);
        http.response.getWriter().print(res);
        http.baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception {
        main().join();
    }

    public static Server main() throws Exception {

        int port = Configuration.INSTANCE.port; //ExampleUtil.getPort(args, "jetty.http.port", 8080);
        Server server = new Server(port);

        String link = server.getURI().getScheme() + "://" + server.getURI().getHost() + ":" + port + server.getURI().getPath();
        System.out.println("\n" + link + "\n");

        var enumeration = NetworkInterface.getNetworkInterfaces();
//        System.out.println();
        while (enumeration.hasMoreElements()) {
            var networkInterface = enumeration.nextElement();
            var enumeration2 = networkInterface.getInetAddresses();
            while (enumeration2.hasMoreElements()) {
                InetAddress inetAddress = enumeration2.nextElement();
                System.out.println(String.format(
                        "%s:/%s:%s%s",
                        server.getURI().getScheme(),
                        inetAddress.toString(),
                        port,
                        server.getURI().getPath()));
            }
        }
        System.out.println();

//        System.exit(0);

        server.setHandler(new JettyMain());
//        server.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        server.start();
//        server.join();
        return server;
    }
}
