package Model;
import java.sql.*;
import DateClass.Date;

public class AvailedLoans {

    enum LoanStatus {
        ACTIVE,
        CLOSED;
    }
    
    private int loan_id;
    private int loan_option_id;
    private double principle_amount;
    private double first_month_principal_amortization;
    private double succeding_principal_amortization;
    private double interest_amortization;
    private double prinicple_balance;
    private double interest_balance;
    private Date start_date;
    private Date end_date;
    private Date month_payment_day;
    private LoanStatus loan_status;
    private int customer_id;

    public void viewLoans(int customer_id){

    }

    public void loanAppli(int customer_id){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password");

            String checkIDQuery= "SELECT COUNT(customer_id) c FROM availed_loans WHERE customer_id = ?";
            PreparedStatement prepedStatementCheck = connection.prepareStatement(checkIDQuery);
            prepedStatementCheck.setInt(1, customer_id);
            ResultSet checkIDResult = prepedStatementCheck.executeQuery();
            if (checkIDResult.next()) {
                int loanCount = checkIDResult.getInt("c");
                if (loanCount >= 2) {
                    System.out.println("Customer cannot avail of more loans. They already have 2 loans.");
                } else {
                    String newApplicationQuery = "INSERT INTO availed_loans "
                            + "( ) "
                            + "VALUES ( )";
                    PreparedStatement preparedStatementInput = connection.prepareStatement(newApplicationQuery);

                }
            }






            //Check if the customer already have 2 if there is print a note that says can't anymore since 2 loans maz
            //Insert a new record in the Loans table with details such as loanTypeName, principalAmount, interestRate, startDate, and loanEndDate.
            //Calculate and store the firstMonthPrincipalAmortization and subsequent monthly amortizations based on the loan terms.
            //Check if the account can pay the first month and the second month
            //If approved, update the loanStatus to "Approved," and if rejected, update the status to "Rejected" with an accompanying reason.

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public double firstMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm, double roundedPrincipleAmort) {
        double answer =0;
        answer = (roundedPrincipleAmort * (loanTerm - 1)) - principalAmount;
        return answer;
    }

    public double succMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm) {
        double answer =0;
        answer = (int) Math.floor((double) principalAmount / loanTerm);
        return answer;
    }

    public double interest_amortization(double principalAmount, int loanTerm, double interestRate) {
        double answer =0;
        answer=(principalAmount * interestRate / 100) / loanTerm;
        return answer;
    }






}
