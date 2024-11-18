package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class TransactionHistory {
    private int transaction_id, account_id, loan_id;
    private String transaction_type;
    private double amount;
    private Date transaction_date;
    private String transaction_status;

    public static void generateTransactionRecord(int transaction_id, String transaction_type,
                                                 int account_id, int loan_id, double amount){
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password");

            String insert = "INSERT INTO transaction_history (transaction_id, transaction_type, amount, " +
                            "transaction_date, transaction_status, account_id, loan_id)" +
                            "VALUES (?, ?, ?, ?, ?, ? ,?)";

            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setInt(1, transaction_id);
                preparedStatement.setString(2, transaction_type);
                preparedStatement.setDouble(3, amount);
                java.util.Date curr = new java.util.Date();
                java.sql.Date sqlDate = new java.sql.Date(curr.getTime());
                preparedStatement.setDate(4, sqlDate);
                preparedStatement.setString(5, "success");
                preparedStatement.setInt(6, account_id);
                preparedStatement.setInt(7, loan_id);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
