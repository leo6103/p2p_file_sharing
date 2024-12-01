package hust.networkprogramming.server_app.service;

import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.UUID;

import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;

public class SessionService {
    public static final int EXPIRY_TIME = 360 * 1000;

    public static String createOrGetActiveCookie(String username) throws SQLException, IOException {
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            String sql = "SELECT s.cookie, s.expiry_date, s.user_id " +
                    "FROM sessions s " +
                    "JOIN users u ON s.user_id = u.id " +
                    "WHERE u.username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                preparedStatement.setString(1, username);

                if (!resultSet.next()) {
                    connection.commit();
                    return createNewCookie(username);
                }

                String cookie = resultSet.getString("cookie");

                if (!isCookieValid(username, cookie)) {
                    removeExpiredCookie(resultSet.getInt("user_id"));
                    connection.commit();
                    return createNewCookie(username);
                }

                connection.commit();
                return cookie;
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error while fetching cookie for username: " + username);
            throw e;
        }
    }

    private static String createNewCookie(String username) throws IOException, SQLException {
        String newCookie = UUID.randomUUID().toString();
        String expiryDate = String.valueOf(System.currentTimeMillis() + EXPIRY_TIME); // 1 hour expiry

        int userId = getUserId(username);

        String insertSQL = "INSERT INTO sessions (cookie, expiry_date, user_id) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, newCookie);
            preparedStatement.setString(2, expiryDate);
            preparedStatement.setInt(3, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating new cookie for user: " + username);
            e.printStackTrace();
            throw e;
        }

        return newCookie;
    }

    private static int getUserId(String username) throws SQLException {
        String userIdSQL = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userIdSQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new SQLException("User not found: " + username);
        } catch (SQLException e) {
            System.out.println("Error fetching user ID for username: " + username);
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean isCookieValid(String username, String cookie) {
        String sql = "SELECT s.expiry_date " +
                "FROM sessions s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.username = ? AND s.cookie = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, cookie);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("a");
                return false;
            }

            String expiryDate = resultSet.getString("expiry_date");
            long currentTime = System.currentTimeMillis();
            long expiryTime = Long.parseLong(expiryDate);
            return currentTime < expiryTime;
        } catch (SQLException e) {
            System.out.println("Error validating cookie for username: " + username + " and cookie: " + cookie);
        }
        System.out.println("b");
        return false;
    }

    private static void removeExpiredCookie(int userId) throws SQLException {
        System.out.println("Remove cookie");
        Connection connection1 = DatabaseManager.getConnection();
        String sql = "SELECT * FROM sessions";
        PreparedStatement preparedStatement1 = connection1.prepareStatement(sql);
        String deleteSQL = "DELETE FROM sessions WHERE user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
            ResultSet a = preparedStatement1.executeQuery();
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("???");
        } catch (SQLException e) {
            System.out.println("Error removing expired cookie for user ID: " + userId);
            e.printStackTrace();
        }
    }

    public static boolean isSessionExpired(Socket socket, JsonObject data) throws IOException {
        String username = data.get("username").getAsString();
        String cookie = data.get("cookie").getAsString();

        System.out.println("Is session expired checking " + cookie);

        if (SessionService.isCookieValid(username, cookie)) {
            return false;
        }
        ResponseMessage responseMessage = new ResponseMessage(ResponseMessage.SESSION_EXPIRED_CODE);
        SocketHandler.sendMessage(socket, responseMessage.toString());

        return true;
    }
}
