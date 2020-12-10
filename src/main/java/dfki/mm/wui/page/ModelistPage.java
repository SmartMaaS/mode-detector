package dfki.mm.wui.page;

import dfki.mm.*;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.predict.ModelMethod;
import dfki.mm.predict.PredictionModel;
import dfki.mm.request.JsonOut;
import dfki.mm.request.MyApiField;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.request.RequestUtil;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.wui.MyWebUtils;
import org.eclipse.jetty.server.Request;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import java.util.*;

public class ModelistPage extends PageTemplate implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ModelistPage.class);

    private final Object syncPage = new Object();

    public ModelistPage(String filename, String pageName, String address) {
        super(filename, pageName, address);

//        init();
    }

    public void init() {
//        Configuration.INSTANCE.subscribe(InternalMessage.MODEL_ADDED, this);
        Configuration.INSTANCE.subscribe(InternalMessage.EXTENSIONS_RELOAD, this);
        Configuration.INSTANCE.subscribe(InternalMessage.MODELS_RELOAD, this);
//        Configuration.INSTANCE.subscribe(InternalMessage.MODEL_STATUS, this);
//        Configuration.INSTANCE.subscribe(InternalMessage.TRACK_ADDED, this);
//        Configuration.INSTANCE.subscribe(InternalMessage.TRACK_REMOVED, this);
        Configuration.INSTANCE.subscribe(InternalMessage.TRACKS_RELOAD, this);

        Element element = getDoc().getElementById(MyApiField.method.name());
        element.children().remove();
        for (ModelMethod method : ModelMethod.values()) {
            if (method == ModelMethod.REMOTE) {
                continue;
            }
            element.appendChild(new Element(HTML.Tag.OPTION.toString())
                    .attr("value", method.name()).appendText(method.name()));
        }

        updateFields();
//        updateTracks();

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
        List<ModelMethod> modelMethods = RequestUtil.parseParamMethod(httpRequest);
        List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);

        List<GpsTrack> validation = RequestUtil.parseParamTracks(httpRequest, MyApiField.param2.name(), false);
        Objects.requireNonNull(fields);
        fields.removeAll(Collections.singleton(ExtensionManager.INSTANCE.mainExtension.mode));
//        List<Integer> ret = new ArrayList<>();
        for (ModelMethod modelMethod : modelMethods) {
            DataHolder.INSTANCE.modelData.createModel(fields, modelMethod, gpsTracks, validation);
//            if (m != null) {
//                m.train(gpsTracks);
//                ret.add(m.hid);
//            }
        }
//                ModelMethod modelMethod = ModelMethod.valueOf(MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.method.name()));
//        JsonOut.success(httpRequest, ret);
        MyWebUtils.sendRedirect(httpRequest, getUrl());
        return;

    }

    public void updateModels() {
        synchronized (syncPage) {
            Element element = doc.getElementById("model-list");

//            appendModelInfoTable(element);
        }
    }

    public void updateTracks(String elementId) {
        synchronized (syncPage) {
            Element element = getDoc().getElementById(elementId);
            element.children().remove();
            for (GpsTrack track : DataHolder.INSTANCE.trackManager.tracks) {
                element.appendChild(new Element(HTML.Tag.OPTION.toString())
                        .attr("value", String.valueOf(track.id)).appendText(track.name));
            }
        }
    }

    public void updateFields() {
        synchronized (syncPage) {
            Element element = getDoc().getElementById(MyApiField.field.name());
            element.children().remove();
            for (Field<?> field : ExtensionManager.INSTANCE.getAllFields()) {
                element.appendChild(new Element(HTML.Tag.OPTION.toString())
                        .attr("value", String.valueOf(field.id)).appendText(field.getFullName()));
            }
        }
    }

//    public void appendModelInfoTable(Element element) {
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
//
//    }

    @Override
    public void onMessage(InternalMessage message, Collection<String> data) {
        switch (message) {
//            case MODEL_ADDED:
            case MODELS_RELOAD:
                updateModels();
                return;
//            case TRACK_ADDED:
//            case TRACK_REMOVED:
            case EXTENSIONS_RELOAD:
                updateFields();
                return;
            case TRACKS_RELOAD:
                updateTracks(MyApiField.track.name());
                updateTracks(MyApiField.param2.name());
                return;
            default:
                throw new RuntimeException("fixme" + message);
        }
    }
}
