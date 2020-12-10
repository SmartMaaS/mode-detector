package dfki.mm.predict;

import dfki.mm.TravelMode;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.tracks.ArffUtil;
import dfki.mm.tracks.Field;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.TrackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

class LocalPredictionModel extends PredictionModel {
    private static final Logger log = LoggerFactory.getLogger(PredictionModel.class);
    private static final int SEED = 12345;

    private String info;

    public String name;
//    public ModelMethod method;
    Classifier fc;

//    private PredictionModel() {
//    }

    public LocalPredictionModel(String name, List<Field<?>> fields, ModelMethod method) {
        //}, Field fieldTrue, Field fieldPredict) {
        super(fields, method, method + "#" + fields);
        this.name = name;

        info = String.format("Method=%s, fields=%s", method.toString(), fields);
    }

    /**
     * ???
     * old.avgAcceleration
     * old.avgHeadingChange
     * old.standardDeviation
     * old.avgSpeed
     * old.gps_avgHeadingChange
     * ???
     * @param tracks
     * @param fieldTrue
     * @throws Exception
     */
    public void train(List<GpsTrack> tracks, Field<TravelMode> fieldTrue) throws Exception {
        train(tracks, null, fieldTrue, 0);
    }

    public void train(List<GpsTrack> tracks, List<GpsTrack> validation, Field<TravelMode> fieldTrue, int seeds) throws Exception {
//        Instance instance = new Instance()
//        Instances trainingSet = new Instances(new StringReader(ArffUtil.arffForTracks(tracks)));
//        trainingSet..add();

        List<Field<?>> fieldsList = new ArrayList<>();
        for (Field<?> field : fields) {
            if (field != fieldTrue) {
                fieldsList.add(field);
            }
        }
        fieldsList.add(fieldTrue);

        Instances trainingSet = ArffUtil.trackToDataset(tracks, fieldsList);
//        List<Instances> validationSet = null;
//        List<Double> groundTruth = null;
        Instances validationSet = null;
        List<Double> groundTruth = null;
        if (validation != null && seeds > 1 && this.method != ModelMethod.NAIVE_BAYES) {
            validationSet = ArffUtil.trackToDataset(validation, fields, true);
            validationSet.setClassIndex(validationSet.numAttributes() - 1);
            groundTruth = new ArrayList<>();
            for (GpsTrack gpsTrack : validation) {
                groundTruth.addAll(
                    gpsTrack.getData(fieldTrue).stream().map(e -> (double)e.ordinal()).collect(Collectors.toList())
                );
            }
        } else {
//            validation = null;
            seeds = 1;
        }

        // fix start: weka A nominal attribute cannot have duplicate labels 0-0
        trainingSet = new Instances(new StringReader(trainingSet.toString()));
        // fix end: weka A nominal attribute cannot have duplicate labels 0-0

        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

//        Remove rm = new Remove();
//        rm.setInvertSelection(true);
//        rm.setAttributeIndices(fieldsSet.stream().map(Enum::toString).collect(Collectors.joining(",")));
//        System.out.println(String.join(",", indices));
//        rm.setAttributeIndices(String.join(",", indices));
        if (seeds > 1) {
            double bestResult = 0;
            for (int i = SEED; i < SEED + seeds; i++) {
                Classifier fc = createClassifier(method, i);
                fc.buildClassifier(trainingSet);
                double pre = evaluate(fc, validationSet, groundTruth);
                if (pre > bestResult) {
                    bestResult = pre;
                    this.fc = fc;
                    log.info("train:{} seed={} p={} BEST", method, i, pre);
                } else {
                    log.info("train:{} seed={} p={} (ignore)", method, i, pre);
                }

            }
        } else {
            Classifier fc = createClassifier(method, SEED);
            fc.buildClassifier(trainingSet);
            this.fc = fc;
        }
        info += "; trained with " + tracks.stream().map(t -> t.name).collect(Collectors.joining(","));
    }

