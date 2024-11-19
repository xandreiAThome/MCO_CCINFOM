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
}
