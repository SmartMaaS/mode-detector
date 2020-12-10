package dfki.mm.wui.page;

import dfki.mm.*;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.functional.TrackManager;
import dfki.mm.request.MyApiField;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.request.RequestUtil;
import dfki.mm.tracks.*;
import dfki.mm.tracks.extension.PostExtension;
import dfki.mm.tracks.extension.PredictionExtension;
import dfki.mm.tracks.stat.FieldStatistics;
import dfki.mm.wui.*;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TracksPage extends PageTemplate implements MessageListener {

    public static final String WEB_PATH_UPLOAD_GPS = "upload";
    public static final String WEB_PATH_DELETE = "delete/";
    public static final String WEB_PATH_OSM = "osm/";
    //    public static final String WEB_PATH_INFO = "info/";
    public static final String WEB_PATH_SVG = "svg/";
    public static final String WEB_PATH_CHART = "chart/";
    //    public static final String WEB_PATH_RAW = "raw/";
//    public static final String WEB_PATH_HTML = "html/";
    public static final String WEB_PATH_HTML = "";

    private static final DecimalFormat doublePcaFormatter = new DecimalFormat("#.000");


    private static final String TD = HTML.Tag.TD.toString();


    private static final Logger log = LoggerFactory.getLogger(TracksPage.class);
    private final Object syncPage = new Object();

    private Document docMain;
    private Document docExport;
    private Document docExportFlat;
    private Document docEvaluate;
    private Document docEvaluateResult;


    //    private final OSMMap osm;

    public TracksPage(String filename, String pageName, String address) {
        super(filename, pageName, address);
    }

    @Override
    public void init() {
        docMain = getDocCopy();
//        docMain.getElementById("list").attr("style", "display:block");
        docMain.getElementById("list").attr("style", "display:inline-block");

//        docExport = getDocCopy();
//        docExport.getElementById("fu").attr("style", "display:none");
//        docExport.getElementById("fs").attr("style", "display:inline-block");
//        docExport.getElementById("page-title").text("Export tracks:");
//        docExport.getElementById("fs-request").attr("value", "export");
//
//        docExportFlat = docExport.clone();
//        docExportFlat.getElementById("page-title").text("Export tracks (flat):");
//        docExportFlat.getElementById("fs-request").attr("value", "export-flat");

        docEvaluate = getDocCopy();
        docEvaluate.getElementById("fu").attr("style", "display:none");
        docEvaluate.getElementById("fs").attr("style", "display:inline-block");
        docEvaluate.getElementById("page-title").text("Evaluate predictions for tracks:");
//        docEvaluate.getElementById("fs-request").attr("value", "evaluate");
//        docEvaluate.getElementById("fs-request").parent().appendChild(
//                docEvaluate.getElementById("fs-request").clone()
//                .attr("value", "list-modes"));

        docEvaluateResult = getDocCopy();
        docEvaluateResult.getElementById("list").attr("style", "display:inline-block");

        Configuration.INSTANCE.subscribe(InternalMessage.TRACKS_RELOAD, this);
//        Configuration.INSTANCE.subscribe(InternalMessage.TRACK_REMOVED, this);
    }

    public Element createForm(List<String[]> param, String[][] hiddens) {
        Element main = new Element("form");
        Element select = new Element("select")
                .attr("name", "param")
                .attr("size", String.valueOf(param.size()))
                .attr("multiple", true);
        main.appendChild(select);
        param.forEach(e -> select.appendChild(new Element("option").attr("value", e[0]).text(e[1])));
        for (String[] hidden : hiddens) {
            main.appendChild(new Element("input")
                .attr("type", "hidden")
                .attr("name", hidden[0])
                .attr("value", hidden[1]));
        }
//        main.appendChild(new Element("input")
//                .attr("type", "hidden")
//                .attr("name", "request")
//                .attr("value", "list-delete"));
//        main.appendChild(new Element("input")
//                .attr("type", "hidden")
//                .attr("name", "page")
//                .attr("value", getAddress()));
        return main;
    }


    @Override
    public void handle(MyHttpRequest myHttpRequest) {
        log.info("handle: {}", myHttpRequest.baseRequest);

        String r = myHttpRequest.baseRequest.getParameter(MyApiField.request.name());
        if (r == null) {
            MyWebUtils.simpleResponse(myHttpRequest, docMain.toString());
            return;
        }

        switch (r) {
                case "upload":
                    String text = null;

                    //fixme
                    try {
                        handleUploadGps(myHttpRequest);
//                        updateTracks();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        text = e.toString();
                    } catch (ServletException e) {
                        e.printStackTrace();
                        text = e.toString();
                    }
                    MyWebUtils.simpleResponse(myHttpRequest, text);
                    return;
//                    break;
                case "export-csv-full":
                try {
                    handleExportCSV(myHttpRequest, true);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                case "export-csv":
                try {
                    handleExportCSV(myHttpRequest, false);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                case "export":
                    try {
                        handleExport(myHttpRequest, false);
//                        updateTracks();
                        return;
                    } catch (Exception e) {
//                        e.printStackTrace();
//                        text = e.toString();
//                        MyWebUtils.simpleResponse(myHttpRequest, text);
//                        return;
                        throw new RuntimeException(e);
                    }
//                    MyWebUtils.sendRedirect(myHttpRequest, this.getPageUrlString());
//                    return;
//                    break;
                case "export-flat":
                    try {
                        handleExport(myHttpRequest, true);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                case "actionCompleteRedirect":
                    MyWebUtils.simpleResponse(myHttpRequest, docMain.toString());
                    return;
//                case "select-export":
//                    MyWebUtils.simpleResponse(myHttpRequest, docExport.toString());
//                    return;
//                case "select-export-flat":
//                    MyWebUtils.simpleResponse(myHttpRequest, docExportFlat.toString());
//                    return;
                case "select-evaluate":
                    MyWebUtils.simpleResponse(myHttpRequest, docEvaluate.toString());
                    return;
                case "evaluate":
                    handleEvaluate(myHttpRequest);
                    return;
                case "list-modes":
                    handleShowModes(myHttpRequest);
                    return;
//                    text = getPage();
//                    break;
//                case "list-load":
//                    if (listLoad(baseRequest, response)) {
//                        return;
//                    }
//                case "list-save":
//                    if (listSave(baseRequest, response)) {
//                        return;
//                    }
//                case "list-delete":
//                    if (listDelete(baseRequest, response)) {
//                        return;
//                    }
//                case "list-stat":
//                    if (listStatistics(baseRequest, response)) {
//                        return;
//                    }
//                default:
//                    return;
        }

        StringBuilder text;
//        if (text == null) {
            switch (r) {
//                case "upload":
//                    //fixme
//                    try {
//                        handleUploadGps(target, baseRequest, request, response);
//                        updateTracks();
//                        return;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        text = e.toString();
//                    } catch (ServletException e) {
//                        e.printStackTrace();
//                        text = e.toString();
//                    }
//                    break;
                case "delete": {
                    var t = RequestUtil.parseParamTracks(myHttpRequest);
//                    var t = MyHttpRequest.getRequestReader.
                    try {
                        DataHolder.INSTANCE.trackManager.removeTracks(t);
                        log.info("Deleted [{}] ", t);
                        MyWebUtils.sendRedirect(myHttpRequest, getUrl());
                        updateTracks();
                        return;
                    } catch (Exception e) {
                        log.warn("Cannot delete gps={}", t, e);
                    }
                }
//                break;
                case "osm": {
                    List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
                    GpsTrack t = RequestUtil.getOne(MyApiField.track.name(), tracks);
                    String mapString = myHttpRequest.baseRequest.getParameter(MyApiField.map.name());
                    int m = mapString == null ? 0 : Integer.parseInt(mapString);
                    text = new StringBuilder(DataHolder.INSTANCE.webData.osmMapTemplates.get(m).simpleTrack(m, t));
//                    text = DataHolder.INSTANCE.webData.osmMapTemplates.get(m).simpleTrackWithMode(m, t);
                    log.info("OSMed [{}] ", t.name);
                    break;
                }
                case "raw": {
                    List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
                    text = new StringBuilder(tracks.get(0).toTable(ExtensionManager.INSTANCE.getAllFields()));
                    break;
                }
                case "raw4": {
                    List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
//                    text = new StringBuilder(tracks.get(0).toTableNice(ExtensionManager.INSTANCE.getAllFields()));
                    text = new StringBuilder();
                    StringBuilder title = new StringBuilder("Predictions: ");
                    for (GpsTrack track : tracks) {
                        title.append(track.name);
                        text.append(track.toTableNice(ExtensionManager.INSTANCE.getAllFields()));
//                    text = new StringBuilder(tracks.get(0).toTableNice(fields));
                    }
                    text = new StringBuilder(MyWebUtils.wrapInHtml(title.toString(), text.toString()));
                    break;
                }
                case "predictions": {
                    List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
//                    Stream<Field<?>> s = Stream.empty();
//                    s = Stream.concat(s, )
                    ArrayList<Field<?>> fields = new ArrayList<>();
                    fields.addAll(ExtensionManager.INSTANCE.mainExtension.getFields());
                    fields.addAll(ExtensionManager.INSTANCE.osmExtension.getFields());
                    ExtensionManager.INSTANCE.getAllExtensions().stream()
                            .filter(e -> e instanceof PredictionExtension || e instanceof PostExtension)
                            .forEach(e -> fields.addAll(e.getFields()));
//                            .map(e -> e.getFields())
                    text = new StringBuilder();
                    StringBuilder title = new StringBuilder("Predictions: ");
                    for (GpsTrack track : tracks) {
                        title.append(track.name);
                        text.append(track.toTableNice(fields));
//                    text = new StringBuilder(tracks.get(0).toTableNice(fields));
                    }
                    text = new StringBuilder(MyWebUtils.wrapInHtml(title.toString(), text.toString()));
                    break;
                }
//                case "predictions":
//                    var fields = ExtensionManager.INSTANCE.getAllFields();
//                    fields = fields.stream()
//                            .filter(e -> e.extension == ExtensionManager.INSTANCE.mainExtension || e.type == FieldType.ENUM)
//                            .collect(Collectors.toList());
//                    text = t.toTable(fields);
//                    break;
                case "info": {
                    List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
                    GpsTrack t = RequestUtil.getOne(MyApiField.track.name(), tracks);
                    text = new StringBuilder(t.stat().replace("\n", "<br/>"));
                    log.info("OSMed [{}] ", t.name);
//                    text += "<br/><img src='/gps/svg/?track=" + q + "' style='width: 85vw; min-width: 330px;'/><br/>";
//                    text += "<br/><img src='/gps/chart/?track=" + q + "'/><br/>";
//                    text += "<br/><img src='/gps/raw/?track=" + q + "'/><br/>";
                    break;
                }
                case "arff": {
                    text = new StringBuilder("<pre>");
                    List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(myHttpRequest);
//                    List<Field> fields = RequestUtil.parseParamFields(myHttpRequest);
                    var fields = ExtensionManager.INSTANCE.getAllFields();
                    if (gpsTracks == null || gpsTracks.size() == 0) {
//                        JsonOut.fail(httpRequest, null);
//                        return;
                        text.append("error");
                    }
                    text.append(ArffUtil.trackToDataset(gpsTracks, fields).toString());
//                    JsonOut.success(httpRequest, ret);
//                    return;
//                    text += ArffUtil.writeFile(ArffUtil.trackToDataset(t));
                    log.info("arff [{}] ", gpsTracks);
                    text.append("</pre>");
                    break;
                }
//                case "csv":
//                    text = "<pre>";
//                    text += ArffUtil.writeCSV(t.points);
//                    log.info("csv [{}] ", t.name);
//                    text += "</pre>";
//                    break;
                default:
                    text = new StringBuilder(docMain.toString());
//                return;
            }
//        }
        MyWebUtils.simpleResponse(myHttpRequest, text.toString());
    }

    private void handleEvaluate(MyHttpRequest myHttpRequest) {
        List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
        if (tracks == null) {
            throw new IllegalArgumentException("Select tracks please");
        }
        final Element element = docEvaluateResult.getElementById("list");
        element.children().remove();
        var table = element.appendElement(HTML.Tag.TABLE.toString());
        var tr = table.appendElement(HTML.Tag.TR.toString());
        tr.appendElement(HTML.Tag.TD.toString()).text("id");
        tr.appendElement(HTML.Tag.TD.toString()).text("name");
        tr.appendElement(HTML.Tag.TD.toString()).text("modes");
//        var td = tr.appendElement(HTML.Tag.TD.toString()).text("mode");
        ArrayList<Field.ModeField> fieldList = new ArrayList<>();
        Map<Field<?>, List<FieldStatistics.TrackFieldStatistics>> statMap = new HashMap<>();
        for (Field<?> field : ExtensionManager.INSTANCE.getAllFields()) {
            if (field.type == FieldType.ENUM) {
                fieldList.add((Field.ModeField) field);
                tr.appendElement(HTML.Tag.TD.toString()).addClass("tds").text(field.getFullName());
            }
        }
        fieldList.forEach(e -> statMap.put(e, new ArrayList<>()));
        for (GpsTrack track : tracks) {
            tr = table.appendElement(HTML.Tag.TR.toString());
            tr.appendElement(HTML.Tag.TD.toString()).text(String.valueOf(track.id));
            tr.appendElement(HTML.Tag.TD.toString()).text(track.name);
            tr.appendElement(HTML.Tag.TD.toString()).text(String.valueOf(track.modes.entrySet()));
            for (Field<?> field : fieldList) {
                FieldStatistics.TrackFieldStatistics s = null;
                if (field.extension instanceof PredictionExtension) {
                    s = ((PredictionExtension) field.extension).fieldStatistics.results.get(track);
                    statMap.get(field).add(s);
                } else if (field.extension instanceof PostExtension) {
                    s = ((PostExtension) field.extension).fieldStatistics.results.get(track);
                    statMap.get(field).add(s);
                }
                tr.appendElement(HTML.Tag.TD.toString()).text(
                        s == null ? "?" : FieldStatistics.precentageString(s.correct, s.total));
            }
        }

        // balanced accuracy
        {
            List<HashMap<TravelMode, Double>> balancedMap = new ArrayList<>();
            fieldList.forEach(e -> balancedMap.add(new HashMap<>()));
            for (var m : TravelMode.meaningfull) {
                tr = table.appendElement(HTML.Tag.TR.toString());
                tr.appendElement(HTML.Tag.TD.toString());
                tr.appendElement(HTML.Tag.TD.toString()).text(m + " accuracy");
                tr.appendElement(HTML.Tag.TD.toString());
                int i = -1;
                for (Field<?> field : fieldList) {
                    i++;
                    var l = statMap.get(field);
                    if (l.size() == 0) {
                        tr.appendElement(HTML.Tag.TD.toString()).text("na");
                    } else {
                        long correct = l.parallelStream().mapToLong(e -> e.fromTo[m.ordinal()][m.ordinal()]).sum();
                        long total = l.parallelStream().mapToLong(e -> e.totalPerMode[m.ordinal()]).sum();
                        double v = total == 0 ? 0 : correct / (double) total;
                        balancedMap.get(i).put(m, v);
                        tr.appendElement(HTML.Tag.TD.toString()).text(
                                FieldStatistics.precentageString(correct, total));
                    }
                }
            }

            tr = table.appendElement(HTML.Tag.TR.toString());
            tr.appendElement(HTML.Tag.TD.toString());
            tr.appendElement(HTML.Tag.TD.toString()).text("Balanced accuracy");
            tr.appendElement(HTML.Tag.TD.toString());
            int i = -1;
            for (Field<?> field : fieldList) {
                i++;
                var l = statMap.get(field);
                if (l.size() == 0) {
                    tr.appendElement(HTML.Tag.TD.toString()).text("na");
                } else {
                    var bm = balancedMap.get(i);
                    var d = bm.values().parallelStream().mapToDouble(e -> e).summaryStatistics().getAverage();
                    tr.appendElement(HTML.Tag.TD.toString()).text(
                            FieldStatistics.precentageString(d));
                }
            }
        }

        // accuracy
        {
            tr = table.appendElement(HTML.Tag.TR.toString());
            tr.appendElement(HTML.Tag.TD.toString());
            tr.appendElement(HTML.Tag.TD.toString()).text("Accuracy");
            tr.appendElement(HTML.Tag.TD.toString());
            for (Field<?> field : fieldList) {
                var l = statMap.get(field);
                if (l.size() == 0) {
                    tr.appendElement(HTML.Tag.TD.toString()).text("na");
                } else {
                    long correct = l.parallelStream().mapToLong(e -> e.correct).sum();
                    long total = l.parallelStream().mapToLong(e -> e.total).sum();
                    tr.appendElement(HTML.Tag.TD.toString()).text(
                            FieldStatistics.precentageString(correct, total));
                }
            }
        }

        element.appendElement(HTML.Tag.BR.toString());
        try {
            String pcaString = TrackUtil.modelCorrelation(tracks);
            table = element.appendElement(HTML.Tag.TABLE.toString());
            Iterator<String> i = pcaString.lines().iterator();
            while (i.hasNext()) {
                String[] f = i.next().split(",");
                if (f.length < 4) {
                    continue;
                }
                tr = table.appendElement(HTML.Tag.TR.toString());
                for (String s : f) {
                    try {
                        double d = Double.parseDouble(s);
//                        int r = (int) (255 * Math.max(d, 0.5));
                        int r = Math.max((int) ((d) * 255), 0);
                        tr.appendElement(HTML.Tag.TD.toString())
                                .attr("style",
                                        String.format("background-color: rgba( %d, %d, 88, 1);", 255 - r, 127 + r / 2))
                                .text(doublePcaFormatter.format(d));
                    } catch (Exception ignore) {
                        tr.appendElement(HTML.Tag.TD.toString()).text(s);
                    }
                }
            }
        } catch (Exception e) {
            element.appendElement(HTML.Tag.P.toString())
                    .attr("","")
                    .text(e.getMessage());
            log.error("failed pca stat", e);
        }

        // tracks modes
        element.appendElement(HTML.Tag.BR.toString());
        Map<TravelMode, Long> x = new HashMap<>();
//        for (TravelMode value : TravelMode.values()) {
//            x.put(value, 0L);
//        }
        for (GpsTrack track : tracks) {
            for (Map.Entry<TravelMode, Long> entry : track.modes.entrySet()) {
                x.compute(entry.getKey(), (k, v) -> (v == null) ? entry.getValue() : v + entry.getValue());
            }
        }
        element.appendElement(HTML.Tag.P.toString()).text(x.toString());

        MyWebUtils.simpleResponse(myHttpRequest, docEvaluateResult.toString());
    }

    private void handleShowModes(MyHttpRequest myHttpRequest) {
        List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
        if (tracks == null) {
            throw new IllegalArgumentException("Select tracks please");
        }
//        StringBuilder
        long[][] sum = { {0,0,0,0,0}, {0,0,0,0,0} };//new long[][]
        StringBuilder sb = new StringBuilder("<pre>track,BICYCLE,BUS,CAR,TRAIN,WALK\n");
        for (GpsTrack track : tracks) {
            sb.append(track.name).append(',')
                    .append(sum[1][0] = track.modes.getOrDefault(TravelMode.BICYCLE, 0L)).append(',')
                    .append(sum[1][1] = track.modes.getOrDefault(TravelMode.BUS, 0L)).append(',')
                    .append(sum[1][2] = track.modes.getOrDefault(TravelMode.CAR, 0L)).append(',')
                    .append(sum[1][3] = track.modes.getOrDefault(TravelMode.TRAIN, 0L)).append(',')
                    .append(sum[1][4] = track.modes.getOrDefault(TravelMode.WALK, 0L)).append('\n');
            for (int i = 0; i < 5; i++) {
                sum[0][i] += sum[1][i];
            }
        }
        sb.append("sum").append(',')
                .append(sum[0][0]).append(',')
                .append(sum[0][1]).append(',')
                .append(sum[0][2]).append(',')
                .append(sum[0][3]).append(',')
                .append(sum[0][4]).append('\n');
        sb.append("</pre>");
        MyWebUtils.simpleResponse(myHttpRequest, sb.toString());
    }

    private boolean listDelete(Request baseRequest, HttpServletResponse response) {
        String[] param = baseRequest.getParameterValues("param");
        if (param != null && param.length > 0) {
            DataHolder.INSTANCE.trackManager.removeTracks(
                    Stream.of(param)
                            .map(Integer::parseInt)
                            .map(DataHolder.INSTANCE.trackManager::getTrack).collect(Collectors.toList()));
//                        for (String s : param) {
//                            DataHolder.INSTANCE.trackManager.removeTracks(DataHolder.INSTANCE.trackManager.getTrack(Integer.parseInt(s)));
//                        }
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            MyWebUtils.sendRedirect(response, getUrl());// + "&request=list-delete");
            baseRequest.setHandled(true);
            return true;
        }
//                    text = getPage();
        Document d = getDoc().clone();
        Element main = d.getElementById("main");
        main.children().remove();
        main.appendChild(new Element("p").appendText("Delete tracks:"));
        main.appendChild(main = createForm(
                DataHolder.INSTANCE.trackManager.tracks.stream()
                        .map(e -> new String[]{String.valueOf(e.hashCode()), e.name})
                        .collect(Collectors.toList()),
                new String[][]{{"request", "list-delete"}, {"page", getPageName()}, }

        ));
        main.attr("action", "/");
        main.appendChild(new Element("input")
                .attr("type", "submit")
                .attr("value", "Delete")
        );

        MyWebUtils.simpleResponse(baseRequest, response, d.toString());
        baseRequest.setHandled(true);
        return true;
    }

//    private boolean listStatistics(Request baseRequest, HttpServletResponse response) {
//        String[] param = baseRequest.getParameterValues("param");
//        if (param != null && param.length > 0) {
//            DataHolder.INSTANCE.webData.basicPage.publish(response,
//            BasicPage.trackStatistics(Stream.of(param)
//                    .map(Integer::parseInt)
//                    .map(DataHolder.INSTANCE.trackManager::getTrack).collect(Collectors.toList())));
//            response.setStatus(HttpServletResponse.SC_OK);
////            MyWebUtils.sendRedirect(response, getUrl());// + "&request=list-delete");
//            baseRequest.setHandled(true);
//            return true;
//        }
////                    text = getPage();
//        Document d = getDoc().clone();
//        Element main = d.getElementById("main");
//        main.children().remove();
//        main.appendChild(new Element("p").appendText("Statistics for tracks:"));
//        main.appendChild(main = createForm(
//                DataHolder.INSTANCE.trackManager.tracks.stream()
//                        .map(e -> new String[]{String.valueOf(e.hashCode()), e.name})
//                        .collect(Collectors.toList()),
//                new String[][]{{"request", "list-stat"}, {"page", getAddress()}, }
//
//        ));
//        main.attr("action", "/");
//        main.appendChild(new Element("input")
//                .attr("type", "submit")
//                .attr("value", "Submit")
//        );
//
//        MyWebUtils.simpleResponse(baseRequest, response, d.toString());
//        baseRequest.setHandled(true);
//        return true;
//    }

//    private boolean listSave(Request baseRequest, HttpServletResponse response) {
//        String[] param = baseRequest.getParameterValues("param");
//        if (param != null && param.length > 0) {
//            DataHolder.INSTANCE.trackManager.saveTracks(param);
//            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
//            MyWebUtils.sendRedirect(response, getUrl());// + "&request=list-delete");
//            baseRequest.setHandled(true);
//            return true;
//        }
////                    text = getPage();
//        Document d = getDoc().clone();
//        Element main = d.getElementById("main");
//        main.children().remove();
//        main.appendChild(new Element("p").appendText("Save tracks:"));
//        main.appendChild(main = createForm(
//                DataHolder.INSTANCE.trackManager.tracks.stream()
//                        .map(e -> new String[]{String.valueOf(e.hashCode()), e.name})
//                        .collect(Collectors.toList()),
//                new String[][]{{"request", "list-save"}, {"page", getAddress()}, }
//
//        ));
//        main.attr("action", "/");
//        main.appendChild(new Element("input")
//                .attr("type", "submit")
//                .attr("value", "Save")
//        );
//
//        MyWebUtils.simpleResponse(baseRequest, response, d.toString());
//        baseRequest.setHandled(true);
//        return true;
//    }
//
//    private boolean listLoad(Request baseRequest, HttpServletResponse response) {
//        String[] param = baseRequest.getParameterValues("param");
//        if (param != null && param.length > 0) {
//            DataHolder.INSTANCE.trackManager.loadTracks(param);
//            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
//            MyWebUtils.sendRedirect(response, getUrl());// + "&request=list-delete");
//            baseRequest.setHandled(true);
//            return true;
//        }
////                    text = getPage();
//        Document d = getDoc().clone();
//        Element main = d.getElementById("main");
//        main.children().remove();
//        main.appendChild(new Element("p").appendText("Load tracks:"));
//        main.appendChild(main = createForm(
//                DataHolder.INSTANCE.trackManager.getSavedTracks().stream()
//                        .map(e -> new String[]{e, e})
//                        .collect(Collectors.toList()),
//                new String[][]{{"request", "list-load"}, {"page", getAddress()}, }
//
//        ));
//        main.attr("action", "/");
//        main.appendChild(new Element("input")
//                .attr("type", "submit")
//                .attr("value", "Load")
//        );
//
//        MyWebUtils.simpleResponse(baseRequest, response, d.toString());
//        baseRequest.setHandled(true);
//        return true;
//    }

//    private String getPage() {
//        return doc.toString();
//    }



    private void handleExport(MyHttpRequest myHttpRequest, boolean flat) throws Exception {
//        List<Field> fields = RequestUtil.parseParamFields(httpRequest);
        log.info("handleExport");
        List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
        Path path = Paths.get("/dev/shm/1/saves/");
        Files.createDirectories(path);
//        List<Field> fields = ExtensionManager.INSTANCE.getAllFields();
        for (GpsTrack track : tracks) {
            try {
                Path p2 = path.resolve(track.name + ".json");
                if (flat) {
                    Files.writeString(p2,
                            MyJsonParser.trackToJsonFlat(track, ExtensionManager.INSTANCE.getAllFields()).toString(2));
                } else {
                    Files.writeString(p2,
                            MyJsonParser.trackToJson(track, ExtensionManager.INSTANCE.getCoreExtensions()).toString(2));
                }
            } catch (IOException e) {
                myHttpRequest.errors.add(e.getMessage());
                e.printStackTrace();
//                throw new RuntimeException(e);
            }
        }
//        MyWebUtils.simpleResponse(myHttpRequest);
//        myHttpRequest.response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        if (myHttpRequest.errors.size() > 0) {
            MyWebUtils.writeResponse(myHttpRequest.response, String.valueOf(myHttpRequest.errors));
        }
        MyWebUtils.sendRedirect(myHttpRequest, getUrl() + "&request=actionCompleteRedirect");
    }

    private void handleExportCSV(MyHttpRequest myHttpRequest, boolean full) throws Exception {
        log.info("handleExportCSV [{}]", full ? "full" : "basic");
        List<GpsTrack> tracks = RequestUtil.parseParamTracks(myHttpRequest);
        final String outPath;
        final List<Field<?>> fields;
        if (full) {
            outPath = "/dev/shm/1/saves/csv-full/";
            fields = ExtensionManager.INSTANCE.getAllFields();
        } else {
            outPath = "/dev/shm/1/saves/csv-basic/";
            fields = ExtensionManager.INSTANCE.mainExtension.getFields();
        }
        Path path = Paths.get(outPath);
        Files.createDirectories(path);
        for (GpsTrack track : tracks) {
            var s = CSVUtil.trackToCSV(track, fields);
            Path p2 = path.resolve(track.name + ".csv");
            Files.writeString(p2, s);
        }
        if (myHttpRequest.errors.size() > 0) {
            MyWebUtils.writeResponse(myHttpRequest.response, String.valueOf(myHttpRequest.errors));
        }
        MyWebUtils.sendRedirect(myHttpRequest, getUrl() + "&request=actionCompleteRedirect");
    }

    private void handleUploadGps(MyHttpRequest myHttpRequest)
            throws IOException, ServletException {

        List<RequestUtil.TrackData> trackDataList = RequestUtil.loadTracks(myHttpRequest);
        if (trackDataList == null) {
            myHttpRequest.errors.add("no valid files");
            MyWebUtils.simpleResponse(myHttpRequest);
//            JsonOut.fail(httpRequest, "no valid files");
            return;
        }
        List<GpsTrack> gpsTrackList = RequestUtil.parseTracks(trackDataList);
//                RequestUtil.preprocessTracks(gpsTrackList);
//        List<GpsTrack> addedTracks = RequestUtil.addGoodTracks(gpsTrackList, myHttpRequest);
//        JsonOut.success(httpRequest, addedTracks.size());

        myHttpRequest.response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        if (myHttpRequest.errors.size() > 0) {
            MyWebUtils.writeResponse(myHttpRequest.response, String.valueOf(myHttpRequest.errors));
        }
//        myHttpRequest.response.
//        myHttpRequest.response.sendRedirect(getUrl() + "&request=actionCompleteRedirect");
        MyWebUtils.sendRedirect(myHttpRequest, getUrl() + "&request=actionCompleteRedirect");

        return;




//        if (myHttpRequest.baseRequest.getContentType() != null && myHttpRequest.baseRequest.getContentType().startsWith("multipart/form-data")) {
//            myHttpRequest.baseRequest.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, JettyMain.MULTI_PART_CONFIG);
////            baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
//            log.info("files={}", myHttpRequest.baseRequest.getPart("files"));
//
////            Collection<Part> parts = request.getParts();
//            ExecutorService es = Executors.newCachedThreadPool();
//            List<GpsTrack> tracksToAdd = new ArrayList<>();
//
//            for (Part part : myHttpRequest.baseRequest.getParts()) {
//                log.info("Part {}-{}-{} -- {}", part.getName(), part.getSubmittedFileName(), part.getContentType(), part);
//                String filename = part.getSubmittedFileName();
//
//                if (part.getContentType().startsWith("application/json")) {
////                    try {
//                        List<GpsTrack> tracks = GpsTrack.fromJson(MyWebUtils.toString(MyWebUtils.readPart(part)));
//                        for (GpsTrack track : tracks) {
//                            track.name = filename + "-" + track.name;
//                            tracksToAdd.add(track);
//                            es.submit(() -> {
//                                try {
//                                    DataHolder.INSTANCE.preprocessUtilOriginal.updatePoints(track.points);
//                                    log.info("handleUploadGps: processed {}", track.name);
//                                } catch (Exception e) {
//                                    tracksToAdd.remove(track);
//                                    log.warn("handleUploadGps: process failed {}", track.name, e);
//                                }
//                            });
//                        }
////                        int track = GpsTrack.parsePoint(part.getInputStream());
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                        throw new RuntimeException(e);
////                    }
//
//                } else if (part.getContentType().startsWith("application/gpx+xml")) {
//                    GpsTrack track;
//                    try {
//                        track = GpsTrack.fromOSMT(MyWebUtils.toString(MyWebUtils.readPart(part)));
//                    } catch (JDOMException e) {
//                        e.printStackTrace();
//                        throw new RuntimeException(e);
//                    }
//                    track.name = filename;
//                    tracksToAdd.add(track);
//                    es.submit(() -> {
//                        try {
//                            DataHolder.INSTANCE.preprocessUtilOriginal.updatePoints(track.points);
//                            log.info("handleUploadGps: processed {}", track.name);
//                        } catch (Exception e) {
//                            tracksToAdd.remove(track);
//                            log.warn("handleUploadGps: process failed {}", track.name, e);
//                        }
//                    });
////                    TrackUtil.updatePoints(track.points);
////                    log.info("handleUploadGps: processed {}", track.name);
////                    this.tracks.add(track);
//
//                } else {
//                    log.warn("{}", part.getContentType());
//                }
//            }
////            log.info("files={}", request.getPart("files"));
//            es.shutdown();
//            try {
//                es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                log.error("Processing tracks interrupted", e);
//            }
//            DataHolder.INSTANCE.trackManager.addTracks(tracksToAdd);
//            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
//            response.sendRedirect(getUrl() + "&request=actionCompleteRedirect");
////            updateTracks();
//            try {
//                TimeUnit.MILLISECONDS.sleep(100);
//            } catch (InterruptedException ignore) {
//
//            }
//        }
    }

    private void updateTracks() {
        synchronized (syncPage) {
//            PageUtil.updateTracks(docExport, "fs-track");
//            PageUtil.updateTracks(docExportFlat, "fs-track");
            PageUtil.updateTracks(docEvaluate, "fs-track");

            Element element = docMain.getElementById("gps-list");
            element.children().remove();
            Element  copy = element.clone();
    //      Element element = new Element("div")

    //        appendTrackInfo(element);
            appendTrackInfoTable(copy);
            element.replaceWith(copy);
        }
    }



    public void appendTrackInfoTable(Element element) {
        element.children().forEach(Node::remove);
        Element tmp = new Element(HTML.Tag.TABLE.toString());
        element.appendChild(tmp);
        element = tmp;

        int i = 0;
        for (GpsTrack t : DataHolder.INSTANCE.trackManager.tracks) {
            Element child = new Element(HTML.Tag.TR.toString());
            element.appendChild(child);

            tmp = new Element(TD);
            tmp.appendText(String.valueOf(i));
            child.appendChild(tmp);

            MyWebUtils.trackInfoTDs(t).forEach(child::appendChild);

//            child.appendChild(new Element(TD).appendText(t.name));
//
//            child.appendChild(new Element(TD).appendText(" [" + t.points.size() + "] "));
//
//            child.appendChild(new Element(TD).appendText(" [" +
//                    t.points.parallelStream().map(e -> e.mode).distinct().map(Enum::toString).collect(Collectors.joining(",")) +
//                    "] "));
//
//            for (String code : new String[]{
////                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_DELETE + "?track=" + i +"'>delete</a>] ",
////                    " [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_OSM + "?map=2&track=" + i +"'>osm</a>] ",
////                    String.format(" [<a href='%s%s?track=%s&request=info'>info</a>] ", JettyMain.WEB_PATH_GPS, WEB_PATH_HTML, i),
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=delete'>delete</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=osm&map=0'>osm</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=info'>info</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=raw'>raw</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=predictions'>predictions</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=arff'>arff</a>] ",
//                    " [<a href='"+ getUrl() + "&track=" + i +"&request=csv'>csv</a>] ",
//            }) {
//                child.appendChild(new Element(TD).append(code));
//            }

//            child.appendChild(new Element(TD).append());
//            child.appendChild(new Element(TD).append());

//            child.appendChild(new Element(TD).append(String.format(" [<a href='%s%s?track=%s&request=info'>info</a>] ", JettyMain.WEB_PATH_GPS, WEB_PATH_HTML, i)));
//            child.appendChild(new Element(TD).append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"&request=info'>info</a>] "));
//            child.appendChild(new Element(TD).append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"&request=raw'>raw</a>] "));
//            child.appendChild(new Element(TD).append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_HTML + "?track=" + i +"&request=arff'>arff</a>] "));

//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_SVG + "?track=" + i +"'>info</a>] ");
//            child.append(" [<a href='"+ JettyMain.WEB_PATH_GPS + WEB_PATH_CHART + "?track=" + i +"'>info</a>] ");
//            element.appendChild(child);
            i++;
        }

    }

    @Override
    public void onMessage(InternalMessage message, Collection<String> data) {
        switch (message) {
            case TRACKS_RELOAD:
//            case TRACK_REMOVED:
                updateTracks();
                return;
            default:
                throw new RuntimeException("fixme" + message);
        }
    }

//    public List<GpsTrack> getTracks() {
//        return new ArrayList<>(this.tracks);
//    }

//    public void addTracks(Collection<GpsTrack> tracks) {
//        this.tracks.addAll(tracks);
//        updateTracks();
//    }
//
//    public void removeTracks(Collection<GpsTrack> tracks) {
//        this.tracks.removeAll(tracks);
//        updateTracks();
//    }
}
