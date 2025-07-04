package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/mooc_system";
            String username = "root"; // change if needed
            String password = "12345Sp@";     // change if needed

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }

        return conn;
    }

    // ➕ Add this main method for testing
    public static void main(String[] args) {
        getConnection();  // just call the connection to test
    }
}
