package hust.networkprogramming.server_app.service;

import hust.networkprogramming.server_app.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileService {
    public static boolean insertFile(String name, String filePath, String username) throws SQLException {
        int userId = UserService.getUserId(username);
        String sql = "INSERT INTO files (name, file_path, user_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, filePath);
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu insert thành công
        } catch (SQLException e) {
            System.err.println("Error inserting file: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteFile(String name, String filePath, String username) throws SQLException {
        int userId = UserService.getUserId(username);
        String checkSql = "SELECT COUNT(*) FROM files WHERE name = ? AND file_path = ? AND user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, name);
            checkStmt.setString(2, filePath);
            checkStmt.setInt(3, userId);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                String deleteSql = "DELETE FROM files WHERE name = ? AND file_path = ? AND user_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, name);
                    deleteStmt.setString(2, filePath);
                    deleteStmt.setInt(3, userId);
                    deleteStmt.executeUpdate();
                    return true;
                }
            } else {
                System.out.println("No file found to delete.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error during file deletion process: " + e.getMessage());
            return false;
        }
    }

}
