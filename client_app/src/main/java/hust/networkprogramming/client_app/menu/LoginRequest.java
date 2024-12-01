package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonObject;
import hust.networkprogramming.client_app.download_server.DownloadServer;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public final class LoginRequest {
    public static void login(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        data.addProperty("password", password);
        data.addProperty("downloading_port", DownloadServer.getInstance().getPort());

        // Send
        RequestMessage requestMessage = new RequestMessage(RequestMessage.LOGIN, data);
        SocketHandler.sendMessage(socket, requestMessage.toString());

        // Receive
        String rawResponse = SocketHandler.receiveMessage(socket);
        ResponseMessage responseMessage = new ResponseMessage(rawResponse);
        int result = responseMessage.getResult();
        System.out.println(responseMessage.getMessage());

        if (result == ResponseMessage.LOGIN_SUCCESS_CODE) {
            Menu.setCookie(responseMessage.getData().get("cookie").getAsString());
            Menu.setUsername(username);
            System.out.println(responseMessage.getData().get("cookie").getAsString());
        }
    }
}
