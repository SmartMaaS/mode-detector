package dfki.mm.tracks.extension;

import dfki.mm.TravelMode;
import dfki.mm.predict.PredictionModel;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.stat.FieldStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PredictionExtension extends Extension {

    private static final Logger log = LoggerFactory.getLogger(PredictionExtension.class);

    private final List<Field<?>> fields;
    private final List<Extension> requires;
    private PredictionModel model;
    private String name;

    public final Field.ModeField prediction;
    public final FieldStatistics fieldStatistics;

    @Override
    public List<Extension> requires() {
        return requires;
    }

    @Override
    public String getInfo() {
        return "prediction data";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Field<?>> getFields() {
        return fields;
    }



    public static PredictionExtension newPredictionExtension(PredictionModel model) {

        List<Extension> requires = model.fields.parallelStream()
                .map(e -> e.extension).distinct().collect(Collectors.toList());
        return new PredictionExtension(requires, model);
    }

    public PredictionExtension(List<Extension> requires, PredictionModel model) {
        super();
        //todo verify trained
//        if (model instanceof LocalM)
        this.name = model.getName();
        this.requires = Collections.unmodifiableList(requires);
//        updateFields(mainExtension);
        this.fields = List.of(this.prediction = Field.newModeField(this, "p"));
        this.model = model;
        this.fieldStatistics = new FieldStatistics(prediction);
    }

    @Override
    public void compute(GpsTrack track) {
        try {
            List<TravelMode> travelModeList = model.predict(List.of(track), prediction).get(0);
            var i = travelModeList.listIterator();
            var p = track.getGpsPoint();
            var pf = p.get(prediction);
            while (p.next()) {
//            for (GpsTrackPoint point : track.points) {
                pf.set(i.next());
//                prediction.set(point, i.next());
            }
            fieldStatistics.compute(track);
        } catch (Exception e) {
            log.error("", e);
//            track.points.forEach(point -> prediction.set(point, null));
        }
    }
}
