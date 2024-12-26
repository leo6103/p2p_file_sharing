package hust.networkprogramming.server_app.controller;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.service.SessionService;
import hust.networkprogramming.shared_utils.message.RequestMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

public final class RequestMapper {
    public static void mapRequest(Socket socket) throws IOException, SQLException {
        String message = SocketHandler.receiveMessage(socket);
        RequestMessage requestMessage = new RequestMessage(message);
        String action = requestMessage.getAction();
        JsonObject data = requestMessage.getData();

        switch (action) {
            case RequestMessage.LOGIN:
                LoginController.login(socket, data);
                break;
            case RequestMessage.SIGNUP:
                SignupController.signup(socket, data);
                break;
            case RequestMessage.BROWSE:
                if (!SessionService.isSessionExpired(socket, data)) {
                    BrowseController.browse(socket, data);
                }
                break;
            case RequestMessage.PUBLISH:
                if (!SessionService.isSessionExpired(socket, data)) {
                    PublishController.publish(socket, data);
                }
                break;
            case RequestMessage.REPORT_ERROR:
                ReportErrorController.reportError(socket, data);
                break;
            case RequestMessage.CANCEL_PUBLISH:
                if (!SessionService.isSessionExpired(socket, data)) {
                    PublishController.cancel(socket, data);
                }
                break;
            default:
                System.out.println("Invalid action");
        }
    }
}
