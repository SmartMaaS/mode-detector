package dfki.mm;

import dfki.mm.functional.*;
import dfki.mm.wui.WebData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;


public enum DataHolder {
    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(DataHolder.class);

    public final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public final MapData mapData;
    public final TrackManager trackManager;
//    public final PredictionData predictionData;
//    public final PredictionPostData predictionPostData;
    public final ModelData modelData;
    public final WebData webData;

//    public final PreprocessUtilOriginal preprocessUtilOriginal;

    DataHolder() {

        try {

            mapData = new MapData();

//            predictionData = new PredictionData();
//            predictionPostData = new PredictionPostData();
            trackManager = new TrackManager();
            modelData = new ModelData();
            webData = new WebData();

//            preprocessUtilOriginal = new PreprocessUtilOriginal();

//        pages.put("gps", new SavePage("index.html"));
//        pages.put("save", new SavePage("save.html"));
        } catch (RuntimeException e) {
            log.error("BAD", e);
            throw e;
        }
    }

    public void init() {

    }

}
