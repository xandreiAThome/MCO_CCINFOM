package Model;

import java.sql.*;
import java.util.Scanner;

public class UserInfo {
    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password");
             Scanner scanner = new Scanner(System.in)) {

            // Get user input
            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            System.out.print("Enter your date of birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();

            // Insert the data into the database
            String insertQuery = "INSERT INTO customer (customer_first_name, customer_last_name, phone, email, date_of_birth) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, phone);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, dob);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Customer information added successfully!");
                }
            }

            // Retrieve and display all records from the customer table
            String selectQuery = "SELECT * FROM customer";
            try (Statement statement = con.createStatement();
                 ResultSet res = statement.executeQuery(selectQuery)) {

                System.out.println("\nCustomer Records:");
                while (res.next()) {
                    System.out.println("First Name: " + res.getString("customer_first_name"));
                    System.out.println("Last Name: " + res.getString("customer_last_name"));
                    System.out.println("Phone: " + res.getString("phone"));
                    System.out.println("Email: " + res.getString("email"));
                    System.out.println("Date of Birth: " + res.getString("date_of_birth"));
                    System.out.println("-----------------------");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
