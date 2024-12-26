package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ReportErrorRequest {
    public static void reportError(Socket socket, String filepath, String username) throws IOException {
        JsonObject data = new JsonObject();
        String filename = new File(filepath).getName();
        data.addProperty("filename", filename);
        data.addProperty("filepath", filepath);
        data.addProperty("username", username);

        RequestMessage requestMessage = new RequestMessage(RequestMessage.REPORT_ERROR, data);
        SocketHandler.sendMessage(socket, requestMessage.toString());

        String rawResponse = SocketHandler.receiveMessage(socket);
        ResponseMessage responseMessage = new ResponseMessage(rawResponse);
        System.out.println(responseMessage.getMessage());
    }
}
