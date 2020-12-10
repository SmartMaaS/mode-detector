package dfki.mm.predict;

import dfki.mm.TravelMode;
import dfki.mm.tracks.ArffUtil;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PCAModel {

    private static final Logger log = LoggerFactory.getLogger(PCAModel.class);
    private static final AtomicInteger counter = new AtomicInteger();

    public final List<Field<?>> fields;
//    public final Field fieldTrue;
//    public final Field fieldPredict;
//    public final ModelMethod method;
//    protected final String mid;
    private final String name;
    private String info = "";
    public final PrincipalComponents pca = new PrincipalComponents();

    private PCAModel(String name, List<Field<?>> fields) {
//                              Field fieldTrue, Field fieldPredict) {
        this.name = name;
        SortedSet<Field<?>> fieldSortedSet = new TreeSet<>(fields);
        this.fields = List.copyOf(fieldSortedSet);
    }

    public static PCAModel newModel(String name, List<Field<?>> fields) {
        return new PCAModel(name + "-" + counter.getAndIncrement() + "-" + fields.size(), fields);
    }

//    public abstract List<Prediction> predict(List<GpsTrack> tracks) throws Exception;
    public List<List<Double>> predict(List<GpsTrack> tracks, List<Field<?>> fieldPredict) throws Exception {
        Instances dataset = ArffUtil.trackToDataset(tracks, fields);
        Instances result = pca.transformedData(dataset);
        List<List<Double>> ret = new ArrayList<>();
//        result.numAttributes()
        for (int i = 0; i < result.numAttributes(); i++) {
            ret.add(new ArrayList<>());
        }
        var enu = result.enumerateInstances();
        while (enu.hasMoreElements()) {
            Instance o = (Instance) enu.nextElement();
            for (int i = 0; i < o.numValues(); i++) {
                ret.get(i).add(o.value(i));
            }
//            o.value()
        }
        //fixme
//        log.info("{} {}", ret.size(), ret.get(0).size());
//        System.out.println(ret.size());
//        System.out.println(ret.get(0).size());
        return ret;
//        throw new RuntimeException("##############################");
    }

    public void train(List<GpsTrack> tracks) throws Exception {
        Instances trainingSet = ArffUtil.trackToDataset(tracks, fields);
//        PrincipalComponents pca = new PrincipalComponents();
        pca.buildEvaluator(trainingSet);
        System.out.println(pca);
        this.info += pca.toString();
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public List<Field<?>> getTransformedFields() {
        return Collections.emptyList();
    }

//    public ModelStatus getStatus() {
//        return status;
//    }

}
