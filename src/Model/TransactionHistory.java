package Model;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Date;

public class TransactionHistory {
    private int transaction_id, account_id, loan_id;
    private String transaction_type;
    private double amount;
    private Date transaction_date;
    private String transaction_status;

    public static void generateTransactionRecord(String transaction_type,
                                                 Integer account_id, Integer loan_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password");

            String insert = "INSERT INTO transaction_history (transaction_type, amount, " +
                            "transaction_date, transaction_status, account_id, loan_id)" +
                            "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setString(1, transaction_type);
                preparedStatement.setDouble(2, amount);
                java.util.Date curr = new java.util.Date();
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(curr.getTime());
                preparedStatement.setTimestamp(3, sqlDate);
                preparedStatement.setString(4, "success");
                if(loan_id == null){
                    preparedStatement.setInt(5, account_id);
                    preparedStatement.setNull(6, Types.INTEGER);
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                    preparedStatement.setInt(6, loan_id);
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

    public void generateDailyTransaction(String dateToGenerate){

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

    public void generateAnnualLoanPayment (String yearToGenerate){
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
}
