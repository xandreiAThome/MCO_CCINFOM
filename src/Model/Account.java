package Model;

import HelperClass.UserInput;

import javax.xml.transform.Result;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Account {
    private int account_id;
    private String account_type;
    private double current_balance;
    private java.util.Date date_opened;
    private double interest_rate;
    private String account_status;
    private int customer_id;

    public Account(int account_id, String account_type, double current_balance,
                   Date date_opened, double interest_rate, String account_status, int customer_id){
        this.account_id = account_id;
        this.account_type = account_type;
        this.current_balance = current_balance;
        this.date_opened = date_opened;
        this.interest_rate = interest_rate;
        this.account_status =account_status;
        this.customer_id = customer_id;
    }

    public static void showAccounts(int customer_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            String checkQuery = "SELECT * FROM account WHERE customer_id = ?";

            try(PreparedStatement statement = con.prepareStatement(checkQuery)){
                statement.setInt(1, customer_id);

                try(ResultSet res = statement.executeQuery()){
                    while(res.next()){
                        System.out.print("Account ID: " + res.getInt("account_id"));
                        System.out.println("\tAccount type: " +res.getString("account_type"));
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        System.out.print("Input Account ID to Open Account: ");
        int acc_id = Integer.parseInt(UserInput.getScanner().nextLine());
        viewAccountInfo(acc_id, customer_id);
    }

    public static void viewAccountInfo (int account_id, int customer_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            String checkQuery = "SELECT * FROM account WHERE account_id = ? AND customer_id = ?";

            try(PreparedStatement statement = con.prepareStatement(checkQuery)){
                statement.setInt(1, account_id);
                statement.setInt(2, customer_id);

                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        System.out.print("Account ID: " + res.getInt("account_id"));
                        System.out.println("\tAccount type: " +res.getString("account_type"));
                        System.out.println("Current Balance: " + res.getDouble("current_balance"));
                    } else {
                        System.out.println("Account id doesn't exist for current customer");
                        return;
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        int option;
        do{
            System.out.println("1 - Deposit to Account\n2 - Withdraw from Account\n3 - Transfer to another Account" +
                    "\n4 - View Statement Of Account");
            System.out.println("Choose option: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        } while(option < 1 || option > 4);

        switch (option){
            case 1:
                depositToAccount(account_id);
                break;
            case 2:
                withdrawFromAccount(account_id);
                break;
            case 3:
                transferToAnother(account_id);
                break;
            case 4:
                showMonthStatementOfAccount(account_id);
                break;
        }

    }

    public static void depositToAccount(int account_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            System.out.print("Input amount to deposit: ");
            double amount = Double.parseDouble(UserInput.getScanner().nextLine());
            String query = "UPDATE account SET current_balance = current_balance + ? WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(query)){
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    TransactionHistory.generateAccountTransactionRecord(null, account_id, amount);
                    System.out.println("Deposited Successfully");
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void withdrawFromAccount(int account_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            System.out.print("Input amount to withdraw: ");
            double amount = Double.parseDouble(UserInput.getScanner().nextLine());

            String query = "SELECT current_balance FROM account WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(query)){
                statement.setInt(1, account_id);

                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        if(amount > res.getDouble("current_balance")){
                            System.out.println("Insufficient Balance");
                            return;
                        }
                    }
                }
            }

            String updateQuery = "UPDATE account SET current_balance = current_balance - ? WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(updateQuery)){
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    TransactionHistory.generateAccountTransactionRecord(account_id, null, amount);
                    System.out.println("Withdrawn Successfully");
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void transferToAnother(int account_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            System.out.print("Input amount to transfer: ");
            double amount = Double.parseDouble(UserInput.getScanner().nextLine());

            String query = "SELECT current_balance FROM account WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(query)){
                statement.setInt(1, account_id);

                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        if(amount > res.getDouble("current_balance")){
                            System.out.println("Insufficient Balance");
                            return;
                        }
                    }
                }
            }
            System.out.print("Input Account ID to transfer to: ");
            int dest_id = Integer.parseInt(UserInput.getScanner().nextLine());

            String transferQuery = "UPDATE account SET current_balance = current_balance + ? WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(transferQuery)){
                statement.setDouble(1, amount);
                statement.setInt(2, dest_id);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Account doesn't exist");
                    return;
                }
            }

            String deductQuery = "UPDATE account SET current_balance = current_balance - ? WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(deductQuery)){
                statement.setDouble(1, amount);
                statement.setInt(2, account_id);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transferred Successfully");
                    TransactionHistory.generateAccountTransactionRecord(account_id, dest_id, amount);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void createNewAccount(int customer_id) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {


            String account_type = "";
            int choice;
            do {
                System.out.println("Choose Account Type:");
                System.out.println("1 - Savings");
                System.out.println("2 - Checkings");
                System.out.println("3 - Passbook");
                choice = Integer.parseInt(UserInput.getScanner().nextLine());
                switch (choice) {
                    case 1:
                        account_type = "savings";
                        break;
                    case 2:
                        account_type = "checkings";
                        break;
                    case 3:
                        account_type = "passbook";
                        break;
                    default:
                        System.out.println("Invalid choice. Please select again.");
                }
            } while (choice < 1 || choice > 3);

            // Prompt user for an initial deposit
            System.out.print("Enter Initial Deposit Amount: ");
            double initial_deposit = Double.parseDouble(UserInput.getScanner().nextLine());

            // Insert new account into the database
            String insertQuery = "INSERT INTO account (account_type, current_balance, date_opened, account_status, customer_id) " +
                    "VALUES (?, ?, NOW(), 'Active', ?)";
            try (PreparedStatement statement = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, account_type);
                statement.setDouble(2, initial_deposit);
                statement.setInt(3, customer_id);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Retrieve the generated account ID
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newAccountId = generatedKeys.getInt(1);
                            System.out.println("Account created successfully!");
                            System.out.println("New Account ID: " + newAccountId);
                            System.out.println("Account Type: " + account_type);
                            System.out.println("Current Balance: " + initial_deposit);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    public static void showMonthStatementOfAccount(int account_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            Calendar cal = Calendar.getInstance();
            System.out.print("Input year to view: ");
            int year = Integer.parseInt(UserInput.getScanner().nextLine());
            System.out.print("Input month to view: ");
            int month = Integer.parseInt(UserInput.getScanner().nextLine());

            System.out.println("Statement of Account " +
                    new DateFormatSymbols().getMonths()[month-1] + " " + year);

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.YEAR, year);

            String outgoing = "SELECT SUM(amount) FROM account_transaction_history th\n" +
                    "WHERE sender_acc_id = ? \n" +
                    "AND MONTH(transaction_date) = MONTH(?)" +
                    "AND YEAR(transaction_date) = YEAR(?);";

            String incoming = "SELECT SUM(amount) FROM account_transaction_history th\n" +
                    "WHERE receiver_acc_id = ?\n" +
                    "AND MONTH(transaction_date) = MONTH(?)" +
                    "AND YEAR(transaction_date) = YEAR(?);";

            try(PreparedStatement statement = con.prepareStatement(outgoing)){
                statement.setInt(1, account_id);
                java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
                statement.setDate(2, sqlDate);
                statement.setDate(3, sqlDate);
                System.out.println(sqlDate);


                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        System.out.println("Outgoing: Php " + res.getDouble(1));
                    }
                }
            }

            try(PreparedStatement statement = con.prepareStatement(incoming)){
                statement.setInt(1, account_id);
                java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
                statement.setDate(2, sqlDate);
                statement.setDate(3, sqlDate);


                try(ResultSet res = statement.executeQuery()){
                    if(res.next()){
                        System.out.println("Incoming: Php " + res.getDouble(1));
                    }
                }
            }

            String TransacHistory = "SELECT * FROM account_transaction_history th\n" +
                    "WHERE sender_acc_id = ? OR receiver_acc_id = ?\n" +
                    "AND MONTH(transaction_date) = MONTH(?)\n" +
                    "AND YEAR(transaction_date) = YEAR(?)\n" +
                    "ORDER BY transaction_date;";

            try(PreparedStatement statement = con.prepareStatement(TransacHistory)){
                statement.setInt(1, account_id);
                statement.setInt(2, account_id);
                java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
                statement.setDate(3, sqlDate);
                statement.setDate(4, sqlDate);

                try(ResultSet res = statement.executeQuery()){
                    System.out.printf("%10s  %-10s  %-9s%n", "Transaction", "Date", "Amount");
                    while(res.next()){
                        String amt = "Php " + res.getDouble("amount");

                        if(res.getInt("sender_acc_id") != account_id){
                            System.out.printf("%-10s  %-9s  %-9s%n", "Incoming",
                                    res.getDate("transaction_date"), amt);
                        } else {
                            System.out.printf("%-10s  %-9s  %-9s%n", "Outgoing",
                                    res.getDate("transaction_date"), amt);
                        }
                    }
                }
            }


        } catch (SQLException e){
            e.printStackTrace();


        }
    }

    public static void closeAccount(int account_id) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {

            // Step 1: Verify the account balance is zero and no pending loans
            String checkBalanceQuery = "SELECT current_balance, account_status FROM account WHERE account_id = ?";
            double currentBalance = 0.0;
            String accountStatus = "";

            try (PreparedStatement statement = con.prepareStatement(checkBalanceQuery)) {
                statement.setInt(1, account_id);
                try (ResultSet res = statement.executeQuery()) {
                    if (res.next()) {
                        currentBalance = res.getDouble("current_balance");
                        accountStatus = res.getString("account_status");
                    } else {
                        System.out.println("Account not found.");
                        return;
                    }
                }
            }

            if (currentBalance != 0) {
                System.out.println("Account cannot be closed. Balance is not zero.");
                return;
            }

            if (!accountStatus.equalsIgnoreCase("Active")) {
                System.out.println("Account cannot be closed. It is not in an Active state.");
                return;
            }

            String checkLoansQuery = "SELECT COUNT(*) FROM loans WHERE account_id = ? AND loan_status = 'Pending'";
            try (PreparedStatement statement = con.prepareStatement(checkLoansQuery)) {
                statement.setInt(1, account_id);
                try (ResultSet res = statement.executeQuery()) {
                    if (res.next() && res.getInt(1) > 0) {
                        System.out.println("Account cannot be closed. There are pending loans.");
                        return;
                    }
                }
            }

            // Step 2: Check for any pending transactions or fees
            String checkPendingTransactionsQuery = "SELECT COUNT(*) FROM transaction_history WHERE account_id = ? AND transaction_status = 'Pending'";
            try (PreparedStatement statement = con.prepareStatement(checkPendingTransactionsQuery)) {
                statement.setInt(1, account_id);
                try (ResultSet res = statement.executeQuery()) {
                    if (res.next() && res.getInt(1) > 0) {
                        System.out.println("Account cannot be closed. There are pending transactions or fees.");
                        return;
                    }
                }
            }

            // Step 3: Update the account status to "Closed"
            String updateAccountQuery = "UPDATE account SET account_status = 'Closed' WHERE account_id = ?";
            try (PreparedStatement statement = con.prepareStatement(updateAccountQuery)) {
                statement.setInt(1, account_id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account status updated to Closed.");
                } else {
                    System.out.println("Failed to update account status.");
                    return;
                }
            }

            // Step 4: Record the account closure in Transaction History
            String insertTransactionQuery = "INSERT INTO transaction_history (account_id, transaction_type, transaction_date, amount) " +
                    "VALUES (?, 'Account Closure', NOW(), 0.0)";
            try (PreparedStatement statement = con.prepareStatement(insertTransactionQuery)) {
                statement.setInt(1, account_id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account closure recorded in transaction history.");
                } else {
                    System.out.println("Failed to record account closure.");
                }
            }

            System.out.println("Account closed successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public int getCustomer_id (){
        return  customer_id;
    }

    public int getAccount_id(){
        return account_id;
    }

    public String getAccount_type(){
        return account_type;
    }

    public String getAccount_status(){
        return account_status;
    }

    public double getCurrent_balance(){
        return current_balance;
    }

    public double getInterest_rate(){
        return interest_rate;
    }

    public Date getDate_opened(){
        return date_opened;
    }

}
