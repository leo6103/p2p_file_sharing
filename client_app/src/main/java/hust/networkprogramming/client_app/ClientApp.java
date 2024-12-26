package hust.networkprogramming.client_app;

import hust.networkprogramming.client_app.download_server.DownloadServer;
import hust.networkprogramming.client_app.menu.Menu;
import java.io.*;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        DownloadServer downloadServer = DownloadServer.getInstance();
        Thread thread = new Thread(downloadServer::start);
        thread.start();

        Menu.show();
    }
}
