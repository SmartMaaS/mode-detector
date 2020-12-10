package dfki.mm.request;

import dfki.mm.DataHolder;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.functional.RemoteModelManagerHttp;
import dfki.mm.predict.ModelMethod;
//import dfki.mm.predict.PostProcessor;
//import dfki.mm.predict.Prediction;
import dfki.mm.predict.PredictionModel;
import dfki.mm.tracks.*;
import dfki.mm.wui.JettyMain;
import dfki.mm.wui.MyWebUtils;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PostRequest {

//    public static final String TRACK = "track";
//    public static final String TRACK = "track";

    public MyApiRequest myApiRequest;
    MyHttpRequest httpRequest;
//    List<String> errors = new ArrayList<>();

    private PostRequest(MyApiRequest myApiRequest, MyHttpRequest myHttpRequest) {
        this.myApiRequest = myApiRequest;
        this.httpRequest = myHttpRequest;
    }

    public static PostRequest fromRequest(MyHttpRequest myHttpRequest) {
//        String r = myHttpRequest.baseRequest.getParameter(MyApiField.request.name());
        if (myHttpRequest.baseRequest.getContentType() != null && myHttpRequest.baseRequest.getContentType().startsWith("multipart/form-data")) {
            myHttpRequest.baseRequest.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, JettyMain.MULTI_PART_CONFIG);
        }
        String r = MyHttpRequest.postRequestReader.readString(myHttpRequest, MyApiField.request.name());
        if (r == null) {
            throw new IllegalArgumentException(MyApiField.request.name() + " not found");
        }
        MyApiRequest myApiRequest = MyApiRequest.valueOf(r.toUpperCase());
        return new PostRequest(myApiRequest, myHttpRequest);
    }


    public void process() {
        switch (myApiRequest) {
            case MAP_TRANSPORT: {
                double lat = MyHttpRequest.postRequestReader.readNumber(httpRequest, MyApiField.lat.name()).doubleValue();
                double lon = MyHttpRequest.postRequestReader.readNumber(httpRequest, MyApiField.lon.name()).doubleValue();
                JsonOut.success(httpRequest, RequestUtil.position(lat, lon, 5));
                return;
            }
            case TRACKS_DATA: {
                JSONObject ret = new JSONObject();
                List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);
                List<Field<?>> fields = RequestUtil.parseParamFields(httpRequest);
                if (gpsTracks == null || gpsTracks.size() == 0) {
                    JsonOut.fail(httpRequest, null);
                    return;
                }
                for (GpsTrack t : gpsTracks) {
                    if (fields == null) {
                        ret.put(String.valueOf(t.id), MyJsonParser.trackToJson(t, ExtensionManager.INSTANCE.getAllExtensions()));
                    } else {
                        ret.put(String.valueOf(t.id), MyJsonParser.trackToJsonFlat(t, fields));
                    }
//                    ret.put(String.valueOf(t.hashCode()), RequestUtil.trackData(t, fields));
                }
                JsonOut.success(httpRequest, ret);
                return;
            }
            case TRACK_ARFF: {
                List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);
                List<Field<?>> fields = RequestUtil.parseParamFields(httpRequest);
                if (gpsTracks == null || gpsTracks.size() == 0) {
                    JsonOut.fail(httpRequest, null);
                    return;
                }
                String ret = ArffUtil.trackToDataset(gpsTracks, fields).toString();
                JsonOut.success(httpRequest, ret);
                return;
            }
            case TRACKS_LIST: {
                JSONArray ret = new JSONArray();
                for (GpsTrack t : DataHolder.INSTANCE.trackManager.tracks) {
                    ret.put(RequestUtil.trackInfoJson(t));
                }
                JsonOut.success(httpRequest, ret);
                return;
            }
            case TRACKS_REMOVE: {
                List<GpsTrack> t = RequestUtil.parseParamTracks(httpRequest);
                if (t == null || t.size() == 0) {
                    JsonOut.fail(httpRequest, null);
                    return;
                }
                DataHolder.INSTANCE.trackManager.removeTracks(t);
                JsonOut.success(httpRequest, t.size());
                return;
            }
            case TRACKS_ADD: {
                List<RequestUtil.TrackData> trackDataList = RequestUtil.loadTracks(httpRequest);
                if (trackDataList == null) {
                    JsonOut.fail(httpRequest, "no valid files");
                    return;
                }
                List<GpsTrack> rejected = RequestUtil.parseTracks(trackDataList);
//                RequestUtil.preprocessTracks(gpsTrackList);
//                List<GpsTrack> addedTracks = RequestUtil.addGoodTracks(gpsTrackList, httpRequest);
//                JsonOut.success(httpRequest, addedTracks.size());
                JsonOut.success(httpRequest, new JSONObject().put("rejected", rejected));
                return;
            }
            case MODELS_ADD: {
                List<Field<?>> fields = RequestUtil.parseParamFields(httpRequest);
                List<ModelMethod> modelMethods = RequestUtil.parseParamMethod(httpRequest);
                List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);
                List<GpsTrack> validation = RequestUtil.parseParamTracks(httpRequest, MyApiField.param2.name(), false);
                String ret = "";
                for (ModelMethod modelMethod : modelMethods) {
                    try {
                        DataHolder.INSTANCE.modelData.createModel(fields, modelMethod, gpsTracks, validation);
                    } catch (Exception e) {
                        ret += e;
                        e.printStackTrace();
                    }
                }

