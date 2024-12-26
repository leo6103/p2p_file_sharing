package hust.networkprogramming.client_app.download_server;

import com.google.gson.JsonObject;
import hust.networkprogramming.client_app.menu.Menu;
import hust.networkprogramming.client_app.menu.PublishRequest;
import hust.networkprogramming.client_app.menu.ReportErrorRequest;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;
import hust.networkprogramming.shared_utils.logger.LoggerUtil;

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
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            this.port = serverSocket.getLocalPort();
            System.out.println("Downloading server is running on port " + this.port);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDirectory);

            while (true) {
                Socket socket = serverSocket.accept();
                LoggerUtil.info("New peer connected");

                executor.submit(() -> {
                   try {
                       handleRequest(socket);
                   } catch (Exception e) {
                       System.out.println("Error when handling request");
                   } finally {
                       try {
                           socket.close();
                           LoggerUtil.info("Connection with peer closed.");
                       } catch (IOException e) {
                           LoggerUtil.info("Error closing client socket: " + e.getMessage());
                       }
                   }
                });
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            LoggerUtil.info("Server error: " + e.getMessage());
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

        JsonObject metadata = new JsonObject();
        metadata.addProperty("filepath", filepath);

        ResponseMessage responseMessage;
        if (file.exists()) {
            System.out.println("File exists.");
            metadata.addProperty("filesize", file.length()); // Only add filesize if file exists
            responseMessage = new ResponseMessage(ResponseMessage.DOWNLOAD_FILE_FOUND_CODE, metadata);
            SocketHandler.sendMessage(socket, responseMessage.toString());
            sendFile(socket, data);
        } else {
            System.out.println("File does not exist.");
            metadata.addProperty("username", Menu.getUsername());

            Socket serverSocket = new Socket(Menu.SERVER_HOST, Menu.SERVER_PORT);
            ReportErrorRequest.reportError(serverSocket, filepath, Menu.getUsername());

            ResponseMessage serverResponseMessage = new ResponseMessage(SocketHandler.receiveMessage(socket));
            System.out.println(serverResponseMessage.getMessage());

            responseMessage = new ResponseMessage(ResponseMessage.DOWNLOAD_FILE_NOT_FOUND_CODE, metadata);
            SocketHandler.sendMessage(socket, responseMessage.toString());
        }
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

