package ashoyo1;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class sqlconnect {


        // Step 1: Load JDBC Driver
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new ExceptionInInitializerError(e);
            }
        }



    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    // public static void insertSampleData(Connection connection) throws SQLException {
    //     String insertSQL = "INSERT INTO LeetCodeUsers (user_id, username, email, join_date, last_login) VALUES (?, ?, ?, ?, ?)";
    //     try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
    //         preparedStatement.setInt(1, 1);
    //         preparedStatement.setString(2, "john_doe");
    //         preparedStatement.setString(3, "john_doe@example.com");
    //         preparedStatement.setDate(4, java.sql.Date.valueOf("2022-01-01"));
    //         preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf("2022-01-01 12:00:00"));
    //         preparedStatement.executeUpdate();

    //         preparedStatement.setInt(1, 2);
    //         preparedStatement.setString(2, "jane_smith");
    //         preparedStatement.setString(3, "jane_smith@example.com");
    //         preparedStatement.setDate(4, java.sql.Date.valueOf("2022-02-01"));
    //         preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf("2022-02-01 13:00:00"));
    //         preparedStatement.executeUpdate();
    //     }
    // }

    public static void main(String[] args) {
        try {
            Connection connection = sqlconnect.getConnection();
            if (connection != null) {
                System.out.println("Connection successful!");

              // Insert sample data
              //  insertSampleData(connection);

                // Create statement for sql commands

                Statement statement = connection.createStatement();
                

                // Execute query get excution

                ResultSet resultSet = statement.executeQuery("SELECT * FROM LeetCodeUsers");

                // Process results from data
                
                while (resultSet.next()) {
                    System.out.println("User ID: " + resultSet.getInt("user_id"));
                    System.out.println("Username: " + resultSet.getString("username"));
                    System.out.println("Email: " + resultSet.getString("email"));
           
                    System.out.println("Problems: " + resultSet.getInt("problems"));
                }

                // Close resources
                resultSet.close();
                statement.close();
                connection.close();
            } 

        } catch (SQLException | IOException e) {
            System.err.println(" error " + e.getMessage());
        }
    }
}
