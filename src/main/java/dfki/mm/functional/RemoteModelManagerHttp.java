package dfki.mm.functional;

import dfki.mm.Configuration;
import dfki.mm.DataHolder;
import dfki.mm.TravelMode;
import dfki.mm.predict.ModelMethod;
import dfki.mm.predict.PredictionModel;
import dfki.mm.predict.RemoteModelProxy;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.MyJsonParser;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.tracks.extension.PredictionExtension;
import dfki.mm.util.HttpJettyUtil;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo? http util class for all request nonsesnse
 */
public enum RemoteModelManagerHttp {
    I;

    private static final Logger log = LoggerFactory.getLogger(RemoteModelManagerHttp.class);
    private HttpClient httpClient;

    RemoteModelManagerHttp() {
        httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
//            e.printStackTrace();
        }
    }

    /**
     * ignores the same name ones
     */
    public void reloadModels() throws InterruptedException, ExecutionException, TimeoutException {
        List<String> namesInUse = new ArrayList<>();
        for (Extension extension : ExtensionManager.INSTANCE.getAllExtensions()) {
            namesInUse.add(extension.getName());
        }
        String result = request(Configuration.INSTANCE.URL_LIST);
        JSONArray ja = new JSONArray(result);
//        JSONObject jo = new JSONObject(result);
        List<RemoteModel2> models = new ArrayList<>();
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            String name = jo.getString("name");
            String info = jo.getString("info");
            if (namesInUse.contains(name)) {
//                log.info("reloadModels: {} already present, removing and adding again.", name);
//                log.info("reloadModels: {} already present, ignoring.", name);
                log.info("reloadModels: {} already present, recalculate.", name);
                for (GpsTrack t : DataHolder.INSTANCE.trackManager.tracks) {
                    ExtensionManager.INSTANCE.extensionByName(name).compute(t);
                }
                // fixme make nicer
//                for (Extension extension : ExtensionManager.INSTANCE.getAllExtensions()) {
//                    if (name.equals(extension.getName())) {
//                        ExtensionManager.INSTANCE.extension.id
//                    }
//                }
                continue;
            }
            JSONArray jfa = jo.getJSONArray("fields");
            List<Field<?>> fields = new ArrayList<>();
            for (Object o1 : jfa) {
                Field<?> f = ExtensionManager.INSTANCE.fieldByName((String) o1);
                if (f == null) {
                    log.warn("reloadModels: Cannot find field '{}', skipping the model '{}'", o1, name);
                    continue;
                }
                fields.add(f);
            }
            models.add(new RemoteModel2(fields, ModelMethod.REMOTE, name, info));
        }
        for (RemoteModel2 model : models) {
            PredictionExtension predictionExtension = PredictionExtension.newPredictionExtension(model);
            ExtensionManager.INSTANCE.addExtension(predictionExtension);
        }
    }

    private String request(String url) throws InterruptedException, ExecutionException, TimeoutException {
        return request(url, null, null);
    }

    private String request(String url, Map<String,String> params, ContentProvider content) throws InterruptedException, ExecutionException, TimeoutException {
        if (params == null) {
            params = Collections.emptyMap();
        }
        log.info("request: [{}] params={}", url, params.entrySet());

        Request request = httpClient.newRequest(url)
                .method(HttpMethod.POST)
                .agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0")
//                .content(new StringContentProvider(file), "text/csv")
                .content(content);

        for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
            request.param(stringStringEntry.getKey(), stringStringEntry.getValue());
        }

        ContentResponse response = request.send();
        int status = response.getStatus();
        String ret = response.getContentAsString();
        if (status != 200) {
            log.warn("Request failed [{}] {}", status, ret);
        }
        log.info("request [{}] model={} status={} response={}", url, params.entrySet(), status, ret.length());

        return ret;
    }

    private List<TravelMode> predict(String modelname, String data) throws InterruptedException, ExecutionException, TimeoutException {

        MultiPartContentProvider multiPart = HttpJettyUtil.createMultiPart(Collections.singleton(data), null);
        String resultString = request(Configuration.INSTANCE.URL_PREDICT, Map.of("model", modelname), multiPart);
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

    private final AtomicInteger counter = new AtomicInteger();

    private class RemoteModel2 extends PredictionModel {

        String name;
        String info;

        public RemoteModel2(Collection<Field<?>> fields, ModelMethod method, String name, String info) {
            super(fields, method, "remote-" + counter.getAndIncrement() + "-" + name);
            this.name = name;
            this.info = info;
        }

        @Override
        public List<List<TravelMode>> predict(List<GpsTrack> tracks, Field<TravelMode> fieldPredict) throws Exception {
            List<List<TravelMode>> ret = new ArrayList<>();
            List<Field<?>> fieldsList = new ArrayList<>(fields);
            for (GpsTrack track : tracks) {
                List<TravelMode> r = RemoteModelManagerHttp.this.predict(
                        this.name,
                        MyJsonParser.trackToJsonFlat(track, fieldsList).toString()
                );
                ret.add(r);
            }
            return ret;
        }

        @Override
        public void train(List<GpsTrack> tracks, Field<TravelMode> fieldTrue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void train(List<GpsTrack> tracks, List<GpsTrack> validationTracks, Field<TravelMode> fieldTrue, int seeds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getInfo() {
            return info;
        }
    }
}
