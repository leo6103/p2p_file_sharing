package hust.networkprogramming.server_app.service;

import hust.networkprogramming.server_app.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    public static int getUserId(String username) throws SQLException {
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

    public static void updateUserIpAndPort(String username, String ip, int port) {
        String updateSql = "UPDATE users SET ip = ?, port = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            pstmt.setString(3, username);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated IP and Port for user: " + username);
            } else {
                System.out.println("User not found for updating IP and Port");
            }

        } catch (SQLException e) {
            System.err.println("Error updating user IP and Port: " + e.getMessage());
        }
    }
}
