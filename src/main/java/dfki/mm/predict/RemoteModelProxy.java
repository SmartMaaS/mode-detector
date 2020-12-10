package dfki.mm.predict;

import dfki.mm.Configuration;
import dfki.mm.TravelMode;
import dfki.mm.tracks.Field;
import dfki.mm.util.HttpJettyUtil;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public enum RemoteModelProxy {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(RemoteModelProxy.class);



    public static final String PARAM_INPUT_DIMENSIONS = "INPUT_DIMENSIONS";
    public static final String PARAM_HIDDEN_DIM = "HIDDEN_DIM";
    public static final String PARAM_DEPTH = "DEPTH";
    public static final String PARAM_FIELDS = "fields";
    public static final String PARAM_MODEL = "model";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_FILENAME = "filename";

    HttpClient httpClient;

    RemoteModelProxy() {
        // https://www.eclipse.org/jetty/documentation/current/http-client.html
        // Instantiate HttpClient

        httpClient = new HttpClient();

        // Configure HttpClient, for example:
        httpClient.setFollowRedirects(false);

        // Start HttpClient
        try {
            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * https://www.eclipse.org/jetty/documentation/current/http-client-api.html
     */
    private String request(String url) throws InterruptedException, ExecutionException, TimeoutException {
        log.info("request [{}]", url);

        ContentResponse response = httpClient.newRequest(url)
                .method(HttpMethod.POST)
                .agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0")
                .send();
        int status = response.getStatus();
        String ret = response.getContentAsString();
        if (status != 200) {
//            System.out.println(response.);
            System.out.println(ret);
        }
        System.out.println(status);

        return ret;
    }

    private String request(String url, String model, ContentProvider content) throws InterruptedException, ExecutionException, TimeoutException {
        log.info("request [{}] model={}", url, model);

//        MultiPartContentProvider multiPart = new MultiPartContentProvider();
////        multiPart.addFieldPart("model", new StringContentProvider("default"), null);
////        multiPart.addFilePart("icon", "img.png", new PathContentProvider(Paths.get("/tmp/img.png")), null);
////        multiPart.addFilePart("data", "filename", new StringContentProvider("text/csv", file, Charset.defaultCharset()), null);
//        int i = 0;
//        for (String f : files) {
//            multiPart.addFilePart("file[]", "filename-" + i++,
//                    new StringContentProvider("text/csv", f, Charset.defaultCharset()), null);
//        }
//        multiPart.close();

//        ContentResponse response = client.newRequest("localhost", connector.getLocalPort())
//                .method(HttpMethod.POST)
//                .content(multiPart)
//                .send();


        ContentResponse response = httpClient.newRequest(url)
                .method(HttpMethod.POST)
                .agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0")
//                .content(new StringContentProvider(file), "text/csv")
//                .attribute("model2", model)
                .param("model", model)
                .content(content)
                .send();
        int status = response.getStatus();
        String ret = response.getContentAsString();
        if (status != 200) {
            log.warn("Request failed [{}] {}", status, ret);
        }
        log.info("request [{}] model={} status={} response={}", url, model, status, ret.length());

        return ret;
    }

//    public List<PredictionModel> loadModels() throws Exception {
//        log.info("loadModels");
//        String stringList = request(Configuration.INSTANCE.URL_LIST);
//        List<PredictionModel> ret = new ArrayList<>();
//
//        try {
//            JSONArray list = new JSONArray(stringList);
//            for (int i = 0; i < list.length(); i++) {
//    //            Set<Field> fields = new TreeSet<>();
//                List<Field> fields = new ArrayList<>();
//                String name;
//                String info;
//                JSONObject obj = list.getJSONObject(i);
//    //            JSONArray
//
//                JSONArray ja = obj.getJSONArray(PARAM_FIELDS);
//                for (int j = 0; j < ja.length(); j++) {
//                    fields.add(Field.valueOf(ja.getString(j)));
//                }
//                name = obj.getString(PARAM_NAME);
//                info = obj.getString("info");
//    //            ret.add(PredictionModel.newPredictionModel(name, fields, ModelMethod.REMOTE));
//                RemotePredictionModel remote = new RemotePredictionModel(name, fields, ModelMethod.REMOTE);
//                remote.setInfo(info);
//                ret.add(remote);
//            }
//
//    //        for (Object x : list) {
//    //
//    //        }
//            return ret;
//        } catch (RuntimeException e) {
//            log.error("error while parsing response: {}", stringList, e);
//            throw e;
//        }
//    }




    @Deprecated
    List<List<TravelMode>> predict(String modelname, Collection<String> data) throws InterruptedException, ExecutionException, TimeoutException {


        MultiPartContentProvider multiPart = HttpJettyUtil.createMultiPart(data, null);
        String resultString = request(Configuration.INSTANCE.URL_PREDICT, modelname, multiPart);
        try {
            List<List<TravelMode>> ret = new ArrayList<>();
            JSONArray trackArray = new JSONArray(resultString);
            for (int t = 0; t < trackArray.length(); t++) {
                List<TravelMode> points = new ArrayList<>();
                ret.add(points);
                JSONArray pointArray = trackArray.getJSONArray(t);
                for (int p = 0; p < pointArray.length(); p++) {
                    points.add(TravelMode.fromOrdinal(pointArray.getInt(p)));
                }
            }
            return ret;
        } catch (RuntimeException e) {
            log.error("error while parsing response: {}", resultString, e);
            throw e;
        }
    }

    List<TravelMode> predict(String modelname, String data) throws InterruptedException, ExecutionException, TimeoutException {

        MultiPartContentProvider multiPart = HttpJettyUtil.createMultiPart(Collections.singleton(data), null);
        String resultString = request(Configuration.INSTANCE.URL_PREDICT, modelname, multiPart);
        try {
            List<TravelMode> ret = new ArrayList<>();
            JSONArray pointArray = new JSONArray(resultString);
                for (int p = 0; p < pointArray.length(); p++) {
                    ret.add(TravelMode.fromOrdinal(pointArray.getInt(p)));
            }
            return ret;
        } catch (RuntimeException e) {
            log.error("error while parsing response: {}", resultString, e);
            throw e;
        }
    }

    void train(String modelname, Collection<String> data, List<String> filenames) throws InterruptedException, ExecutionException, TimeoutException {

        MultiPartContentProvider multiPart = HttpJettyUtil.createMultiPart(data, filenames);

        String s = request(Configuration.INSTANCE.URL_TRAIN, modelname, multiPart);
        System.out.println(s);
//        List<TravelMode> ret = new ArrayList<>();
//        JSONArray ja = new JSONArray(s);
//        for (int j = 0; j < ja.length(); j++) {
//            ret.add(TravelMode.fromOrdinal(ja.getInt(j)));
//        }
//        return ret;
    }
}
