package Model;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Date;

public class TransactionHistory {
    private int transaction_id, sender_id, receiver_id;
    private double amount;
    private Date transaction_date;
    private String transaction_status;

    public static void generateAccountTransactionRecord(Integer sender_id, Integer receiver_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password");

            String insert = "INSERT INTO account_transaction_history (amount, " +
                            "transaction_date, transaction_status, sender_acc_id, receiver_acc_id)" +
                            "VALUES (?, NOW(), ?, ?, ?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, "success");
                if(sender_id == null && receiver_id != null){
                    preparedStatement.setNull(3,Types.INTEGER);
                    preparedStatement.setInt(4, receiver_id);
                } else if (sender_id != null && receiver_id == null){
                    preparedStatement.setInt(3,sender_id);
                    preparedStatement.setNull(4, Types.INTEGER);
                } else {
                    preparedStatement.setInt(3,sender_id);
                    preparedStatement.setInt(4, receiver_id);
                }



                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void genereateLoanTransactionRecord(int sender_id, int receiver_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password");

            String insert = "INSERT INTO loan_transaction_history (amount, " +
                    "transaction_date, transaction_status, sender_acc_id, receiver_acc_id)" +
                    "VALUES (?, NOW(), ?, ?, ?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, "success");
                preparedStatement.setInt(3,sender_id);
                preparedStatement.setInt(4, receiver_id);




                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateDailyTransaction(String dateToGenerate){

        double totalOutgoing = 0;
        double totalIncoming = 0;
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            String getDayReportQuery = "SELECT * FROM transaction_history WHERE DATE(transaction_date) = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(getDayReportQuery);
            preparedStatement.setString(1, dateToGenerate);
            ResultSet reportResult = preparedStatement.executeQuery();

            System.out.println("Transaction for that day:");
            while (reportResult.next()){
                System.out.println("------------------------------------------------------");
                System.out.println("Account ID: " + reportResult.getInt("account_id"));
                System.out.println("Transaction Id: " + reportResult.getInt("transaction_id"));
                System.out.println("Transaction Type: " + reportResult.getString("transaction_type"));
                System.out.println("Transaction Amount: ₱" + reportResult.getDouble("amount"));
                System.out.println("Transaction Id: " + reportResult.getDate("transaction_date"));
                System.out.println("Transaction Status: " + reportResult.getString("transaction_status"));
                System.out.println("------------------------------------------------------");
            }

            System.out.println("Total Outgoing: " + totalOutgoing);
            System.out.println("Total Incoming: " + totalIncoming);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateAnnualLoanPayment (String yearToGenerate){
        double totalLoanPayment = 0;
        int totalNumberOfLoanPayment = 0;

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            String getAnnualReportString = "SELECT * FROM transaction_history WHERE YEAR(transaction_date) = ? AND transaction_type = 'loan_payment' ";
            PreparedStatement preparedStatement = connection.prepareStatement(getAnnualReportString);
            preparedStatement.setString(1, yearToGenerate);
            ResultSet reportResult = preparedStatement.executeQuery();

            while (reportResult.next()){
                totalLoanPayment += reportResult.getDouble("amount");
                totalNumberOfLoanPayment++;
            }

            System.out.println("Annual Loan Payment Report");
            System.out.println("January 1, " + yearToGenerate + " - December 31, " + yearToGenerate);
            System.out.println("Total Loan Payment Made: ₱" + Math.round(totalLoanPayment * 100.0) / 100.0);
            System.out.println("Total Number of Loan Payments Made: " + totalNumberOfLoanPayment);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
// haven't tried
    public void generateMonthlySavings(int accountId, String monthToGenerate, String yearToGenerate) {
        double startingBalance = 0;
        double totalOutgoing = 0;
        double totalIncoming = 0;
        double endingBalance = 0;

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );


            String startingBalanceQuery = "SELECT balance FROM account_balance_history " +
                    "WHERE account_id = ? AND DATE_FORMAT(transaction_date, '%Y-%m') < ? " +
                    "ORDER BY transaction_date DESC LIMIT 1";
            PreparedStatement startBalanceStmt = connection.prepareStatement(startingBalanceQuery);
            startBalanceStmt.setInt(1, accountId);
            startBalanceStmt.setString(2, yearToGenerate + "-" + monthToGenerate);
            ResultSet startingBalanceResult = startBalanceStmt.executeQuery();

            if (startingBalanceResult.next()) {
                startingBalance = startingBalanceResult.getDouble("balance");
            }


            String outgoingQuery = "SELECT SUM(amount) AS totalOutgoing FROM transaction_history " +
                    "WHERE account_id = ? AND transaction_type = 'outgoing' " +
                    "AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
            PreparedStatement outgoingStmt = connection.prepareStatement(outgoingQuery);
            outgoingStmt.setInt(1, accountId);
            outgoingStmt.setString(2, yearToGenerate + "-" + monthToGenerate);
            ResultSet outgoingResult = outgoingStmt.executeQuery();

            if (outgoingResult.next()) {
                totalOutgoing = outgoingResult.getDouble("totalOutgoing");
            }


            String incomingQuery = "SELECT SUM(amount) AS totalIncoming FROM transaction_history " +
                    "WHERE account_id = ? AND transaction_type = 'incoming' " +
                    "AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
            PreparedStatement incomingStmt = connection.prepareStatement(incomingQuery);
            incomingStmt.setInt(1, accountId);
            incomingStmt.setString(2, yearToGenerate + "-" + monthToGenerate);
            ResultSet incomingResult = incomingStmt.executeQuery();

            if (incomingResult.next()) {
                totalIncoming = incomingResult.getDouble("totalIncoming");
            }


            endingBalance = startingBalance + totalIncoming - totalOutgoing;


            double moneySaved = startingBalance - endingBalance;


            System.out.println("Monthly Savings Report for " + monthToGenerate + "-" + yearToGenerate);
            System.out.println("Starting Balance: ₱" + Math.round(startingBalance * 100.0) / 100.0);
            System.out.println("Total Outgoing: ₱" + Math.round(totalOutgoing * 100.0) / 100.0);
            System.out.println("Total Incoming: ₱" + Math.round(totalIncoming * 100.0) / 100.0);
            System.out.println("Ending Balance: ₱" + Math.round(endingBalance * 100.0) / 100.0);
            System.out.println("Money Saved: ₱" + Math.round(moneySaved * 100.0) / 100.0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
