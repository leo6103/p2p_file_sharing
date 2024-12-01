// LoginController.java
package hust.networkprogramming.server_app.controller;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;
import hust.networkprogramming.server_app.service.SessionService;
import hust.networkprogramming.server_app.service.AuthService;

public final class LoginController {
    public static void login(Socket socket, JsonObject data) throws IOException, SQLException {
        String ip = socket.getInetAddress().getHostAddress();
        String username = data.get("username").getAsString();
        String password = data.get("password").getAsString();
        int downloadingPort = data.get("downloading_port").getAsInt();

        boolean loginSuccessful = AuthService.validateCredentials(username, password);
        ResponseMessage responseMessage;

        if (loginSuccessful) {
            AuthService.updateUserConnection(username, ip, downloadingPort);

            try {
                String cookie = SessionService.createOrGetActiveCookie(username);
                JsonObject resData = new JsonObject();
                resData.addProperty("cookie", cookie);

                responseMessage = new ResponseMessage(ResponseMessage.LOGIN_SUCCESS_CODE, resData);
                SocketHandler.sendMessage(socket, responseMessage.toString());
            } catch (SQLException e) {
                System.out.println("Error");
                responseMessage = new ResponseMessage(ResponseMessage.LOGIN_FAIL_CODE);
                SocketHandler.sendMessage(socket, responseMessage.toString());
            }
        } else {
            responseMessage = new ResponseMessage(ResponseMessage.LOGIN_FAIL_CODE);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        }
    }
}