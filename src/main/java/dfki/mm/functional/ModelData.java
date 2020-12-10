package dfki.mm.functional;

import dfki.mm.DataHolder;
import dfki.mm.tracks.Field;
import dfki.mm.predict.ModelMethod;
import dfki.mm.predict.PredictionModel;
//import dfki.mm.predict.RemoteModelProxy;
import dfki.mm.tracks.GpsTrack;
import dfki.mm.tracks.extension.Extension;
import dfki.mm.tracks.extension.PredictionExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ModelData  {

    private static final Logger log = LoggerFactory.getLogger(ModelData.class);


//    public final Set<PredictionModel> allPredictionModels = new TreeSet<>();

//    private final AtomicBoolean remoteModelsFailed = new AtomicBoolean(false);


    public void reloadRemoteModels() {
//        try {
//            List<PredictionModel> remoteModels = RemoteModelProxy.INSTANCE.loadModels();
//            remoteModelsFailed.set(false);
//            System.out.println(remoteModels.stream().map(PredictionModel::getName).collect(Collectors.joining()));
////            this.allPredictionModels.clear();
//            this.allPredictionModels.removeIf(m -> m.method == ModelMethod.REMOTE);
//            this.allPredictionModels.addAll(remoteModels);
////            allPredictionModels.forEach(modelPage::addModel);
//            Configuration.INSTANCE.onMessage(InternalMessage.MODELS_RELOAD, Collections.emptyList());
////            modelistPage.updateModels();
////            modelPage.addModel();
//        } catch (Exception e) {
//            if (remoteModelsFailed.get()) {
//                log.error("Cannot reload remote models (again): {}", e.getMessage());
//            } else {
//                remoteModelsFailed.set(true);
//                log.error("Cannot reload remote models", e);
////                e.printStackTrace();
//            }
//        }
    }

//    public void init() {
//        reloadRemoteModels();
//    }

//    public void createModel(int[] fields, ModelMethod modelMethod) {
//        List<Field> list = new ArrayList<>();
//        for (int field : fields) {
//            list.add(ExtensionManager.INSTANCE.fieldById(field));
//        }
////        Arrays.stream(fields).boxed()
////                .map(ExtensionManager.INSTANCE::fieldById).collect(Collectors.toList());
//        createModel(list, modelMethod);
//    }

    public PredictionModel createModel(List<Field<?>> fields, ModelMethod modelMethod,
                                       List<GpsTrack> tracks, List<GpsTrack> validation) throws Exception {

//        List<Field> list = Arrays.stream(fields).map(Field::valueOf).collect(Collectors.toList());
//        PredictionModel pm = new PredictionModel(modelMethod + "-" + fields.length, list, modelMethod);
        try {
            DataHolder.INSTANCE.rwl.writeLock().lock();
            PredictionModel pm = PredictionModel.newPredictionModel(modelMethod + "-" + fields.size(), fields, modelMethod);
            List<Extension> extensions = fields.stream().map(e -> e.extension).distinct().collect(Collectors.toList());
            PredictionExtension predictionExtension = new PredictionExtension(extensions, pm);
            pm.train(tracks, validation, ExtensionManager.INSTANCE.mainExtension.mode, 10);
            ExtensionManager.INSTANCE.addExtension(predictionExtension);

        } finally {
            DataHolder.INSTANCE.rwl.writeLock().unlock();
        }
        return null;
    }

//    public void registerModel(PredictionModel pm, List<GpsTrack> tracks) throws Exception {
////        boolean a = allPredictionModels.add(pm);
//        Configuration.INSTANCE.onMessage(InternalMessage.MODELS_RELOAD, Collections.emptyList());
//        if (a) {
//            return pm;
//        } else {
//            return null;
//        }
//
//    }

    public PredictionModel getModelByHid(Integer hid) {
        if (hid == null) {
            return null;
        }
//        return allPredictionModels.stream().filter(m -> m.hid == hid).findFirst().orElse(null);
        throw new IllegalArgumentException();
    }

    public void remove(PredictionModel model) {
//        DataHolder.INSTANCE.predictionData.allPredictions.removeIf(e -> e.model.hid == hid);
//        allPredictionModels.removeIf(e -> e.hid == hid);
//        DataHolder.INSTANCE.predictionData.removeForModel(model);
//        allPredictionModels.remove(model);
//        Configuration.INSTANCE.onMessage(InternalMessage.MODELS_RELOAD, Collections.emptyList());
    }
}
