package hust.networkprogramming.client_app.menu;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Menu {
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 12345;

    private static String cookie = "";
    private static String username = "";

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        Menu.cookie = cookie;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Menu.username = username;
    }

    public static void show() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Main menu ---");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. Browse");
            System.out.println("4. Publish");
            System.out.println("5. Cancel publish");
            System.out.println("6. Report Error");
            System.out.println("7. Download file");

            System.out.println("Choose an option: ");
            int option = scanner.nextInt();

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
                switch (option) {
                    case 1:
                        SignupRequest.signup(socket);
                        break;
                    case 2:
                        LoginRequest.login(socket);
                        break;
                    case 3:
                        BrowseRequest.browse(socket);
                        break;
                    case 4:
                        PublishRequest.publish(socket);
                        break;
                    case 5:
                        PublishRequest.cancel(socket);
                        break;
                    case 6:
                        ReportErrorRequest.reportError(socket);
                        break;
                    case 7:
                        // From peer
                        DownloadRequest.downloadMetaData();
                        break;
                    default:
                        LoginRequest.login(socket);
                }
            } catch (IOException e) {
                System.out.println("Can not connect to server");
            }
        }
    }
}
