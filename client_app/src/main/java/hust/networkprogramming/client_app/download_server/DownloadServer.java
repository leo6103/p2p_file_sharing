package hust.networkprogramming.client_app.download_server;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadServer {
    private static DownloadServer instance;
    private ExecutorService executor;
    private int port = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    private static final int CHUNK_SIZE = 1024;


    public static synchronized DownloadServer getInstance() {
        if (instance == null) {
            instance = new DownloadServer();
        }
        return instance;
    }

    private DownloadServer() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Downloading server is running on port " + this.port);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDirectory);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New peer connected");

                executor.submit(() -> {
                   try {
                       handleRequest(socket);
                   } catch (Exception e) {
                       System.out.println("Error when handling request");
                   } finally {
                       try {
                           socket.close();
                           System.out.println("Connection with client closed.");
                       } catch (IOException e) {
                           System.err.println("Error closing client socket: " + e.getMessage());
                       }
                   }
                });
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private void handleRequest(Socket socket) throws IOException {
        String rawMessage = SocketHandler.receiveMessage(socket);
        RequestMessage requestMessage = new RequestMessage(rawMessage);

        String action = requestMessage.getAction();
        JsonObject data = requestMessage.getData();

        if (Objects.equals(action, RequestMessage.DOWNLOAD)) {
            firstRequest(socket, data);
        } else if (Objects.equals(action, RequestMessage.READY_DOWNLOAD)) {
            sendFile(socket, data);
        }

    }

    private void firstRequest(Socket socket, JsonObject data) throws IOException {
        String filepath = data.get("filepath").getAsString();

        File file = new File(filepath);


        ResponseMessage responseMessage;
        if (file.exists()) {
            System.out.println("exist");
            JsonObject metadata = new JsonObject();
            metadata.addProperty("filepath", filepath);
            metadata.addProperty("filesize", file.length());

            responseMessage = new ResponseMessage(ResponseMessage.DOWNLOAD_FILE_FOUND_CODE, metadata);
            SocketHandler.sendMessage(socket, responseMessage.toString());

            sendFile(socket, data);

            return;
        }

        responseMessage = new ResponseMessage(ResponseMessage.DOWNLOAD_FILE_NOT_FOUND_CODE);
        SocketHandler.sendMessage(socket, responseMessage.toString());
    }

    private void sendFile(Socket socket, JsonObject data) throws IOException {
        String filepath = data.get("filepath").getAsString();
        File file = new File(filepath);

        if (file.exists()) {
            try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
                 OutputStream socketOutputStream = socket.getOutputStream()) {

                byte[] buffer = new byte[CHUNK_SIZE];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    socketOutputStream.write(buffer, 0, bytesRead);
                    socketOutputStream.flush();
                }

            } catch (IOException e) {
                System.err.println("Error sending file: " + e.getMessage());
            }
        }
    }
}

