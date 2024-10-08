
package ashoyo1;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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
import java.util.Set;




//JDA
// onmessageRecived is auto call by JDA
// JDA internally manages a WebSocket connection to discord server
//  made changes to config email

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





        // get all commands

        if (content.equalsIgnoreCase("!commands")) {
            StringBuilder response = new StringBuilder("Available Commands:\n");
            response.append("!Users - View all users and their details.\n");
            response.append("!link -  link discord user with an leetcode user.\n");
            response.append("!daily -  sends daily to all the linked users\n");
            response.append("!Deleteuser -  !deleteuser follow by the leetcode username to delete from database\n");
            response.append("!get - View the specific user details, call this command by !get username .\n");
            response.append("!leaderboard - View the leaderboard sorted by the highest number of problems solved.\n");
            response.append("!ai - Ask the AI a question related to the Olympics.\n"); // Adding new command

            // Add more command descriptions here as you implement them
        
            event.getChannel().sendMessage(response.toString()).queue();
        }




            // AI command for Olympics-related questions
         if (content.toLowerCase().startsWith("!ai")) {
        String question = content.substring(4).trim();
        if (question.isEmpty()) {
            event.getChannel().sendMessage("Please ask a question about the Olympics after the !ai command.").queue();
        } else {
            String response =  AIClient.getAIResponse(question);
            event.getChannel().sendMessage(response).queue();
        }
    }

    


        // get user info 

        if (content.toLowerCase().startsWith("!get")) {

          
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


        if (content.toLowerCase().startsWith("!link")) {
            String linkuser = content.substring(5).trim(); 
            String  discorduserId = event.getAuthor().getId();  

            try {
                DatabaseManager.updateDiscordId(linkuser, discorduserId);
                event.getChannel().sendMessage("Successfully linked your Discord ID with LeetCode user: " + linkuser).queue();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("Fail to linked").queue();
            }
        }





// daily and send to user 

        if (content.toLowerCase().startsWith("!daily")) {
            try {
                // Retrieve the daily problem and Discord user IDs
                String problem = DatabaseManager.getRandomProblem();
                Set<String> discordUserIds = DatabaseManager.getDiscordIDs();
                String daily = "Here is the problem of the day! " + problem;

                // Send the daily problem to each user
                for (String userId : discordUserIds) {
                    User user = event.getJDA().getUserById(userId);
                    if (user != null) {
                        user.openPrivateChannel().queue(privateChannel -> 
                            privateChannel.sendMessage(daily).queue()
                        );
                    }
                }
            } catch (SQLException e) {
                // Handle SQL exception
                e.printStackTrace();
            } catch (IOException e) {
                // Handle IO exception
                e.printStackTrace();
            } catch (Exception e) {
                // Handle any other exceptions
                e.printStackTrace();
            }
        }






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





// delete user from the database 

        if (content.toLowerCase().startsWith("!deleteuser")) {
            String usernameToDelete = content.substring(11).trim();
        
            if (!usernameToDelete.isEmpty()) {
                try {
                    Connection connection = sqlconnect.getConnection();
                    if (connection != null) {
                        String query = "DELETE FROM LeetCodeUsers WHERE username = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
        
                        preparedStatement.setString(1, usernameToDelete);
                        int rowsAffected = preparedStatement.executeUpdate();
        
                        if (rowsAffected > 0) {
                            event.getChannel().sendMessage("User " + usernameToDelete + " has been deleted.").queue();
                        } else {
                            event.getChannel().sendMessage("User " + usernameToDelete + " not found.").queue();
                        }
        
                        preparedStatement.close();
                        connection.close();
                    } else {
                        event.getChannel().sendMessage("Failed to establish a database connection.").queue();
                    }
                } catch (SQLException e) {
                    event.getChannel().sendMessage("SQL error: " + e.getMessage()).queue();
                    e.printStackTrace(); // For debugging purposes
                } catch (IOException e) {
                    event.getChannel().sendMessage("IO error: " + e.getMessage()).queue();
                    e.printStackTrace(); // For debugging purposes
                }
            } else {
                event.getChannel().sendMessage("Usage: !deleteuser <username>").queue();
            }
        }
        







        // get leaderboard of all on table users 

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

