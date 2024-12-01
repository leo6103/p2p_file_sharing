package hust.networkprogramming.server_app;

import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.server_app.web_server.Server;

public class ServerApp {
    private static final int PORT = 12345;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        DatabaseManager.checkAndCreateTables();
        Server server = Server.getInstance();
        server.startServer();
    }
}
