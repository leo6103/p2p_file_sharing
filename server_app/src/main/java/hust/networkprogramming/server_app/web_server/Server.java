package hust.networkprogramming.server_app.web_server;

import hust.networkprogramming.server_app.controller.RequestMapper;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;
    private static final int THREAD_POOL_SIZE = 10;
    private static Server instance;
    private ExecutorService executor;

    // Private constructor để đảm bảo không thể tạo object từ ngoài lớp
    private Server() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    // Phương thức getInstance để lấy instance duy nhất của server
    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    // Phương thức startServer để bắt đầu lắng nghe kết nối
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDirectory);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                executor.submit(() -> {
                    try {
                        RequestMapper.mapRequest(clientSocket);
                    } catch (IOException e) {
                        System.err.println("Error handling client: " + e.getMessage());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            clientSocket.close();
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

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}