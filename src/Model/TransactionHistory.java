package Model;

import HelperClass.UserInput;

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

    public static void generateLoanTransactionRecord(int sender_id, int receiver_id, double amount){
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
                    "jdbc:mysql://localhost:3306/bankdb",
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
// tried
public void generateMonthlySavings(int customer_id, String monthToGenerate, String yearToGenerate) {
    double totalOutgoing = 0;
    double totalIncoming = 0;

    try {
        // Establish connection to the database
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/bankdb",
                "java",
                "password"
        );

        // Query to compute the total outgoing transactions (where the account is the sender)
        String outgoingQuery = "SELECT SUM(amount) AS totalOutgoing FROM account_transaction_history " +
                "WHERE sender_acc_id = ? " +
                "AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
        PreparedStatement outgoingStmt = connection.prepareStatement(outgoingQuery);
        outgoingStmt.setInt(1, customer_id);
        outgoingStmt.setString(2, yearToGenerate + "-" + monthToGenerate);
        ResultSet outgoingResult = outgoingStmt.executeQuery();

        if (outgoingResult.next()) {
            totalOutgoing = outgoingResult.getDouble("totalOutgoing");
        }

        // Query to compute the total incoming transactions (where the account is the receiver)
        String incomingQuery = "SELECT SUM(amount) AS totalIncoming FROM account_transaction_history " +
                "WHERE receiver_acc_id = ? " +
                "AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
        PreparedStatement incomingStmt = connection.prepareStatement(incomingQuery);
        incomingStmt.setInt(1, customer_id);
        incomingStmt.setString(2, yearToGenerate + "-" + monthToGenerate);
        ResultSet incomingResult = incomingStmt.executeQuery();

        if (incomingResult.next()) {
            totalIncoming = incomingResult.getDouble("totalIncoming");
        }

        // Compute the balance for the month
        double monthlySavings = totalIncoming - totalOutgoing;

        // Display the results
        System.out.println("Monthly Savings Report for " + monthToGenerate + "-" + yearToGenerate);
        System.out.println("Total Incoming: ₱" + Math.round(totalIncoming * 100.0) / 100.0);
        System.out.println("Total Outgoing: ₱" + Math.round(totalOutgoing * 100.0) / 100.0);
        System.out.println("Net Savings: ₱" + Math.round(monthlySavings * 100.0) / 100.0);

        // Close the database connection
        connection.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void viewTransactionHistoryOfAccount(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            System.out.print("Input account id to view transaction history: ");
            int id = Integer.parseInt(UserInput.getScanner().nextLine());

            System.out.println("Sort By\n1 - Date\n2 - Size of Transaction");
            System.out.print("Choose option: ");
            int sort = Integer.parseInt(UserInput.getScanner().nextLine());

            String query = "SELECT * FROM account_transaction_history\n" +
                    "WHERE sender_acc_id = ? OR receiver_acc_id = ?";
            String orderAmt = " ORDER BY amount DESC;";
            String orderDate = " ORDER BY transaction_date DESC;";

            if(sort == 1){
                query += orderDate;
            } else if (sort == 2){
                query += orderAmt;
            }

            try (PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, id);
                statement.setInt(2, id);

                ResultSet res = statement.executeQuery();
                if (!res.isBeforeFirst() ) {
                    System.out.println("Account has no transaction yet");
                }

                while(res.next()){
                    System.out.println("Transaction ID: " + res.getInt("transaction_id") +
                            "\tTransaction Date: " + res.getDate("transaction_date") +
                            "\tSender Acc ID: " + res.getInt("sender_acc_id") +
                            "\tReceiver Acc ID: " + res.getInt("receiver_acc_id") +
                            "\tAmount: " + res.getDouble("amount"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void viewLoanPaymentHistoryOfAccount(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            System.out.print("Input account id to view Loan Payment history: ");
            int id = Integer.parseInt(UserInput.getScanner().nextLine());

            System.out.println("Sort By\n1 - Date\n2 - Size of Payment");
            System.out.print("Choose option: ");
            int sort = Integer.parseInt(UserInput.getScanner().nextLine());

            String query = "SELECT * FROM loan_transaction_history\n" +
                    "WHERE sender_acc_id = ?";
            String orderAmt = " ORDER BY amount DESC;";
            String orderDate = " ORDER BY transaction_date DESC;";

            if(sort == 1){
                query += orderDate;
            } else if (sort == 2){
                query += orderAmt;
            }

            try (PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, id);
                statement.setInt(2, id);

                ResultSet res = statement.executeQuery();
                if (!res.isBeforeFirst() ) {
                    System.out.println("Account has no payment history yet");
                }

                while(res.next()){
                    System.out.println("Payment ID: " + res.getInt("transaction_id") +
                            "\tTransaction Date: " + res.getDate("transaction_date") +
                            "\tSender Acc ID: " + res.getInt("sender_acc_id") +
                            "\tReceiver Loan ID: " + res.getInt("receiver_loan_id") +
                            "\tAmount: " + res.getDouble("amount"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
