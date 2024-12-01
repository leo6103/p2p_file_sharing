package hust.networkprogramming.shared_utils.message;

import com.google.gson.JsonObject;

public abstract class Message {
    protected String rawMessage;
    protected JsonObject jsonObject;

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
