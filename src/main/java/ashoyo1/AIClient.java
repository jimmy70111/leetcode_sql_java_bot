package ashoyo1;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class AIClient {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";

    public static String getAIResponse(String question) {
        try {
            // Create URL object
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the JSON payload
            JSONObject payload = new JSONObject();
            payload.put("model", "meta-llama/llama-3.1-8b-instruct:free");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are a chatbot designed to answer questions specifically about leetcode and coding in java on leetcode"));
            messages.put(new JSONObject().put("role", "user").put("content", question));
            payload.put("messages", messages);
            payload.put("top_p", 1);
            payload.put("temperature", 1);
            payload.put("repetition_penalty", 1);

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse the response
                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.has("choices")) {
                    JSONArray choices = responseJson.getJSONArray("choices");
                    if (choices.length() > 0) {
                        return choices.getJSONObject(0).getJSONObject("message").getString("content");
                    }
                }
                return "Hello";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while retrieving the AI response.";
        }
    }
}
