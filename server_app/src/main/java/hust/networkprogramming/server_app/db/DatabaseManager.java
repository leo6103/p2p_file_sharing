package hust.networkprogramming.server_app.db;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/serverdb/my_database.db";

    public static void checkAndCreateTables() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "ip TEXT," +
                "port INTEGER" +
                ");";

        String createFilesTableSQL = "CREATE TABLE IF NOT EXISTS files (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "file_path TEXT NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        String createSessionsTableSQL = "CREATE TABLE IF NOT EXISTS sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cookie TEXT NOT NULL," +
                "expiry_date TEXT NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        try (
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(createUsersTableSQL);
            stmt.execute(createFilesTableSQL);
            stmt.execute(createSessionsTableSQL);

            System.out.println("Tables are ready.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        connection.setAutoCommit(true);

        return connection;
    }
}
