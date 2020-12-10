package dfki.mm.request;

import org.json.JSONObject;

public class JsonRequest {
    public MyApiRequest myApiRequest;
    JSONObject root;

    private JsonRequest(MyApiRequest myApiRequest, JSONObject root) {
        this.myApiRequest = myApiRequest;
        this.root = root;
    }

    public static JsonRequest fromJson(JSONObject json) {
        String r = json.getString(MyApiField.request.name());
        if (r == null) {
            throw new IllegalArgumentException(MyApiField.request.name() + " not found");
        }
        MyApiRequest myApiRequest = MyApiRequest.valueOf(r);
        return new JsonRequest(myApiRequest, json);
    }

    public static JsonRequest fromString(String json) {
        JSONObject o = new JSONObject(json);
        return fromJson(o);
    }


    public void process() {
        switch (myApiRequest) {

        }
    }
}
