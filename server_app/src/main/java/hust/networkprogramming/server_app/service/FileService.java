package hust.networkprogramming.server_app.service;

import hust.networkprogramming.server_app.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileService {
    public static boolean insertFile(String name, String filePath, String username) {
        String sql = "INSERT INTO files (name, file_path, user_id) VALUES (?, ?, 1)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, filePath);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu insert thành công
        } catch (SQLException e) {
            System.err.println("Error inserting file: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteFile(String name, String filePath, String username) {
        String sql = "DELETE FROM files WHERE name = ? AND file_path = ? AND user_id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, filePath);

            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }

    }
}
