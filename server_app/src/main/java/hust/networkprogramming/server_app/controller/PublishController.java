package hust.networkprogramming.server_app.controller;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.server_app.service.FileService;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;

public class PublishController {
    public static void publish(Socket socket, JsonObject data) throws IOException {
        String name = data.get("filename").getAsString();
        String filePath = data.get("filepath").getAsString();
        String username = data.get("username").getAsString();

        boolean isSuccess = FileService.insertFile(name, filePath, username);

        ResponseMessage responseMessage;
        if (isSuccess) {
            responseMessage = new ResponseMessage(ResponseMessage.PUBLISH_SUCCESS_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        } else {
            responseMessage = new ResponseMessage(ResponseMessage.PUBLISH_FAIL_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        }
    }

    public static void cancel(Socket socket, JsonObject data) throws IOException {
        String name = data.get("filename").getAsString();
        String filePath = data.get("filepath").getAsString();
        String username = data.get("username").getAsString();

        boolean isSuccess = FileService.deleteFile(name, filePath, username);

        ResponseMessage responseMessage;
        if (isSuccess) {
            responseMessage = new ResponseMessage(ResponseMessage.CANCEL_PUBLISH_SUCCESS_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        } else {
            responseMessage = new ResponseMessage(ResponseMessage.CANCEL_PUBLISH_FAIL_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        }
    }
}
