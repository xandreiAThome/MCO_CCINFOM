//reupload
package Model;

import HelperClass.UserInput;

import java.sql.*;
import java.util.Date;

public class Customer {
    private int customer_id;
    private String customer_first_name, customer_last_name;
    private Date birth_date;
    private String phone_number, email_address;

    public Customer(int id, String firstName, String lastName, java.sql.Date birthDate, String phoneNum, String email){
        customer_id = id;
        customer_first_name = firstName;
        customer_last_name = lastName;
        birth_date = new Date(birthDate.getTime());
        phone_number = phoneNum;
        email_address = email;
    }

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
                    if(resultSet.next()){
                        System.out.println("Account already exists");
                        return;
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
                        Customer loggedInCustomer = new Customer(res.getInt("customer_id"),
                                res.getString("customer_first_name"), res.getString("customer_last_name"),
                                res.getDate("birth_date"), res.getString("phone_number"),
                                res.getString("email_address"));
                        showCustomerActions(loggedInCustomer);

                    } else {
                        System.out.println("No existing customer record");
                    }
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void showCustomerActions(Customer loggedInSession) {
        int option;

        do {
            System.out.println("1 - View Accounts\n2 - Open New Account\n3 - View Loans\n4 - Avail New Loan");
            System.out.print("Choose option: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());

            switch (option) {
                case 1:
                    System.out.println("View Accounts");
                    break;

                case 2:
                    System.out.println("Open New Account");
                    break;

                case 3:
                    System.out.println("View Loans");
                    break;

                case 4:
                    System.out.println("Avail Loans");
                    break;

                default:
                    System.out.println("\nInvalid option. Please choose again.");
            }
        } while (option < 1 || option > 4);
    }


    public int getCustomer_id(){
        return this.customer_id;
    }

    public String getCustomer_first_name(){
        return customer_first_name;
    }

    public String getCustomer_last_name(){
        return customer_last_name;
    }

    public String getPhone_number(){
        return phone_number;
    }

    public String getEmail_address(){
        return email_address;
    }

    public Date getBirth_date(){
        return birth_date;
    }

}
