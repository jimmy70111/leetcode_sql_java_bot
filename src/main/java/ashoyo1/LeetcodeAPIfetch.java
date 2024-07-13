package ashoyo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Import JSON library
import org.json.JSONObject;

public class LeetcodeAPIfetch {
    private static final String API_URL = "https://leetcode-stats-api.herokuapp.com/";

    public static void main(String[] args) {
        String username = "ashoyo1"; // Replace with your LeetCode username
        try {
            String apiUrl = API_URL + username;
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

                // Print specific fields
               // System.out.println("Status: " + jsonObject.getString("status"));
            //    System.out.println("Message: " + jsonObject.getString("message"));
                System.out.println("Total Solved: " + jsonObject.getInt("totalSolved"));
           //     System.out.println("Total Questions: " + jsonObject.getInt("totalQuestions"));
                System.out.println("Easy Solved: " + jsonObject.getInt("easySolved"));
          //      System.out.println("Total Easy: " + jsonObject.getInt("totalEasy"));
                System.out.println("Medium Solved: " + jsonObject.getInt("mediumSolved"));
           //     System.out.println("Total Medium: " + jsonObject.getInt("totalMedium"));
                System.out.println("Hard Solved: " + jsonObject.getInt("hardSolved"));
          //      System.out.println("Total Hard: " + jsonObject.getInt("totalHard"));
                System.out.println("Acceptance Rate: " + jsonObject.getDouble("acceptanceRate"));
        //        System.out.println("Ranking: " + jsonObject.getInt("ranking"));
        //        System.out.println("Contribution Points: " + jsonObject.getInt("contributionPoints"));
        //        System.out.println("Reputation: " + jsonObject.getInt("reputation"));
        //        System.out.println("Submission Calendar: " + jsonObject.getJSONObject("submissionCalendar"));
                
            } else {
                System.out.println("HTTP GET request failed: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
