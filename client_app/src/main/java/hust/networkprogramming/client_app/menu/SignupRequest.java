package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public final class SignupRequest {
    public static void signup(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        data.addProperty("password", password);

        // Send
        RequestMessage requestMessage = new RequestMessage(RequestMessage.SIGNUP, data);
        SocketHandler.sendMessage(socket, requestMessage.toString());

        // Receive
        String rawResponse = SocketHandler.receiveMessage(socket);
        System.out.println("raw signup "+rawResponse);
    }
}
