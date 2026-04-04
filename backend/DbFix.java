import java.sql.*;

public class DbFix {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/servesmart_db";
        String user = "root";
        String pass = "Ajay@111";
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Updating Tables for Restaurant 1...");
            try (Statement stmt = conn.createStatement()) {
                // Fix Table 1
                stmt.executeUpdate("DELETE FROM restaurant_tables WHERE table_number = 1");
                stmt.executeUpdate("INSERT INTO restaurant_tables (table_number, restaurant_id, status, qr_generated, qr_code_url) VALUES (1, 1, 'AVAILABLE', 0, 'http://localhost:5173/login?hotelId=1&tableId=1')");
                System.out.println("Table 1 Fixed.");

                // Fix Table 2
                stmt.executeUpdate("DELETE FROM restaurant_tables WHERE table_number = 2");
                stmt.executeUpdate("INSERT INTO restaurant_tables (table_number, restaurant_id, status, qr_generated, qr_code_url) VALUES (2, 1, 'AVAILABLE', 0, 'http://localhost:5173/login?hotelId=1&tableId=2')");
                System.out.println("Table 2 Fixed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
