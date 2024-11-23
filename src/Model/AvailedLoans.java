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

    public static void showAvailedLoans (int customer_id){
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

            if(!infoResultSet.isBeforeFirst()){
                System.out.println("No Availed Loans");
            }

            System.out.println("Your Availed Loans: ");
            while(infoResultSet.next()){
                System.out.println("------------------------------------------------------");
                System.out.println("Loan Id: " + infoResultSet.getInt("loan_id"));
                System.out.println("Loan Option Id: " + infoResultSet.getInt("loan_option_id"));
                System.out.println("Principle Amount: ₱" + infoResultSet.getDouble("principal_amount"));
                System.out.println("Principle Amortization: ₱" + infoResultSet.getDouble("first_month_principal_amortization"));
                System.out.println("Succeeding Principal Amortization: ₱" + infoResultSet.getDouble("succeeding_principal_amortization"));
                System.out.println("Interest Amortization: ₱" + infoResultSet.getDouble("interest_amortization"));
                System.out.println("Principle Balance: ₱" + infoResultSet.getDouble("principal_balance"));
                System.out.println("Interest Balance: ₱" + infoResultSet.getDouble("interest_balance"));
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

    public static void loanAppli(int customer_id){
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
                            System.out.println("Invalid choice. Please select an amount between " + min +  " and " + max);
                        }
                    } while (loanPrin < min || loanPrin > max);

                    double principleAmort = succMonthPrincipalAmortizationLoanFormula(loanPrin,loanTerm);
                    double firstMonthPay = firstMonthPrincipalAmortizationLoanFormula(loanPrin,loanTerm,principleAmort);
                    double interestAmort = interest_amortization(loanPrin,loanTerm,interestRate);


                        System.out.println("Approved!");

                        String newApplicationQuery = "INSERT INTO availed_loans "
                                + "(loan_option_id, principal_amount, first_month_principal_amortization, succeeding_principal_amortization,interest_amortization,principal_balance,interest_balance,start_date,end_date,month_payment_day,loan_status,customer_id) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,20,?,?)";
                        PreparedStatement preparedStatementInput = connection.prepareStatement(newApplicationQuery);
                        preparedStatementInput.setInt(1,chosenLoan);
                        preparedStatementInput.setDouble(2,loanPrin);
                        preparedStatementInput.setDouble(3,firstMonthPay);
                        preparedStatementInput.setDouble(4,principleAmort);
                        preparedStatementInput.setDouble(5,interestAmort);
                        preparedStatementInput.setDouble(6,loanPrin);
                        preparedStatementInput.setDouble(7, interestAmort*loanTerm);

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


    public static double firstMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm, double roundedPrincipleAmort) {
        double answer;
        answer = principalAmount - (roundedPrincipleAmort * (loanTerm - 1));
        return Math.round(answer * 100.0) / 100.0;
    }

    public static double succMonthPrincipalAmortizationLoanFormula(double principalAmount, int loanTerm) {
        double answer;
        answer = principalAmount / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }

    public static double interest_amortization(double principalAmount, int loanTerm, double interestRate) {
        double answer;
        answer=(principalAmount * interestRate) / loanTerm;
        return Math.round(answer * 100.0) / 100.0;
    }

    public static void loanPayment(int customer_id){

        Date startDate = null;
        Date endDate;
        double firstMonthAmort = 0;
        double monthlyAmort = 0;
        double interestAmort = 0;
        double principleBal = 0;
        double interestBal = 0;
        boolean monthChecker = false;
        double monthPayment;
        double currentMoney = 0;
        double accountMinBal = 0;
        double outstandingBal;
        LoanStatus loanStatus;
        int account_id;
        double accountDeduction;
        double lateLoanFee;
        String accountType = null;
        int senderID = 0;
        int receiverID = 0;

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            showAvailedLoans(customer_id);
            System.out.println("Select which loan to pay ");
            System.out.print("Enter Loan ID: ");
            int loan_id = Integer.parseInt(UserInput.getScanner().nextLine());

            String getAmountInfoQuery = "SELECT * FROM availed_loans WHERE loan_id = ? ";
            PreparedStatement preparedStatementAmountInfo = connection.prepareStatement(getAmountInfoQuery);
            preparedStatementAmountInfo.setInt(1,loan_id);
            ResultSet amtInfoResultSet = preparedStatementAmountInfo.executeQuery();

            LocalDate currentDate = LocalDate.now();

            if (amtInfoResultSet.next()){
                startDate = amtInfoResultSet.getDate("start_date");
                endDate = amtInfoResultSet.getDate("end_date");
                firstMonthAmort = amtInfoResultSet.getDouble("first_month_principal_amortization");
                monthlyAmort = amtInfoResultSet.getDouble("succeeding_principal_amortization");
                interestAmort = amtInfoResultSet.getDouble("interest_amortization");
                principleBal = amtInfoResultSet.getDouble("principal_balance");
                interestBal = amtInfoResultSet.getDouble("interest_balance");

                if (startDate.getMonth() + 1 == currentDate.getMonthValue()){
                    monthChecker = true;
                }
            }

            if (monthChecker){
                System.out.println("This will be your first month paying");
                monthPayment = firstMonthAmort;

            } else {
                monthPayment = monthlyAmort;
            }

            lateLoanFee = lateLoanAmount(loan_id,firstMonthAmort,monthlyAmort,interestAmort,startDate);

            outstandingBal = monthPayment + interestAmort + lateLoanFee;
            System.out.println("---BREAKDOWN---");
            System.out.println("Amortization for the Current Month: ₱" + monthPayment);
            System.out.println("Monthly Interest Amortization: ₱" + interestAmort);
            System.out.println("Late Fee: ₱" + lateLoanFee);
            System.out.println("Outstanding Balance for the Month: ₱" + outstandingBal);


            System.out.println("Select Account to Pay ");
            System.out.print("Enter Account ID: ");
            account_id = Integer.parseInt(UserInput.getScanner().nextLine());


            //Check if account is for customer account

            String moneyCheckQuery = "SELECT * FROM account WHERE account_id = ? AND customer_id = ?";
            PreparedStatement preparedStatementMoneyQuery = connection.prepareStatement(moneyCheckQuery);
            preparedStatementMoneyQuery.setInt(1,account_id);
            preparedStatementMoneyQuery.setInt(2, customer_id);
            ResultSet moneyResultSet = preparedStatementMoneyQuery.executeQuery();

            if (moneyResultSet.next()){
                currentMoney = moneyResultSet.getDouble("current_balance");
            } else {
                System.out.println("Customer has no account with that ID");
                return;
            }

            String accountTypeQuery = "SELECT * FROM account WHERE account_id = ? ";
            PreparedStatement preparedStatementAccountType = connection.prepareStatement(accountTypeQuery);
            preparedStatementAccountType.setInt(1,account_id);
            ResultSet accountTypeResultSet = preparedStatementAccountType.executeQuery();

            if (accountTypeResultSet.next()){
                accountType = accountTypeResultSet.getString("account_type");
            }

            String moneyCheckQuery2 = "SELECT * FROM account_type WHERE account_name = ? ";
            PreparedStatement preparedStatementMinimumBal = connection.prepareStatement(moneyCheckQuery2);
            preparedStatementMinimumBal.setString(1,accountType);
            ResultSet minimumBalResultSet = preparedStatementMinimumBal.executeQuery();

            if (minimumBalResultSet.next()){
                accountMinBal = minimumBalResultSet.getDouble("minimum_balance");
            }


            if (currentMoney < outstandingBal){
                System.out.println("Insufficient funds...going back to the main menu");
            } else {
                if (currentMoney - outstandingBal < accountMinBal){
                    System.out.println("Insufficient funds you will be exceeding the minimum required balance...going back to the main menu");
                } else {
                    String updateAvailedLoansQuery = "UPDATE availed_loans "
                            + "SET principal_balance = ?, interest_balance = ?, loan_status = ?"
                            + "WHERE loan_id = ?";

                    principleBal = principleBal - monthPayment;
                    interestBal = interestBal - interestAmort;

                    if (principleBal + interestBal == 0){
                        loanStatus = LoanStatus.CLOSED;
                    } else {
                        loanStatus = LoanStatus.ACTIVE;
                    }

                    PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateAvailedLoansQuery);
                    preparedStatementUpdate.setDouble(1,principleBal);
                    preparedStatementUpdate.setDouble(2,interestBal);
                    preparedStatementUpdate.setString(3,loanStatus.name());
                    preparedStatementUpdate.setInt(4,loan_id);

                    int rowsInserted = preparedStatementUpdate.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("You've successfully paid for the month!");
                    }

                    String updateAccountQuery = "UPDATE account "
                            + "SET current_balance = ? "
                            + "WHERE account_id = ?";
                    accountDeduction = currentMoney - outstandingBal;

                    PreparedStatement preparedStatementUpdate2 = connection.prepareStatement(updateAccountQuery);
                    preparedStatementUpdate2.setDouble(1, accountDeduction);
                    preparedStatementUpdate2.setDouble(2, account_id);
                    int rowsInserted2 = preparedStatementUpdate2.executeUpdate();
                    if (rowsInserted2 > 0) {
                        System.out.println("Successfully deducted from your account");
                    }

                    TransactionHistory.generateLoanTransactionRecord(account_id, loan_id, outstandingBal);
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static double lateLoanAmount(int loan_id, double firstMonthPayment, double succeedingMonthPayment, double interestPayment, Date loanAvailed){

        double answer = 0;
        LocalDate currDate = LocalDate.now();
        Date lastDatePaid = null;
        double lateFee = 500;


        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            String getLastDatePaidQuery = "SELECT * FROM loan_transaction_history WHERE receiver_loan_id = ? ORDER BY transaction_date DESC LIMIT 1 ";
            PreparedStatement preparedStatement = connection.prepareStatement(getLastDatePaidQuery);
            preparedStatement.setInt(1,loan_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                lastDatePaid =  resultSet.getDate("transaction_date");
            }

            //System.out.println("TEST Current Date: " + currDate);
            //System.out.println("TEST Latest Date Paid: " + lastDatePaid);
            //System.out.println("TEST Last Date Paid month: " + (lastDatePaid.getMonth() + 1));
            //System.out.println("TEST Current Date Paid month: " + currDate.getMonthValue());
            //System.out.println("TEST Last Date Paid Year: " + (lastDatePaid.getYear()) + 1900); //Since date is offset from 1900
            //System.out.println("TEST Current Date Paid Year: " + currDate.getYear());

            if (lastDatePaid == null) {
                if (currDate.getYear() == (loanAvailed.getYear() + 1900) && currDate.getMonthValue() == (loanAvailed.getMonth() + 1)){
                    System.out.println("You don't have late fees!");
                    return answer;
                } else {
                    int currDateYear = currDate.getYear();
                    int currDateMonth = currDate.getMonthValue();
                    int availedLoanYear = loanAvailed.getYear() + 1900;
                    int availedLoanMonth = loanAvailed.getMonth() + 1;

                    int monthsBetween = ((availedLoanYear - currDateYear) * 12 + (availedLoanMonth - currDateMonth));
                    //System.out.println("TEST Months Between: " + monthsBetween);

                    if (monthsBetween == 0) {
                        monthsBetween = 1;
                    } else if (monthsBetween < 0) {
                        monthsBetween = monthsBetween * -1;
                    }
                    answer = ((firstMonthPayment + interestPayment + succeedingMonthPayment) * (monthsBetween - 1)) + (lateFee * monthsBetween);
                }

            } else {
                if (currDate.getYear() == (lastDatePaid.getYear() + 1900) && currDate.getMonthValue() == (lastDatePaid.getMonth() + 1)){
                    System.out.println("You don't have late fees!");
                    return answer;
                } else {
                    int currDateYear = currDate.getYear();
                    int currDateMonth = currDate.getMonthValue();
                    int lastPaidYear = lastDatePaid.getYear() + 1900;
                    int lastPaidMonth = lastDatePaid.getMonth() + 1;

                    int monthsBetween = ((lastPaidYear - currDateYear) * 12 + (lastPaidMonth - currDateMonth));
                    //System.out.println("TEST Months Between: " + monthsBetween);

                    if (monthsBetween == 0) {
                        monthsBetween = 1;
                    } else if (monthsBetween < 0) {
                        monthsBetween = monthsBetween * -1;
                    }
                    answer = ((succeedingMonthPayment + interestPayment) * monthsBetween) + (lateFee * monthsBetween);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return Math.round(answer * 100.0) / 100.0;
    }

}
