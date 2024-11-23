package Model;

import HelperClass.UserInput;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
                    "transaction_date, transaction_status, sender_acc_id, receiver_loan_id)" +
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

    public static void generateAnnualTransaction(){

        double totalOutgoing = 0;
        double totalIncoming = 0;
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );
            Calendar cal = Calendar.getInstance();
            System.out.print("Input year to view: ");
            int year = Integer.parseInt(UserInput.getScanner().nextLine());

            System.out.println("Transaction Volume for the Year " + year);

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.YEAR, year);

            String depositQuery = "SELECT SUM(amount) FROM account_transaction_history WHERE YEAR(transaction_date) = YEAR(?) " +
                    "AND sender_acc_id is null";
            PreparedStatement incomingStatement = connection.prepareStatement(depositQuery);
            incomingStatement.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet depositResult = incomingStatement.executeQuery();

            String withdrawQuery = "SELECT SUM(amount) FROM account_transaction_history WHERE YEAR(transaction_date) = YEAR(?) " +
                    "AND receiver_acc_id is null";
            PreparedStatement outgoingStatement = connection.prepareStatement(withdrawQuery);
            outgoingStatement.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet withdrawResult = outgoingStatement.executeQuery();

            if(withdrawResult.next()){
                System.out.println("Total Outgoing: " + withdrawResult.getDouble(1));
            }

            if(depositResult.next()){
                System.out.println("Total Incoming: " + depositResult.getDouble(1));
            }


        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateAnnualLoanPayment (){
        double totalLoanPayment = 0;
        int totalNumberOfLoanPayment = 0;

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password"
            );

            System.out.print("Input year: ");
            int year = Integer.parseInt(UserInput.getScanner().nextLine());

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Calendar.YEAR, year);

            String getAnnualReportString = "SELECT * FROM loan_transaction_history WHERE YEAR(transaction_date) = YEAR(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(getAnnualReportString);
            preparedStatement.setDate(1, new java.sql.Date(calendar.getTime().getTime()));
            ResultSet reportResult = preparedStatement.executeQuery();

            while (reportResult.next()){
                totalLoanPayment += reportResult.getDouble("amount");
                totalNumberOfLoanPayment++;
            }

            System.out.println("Annual Loan Payment Volume Report " + year);
            System.out.println("Total Loan Payment Made: ₱" + Math.round(totalLoanPayment * 100.0) / 100.0);
            System.out.println("Total Number of Loan Payments Made: " + totalNumberOfLoanPayment);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

public void generateMonthlySavings(int customer_id, String yearToGenerate) {
    double totalOutgoing = 0;
    double totalIncoming = 0;

    try {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/bankdb",
                "java",
                "password"
        );

        
        String accountQuery = "SELECT account_id FROM account WHERE customer_id = ?";
        PreparedStatement accountStmt = connection.prepareStatement(accountQuery);
        accountStmt.setInt(1, customer_id);
        ResultSet accountResult = accountStmt.executeQuery();


        while (accountResult.next()) {
            int accountId = accountResult.getInt("account_id");


            String outgoingQuery = "SELECT SUM(amount) AS totalOutgoing FROM account_transaction_history " +
                    "WHERE sender_acc_id = ? AND DATE_FORMAT(transaction_date, '%Y') = ?";
            PreparedStatement outgoingStmt = connection.prepareStatement(outgoingQuery);
            outgoingStmt.setInt(1, accountId);
            outgoingStmt.setString(2, yearToGenerate);
            ResultSet outgoingResult = outgoingStmt.executeQuery();

            if (outgoingResult.next()) {
                totalOutgoing += outgoingResult.getDouble("totalOutgoing");
            }


            String incomingQuery = "SELECT SUM(amount) AS totalIncoming FROM account_transaction_history " +
                    "WHERE receiver_acc_id = ? AND DATE_FORMAT(transaction_date, '%Y') = ?";
            PreparedStatement incomingStmt = connection.prepareStatement(incomingQuery);
            incomingStmt.setInt(1, accountId);
            incomingStmt.setString(2, yearToGenerate);
            ResultSet incomingResult = incomingStmt.executeQuery();

            if (incomingResult.next()) {
                totalIncoming += incomingResult.getDouble("totalIncoming");
            }
        }


        double yearlySavings = totalIncoming - totalOutgoing;


        System.out.println("Yearly Savings Report for " + yearToGenerate);
        System.out.println("Total Incoming: ₱" + Math.round(totalIncoming * 100.0) / 100.0);
        System.out.println("Total Outgoing: ₱" + Math.round(totalOutgoing * 100.0) / 100.0);
        System.out.println("Net Savings: ₱" + Math.round(yearlySavings * 100.0) / 100.0);

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
