package dfki.mm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum InternalMessage  {
//    TRACK_ADDED,
//    TRACK_REMOVED,
//    MODEL_ADDED,
//    MODEL_STATUS,
//    PREDICTION_STATUS,
    TRACKS_RELOAD,
    MODELS_RELOAD,
//    POST_ADD,
//    POST_REMOVE,
    EXTENSIONS_RELOAD,
    ;
}
