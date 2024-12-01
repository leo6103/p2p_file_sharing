package hust.networkprogramming.client_app;

import hust.networkprogramming.client_app.download_server.DownloadServer;
import hust.networkprogramming.client_app.menu.Menu;
import java.io.*;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        System.out.println("Configure your file downloading port :");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();

        DownloadServer downloadServer = DownloadServer.getInstance();
        downloadServer.setPort(port);
        Thread thread = new Thread(downloadServer::start);
        thread.start();

        Menu.show();
    }
}
