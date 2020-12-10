package dfki.mm.request;

import dfki.mm.wui.MyWebUtils;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

public class JsonOut {

    public static final String ERROR = "error";
    public static final String STATUS = "status";
    public static final String RESULT = "result";

    int code;
    String text;

    public static boolean simpleResponse(Request baseRequest, HttpServletResponse response, int status, JSONObject res) {
        response.setContentType("text/json; charset=utf-8");
        response.setStatus(status);
        if (MyWebUtils.writeResponse(response, res.toString())) {
            baseRequest.setHandled(true);
            return true;
        }
        return false;
    }

    public static boolean fail(MyHttpRequest myHttpRequest, int status, String text) {
        JSONObject res = new JSONObject();
        res.put(STATUS, ERROR);
        JSONArray errors = new JSONArray();
        res.put(ERROR, errors);
        if (text != null) {
            errors.put(text);
        }
        myHttpRequest.errors.forEach(errors::put);
        return simpleResponse(myHttpRequest.baseRequest, myHttpRequest.response, status, res);
    }

    public static boolean fail(MyHttpRequest myHttpRequest, String text) {
//        List.
        JSONObject res = new JSONObject();
        res.put(STATUS, ERROR);
        JSONArray errors = new JSONArray();
        res.put(ERROR, errors);
        if (text != null) {
            errors.put(text);
        }
        myHttpRequest.errors.forEach(errors::put);
        return simpleResponse(myHttpRequest.baseRequest, myHttpRequest.response, 200, res);
    }

    public static boolean success(MyHttpRequest myHttpRequest, Object result) {
        JSONObject res = new JSONObject();
        res.put(STATUS, RESULT);
        res.put(RESULT, result);
        res.put(ERROR, new JSONArray(myHttpRequest.errors));
//        JSONArray errors = ;
//        .forEach(errors::put);
        return simpleResponse(myHttpRequest.baseRequest, myHttpRequest.response, 200, res);
    }
}
