package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public final class PublishRequest {
    public static void publish(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String path = "";

        while (true) {
            System.out.println("Enter file path: ");
            path = scanner.nextLine();

            File file = new File(path);
            if (file.exists() && file.isFile()) {
                String filename = file.getName();
                JsonObject data = new JsonObject();
                data.addProperty("filename", filename);
                data.addProperty("filepath", path);
                data.addProperty("username", Menu.getUsername());
                data.addProperty("cookie", Menu.getCookie());

                RequestMessage requestMessage = new RequestMessage(RequestMessage.PUBLISH, data);
                SocketHandler.sendMessage(socket, requestMessage.toString());

                String rawResponse = SocketHandler.receiveMessage(socket);
                ResponseMessage responseMessage = new ResponseMessage(rawResponse);
                String message = responseMessage.getMessage();
                System.out.println(message);
                break;
            } else {
                System.out.println("File does not exist. Please enter a valid file path.");
            }
        }
    }

    public static void cancel(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String path = "";

        // Nhập đường dẫn file để hủy
        while (true) {
            System.out.println("Enter file path to cancel: ");
            path = scanner.nextLine();

            File file = new File(path);
            if (file.exists() && file.isFile()) {
                String filename = file.getName();
                JsonObject data = new JsonObject();
                data.addProperty("filename", filename);
                data.addProperty("filepath", path);
                data.addProperty("username", Menu.getUsername());
                data.addProperty("cookie", Menu.getCookie());

                RequestMessage requestMessage = new RequestMessage(RequestMessage.CANCEL_PUBLISH, data);
                SocketHandler.sendMessage(socket, requestMessage.toString());

                String rawResponse = SocketHandler.receiveMessage(socket);
                ResponseMessage responseMessage = new ResponseMessage(rawResponse);
                String message = responseMessage.getMessage();
                System.out.println(message);
                break;
            } else {
                System.out.println("File does not exist. Please enter a valid file path.");
            }
        }
    }
}
