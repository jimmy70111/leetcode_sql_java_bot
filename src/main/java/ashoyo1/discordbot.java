
package ashoyo1;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

import com.mysql.cj.xdevapi.PreparableStatement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;




//JDA
// onmessageRecived is auto call by JDA
// JDA internally manages a WebSocket connection to discord server
// 

public class discordbot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, IOException {
        // properties objevt to load 

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            //LOAD AND retrieve
            properties.load(input);
            String token = properties.getProperty("bot.token");



            JDABuilder.createDefault(token)
            // enable bot to see mesgge 
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT) 
                    .addEventListeners(new discordbot()) // register bot class to event listener
                    .build(); // build jda instances 
        } catch (IOException | LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event) {

        // users from bot mesg and get raw of it 
        Message message = event.getMessage();
        String content = message.getContentRaw();

        // queue make it wait in line of thread 

        // get user profile method


        if (content.toLowerCase().startsWith("!get")) {
            String user = content.substring(5).trim(); 
        
            try {
                Connection connection = sqlconnect.getConnection();
                if (connection != null) {

                    // use preparedStatement to set to index
                    String query = "SELECT * FROM LeetCodeUsers WHERE username = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user); 
        
                    ResultSet resultSet = preparedStatement.executeQuery();
        
                    StringBuilder response = new StringBuilder("LeetCode User:\n");
                    if (resultSet.next()) {
                        response.append("User ID: ").append(resultSet.getInt("user_id")).append("\n");
                        response.append("Username: ").append(resultSet.getString("username")).append("\n");
                        response.append("Email: ").append(resultSet.getString("email")).append("\n");
                        response.append("Problems Solved: ").append(resultSet.getInt("problems")).append("\n");
                    } else {
                        response = new StringBuilder("No user found with username: ").append(user);
                    }
        
                    event.getChannel().sendMessage(response.toString()).queue();
        
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } else {
                    event.getChannel().sendMessage("Failed to establish database connection.").queue();
                }
            } catch (SQLException | IOException e) {
                event.getChannel().sendMessage("Database error: " + e.getMessage()).queue();
            }
        }
        

   


// getr all users 
     
        if (content.equalsIgnoreCase("!getUsers")) {
            try {
                Connection connection = sqlconnect.getConnection();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM LeetCodeUsers");

                    StringBuilder response = new StringBuilder("LeetCode Users:\n");
                    while (resultSet.next()) {
                        response.append("User ID: ").append(resultSet.getInt("user_id")).append("\n");
                        response.append("Username: ").append(resultSet.getString("username")).append("\n");
                        response.append("Email: ").append(resultSet.getString("email")).append("\n");
                        response.append("Problems: ").append(resultSet.getInt("problems")).append("\n\n");
                    }

                    event.getChannel().sendMessage(response.toString()).queue();

                    resultSet.close();
                    statement.close();
                    connection.close();
                }
            } catch (SQLException | IOException e) {
                event.getChannel().sendMessage("Database error: " + e.getMessage()).queue();
            }
        }




    }
}