package hust.networkprogramming.client_app.menu;

import com.google.gson.JsonObject;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DownloadRequest {

    // Method to handle metadata downloading (file information)
    public static void downloadMetaData() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter peer IP: ");
        String peerIP = scanner.nextLine();

        System.out.print("Enter peer port: ");
        int peerPort = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();

        // Get download folder from user and ensure it's valid
        String downloadFolder = getDownloadFolder(scanner);

        JsonObject data = new JsonObject();
        data.addProperty("filepath", filePath);

        RequestMessage requestMessage = new RequestMessage(RequestMessage.DOWNLOAD, data);

        try (Socket socket = new Socket(peerIP, peerPort)) {
            SocketHandler.sendMessage(socket, requestMessage.toString());

            String rawResponse = SocketHandler.receiveMessage(socket);
            System.out.println("rawResponse: " + rawResponse);
            ResponseMessage responseMessage = new ResponseMessage(rawResponse);
            int result = responseMessage.getResult();

            if (result == ResponseMessage.DOWNLOAD_FILE_FOUND_CODE) {
                long filesize = responseMessage.getData().get("filesize").getAsLong();
                String filepath = responseMessage.getData().get("filepath").getAsString();

                String filename = new File(filepath).getName();

                downloadFile(socket, filesize, downloadFolder, filename);
            } else {
                System.out.println("File not found");
            }

        } catch (IOException e) {
            System.err.println("Error connecting to peer: " + e.getMessage());
        }
    }

    // Method to get valid download folder from user
    private static String getDownloadFolder(Scanner scanner) {
        String folderPath;
        while (true) {
            System.out.print("Enter download folder (e.g. /home/leo/Downloads/): ");
            folderPath = scanner.nextLine();
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                break; // Valid folder path
            } else {
                System.out.println("Invalid folder path. Please enter a valid directory.");
            }
        }
        return folderPath;
    }

    // Method to handle file downloading with progress bar
    public static void downloadFile(Socket socket, long filesize, String downloadFolder, String filename) throws IOException {
        File outputFile = new File(downloadFolder, filename);
        try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
             DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesReceived = 0;

            // Progress bar configuration
            int progressBarWidth = 50; // Total width of the progress bar

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytesReceived += bytesRead;
                fileOutputStream.write(buffer, 0, bytesRead);

                // Calculate progress
                int progress = (int) ((totalBytesReceived * 100) / filesize);

                // Create progress bar
                StringBuilder progressBar = new StringBuilder("[");
                int completedWidth = (progress * progressBarWidth) / 100;

                // Add completed part
                for (int i = 0; i < completedWidth; i++) {
                    progressBar.append("=");
                }

                // Add current position marker
                if (completedWidth < progressBarWidth) {
                    progressBar.append(">");
                }

                // Add remaining empty part
                for (int i = completedWidth + 1; i < progressBarWidth; i++) {
                    progressBar.append(" ");
                }

                progressBar.append("] ").append(progress).append("%");

                // Move cursor to start of line and print progress bar
                System.out.print("\r" + progressBar);
            }

            // Print new line after download completes
            System.out.println("\nDownload completed!");

        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
        }
    }
}
