package dfki.mm.wui.page;

import dfki.mm.DataHolder;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.wui.MyWebUtils;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

public class MapPage extends PageTemplate {

    private static final Logger log = LoggerFactory.getLogger(MapPage.class);

    public MapPage(String filename, String pageName, String address) {
        super(filename, pageName, address);
    }

    @Override
    public void handle(MyHttpRequest http) {
        handleOSM(http);
    }

    private void handleOSM(MyHttpRequest http) {
        int map = MyWebUtils.getParam(http.baseRequest, "map", 0);
        float lat = MyWebUtils.getParam(http.baseRequest, "lat", 52.5f);
        float lon = MyWebUtils.getParam(http.baseRequest, "lon", 13.4f);
        String text = DataHolder.INSTANCE.webData.osmMapTemplates.get(map).simpleCenter(lat, lon, true);

        MyWebUtils.simpleResponse(http, text);
    }

    @Override
    public void init() {

    }
}
