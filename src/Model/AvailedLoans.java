package Model;
import java.sql.*;
import DateClass.Date;
import HelperClass.UserInput;

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
                    System.out.println("Choose which Loan Type to avail");
                    Statement statement1 = connection.createStatement();
                    String loanTypesQuery = "SELECT * FROM loan_options";
                    ResultSet loanTypesSet = statement1.executeQuery(loanTypesQuery);
                    int counter = 0;
                    while (loanTypesSet.next()) {
                        System.out.print(loanTypesSet.getInt("loan_option_id"));
                        System.out.print(": ");
                        System.out.println(loanTypesSet.getString(("loan_option_type")));
                        counter++;
                    }

                    int chosenLoan;
                    do {
                        System.out.print("Select Loan Type: ");
                        chosenLoan = Integer.parseInt(UserInput.getScanner().nextLine());
                        if (chosenLoan < 1 || chosenLoan > counter) {
                            System.out.println("Invalid choice. Please select a number between 1 and " + counter + ".");
                        }
                    } while (chosenLoan < 1 || chosenLoan > counter);


                    String loanAmtRange = "SELECT * FROM loan_options WHERE loan_option_id = ?";
                    PreparedStatement prepStmtLoanAmt = connection.prepareStatement(loanAmtRange);
                    prepStmtLoanAmt.setInt(1,chosenLoan);
                    ResultSet loanAmtRangeSet = prepStmtLoanAmt.executeQuery();

                    double min = 0;
                    double max = 0;
                    int loanTerm = 0;
                    double interestRate = 0;
                    while (loanAmtRangeSet.next()){
                        min = loanAmtRangeSet.getDouble("min_loan_amt");
                        max = loanAmtRangeSet.getDouble("max_loan_amt");
                        loanTerm = loanAmtRangeSet.getInt("loan_duration_month");
                        interestRate = loanAmtRangeSet.getDouble("interest_rate");

                    }

                    double loanPrin = 0;
                    do {
                        System.out.print("Enter the amount you want to loan: ");
                        loanPrin = Double.parseDouble(UserInput.getScanner().nextLine());
                        if (loanPrin < min || loanPrin > max) {
                            System.out.println("Invalid choice. Please select an amount between " + min +  "and " + max + ".");
                        }
                    } while (loanPrin < min || loanPrin > max);

                    double principleAmort = succMonthPrincipalAmortizationLoanFormula(loanPrin,loanTerm);
                    double firstMonthPay = firstMonthPrincipalAmortizationLoanFormula(loanPrin,loanTerm,principleAmort);

                    int accountUse;
                    System.out.print("Which account will you use to loan? ");
                    accountUse = Integer.parseInt(UserInput.getScanner().nextLine());

                    String customerMoney = "SELECT * FROM account WHERE account_id = ?";
                    PreparedStatement prepStmtMoney = connection.prepareStatement(checkIDQuery);
                    prepStmtMoney.setInt(1, accountUse);
                    ResultSet moneyResult = prepStmtMoney.executeQuery();

                    double money = 0;
                    while (moneyResult.next()){
                        money = moneyResult.getDouble("current_balance");
                    }

                    double sufficientBalChecker = principleAmort + firstMonthPay;
                    int paymentDay;
                    if (money >= sufficientBalChecker){
                        do {
                            System.out.print("Payment day: ");
                            paymentDay = Integer.parseInt(UserInput.getScanner().nextLine());
                            if (paymentDay < 1 || paymentDay > 30) {
                                System.out.println("Invalid choice. Please select an amount between 1 and 30.");
                            }
                        } while (paymentDay < 1 || paymentDay > 30);

                        String newApplicationQuery = "INSERT INTO availed_loans "
                                + "(loan_option_id, principal_amount, first_month_principal_amortization, succeding_principal_amortization,interest_amortization,principal_balance,interest_balance,start_date,end_date,month_payment_day,loan_status,customer_id) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                        PreparedStatement preparedStatementInput = connection.prepareStatement(newApplicationQuery);
                    } else {
                        System.out.println("Insufficient Funds... Going back to the Main Menu");
                    }
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
