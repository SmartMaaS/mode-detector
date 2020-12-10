package dfki.mm.wui.android;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dfki.mm.Configuration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty Server for Android requests.
 * Registers single handler.
 *
 * @see #main()
 */
public class JettyMainSubmit extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(JettyMainSubmit.class);
    private final HandlerAndroidApp handlerAndroidApp;
    private final Map<String, String> statics = new HashMap<>();

    public JettyMainSubmit() throws IOException, URISyntaxException {
        handlerAndroidApp = new HandlerAndroidApp();
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
//        System.out.println();
        log.debug("{}, {}, {}", target, baseRequest.getMethod(), baseRequest);

        try {
            handlerAndroidApp.handle(target, baseRequest, request, response);
        } catch (Exception e) {
            log.warn("Exception handling position update: {}, {}, {}", target, baseRequest.getMethod(), baseRequest, e);
        }
        if (!baseRequest.isHandled()) {

            handleMain(target, baseRequest, request, response);
        }
    }

    private void handleMain(String target,
                            Request baseRequest,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        response.getWriter().println("Empty page");
        baseRequest.setHandled(true);
    }

    /**
     * Creates a server (without joining) and returns it.
     * @return A running jetty server
     *
     * @see Configuration Server port is defined in Configuration
     */
    public static Server main(/*String[] args*/) throws Exception {
        log.info("Initializing Jetty Server for Android position updates");
        int port = Configuration.INSTANCE.androidSubmitPort;
        Server server = new Server(port);

        String link = server.getURI().getScheme() + "://" + server.getURI().getHost() + ":" + port + server.getURI().getPath();
        System.out.println("\n" + link + "\n");

        // print server address with port
        var enumeration = NetworkInterface.getNetworkInterfaces();
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

        server.setHandler(new JettyMainSubmit());
//        server.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        server.start();
//        server.join();
        return server;
    }
}
