package hust.networkprogramming.server_app.controller;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SignupController {
    public static void signup(Socket socket, JsonObject data) throws IOException, SQLException {
        String username = data.get("username").getAsString();
        String password = data.get("password").getAsString();

        boolean signupSuccessful = createUser(username, password);

        ResponseMessage responseMessage;
        if (signupSuccessful) {
            responseMessage = new ResponseMessage(ResponseMessage.SIGNUP_SUCESS_CODE);
        } else {
            responseMessage = new ResponseMessage(ResponseMessage.SIGNUP_FAIL_CODE);
        }
        SocketHandler.sendMessage(socket, responseMessage.toString());


    }

    private static boolean createUser(String username, String password) throws SQLException {
        if (isUserExist(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private static boolean isUserExist(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}
