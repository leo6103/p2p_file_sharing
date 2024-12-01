package hust.networkprogramming.server_app.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hust.networkprogramming.server_app.db.DatabaseManager;
import hust.networkprogramming.shared_utils.message.ResponseMessage;
import hust.networkprogramming.shared_utils.net.SocketHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.net.Socket;

public final class BrowseController {
    public static void browse(Socket socket, JsonObject data) throws IOException {
        String filename = data.get("filename").getAsString();
        JsonObject resData = new JsonObject();
        resData.add("files", getFiles(filename));

        ResponseMessage responseMessage = new ResponseMessage(ResponseMessage.BROWSE_SUCCESS_CODE, resData);
        System.out.println(responseMessage);
        SocketHandler.sendMessage(socket, responseMessage.toString());
    }

    private static JsonArray getFiles(String filename) {
        String sql = "SELECT f.name, f.file_path, u.username, u.ip, u.port " +
                "FROM files f " +
                "JOIN users u ON f.user_id = u.id " +
                "WHERE f.name LIKE ?";

        JsonArray resultArray = new JsonArray();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + filename + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject fileData = new JsonObject();
                    fileData.addProperty("name", rs.getString("name"));
                    fileData.addProperty("file_path", rs.getString("file_path"));
                    fileData.addProperty("username", rs.getString("username"));
                    fileData.addProperty("ip", rs.getString("ip"));
                    fileData.addProperty("port", rs.getInt("port"));

                    resultArray.add(fileData);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultArray.size() == 0) {
            resultArray.add(new JsonObject());
        }

        return resultArray;
    }
}
