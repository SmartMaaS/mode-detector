package dfki.mm.tracks.extension;

import dfki.mm.tracks.Field;
import dfki.mm.tracks.FieldType;
import dfki.mm.tracks.GpsTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScaleExtension extends Extension {

    private static final Logger log = LoggerFactory.getLogger(ScaleExtension.class);
    private static final AtomicInteger counter = new AtomicInteger();

    private final List<Extension> requires;
    private final List<Field<?>> sourceFields;
    private final List<Field<?>> scaledFields;

    List<Double> subs;
    List<Double> mult;

    private String name;

//    private class Container {
//        double mult;
//        double add;
//
//    }

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
        return scaledFields;
    }

//    private ScaleExtension(List<Extension> requires, List<Field<?>> fromFields) throws Exception {
//
//    }


    public ScaleExtension(List<Field<?>> fromFields, List<GpsTrack> tracks) throws Exception {
        super();
        //todo verify trained
//        if (model instanceof LocalM)
        this.name = "s" + counter.getAndIncrement();
        List<Field<?>> fromFiltered = new ArrayList<>();
        List<Field<?>> newFields = new ArrayList<>();
        for (Field<?> fromField : fromFields) {
            if (fromField.type == FieldType.DOUBLE /*|| fromField.type == FieldType.LONG*/) {
                fromFiltered.add(fromField);
                newFields.add(Field.newDoubleField(this, fromField.extension.getName() + "_" + fromField.name));
            }
        }
        this.sourceFields = fromFiltered;
        this.scaledFields = newFields;
        this.requires = fromFiltered.parallelStream()
                .map(e -> e.extension).distinct().collect(Collectors.toUnmodifiableList());
        var r = train01(fromFiltered, tracks);
        this.subs = r.get(0);
        this.mult = r.get(1);
//        updateFields(mainExtension);
//        this.fields = List.of(this.prediction = Field.newModeField(this, "p"));
//        List<Field<?>> ff = new ArrayList<>();
//        Instances header = model.pca.transformedHeader();
//        for (int i = 0; i < header.numAttributes(); i++) {
////            ff.add(Field.newDoubleField(this, header.attribute(i).name()));
//            ff.add(Field.newDoubleField(this, "v" + i));
//        }
//        this.fields = Collections.unmodifiableList(ff);
//        this.model = model;
    }

    private static List<List<Double>> train01(List<Field<?>> fromFields, List<GpsTrack> tracks) {
        List<Double> mean = new ArrayList<>();
        List<Double> var = new ArrayList<>();
//        List<Double> tmp = new ArrayList<>();
        var ret = List.of(mean, var);
        for (Field<?> fromField : fromFields) {
            if (fromField.type == FieldType.DOUBLE) {
                DoubleSummaryStatistics dss = new DoubleSummaryStatistics();
                for (GpsTrack track : tracks) {
                    try {
                        DoubleSummaryStatistics ss = ((List<Double>) track.getData(fromField))
                                .parallelStream().mapToDouble(e -> e).summaryStatistics();
                        dss.combine(ss);
                    } catch (RuntimeException e) {
                        log.error("Error working on field {} of track {}", fromField, track);
                        throw e;
                    }
                }
                double m = dss.getMin();
                mean.add(m);
                double d = dss.getMax() - m;
                var.add(d == 0 ? 1 : 1 / d);
//            } else if (fromField.type == FieldType.LONG) {
//                LongSummaryStatistics dss = new LongSummaryStatistics();
//                for (GpsTrack track : tracks) {
//                    LongSummaryStatistics ss = ((List<Long>) track.getData(fromField))
//                            .parallelStream().mapToLong(e -> e).summaryStatistics();
//                    dss.combine(ss);
//                }
//                double m = dss.getMin();
//                mean.add(m);
//                var.add(1 / (dss.getMax() - m));
            } else {
                throw new RuntimeException(fromField.getFullName());
            }
        }
        return ret;
    }

//    private void apply(List<Field<?>> fromFields,  List<Field<?>> newFields, List<GpsTrack> tracks) {
//    }



    @Override
    public void compute(GpsTrack track) {
        try {
//            List<List<Double>> res = model.predict(Collections.singletonList(track), null);
            Iterator<Field<?>> fi = this.sourceFields.iterator();
            Iterator<Field<?>> sfi = this.scaledFields.iterator();
            Iterator<Double> si = this.subs.iterator();
            Iterator<Double> mi = this.mult.iterator();
//            Iterator<List<Double>> ri = res.iterator();
            while (fi.hasNext()) {
                double s = si.next();
                double m = mi.next();
                Field<Double> f = (Field<Double>) fi.next();
                track.setData((Field<Double>) sfi.next(),
                        track.getData(f).stream().map(e -> e == null ? 0F : (e - s) * m).collect(Collectors.toList()));
//                var r = ri.next();
//                track.setData(f, r);
            }
        } catch (Exception e) {
            log.error("", e);
//            track.points.forEach(point -> prediction.set(point, null));
        }
    }
}
