package dfki.mm.predict;

import dfki.mm.*;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldType;
import dfki.mm.tracks.GpsTrack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PredictionModel implements Comparable<PredictionModel> {

    public final static AtomicInteger counter = new AtomicInteger();

    public final List<Field<?>> fields;
    //    public final Field fieldTrue;
    //    public final Field fieldPredict;
    public final ModelMethod method;
    public final int id = counter.getAndIncrement();
    protected final String mid;

//    public PostProcessor postprocessor;

//    public static final Set<Field> alwaysRemove = Collections.unmodifiableSet(EnumSet.of(
//            Field.tripId,
//            Field.timeString,
//            Field.kindOfBus,
//            Field.kindOfTrain,
//            Field.mode
//    ));

    private ModelStatus status = ModelStatus.CREATED;

    protected PredictionModel(Collection<Field<?>> fields, ModelMethod method, String mid) {
//                              Field fieldTrue, Field fieldPredict) {
        this.method = method;
        this.mid = mid;
//        this.hid = Objects.hash(mid);
        SortedSet<Field<?>> fieldSortedSet = new TreeSet<>(fields);
        for (Field<?> field : fields) {
            if (field.type == FieldType.STRING || field == ExtensionManager.INSTANCE.mainExtension.mode) {
                fieldSortedSet.remove(field);
            }
        }
//        fieldSortedSet.removeAll(alwaysRemove);

        this.fields = List.copyOf(fieldSortedSet);
    }

    public static PredictionModel newPredictionModel(String name, List<Field<?>> fields, ModelMethod method) {
        switch (method) {
            case NAIVE_BAYES:
            case RANDOM_FOREST:
            case DECISION_TREE:
                return new LocalPredictionModel(name, fields, method
//                        ExtensionManager.INSTANCE.mainExtension.mode,
//                        Field.newModeField()
                );
//                break;
            case REMOTE:
                return new RemotePredictionModel(name, fields, method);
//                break;
            default:
                throw new RuntimeException(method.toString());
        }
    }

//    public abstract List<Prediction> predict(List<GpsTrack> tracks) throws Exception;
    public abstract List<List<TravelMode>> predict(List<GpsTrack> tracks, Field<TravelMode> fieldPredict) throws Exception;
    public abstract void train(List<GpsTrack> tracks, Field<TravelMode> fieldTrue) throws Exception;
    public abstract void train(List<GpsTrack> tracks, List<GpsTrack> validationTracks,
                               Field<TravelMode> fieldTrue, int seeds) throws Exception;
    public abstract String getName();
    public abstract String getInfo();

    public ModelStatus getStatus() {
        return status;
    }

//    protected Prediction registerPrediction(GpsTrack track, List<TravelMode> prediction) {
//        Prediction p = new Prediction(this, track, prediction);
//        DataHolder.INSTANCE.predictionData.updatePrediction(p);
//        track.predictions.put(this, p);
//        return p;
//    }



    //    public static String serialize(PredictionModel model) {
//        model.fc
//
//        JSONObject root = new JSONObject();
//        root.put("name", track.name);
//        JSONArray points = new JSONArray();
//        for (GPSPoint p : track.points) {
//            points.put(ArffUtil.pointToCSV(p));
//        }
//        root.put("points", points);
//        return root.toString();
//    }
//
//    public static PredictionModel deserialize(String json) {
//        PredictionModel ret = new PredictionModel();
//        JSONObject root = new JSONObject(json);
//        ret.name = root.getString("name");
//        JSONArray points = root.getJSONArray("points");
//        for (int i = 0; i < points.length(); i++) {
//            ret.points.add(ArffUtil.pointFromCSV(points.getString(i)));
//        }
//        return ret;
////        for (String p : ) {
////            points.put(p.serialize());
////        }
//    }
//    protected abstract void setName(String name);

    @Override
    public int compareTo(PredictionModel o) {
        return mid.compareTo(o.mid);
    }

//    public void attachPostprocessor(PostProcessor postProcessor) { // PostprocessUtilOriginal postprocessUtilOriginal
//        this.postprocessor = postProcessor;
////        this.postprocessUtilOriginal = postprocessUtilOriginal;
////        if (postprocessor == null) {
////            this.postprocessor = new PostprocessUtilOriginal();
////            for (Prediction prediction : DataHolder.INSTANCE.predictionData.allPredictions) {
////                if (prediction.model == this) {
////                    prediction.prediction = postprocessor.process(prediction.prediction);
////                }
////            }
////            Configuration.INSTANCE.onMessage(InternalMessage.MODELS_RELOAD, Collections.singleton(this.mid));
////        }
//    }
}
