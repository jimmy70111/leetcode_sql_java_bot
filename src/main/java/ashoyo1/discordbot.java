
package ashoyo1;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class discordbot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, IOException {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            String token = properties.getProperty("bot.token");

            JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT) 
                    .addEventListeners(new discordbot())
                    .build();
        } catch (IOException | LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        String content = message.getContentRaw();

        if (content.equalsIgnoreCase("!hi")) {
            event.getChannel().sendMessage("Hello! How are you doing?").queue();
        }

        if (content.equals("!getUsers")) {
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
                        response.append("Join Date: ").append(resultSet.getDate("join_date")).append("\n");
                        response.append("Last Login: ").append(resultSet.getTimestamp("last_login")).append("\n");
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
