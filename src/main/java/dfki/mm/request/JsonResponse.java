package dfki.mm.request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JsonResponse {
    public AtomicBoolean fail = new AtomicBoolean();
    private JSONArray error = new JSONArray();
    Object response;

    public void fail(String msg) {
        fail.set(true);
        if (msg != null) {
            error.put(msg);
        }
    }

    public void setResponse(Object object) {
        this.response = response;
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put(JsonOut.STATUS, fail.get() ? JsonOut.ERROR : JsonOut.RESULT);
        res.put(JsonOut.RESULT, response);
        res.put(JsonOut.ERROR, error);
        return res;
    }

    public static JsonResponse simpleResponse(boolean fail, List<? extends Object> errors, Object response) {
        JsonResponse ret = new JsonResponse();
        ret.fail.set(fail);
        for (Object error : errors) {
            ret.error.put(error);
        }
        ret.response = response;
        return ret;
    }
}
