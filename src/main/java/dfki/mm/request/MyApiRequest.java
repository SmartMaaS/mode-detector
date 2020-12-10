package dfki.mm.request;

public enum MyApiRequest {

    REDIRECT,

    /**
     * Upload a tracks
     * Required: file(s)
     */
    TRACKS_ADD,
    /**
     * Delete tracks
     * Required: hid(s)
     */
    TRACKS_REMOVE,
    /**
     * List all tracks
     */
    TRACKS_LIST,
    /**
     * Get tracks values in json format
     * Required: hid(s)
     * Optional: field(s)
     */
    TRACKS_DATA,

    TRACK_CSV,
    TRACK_GPX,
    TRACK_ARFF,
    TRACK_PREDICTIONS,
    /**
     * Get nearest transport
     * Required: lat, lon (double)
     * Not implemented: radius (5)
     */
    MAP_TRANSPORT,

    MODELS_ADD,
    MODELS_REMOVE,
    MODELS_LIST,
    MODELS_TRAIN,
    MODELS_PREDICT,
    MODELS_REMOTE_RELOAD,

//    POST_ADD,
//    POST_REMOVE,
//    POST_GET,
//    POST_LIST,
//    POST_ATTACH,

//    MODELS_FIELDS,
    BACKUP,
    RESTORE,

    UTIL_MODEL_PCA,
    ;
}
