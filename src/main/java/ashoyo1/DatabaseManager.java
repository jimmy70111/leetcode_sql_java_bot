package ashoyo1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {


    private static final String getRandProblem = "SELECT url FROM leetcodeproblems ORDER BY RAND() LIMIT 1";
    private static final String getDiscordUsers =  "SELECT discordUserId FROM LeetCodeUsers WHERE discordUserId IS NOT NULL";


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



    private String getRandomProblem() throws SQLException, IOException {
        String url = null;
        try (Connection connection = sqlconnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(getRandProblem);
             ResultSet resultSet = statement.executeQuery()) {
    
            if (resultSet.next()) {
                url = resultSet.getString("url");
            }
        }
    
        return url; 
    }



    private Set<String>  getDiscordIDs() throws SQLException, IOException {

        Set<String> userIds = new HashSet<>();
        try (Connection connection = sqlconnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(getDiscordUsers);
             ResultSet resultSet = statement.executeQuery()) {
    
            while(resultSet.next()) {
                userIds.add(resultSet.getString("discordUserId"));
            }
        }
    
        return userIds;
    }




    




    

}