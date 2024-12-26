package hust.networkprogramming.server_app.controller;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.service.FileService;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public final class ReportErrorController {
    public static void reportError(Socket socket, JsonObject data) throws SQLException, IOException {
        String name = data.get("filename").getAsString();
        String filePath = data.get("filepath").getAsString();
        String username = data.get("username").getAsString();
        System.out.println("Data "+ name + " " + filePath + " " + username);

        boolean isSuccess = FileService.deleteFile(name, filePath, username);
        ResponseMessage responseMessage;
        if (isSuccess) {
            responseMessage = new ResponseMessage(ResponseMessage.REPORT_ERROR_SUCCESS_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
            System.out.println(responseMessage.toString());
        } else {
            responseMessage = new ResponseMessage(ResponseMessage.REPORT_ERROR_FAIL_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
            System.out.println(responseMessage.toString());
        }

    }
}
