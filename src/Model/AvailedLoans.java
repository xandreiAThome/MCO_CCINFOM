package Model;
import java.sql.*;
import java.time.LocalDate;

import java.sql.Date;
import HelperClass.UserInput;

public class AvailedLoans {

    enum LoanStatus {
        ACTIVE,
        CLOSED
    }

    public void showAvailedLoans (int customer_id){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            String getInfoQuery = "SELECT * FROM availed_loans WHERE customer_id = ?";
            PreparedStatement preparedStatementInfo = connection.prepareStatement(getInfoQuery);
            preparedStatementInfo.setInt(1, customer_id);
            ResultSet infoResultSet = preparedStatementInfo.executeQuery();

            System.out.println("Your Availed Loans: ");
            while(infoResultSet.next()){
                System.out.println("------------------------------------------------------");
                System.out.println("Loan Id: " + infoResultSet.getInt("loan_id"));
                System.out.println("Loan Option Id: " + infoResultSet.getInt("loan_option_id"));
                System.out.println("Principle Amount: " + infoResultSet.getDouble("principal_amount"));
                System.out.println("Principle Amortization: " + infoResultSet.getDouble("first_month_principal_amortization"));
                System.out.println("Succeeding Principal Amortization: " + infoResultSet.getDouble("succeding_principal_amortization"));
                System.out.println("Interest Amortization: " + infoResultSet.getDouble("interest_amortization"));
                System.out.println("Principle Balance: " + infoResultSet.getDouble("principal_balance"));
                System.out.println("Interest Balance: " + infoResultSet.getDouble("interest_balance"));
                System.out.println("Start Date: " + infoResultSet.getDate("start_date"));
                System.out.println("End Date: " + infoResultSet.getDate("end_date"));
                System.out.println("Monthly Payment Day: " + infoResultSet.getInt("month_payment_day"));
                System.out.println("Loan Status: " + infoResultSet.getString("loan_status"));
                System.out.println("------------------------------------------------------");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
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

                    double loanPrin;
                    do {

                        //System.out.println("TEST Max = " + max);
                        //System.out.println("TEST Min = " + min);

                        System.out.print("Enter the amount you want to loan: ");
                        loanPrin = Double.parseDouble(UserInput.getScanner().nextLine());

                        //System.out.println("TEST loanPrin = " + loanPrin);

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
                    PreparedStatement prepStmtMoney = connection.prepareStatement(customerMoney);
                    prepStmtMoney.setInt(1, accountUse);
                    ResultSet moneyResult = prepStmtMoney.executeQuery();

                    double money = 0;
                    while (moneyResult.next()){
                        money = moneyResult.getDouble("current_balance");
                    }

                    double interestAmort = interest_amortization(loanPrin,loanTerm,interestRate);
                    //System.out.println("TEST Principle Amort = " + principleAmort);
                    //System.out.println("TEST First Month Pay = " + firstMonthPay);
                    //System.out.println("TEST Interest Amortization = " + interestAmort);

                    double sufficientBalChecker = principleAmort + interestAmort + firstMonthPay + interestAmort;
                    //System.out.println("TEST Money = " + money);
                    //System.out.println("TEST Sufficient Bal Checker = " + sufficientBalChecker);
                    if (money >= sufficientBalChecker){

                        System.out.println("Approved!");

                        String newApplicationQuery = "INSERT INTO availed_loans "
                                + "(loan_option_id, principal_amount, first_month_principal_amortization, succeding_principal_amortization,interest_amortization,principal_balance,interest_balance,start_date,end_date,month_payment_day,loan_status,customer_id) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,20,?,?)";
                        PreparedStatement preparedStatementInput = connection.prepareStatement(newApplicationQuery);
                        preparedStatementInput.setInt(1,chosenLoan);
                        preparedStatementInput.setDouble(2,loanPrin);
                        preparedStatementInput.setDouble(3,firstMonthPay);
                        preparedStatementInput.setDouble(4,principleAmort);
                        preparedStatementInput.setDouble(5,interest_amortization(loanPrin,loanTerm,interestRate));
                        preparedStatementInput.setDouble(6,loanPrin);
                        preparedStatementInput.setDouble(7, loanPrin*interestRate*loanTerm);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate endDate = currentDate.plusMonths(loanTerm);

                        preparedStatementInput.setDate(8, Date.valueOf(currentDate));//Start date
                        preparedStatementInput.setDate(9, Date.valueOf(endDate));//end date

                        LoanStatus status = LoanStatus.ACTIVE;

                        preparedStatementInput.setString(10, status.name());
                        preparedStatementInput.setInt(11, customer_id);

                        int rowsInserted = preparedStatementInput.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("New loan application inserted successfully.");
                        }

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
        double answer;
        answer = principalAmount - (roundedPrincipleAmort * (loanTerm - 1));
        return Math.round(answer * 100.0) / 100.0;
    }

    public double succMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm) {
        double answer;
        answer = principalAmount / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }

    public double interest_amortization(double principalAmount, int loanTerm, double interestRate) {
        double answer;
        answer=(principalAmount * interestRate) / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }






}
