package dfki.mm.wui;

import dfki.mm.DataHolder;
import dfki.mm.functional.ExtensionManager;
import dfki.mm.request.MyHttpRequest;
import dfki.mm.tracks.extension.PredictionExtension;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Simple JSON API for GET requests
 */
public class SimpleGet {
    /**
     *
     * @param target    web path
     * @param http      request data
     */
    public static void main(String target, MyHttpRequest http) throws IOException {
        String ret;
        switch (target) {
            case "tracks": {
                var list = DataHolder.INSTANCE.trackManager.tracks.stream().map(e ->
                        new JSONObject().put("id", e.id).put("name", e.name)).collect(Collectors.toList());
                ret = new JSONArray(list).toString();
                break;
            }
            case "models": {
                var list = ExtensionManager.INSTANCE.getAllExtensions().stream()
                        .filter(e -> e instanceof PredictionExtension)
                        .map(e -> new JSONObject().put("id", e.id).put("name", e.getName()))
                        .collect(Collectors.toList());
                ret = new JSONArray(list).toString();
                break;
            }
            case "ext": {
                var list = ExtensionManager.INSTANCE.getAllExtensions().stream()
//                        .filter(e -> e instanceof PredictionExtension)
                        .map(e -> new JSONObject().put("id", e.id).put("name", e.getName()))
                        .collect(Collectors.toList());
                ret = new JSONArray(list).toString();
                break;
            }
            case "fields": {
                var list = ExtensionManager.INSTANCE.getAllFields().stream()
//                        .filter(e -> e instanceof PredictionExtension)
                        .map(e -> new JSONObject().put("id", e.id).put("name", e.getFullName()))
                        .collect(Collectors.toList());
                ret = new JSONArray(list).toString();
                break;
            }
            default:
                ret = String.format("unknown target: %s", target);
        }
        http.response.setStatus(HttpServletResponse.SC_OK);
        http.response.getWriter().println(ret);
        http.baseRequest.setHandled(true);
    }
}