    private Classifier createClassifier(ModelMethod method, int seed) {
        FilteredClassifier fc = new FilteredClassifier();
//        fc.setFilter(rm);
        switch (method) {
            case RANDOM_FOREST:
                RandomForest rf = new RandomForest();
                rf.setSeed(seed);
                fc.setClassifier(rf);
                break;
            case NAIVE_BAYES:
                NaiveBayes nb = new NaiveBayes();
                fc.setClassifier(nb);
                break;
            default:
                J48 dt = new J48();
                dt.setSeed(seed);
                fc.setClassifier(dt);
                break;
        }
        return fc;
    }

    public List<List<TravelMode>> predict(List<GpsTrack> tracks, Field<TravelMode> fieldPredict) throws Exception {
//        Instances test = new Instances(DataFileUtil.readDataFile(fileName));
        List<List<TravelMode>> ret = new ArrayList<>();

        List<Field<?>> fieldsList = new ArrayList<>(fields);
//        fieldsList.addAll(fields);
        fieldsList.add(fieldPredict);

//        Instances datasets = ArffUtil.trackToDataset(tracks, fields);
        for (GpsTrack track : tracks) {
//            track.reset(fieldPredict);
            Instances dataset = ArffUtil.trackToDataset(Collections.singletonList(track), fieldsList);
//            datasets
            List<TravelMode> predictedTravelModes = new ArrayList<>();
//            Instances dataset = new Instances(new StringReader(ArffUtil.writeFile(track.points)));
//            Instances dataset = ArffUtil.trackToDataset().writeFile(track.points)));

            dataset.setClassIndex(dataset.numAttributes() - 1);

            Classifier model = fc;
            for (int i = 0; i < dataset.numInstances(); i++) {
                Instance testInstance = dataset.instance(i);
                Double outClass = model.classifyInstance(testInstance);
                int outClassInt = outClass.intValue();
                TravelMode mode = TravelMode.valueOf(dataset.attribute(dataset.numAttributes() - 1).value(outClassInt));
//                System.out.println();
                predictedTravelModes.add(mode);
            }

//            List<TravelMode> prediction = registerPrediction(track, predictedTravelModes);
//            if (postprocessor != null) {
//                postprocessor.process(predictedTravelModes);
//            }
            ret.add(predictedTravelModes);
//            ret.add(prediction);
//            TrackUtil.trackPredictionStat(track, predictedTravelModes);
        }
        return ret;
    }



    public boolean isTrained() {
        return fc != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
//        return info + ",\npost=" + postprocessor;
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalPredictionModel that = (LocalPredictionModel) o;
        return mid.equals(that.mid);// && that.postprocessUtilOriginal == this.postprocessUtilOriginal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid);
    }

    private static double evaluate(Classifier classifier, List<Instances> dataset, List<List<Double>> groundTruth) throws Exception {
        long correct = 0;
        long total = 0;
        var di = dataset.iterator();
        var ti = groundTruth.iterator();
        while (di.hasNext()) {
            var ii = di.next();
            var tt = ti.next();
            var ddi = ii.enumerateInstances();
            var tti = tt.iterator();
            while (ddi.hasMoreElements()) {
                Instance i = (Instance) ddi.nextElement();
                double v = classifier.classifyInstance(i);
                double t = tti.next();
                if (t == v) {
                    correct++;
                }
                total++;
            }
        }
        return correct / (double) total;
    }

    private static double evaluate(Classifier classifier, Instances dataset, List<Double> groundTruth) throws Exception {
        long correct = 0;
        long total = 0;
        var ddi = dataset.enumerateInstances();
        var tti = groundTruth.iterator();
        while (ddi.hasMoreElements()) {
            Instance i = (Instance) ddi.nextElement();
            double v = classifier.classifyInstance(i);
            double t = tti.next();
            if (t == v) {
                correct++;
            }
            total++;
        }
        return correct / (double) total;
    }
}
