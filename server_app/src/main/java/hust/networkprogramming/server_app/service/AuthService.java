package hust.networkprogramming.server_app.service;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

public class AuthService {
    public static boolean validateCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking login credentials: " + e.getMessage());
            return false;
        }
    }

    public static void updateUserConnection(String username, String ip, int downloadingPort) {
        UserService.updateUserIpAndPort(username, ip, downloadingPort);
    }
}