package dfki.mm.wui.page;

import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.InternalMessage;
import dfki.mm.MessageListener;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.request.MyApiField;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.request.RequestUtil;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldType;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.tracks.extension.PostExtension;
import dfki.mm.wui.MyWebUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import java.util.Collection;
import java.util.List;

public class ExtPostPage extends PageTemplate implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ExtPostPage.class);

    private final Object syncPage = new Object();

    public ExtPostPage(String filename, String pageName, String address) {
        super(filename, pageName, address);
//        init();
    }

    public void init() {
        Configuration.INSTANCE.subscribe(InternalMessage.EXTENSIONS_RELOAD, this);

        Document docMain = getDoc();

        docMain.getElementById("mtd").remove();
        docMain.getElementById("ttd").remove();
        docMain.getElementById("ttd2").remove();
//        getDoc().getElementById(MyApiField.field.name()).attr("multiple", false);
        docMain.getElementById(HIDDEN_PAGE).attr("value", this.getPageName());
        docMain.getElementById(HEADER_LIST).text("Existing post processing");
        docMain.getElementById(HEADER_CREATE).text("Add post processing:");
        updateFields();
//        updateTracks();

    }

    private void updateFields() {
        Element element = getDoc().getElementById(MyApiField.field.name());
        element.children().remove();
        for (Field field : ExtensionManager.INSTANCE.getAllFields()) {
            if (field.type == FieldType.ENUM) {
                element.appendChild(new Element(HTML.Tag.OPTION.toString())
                        .attr("value", String.valueOf(field.id)).appendText(field.getFullName()));
            }
        }
    }

    @Override
    public void handle(MyHttpRequest http) {
        log.info("handle: {}", http.baseRequest);


//        updateModels();
        DataHolder.INSTANCE.modelData.reloadRemoteModels();
        String r = http.baseRequest.getParameter(MyApiField.request.name());

        if (r == null) {
            MyWebUtils.simpleResponse(http, getPageString());
            return;
        }

        switch (r) {
            case "create":
                try {
                    createModels(http);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new RuntimeException("r = " + r);
        }



//        http.response.setContentType("text/html; charset=utf-8");
//        http.response.setStatus(HttpServletResponse.SC_OK);
//        if (MyWebUtils.writeResponse(http.response, getPageString())) {
//            http.baseRequest.setHandled(true);
//        }
    }

    private void createModels(MyHttpRequest httpRequest) throws Exception {
        List<Field<?>> fields = RequestUtil.parseParamFields(httpRequest);
//        Field f = null;
        if (fields == null || fields.size() < 1 || fields.parallelStream().anyMatch(e -> e.type != FieldType.ENUM)) {
            MyWebUtils.simpleError(httpRequest, HttpServletResponse.SC_BAD_REQUEST, "requires 1 or more Mode fields");
            return;
        }
        for (Field field : fields) {
            ExtensionManager.INSTANCE.addExtension(new PostExtension((Field.ModeField) field));
        }
        MyWebUtils.sendRedirect(httpRequest, getUrl());
    }

    public void updateModels() {
        synchronized (syncPage) {
            Element element = doc.getElementById("model-list");
            element.children().remove();
            appendModelInfoTable(element);
        }
    }

    public void appendModelInfoTable(Element element) {

        for (Extension extension : ExtensionManager.INSTANCE.getAllExtensions()) {
            if (extension instanceof PostExtension) {
//                element.appendChild("");
                element.appendChild(new Element("p").appendText(extension.id + ": " + extension.getName()));
            }
        }


//        element.children().forEach(Node::remove);
//        Element tmp = new Element(HTML.Tag.TABLE.toString());
//        element.appendChild(tmp);
//        element = tmp;
//        final String TD = HTML.Tag.TD.toString();
//
//        int i = 0;

//        List<PredictionModel> models = new ArrayList<>(DataHolder.INSTANCE.modelData.allPredictionModels);
////        models.addAll(this.models);
//        final String url = DataHolder.INSTANCE.webData.modelPage.getUrl();
//        for (PredictionModel predictionModel : models) {
//            Element child = new Element(HTML.Tag.TR.toString());
//            element.appendChild(child);
//
//            tmp = new Element(TD);
//            tmp.appendText(String.valueOf(i));
//            child.appendChild(tmp);
//
//
//
//            child.appendChild(new Element(TD).appendText( predictionModel.getStatus().toString()));
//
//            child.appendChild(new Element(TD).appendText(predictionModel.getName()));
//
//            child.appendChild(new Element(TD).appendText(" [" + predictionModel.method + "] "));
//
//            if (predictionModel.postprocessor == null) {
//                child.appendChild(new Element(TD).appendText("no post"));
////                child.appendChild(new Element(TD).appendChild(new Element("a")
////                        .attr("href", url + "&request=attach&model=" + predictionModel.hid)
////                        .appendText("+post")));
//            } else {
//                child.appendChild(new Element(TD).appendText("has post"));
//
//            }
////            child.appendChild(new Element(TD).appendText(" [" +
////                    t.points.parallelStream().map(e -> e.mode).distinct().map(Enum::toString).collect(Collectors.joining(",")) +
////                    "] "));
//
//            for (String code : new String[]{
////                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_DELETE + "?track=" + i +"'>delete</a>] ",
////                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_OSM + "?map=2&track=" + i +"'>osm</a>] ",
////                    String.format(" [<a href='%s%s?track=%s&request=info'>info</a>] ", JettyMain.WEB_PATH_GPS, WEB_PATH_HTML, i),
//                    " [<a href='"+ url + "&model=" + predictionModel.hid +"&request='>open</a>] ",
////                    " [<a href='"+ getUrl() + "&track=" + i +"&request=osm&map=0'>osm</a>] ",
////                    " [<a href='"+ getUrl() + "&track=" + i +"&request=info'>info</a>] ",
////                    " [<a href='"+ getUrl() + "&track=" + i +"&request=raw'>raw</a>] ",
////                    " [<a href='"+ getUrl() + "&track=" + i +"&request=arff'>arff</a>] ",
////                    " [<a href='"+ getUrl() + "&track=" + i +"&request=csv'>csv</a>] ",
//            }) {
//                child.appendChild(new Element(TD).append(code));
//            }
//            i++;
//        }

    }
    @Override
    public void onMessage(InternalMessage message, Collection<String> data) {
        switch (message) {
            case EXTENSIONS_RELOAD:
                updateFields();
                updateModels();
                return;
            default:
                throw new RuntimeException("fixme" + message);
        }
    }
}
