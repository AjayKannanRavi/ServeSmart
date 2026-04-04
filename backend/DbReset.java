import java.sql.*;

public class DbReset {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/servesmart_db?allowMultiQueries=true";
        String user = "root";
        String pass = "Ajay@111";
        
        String[] tables = {
            "order_items", "restaurant_orders", "payments", "reviews", "customers",
            "daily_usage_logs", "raw_materials", "menu_items", "categories",
            "staff", "restaurant_tables", "restaurant"
        };

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Starting Database Reset...");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                for (String table : tables) {
                    System.out.println("Truncating table: " + table);
                    stmt.execute("TRUNCATE TABLE " + table);
                }
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                System.out.println("Database Reset Successful.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
