package dfki.mm.tracks.extension;

import dfki.mm.predict.PCAModel;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PCAExtension extends Extension {

    private static final Logger log = LoggerFactory.getLogger(PCAExtension.class);

    private final List<Field<?>> fields;
    private final List<Extension> requires;

    private PCAModel model;

    private String name;

//    public final Field.ModeField prediction;

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



    public PCAExtension(List<Extension> requires, PCAModel model) throws Exception {
        super();
        //todo verify trained
//        if (model instanceof LocalM)
        this.name = model.getName();
        this.requires = Collections.unmodifiableList(requires);
//        updateFields(mainExtension);
//        this.fields = List.of(this.prediction = Field.newModeField(this, "p"));
        List<Field<?>> ff = new ArrayList<>();
        Instances header = model.pca.transformedHeader();
        for (int i = 0; i < header.numAttributes(); i++) {
//            ff.add(Field.newDoubleField(this, header.attribute(i).name()));
            ff.add(Field.newDoubleField(this, "v" + i));
        }
        this.fields = Collections.unmodifiableList(ff);
        this.model = model;
    }

    @Override
    public void compute(GpsTrack track) {
        try {
            List<List<Double>> res = model.predict(Collections.singletonList(track), null);
            Iterator<Field<?>> fi = fields.iterator();
            Iterator<List<Double>> ri = res.iterator();
            while (fi.hasNext()) {
                Field<Double> f = (Field<Double>) fi.next();
                var r = ri.next();
                track.setData(f, r);
            }
        } catch (Exception e) {
            log.error("", e);
//            track.points.forEach(point -> prediction.set(point, null));
        }
    }
}
