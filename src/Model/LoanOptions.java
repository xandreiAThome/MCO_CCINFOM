package Model;
import java.sql.*;

public class LoanOptions {
    public LoanOptions(String url, String user, String password){
        try {
            Connection connection = DriverManager.getConnection(
                    url,
                    user,
                    password
            );

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();

            String selectQuery1 = "SELECT * FROM loan_options WHERE loan_option_ID = 1";
            String selectQuery2 = "SELECT * FROM loan_options WHERE loan_option_ID = 2";
            String selectQuery3 = "SELECT * FROM loan_options WHERE loan_option_ID = 3";

            ResultSet results1 = statement1.executeQuery(selectQuery1);
            ResultSet results2 = statement2.executeQuery(selectQuery2);
            ResultSet results3 = statement3.executeQuery(selectQuery3);

            if (!results1.next()){
                String personal_loan = "INSERT INTO loan_options "
                        + "(loan_option_id, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('1', 'Personal Loan', '0.07', '24', '1000000', '20000')";
                statement1.executeUpdate(personal_loan);
            }
            if (!results2.next()){
                String auto_loan = "INSERT INTO loan_options "
                        + "(loan_option_id, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('2', 'Auto Loan', '0.10', '36', '3000000', '250000')";
                statement2.executeUpdate(auto_loan);
            }

            if (!results3.next()) {
                String emergency_loan = "INSERT INTO loan_options "
                        + "(loan_option_id, loan_option_type, interest_rate, loan_duration_month, max_loan_amt, min_loan_amt) "
                        + "VALUES ('3', 'Emergency Loan', '0.05', '24', '750000', '20000')";
                statement3.executeUpdate(emergency_loan);

            }

            System.out.println("Insert Complete!");

            results1.close();
            results2.close();
            results3.close();
            statement1.close();
            statement2.close();
            statement3.close();
            connection.close();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    public void addLoanOption(){

    }
}
