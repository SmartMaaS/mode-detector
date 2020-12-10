package dfki.mm.predict;

import dfki.mm.TravelMode;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.MyJsonParser;
import dfki.mm.tracks.TrackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

class RemotePredictionModel extends PredictionModel {
    private static final Logger log = LoggerFactory.getLogger(PredictionModel.class);

    String info;

    public String name;
    //    public ModelMethod method;

//    private PredictionModel() {
//    }

    public RemotePredictionModel(String name, Collection<Field<?>> fields, ModelMethod method) {
        //}, Field fieldTrue, Field fieldPredict) {
        super(fields, method, name + "#" + fields);
        this.name = name;
        this.info = String.format("Method=%s, fields=%s", method.toString(), fields);
    }

    @Override
    public void train(List<GpsTrack> tracks, List<GpsTrack> validationTracks, Field<TravelMode> fieldTrue, int seeds) throws Exception {
        //fixme
        info += "; (not really) trained with " + tracks.stream().map(t -> t.name).collect(Collectors.joining(","));
    }

    @Override
    public void train(List<GpsTrack> tracks, Field<TravelMode> fieldTrue) throws Exception {
//    public void train(List<GpsTrack> tracks) throws Exception {
//        List<Field<?>> fieldsList = new ArrayList<>();
//        for (Field<?> field : fields) {
//            if (field != fieldTrue) {
//                fieldsList.add(field);
//            }
//        }
//        fieldsList.add(fieldTrue);
//
//        List<Field> fieldsWithModel = new ArrayList<>(fields);
//        fieldsWithModel.add(Field.modeInteger);
//        List<String> data = tracks.stream().map(track -> track.toCSV(fieldsWithModel, false)).collect(Collectors.toList());
//        List<String> filenames = tracks.stream().map(track -> track.name).collect(Collectors.toList());
//        RemoteModelProxy.INSTANCE.train(this.name, data, filenames);
        //fixme
        info += "; (not really) trained with " + tracks.stream().map(t -> t.name).collect(Collectors.joining(","));
    }

//    public List<Prediction> predict(List<GpsTrack> tracks) throws Exception {
////        for (GpsTrack track : tracks) {
////            String data = track.toCSV(fields, false);
////            List<TravelMode> prediction = RemoteModelProxy.INSTANCE.predict(this.name, data);
////            track.predictions.put(this, prediction);
////            TrackUtil.trackPredictionStat(track, prediction);
////        }
//        List<String> data = tracks.stream().map(track -> track.toCSV(fields, false)).collect(Collectors.toList());
////        List<String> filenames = tracks.stream().map(track -> track.name).collect(Collectors.toList());
//        List<List<TravelMode>> predictions = RemoteModelProxy.INSTANCE.predict(this.name, data);
//        int i = 0;
//        List<Prediction> ret = new ArrayList<>();
//        for (GpsTrack track : tracks) {
//            List<TravelMode> prediction = predictions.get(i++);
//
//            ret.add(registerPrediction(track, prediction));
//
//            TrackUtil.trackPredictionStat(track, prediction);
//        }
//        return ret;
//    }


    @Override
    public List<List<TravelMode>> predict(List<GpsTrack> tracks, Field<TravelMode> fieldPredict) throws Exception {
        List<List<TravelMode>> ret = new ArrayList<>();
        List<Field<?>> fieldsList = new ArrayList<>(fields);
//        fieldsList.add(fieldPredict);
        for (GpsTrack track : tracks) {
//            var mp = HttpJettyUtil.createMultiPart(
//                    Collections.singletonList(MyJsonParser.trackToJsonFlat(track, fieldsList).toString()),
//                    Collections.singletonList(track.name)
//            );
            var r = RemoteModelProxy.INSTANCE.predict(
                    "modelNameHere",
                    MyJsonParser.trackToJsonFlat(track, fieldsList).toString()
            );
            ret.add(r);
//            var r = RemoteModelProxy.INSTANCE.predict(
//                    "modelNameHere",
//                    Collections.singletonList(MyJsonParser.trackToJsonFlat(track, fieldsList).toString())
//            );
//            ret.add(r.get(0));
        }
        return ret;
    }



    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return info;
    }

    void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemotePredictionModel that = (RemotePredictionModel) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
