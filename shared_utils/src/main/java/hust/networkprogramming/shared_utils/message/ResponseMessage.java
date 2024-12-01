package hust.networkprogramming.shared_utils.message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResponseMessage extends Message {
    public static final int SIGNUP_SUCESS_CODE = 100;
    public static final String SIGNUP_SUCCESS_MESSAGE = "Sign up successfully";
    public static final int SIGNUP_FAIL_CODE = 101;
    public static final String SIGNUP_FAIL_MESSAGE = "Sign up failed";

    public static final int LOGIN_SUCCESS_CODE = 200;
    public static final String LOGIN_SUCCESS_MESSAGE = "Login successfully";
    public static final int LOGIN_FAIL_CODE = 201;
    public static final String LOGIN_FAIL_MESSAGE = "Login failed";
    public static final int SESSION_EXPIRED_CODE = 202;
    public static final String SESSION_EXPIRED_MESSAGE = "Session expired, please login";

    public static final int BROWSE_SUCCESS_CODE = 300;
    public static final String BROWSE_SUCCESS_MESSAGE = "Browse successfully";

    public static final int PUBLISH_SUCCESS_CODE = 400;
    public static final String PUBLISH_SUCCESS_MESSAGE = "Publish successfully";
    public static final int PUBLISH_FAIL_CODE = 401;
    public static final String PUBLISH_FAIL_MESSAGE = "Publish failed";

    public static final int CANCEL_PUBLISH_SUCCESS_CODE = 500;
    public static final String CANCEL_PUBLISH_SUCCESS_MESSAGE = "Cancel publish successfully";
    public static final int CANCEL_PUBLISH_FAIL_CODE = 501;
    public static final String CANCEL_PUBLISH_FAIL_MESSAGE = "Cancel publish failed";

    public static final int DOWNLOAD_FILE_FOUND_CODE = 600;
    public static final String DOWNLOAD_FILE_FOUND_MESSAGE = "File found, please download";
    public static final int DOWNLOAD_FILE_NOT_FOUND_CODE = 601;
    public static final String DOWNLOAD_FILE_NOT_FOUND_MESSAGE = "File not found";

    protected int result;
    protected String message;
    protected JsonObject data;

    // Constructors for server
    public ResponseMessage(int result) {
        this.result = result;
        setMessage(mapMessage(result));

        jsonObject = new JsonObject();
        jsonObject.addProperty("result", result);
        jsonObject.addProperty("message", message);
    }

    public ResponseMessage(int result, JsonObject data) {
        this.result = result;
        setMessage(mapMessage(result));
        this.data = data;

        jsonObject = new JsonObject();
        jsonObject.addProperty("result", result);
        jsonObject.addProperty("message", message);
        jsonObject.add("data", data);

        rawMessage = jsonObject.toString();
    }

    // Constructor for client
    public ResponseMessage(String rawMessage) {
        this.rawMessage = rawMessage;

        jsonObject = JsonParser.parseString(rawMessage).getAsJsonObject();

        result = jsonObject.get("result").getAsInt();
        message = jsonObject.get("message").getAsString();

        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
            data = jsonObject.get("data").getAsJsonObject();
        } else {
            data = null;
        }
    }


    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject getData() {
        return data;
    }

    public static String mapMessage(int result) {
        return switch (result) {
            case SIGNUP_SUCESS_CODE -> SIGNUP_SUCCESS_MESSAGE;
            case SIGNUP_FAIL_CODE -> SIGNUP_FAIL_MESSAGE;
            case LOGIN_SUCCESS_CODE -> LOGIN_SUCCESS_MESSAGE;
            case LOGIN_FAIL_CODE -> LOGIN_FAIL_MESSAGE;
            case SESSION_EXPIRED_CODE -> SESSION_EXPIRED_MESSAGE;
            case BROWSE_SUCCESS_CODE -> BROWSE_SUCCESS_MESSAGE;
            case PUBLISH_FAIL_CODE -> PUBLISH_FAIL_MESSAGE;
            case PUBLISH_SUCCESS_CODE -> PUBLISH_SUCCESS_MESSAGE;
            case CANCEL_PUBLISH_FAIL_CODE -> CANCEL_PUBLISH_FAIL_MESSAGE;
            case CANCEL_PUBLISH_SUCCESS_CODE -> CANCEL_PUBLISH_SUCCESS_MESSAGE;
            case DOWNLOAD_FILE_FOUND_CODE -> DOWNLOAD_FILE_FOUND_MESSAGE;
            case DOWNLOAD_FILE_NOT_FOUND_CODE -> DOWNLOAD_FILE_NOT_FOUND_MESSAGE;

            default -> "Unkown message";
        };
    }
}
