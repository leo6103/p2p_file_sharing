package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public final class BrowseRequest {
    public static void browse(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name: ");
        String filename = scanner.nextLine();

        JsonObject data = new JsonObject();
        data.addProperty("filename", filename);
        data.addProperty("cookie", Menu.getCookie());
        data.addProperty("username", Menu.getUsername());

        // Send
        RequestMessage requestMessage = new RequestMessage(RequestMessage.BROWSE, data);
        SocketHandler.sendMessage(socket, requestMessage.toString());

        // Receive
        String rawResponse = SocketHandler.receiveMessage(socket);
        ResponseMessage responseMessage = new ResponseMessage(rawResponse);
        int result = responseMessage.getResult();
        switch (result) {
            case ResponseMessage.BROWSE_SUCCESS_CODE -> {
                System.out.println(formatResult(responseMessage.getData()));
            }
            case ResponseMessage.SESSION_EXPIRED_CODE -> {
                System.out.println(responseMessage.getMessage());
            }
            default -> {
                System.out.println("Unknown result code");
            }
        }
    }

    private static String formatResult(JsonObject data) {
        if (data == null || !data.has("files") || data.get("files").isJsonNull()) {
            return "No files available";
        }

        JsonArray filesArray = data.getAsJsonArray("files");
        if (filesArray.isEmpty()) {
            return "No files found";
        }

        StringBuilder table = new StringBuilder();
        table.append(String.format("%-4s %-10s %-20s %-10s %-15s %-5s\n", "ID", "Name", "File Path", "Username", "IP", "Port"));
        table.append("---------------------------------------------------------------\n");

        int id = 1;
        for (JsonElement element : filesArray) {
            JsonObject fileData = element.getAsJsonObject();

            String name = safeGetString(fileData, "name");
            String filePath = safeGetString(fileData, "file_path");
            String username = safeGetString(fileData, "username");
            String ip = safeGetString(fileData, "ip");
            int port = safeGetInt(fileData, "port");

            table.append(String.format("%-4d %-10s %-20s %-10s %-15s %-5d\n", id++,
                    name, filePath, username, ip, port));
        }

        return table.toString();
    }

    private static String safeGetString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "N/A";
    }

    private static int safeGetInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

}
