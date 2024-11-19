package Model;

import HelperClass.UserInput;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

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
        viewAccountInfo(acc_id);
    }

    public static void viewAccountInfo (int account_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            String checkQuery = "SELECT * FROM account WHERE account_id = ?";

            try(PreparedStatement statement = con.prepareStatement(checkQuery)){
                statement.setInt(1, account_id);

                try(ResultSet res = statement.executeQuery()){
                    while(res.next()){
                        System.out.print("Account ID: " + res.getInt("account_id"));
                        System.out.println("\tAccount type: " +res.getString("account_type"));
                        System.out.println("Current Balance: " + res.getDouble("current_balance"));
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        int option;
        do{
            System.out.println("1 - Deposit to Account\n2 - Withdraw from Account\n3 - Transfer to another Account");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        } while(option < 1 || option > 3);

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
                    TransactionHistory.generateTransactionRecord("deposit", account_id, null, amount);
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
                }
            }

        } catch (SQLException e){
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
