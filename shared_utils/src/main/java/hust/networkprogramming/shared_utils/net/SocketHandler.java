package hust.networkprogramming.shared_utils.net;

import java.io.*;
import java.net.*;
import hust.networkprogramming.shared_utils.logger.LoggerUtil;

public class SocketHandler {
    public static void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);

        String destination = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        LoggerUtil.info("Message sent to " + destination + " :\n" + message);
    }

    public static String receiveMessage(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder message = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            message.append(line).append("\n");
            if (!in.ready()) {
                break;
            }
        }

        String source = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        LoggerUtil.info("Message received from " + source + " :\n" + message);
        return message.toString().trim();
    }
}