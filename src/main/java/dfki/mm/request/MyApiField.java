package dfki.mm.request;

import dfki.mm.tracks.GpsTrack;

import java.util.prefs.AbstractPreferences;

public enum MyApiField {
    /**
     * Action to be performed
     * @see MyApiRequest
     */
    request,
    /**
     * GPS track id (number)
     * @see GpsTrack#id
     */
    track,
    /**
     * Map number? fixme
     */
    map,
    /**
     * Latitude -70..70
     */
    lat,
    /**
     * Longitude -180..180
     */
    lon,
    /**
     * GPS track field (e.g. speed)
     * @see dfki.mm.tracks.Field
     */
    field,
    /**
     * File upload
     */
    file,
    /**
     * ML/NN method name
     * @see dfki.mm.predict.ModelMethod
     */
    method,
    /**
     * Model id (int hid)
     */
    model,
    post,
    param,
    param2,

    /**
     * For WUI
     */
    page,
    ;


}
