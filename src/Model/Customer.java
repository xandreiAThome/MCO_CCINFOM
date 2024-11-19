//reupload
package Model;

import HelperClass.UserInput;

import java.sql.*;
import java.util.Scanner;

public class Customer {

    public static void signUp() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {

            // Get user input
            System.out.print("Enter your first name: ");
            String firstName = UserInput.getScanner().nextLine();

            System.out.print("Enter your last name: ");
            String lastName = UserInput.getScanner().nextLine();

            // Check if user already exists
            String checkQuery = "SELECT * FROM customer WHERE customer_first_name = ? AND customer_last_name = ?";
            try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
                checkStatement.setString(1, firstName);
                checkStatement.setString(2, lastName);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("The user already exists in the database.");
                        return; // Exit the method if the user exists
                    }
                }
            }


            System.out.print("Enter your phone number: ");
            String phone = UserInput.getScanner().nextLine();

            System.out.print("Enter your email: ");
            String email = UserInput.getScanner().nextLine();

            System.out.print("Enter your date of birth (YYYY-MM-DD): ");
            String dob = UserInput.getScanner().nextLine();

            // Insert the data into the database
            String insertQuery = "INSERT INTO customer (customer_first_name, customer_last_name, phone_number, email_address, birth_date) " +
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
                    System.out.println("Phone: " + res.getString("phone_number"));
                    System.out.println("Email: " + res.getString("email_address"));
                    System.out.println("Date of Birth: " + res.getString("birth_date"));
                    System.out.println("-----------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void login(){
        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")){
            System.out.print("Input first name: ");
            String firstName = UserInput.getScanner().nextLine();
            System.out.print("Input last name: ");
            String lastName = UserInput.getScanner().nextLine();

            String checkQuery = "SELECT * FROM customer WHERE customer_first_name = ? AND customer_last_name = ?";
            try(PreparedStatement statement = con.prepareStatement(checkQuery)){
                statement.setString(1, firstName);
                statement.setString(2, lastName);

                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        System.out.println("Logged In " + firstName + " " + lastName);
                        Account.getAccount(res.getInt("customer_id"));
                    } else {
                        System.out.println("No existing customer record");
                    }
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
