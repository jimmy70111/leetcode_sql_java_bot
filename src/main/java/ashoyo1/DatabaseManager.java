package ashoyo1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {


    public static void updateDiscordId(String linkuser, String discorduserId) throws SQLException, IOException {
        Connection connection = sqlconnect.getConnection();
        if (connection != null) {
            String sql = "UPDATE LeetCodeUsers SET discordUserId = ? WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, discorduserId);
                stmt.setString(2, linkuser);
    
                int rowsAffected = stmt.executeUpdate(); 
                if (rowsAffected > 0) {
                    System.out.println("Update successful. Rows affected: " + rowsAffected);
                } else {
                    System.out.println("No rows were updated.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection.close(); // Ensure the connection is closed
            }
        } else {
            System.out.println("Failed to establish database connection.");
        }
    }
    

}