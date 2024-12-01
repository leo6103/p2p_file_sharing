package hust.networkprogramming.shared_utils.net;

import java.io.*;
import java.net.*;

public class SocketHandler {
    public static void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
    }

    public static String receiveMessage(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder message = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            message.append(line).append("\n");
            // Check if there's no more data available to read
            if (!in.ready()) {
                break;
            }
        }
        return message.toString().trim();
    }
}