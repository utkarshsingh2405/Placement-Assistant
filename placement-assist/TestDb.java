import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestDb {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Connection conn = DriverManager.getConnection("jdbc:sqlite:data/portal.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, email, password_hash, role FROM portal_users");
        while (rs.next()) {
            String email = rs.getString("email");
            String hash = rs.getString("password_hash");
            System.out.println("USER: " + email + " | Role: " + rs.getString("role"));
            System.out.println("  Hash: " + hash);
            System.out.println("  Matches 'Tpo@123': " + encoder.matches("Tpo@123", hash));
            System.out.println("  Matches 'Admin@123': " + encoder.matches("Admin@123", hash));
            System.out.println("  Matches 'Password@123': " + encoder.matches("Password@123", hash));
        }
        conn.close();
    }
}
