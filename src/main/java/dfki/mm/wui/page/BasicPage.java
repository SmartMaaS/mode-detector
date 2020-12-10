package dfki.mm.wui.page;

import dfki.mm.TravelMode;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.TrackUtil;
import dfki.mm.wui.MyWebUtils;
import org.eclipse.jetty.server.Request;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class BasicPage extends PageTemplate {

    private static final Logger log = LoggerFactory.getLogger(BasicPage.class);

//    private String savesPath = "/dev/shm/1/saves";
//    private String tracksPath = savesPath + "/tracks";
//    private String modelsPath = savesPath + "/models";

    public BasicPage(String filename, String pageName, String address) {
        super(filename, pageName, address);
    }

    public void publish(HttpServletResponse response, Element mainDiv) {
        Document d = getDoc().clone();
        Element div = d.getElementById("main");
        div.replaceWith(mainDiv);

        MyWebUtils.writeResponse(response, d.toString());
    }

    @Override
    public void handle(MyHttpRequest http) {
        log.info("handle: {}", http.baseRequest);
        MyWebUtils.simpleResponse(http, getPageString());
    }

    @Override
    public void init() {

    }

    //    public static Element trackStatistics(List<GpsTrack> tracks) {
//        Element main = new Element("div");
//
//        TrackUtil.TrackStatistics ts = new TrackUtil.TrackStatistics(tracks);
//        var table = new Element("table");
//        main.appendChild(table);
//        var tr = new Element("tr")
//                .appendChild(new Element("td").appendText("track"))
//                .appendChild(new Element("td").appendText("min"))
//                .appendChild(new Element("td").appendText("min@"))
//                .appendChild(new Element("td").appendText("max"))
//                .appendChild(new Element("td").appendText("max@"))
////                .appendChild(new Element("td").appendText("modes"))
//                ;
//        table.appendChild(tr);
//        for (Field floatField : Field.floatFields) {
//            tr = new Element("tr")
//                    .appendChild(new Element("td").appendText(floatField.toString()))
//                    .appendChild(new Element("td").appendText(String.valueOf(ts.min.get(floatField).d)))
//                    .appendChild(new Element("td").appendText(String.valueOf(ts.min.get(floatField).s)))
//                    .appendChild(new Element("td").appendText(String.valueOf(ts.max.get(floatField).d)))
//                    .appendChild(new Element("td").appendText(String.valueOf(ts.max.get(floatField).s)))
////                .appendChild(new Element("td").appendText("modes"))
//                    ;
//            table.appendChild(tr);
//
//        }
//        return main;
//    }
}
