package ashoyo1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {


    public static void updateDiscordId(String linkuser,String discorduserId) throws SQLException, IOException{

        Connection connection = sqlconnect.getConnection();
        if (connection != null) {
            String sql = "UPDATE LeetCodeUsers SET discordUserId = ? WHERE username  = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, discorduserId);
                stmt.setString(2, linkuser);

              

            } catch (SQLException e) {
                e.printStackTrace();
            }


    }
    
}

}