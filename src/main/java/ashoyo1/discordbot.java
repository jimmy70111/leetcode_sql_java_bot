
package ashoyo1;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final String API_URL = "https://leetcode-stats-api.herokuapp.com/";


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

        if (content.equalsIgnoreCase("!commands")) {
            StringBuilder response = new StringBuilder("Available Commands:\n");
            response.append("!Users - View all users and their details.\n");
            response.append("!get - View the specific user details, call this command by !get username .\n");
            response.append("!leaderboard - View the leaderboard sorted by the highest number of problems solved.\n");
            // Add more command descriptions here as you implement them
        
            event.getChannel().sendMessage(response.toString()).queue();
        }


        if (content.toLowerCase().startsWith("!get")) {
            // String user = content.substring(5).trim(); 
        
            // try {
            //     Connection connection = sqlconnect.getConnection();
            //     if (connection != null) {

            //         // use preparedStatement to set to index
            //         String query = "SELECT * FROM LeetCodeUsers WHERE username = ?";
            //         PreparedStatement preparedStatement = connection.prepareStatement(query);
            //         preparedStatement.setString(1, user); 
        
            //         ResultSet resultSet = preparedStatement.executeQuery();
        
            //         StringBuilder response = new StringBuilder("LeetCode User:\n");
            //         if (resultSet.next()) {
            //             response.append("User ID: ").append(resultSet.getInt("user_id")).append("\n");
            //             response.append("Username: ").append(resultSet.getString("username")).append("\n");
            //             response.append("Email: ").append(resultSet.getString("email")).append("\n");
            //             response.append("Problems Solved: ").append(resultSet.getInt("problems")).append("\n");
            //             response.append("Easy: ").append(resultSet.getInt("Easy")).append("\n");
            //             response.append("Medium: ").append(resultSet.getInt("Medium")).append("\n");
            //             response.append("Hard: ").append(resultSet.getInt("Hard")).append("\n\n");
            //         } else {
            //             response = new StringBuilder("No user found with username: ").append(user);
            //         }
        
            //         event.getChannel().sendMessage(response.toString()).queue();
        
            //         resultSet.close();
            //         preparedStatement.close();
            //         connection.close();
            //     } else {
            //         event.getChannel().sendMessage("Failed to establish database connection.").queue();
            //     }
            // } catch (SQLException | IOException e) {
            //     event.getChannel().sendMessage("Database error: " + e.getMessage()).queue();
            // }


          
            String user = content.substring(5).trim(); 

        try {
            String apiUrl = API_URL + user;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Print the raw response
               // System.out.println("Response: " + response.toString());

                // Parse JSON response
                JSONObject jsonObject = new JSONObject(response.toString());
                StringBuilder responseMessage = new StringBuilder("LeetCode User:\n");

                int totalSolved = jsonObject.getInt("totalSolved");
                int easySolved = jsonObject.getInt("easySolved");
                int mediumSolved = jsonObject.getInt("mediumSolved");
                int hardSolved = jsonObject.getInt("hardSolved");
                double acceptanceRate = jsonObject.getDouble("acceptanceRate");

                //responseMessage.append("Username: ").append(user).append("\n");
                responseMessage.append("Total Solved: ").append(totalSolved).append("\n");
                responseMessage.append("Easy Solved: ").append(easySolved).append("\n");
                responseMessage.append("Medium Solved: ").append(mediumSolved).append("\n");
                responseMessage.append("Hard Solved: ").append(hardSolved).append("\n");
                responseMessage.append("Acceptance Rate: ").append(acceptanceRate).append("%\n");

                event.getChannel().sendMessage(responseMessage.toString()).queue();



                try (Connection dbConnection = sqlconnect.getConnection();
                PreparedStatement statement = dbConnection.prepareStatement(
                        "INSERT INTO LeetCodeUsers (username, problems, Easy, Medium, Hard, acceptanceRate) " +
                                "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                                "problems = VALUES(problems), Easy = VALUES(Easy), Medium = VALUES(Medium), " +
                                "Hard = VALUES(Hard), acceptanceRate = VALUES(acceptanceRate)")) {

               statement.setString(1, user);
               statement.setInt(2, totalSolved);
               statement.setInt(3, easySolved);
               statement.setInt(4, mediumSolved);
               statement.setInt(5, hardSolved);
               statement.setDouble(6, acceptanceRate);

               statement.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }

                
            } else {
                System.out.println("HTTP GET request failed: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

            
        }





// linked user

        if (content.toLowerCase().startsWith("!Link")) {
            String linkuser = content.substring(5).trim(); 
            String discorduserId = event.getAuthor().getId();

            try {
                DatabaseManager.updateDiscordId(linkuser, discorduserId);
                event.getChannel().sendMessage("Successfully linked your Discord ID with LeetCode user: " + linkuser).queue();

            } catch (SQLException | IOException e) {

                e.printStackTrace();
                event.getChannel().sendMessage("error").queue();
            }
            

        }

// Setting up dailies and users will get a problem to solve 
        

// getr all users 
     
        if (content.equalsIgnoreCase("!Users")) {
            try {
                Connection connection = sqlconnect.getConnection();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM LeetCodeUsers");

                    StringBuilder response = new StringBuilder("LeetCode Users:\n");
                    while (resultSet.next()) {
                       // response.append("User ID: ").append(resultSet.getInt("user_id")).append("\n");
                        response.append("Username: ").append(resultSet.getString("username")).append("\n");
                        //response.append("Email: ").append(resultSet.getString("email")).append("\n");
                        response.append("Problems: ").append(resultSet.getInt("problems")).append("\n");
                    
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



        if (content.equalsIgnoreCase("!Leaderboard")) {
            try {
                Connection connection = sqlconnect.getConnection();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM LeetCodeUsers ORDER BY problems DESC LIMIT 10 ");

                    StringBuilder response = new StringBuilder("Leaderboard:\n");
                    while (resultSet.next()) {
                        response.append("Username: ").append(resultSet.getString("username")).append(" | ");
                        response.append("Problems: ").append(resultSet.getInt("problems")).append("\n");
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