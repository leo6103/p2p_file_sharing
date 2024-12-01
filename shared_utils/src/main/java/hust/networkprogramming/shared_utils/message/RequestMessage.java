package hust.networkprogramming.shared_utils.message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RequestMessage extends Message {
    public static final String LOGIN = "login";
    public static final String SIGNUP = "create_account";
    public static final String BROWSE = "browse";
    public static final String PUBLISH = "publish";
    public static final String REPORT_ERROR = "report_error";
    public static final String CANCEL_PUBLISH = "cancel_publish";
    public static final String DOWNLOAD = "download";
    public static final String READY_DOWNLOAD = "ready_download";

    protected String action;
    protected JsonObject data;

    public RequestMessage(String rawMessage) {
        this.rawMessage = rawMessage;

        jsonObject = JsonParser.parseString(rawMessage).getAsJsonObject();
        action = jsonObject.get("action").getAsString();
        data = jsonObject.get("data").getAsJsonObject();
    }

    public RequestMessage(String action, JsonObject data) {
        this.action = action;
        this.data = data;
        jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.add("data", data);
        rawMessage = jsonObject.toString();
    }

    public String getAction() {
        return action;
    }

    public JsonObject getData() {
        return data;
    }
}