//                List<Integer> ret = new ArrayList<>();
//                for (ModelMethod modelMethod : modelMethods) {
//                    PredictionModel m = DataHolder.INSTANCE.modelData.createModel(fields, modelMethod);
//                    if (m != null) {
//                        ret.add(m.hid);
//                    }
//                }
//                ModelMethod modelMethod = ModelMethod.valueOf(MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.method.name()));
                JsonOut.success(httpRequest, ret);
                return;
            }
//            case MODELS_LIST: {
//                JSONArray ret = new JSONArray();
//                for (PredictionModel predictionModel : DataHolder.INSTANCE.modelData.allPredictionModels) {
//                    ret.put(RequestUtil.modelInfo(predictionModel));
//                }
//                JsonOut.success(httpRequest, ret);
//                return;
//            }
            case MODELS_REMOVE: {
                List<PredictionModel> models = RequestUtil.parseParamModels(httpRequest);
                for (PredictionModel model : models) {
                    DataHolder.INSTANCE.modelData.remove(model);
                }
                JsonOut.success(httpRequest, models.size());
                return;
            }
            case MODELS_REMOTE_RELOAD: {
                try {
                    RemoteModelManagerHttp.I.reloadModels();
                    JsonOut.success(httpRequest, "");
                } catch (Exception e) {
                    JsonOut.fail(httpRequest, e.getLocalizedMessage());
                }
                return;
            }
            case BACKUP: {
                DataHolder.INSTANCE.trackManager.backup(DataHolder.INSTANCE.trackManager.tracks);
                JsonOut.success(httpRequest, "");
                return;
            }
            case RESTORE: {
                String path = MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.param.name());
                DataHolder.INSTANCE.trackManager.restore(path);
                JsonOut.success(httpRequest, "");
                return;
            }
            case UTIL_MODEL_PCA: {
//                String path = MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.param.name());
                List<GpsTrack> gpsTracks;
                if (httpRequest.baseRequest.getParameter(MyApiField.track.name()) != null) {
                    gpsTracks = RequestUtil.parseParamTracks(httpRequest);
                } else {
                    gpsTracks = DataHolder.INSTANCE.trackManager.tracks;
                }
                String s = null;
                try {
                    s = TrackUtil.modelCorrelation(gpsTracks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                JsonOut.success(httpRequest, s);
                MyWebUtils.simpleResponse(httpRequest, s);
                return;
            }
//            case MODELS_TRAIN: {
//                List<PredictionModel> models = RequestUtil.parseParamModels(httpRequest);
//                List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);
//                for (PredictionModel model : models) {
//                    try {
//                        model.train(gpsTracks);
//                    } catch (Exception e) {
//                        JsonOut.fail(httpRequest, e.getMessage());
//                        return;
//                    }
//                }
//                JsonOut.success(httpRequest, models.size());
//                return;
//            }
//            case MODELS_PREDICT: {
//                List<PredictionModel> models = RequestUtil.parseParamModels(httpRequest);
//                List<GpsTrack> gpsTracks = RequestUtil.parseParamTracks(httpRequest);
//                JSONObject ret = new JSONObject();
//                for (PredictionModel model : models) {
//                    try {
//                        List<Prediction> predictions = model.predict(gpsTracks);
//                        JSONObject m = new JSONObject();
//                        ret.put(String.valueOf(model.hid), m);
//                        for (Prediction prediction : predictions) {
//                            JSONObject t = new JSONObject();
//                            ret.put(String.valueOf(prediction.track.id), t);
//                            t.put("summary", prediction.modes);
//                            t.put("precision", prediction.correctness);
//                            t.put("modes", prediction.prediction);
//                        }
//                    } catch (Exception e) {
//                        JsonOut.fail(httpRequest, e.getMessage());
//                        return;
//                    }
//                }
//                JsonOut.success(httpRequest, ret);
//                return;
//            }
//            case POST_ADD: {
//                PostProcessor.Type type = RequestUtil.parsePostType(httpRequest);
//                JSONObject param = new JSONObject(MyHttpRequest.postRequestReader.readString(httpRequest, MyApiField.param.name()));
//                PostProcessor ret = PostProcessor.get(type, param);
//                DataHolder.INSTANCE.predictionPostData.addPostProcessor(ret);
//                JsonOut.success(httpRequest, ret.id);
//                return;
//            }
//            case POST_REMOVE: {
//                List<PostProcessor> pp = RequestUtil.parsePostList(httpRequest);
//                int ret = DataHolder.INSTANCE.predictionPostData.removePostProcessor(pp);
//                JsonOut.success(httpRequest, ret);
//                return;
//            }
//            case POST_ATTACH: {
//                PostProcessor pp = RequestUtil.parsePost(httpRequest);
//                List<PredictionModel> models = RequestUtil.parseParamModels(httpRequest);
//                for (PredictionModel model : models) {
//                    model.attachPostprocessor(pp);
//                }
////                int ret = DataHolder.INSTANCE.predictionPostData.removePostProcessor(pp);
//                JsonOut.success(httpRequest, models.size());
//                return;
//            }
//            case POST_LIST: {
//                JSONObject ret = new JSONObject();
//                for (PostProcessor allPost : DataHolder.INSTANCE.predictionPostData.allPosts) {
//                    ret.put("id", allPost.id);
//                    ret.put("info", allPost.info);
//                }
//                JsonOut.success(httpRequest, ret);
//                return;
//            }

        }
        JsonOut.fail(httpRequest, "something went wrong");
    }

}
