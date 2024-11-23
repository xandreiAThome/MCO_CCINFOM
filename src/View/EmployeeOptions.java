package View;

import HelperClass.UserInput;
import Model.Account;
import Model.AccountType;
import Model.LoanOptions;
import Model.TransactionHistory;

import java.sql.*;

public class EmployeeOptions {
    public static void showOptions(){
        System.out.println("1 - Add Loan Options\n2 - View Account Types\n3 - View Customers\n" +
                "4 - View Transaction History of an Account\n5 - View Payment History of an Account" +
                "\n6 - View Annual Transaction Volume ");
        System.out.print("Choose option: ");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());
        while (option < 1 || option > 6) {
            System.out.println("\nInvalid option Choose again");
            System.out.println("1 - Add Loan Options\n2 - View Account Types\n" +
                    "3 - View Customers\n4 - View Transaction History of an Account" +
                    "\n5 - View Payment History of an Account");
            System.out.print("Choose option: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        }

        switch (option){
            case 1:
                System.out.println("Input the necessary Information for the new loan type: ");
                System.out.print("Loan Type: ");
                String loanType = UserInput.getScanner().nextLine();
                System.out.print("Interest Rate (decimal): ");
                double interestRate = Double.parseDouble(UserInput.getScanner().nextLine());
                System.out.print("Loan Duration (months): ");
                int loanDur = Integer.parseInt(UserInput.getScanner().nextLine());
                System.out.print("Max Loan Price: ");
                double maxLoan = Double.parseDouble(UserInput.getScanner().nextLine());
                System.out.print("Minimum Loan Price: ");
                double minLoan = Double.parseDouble(UserInput.getScanner().nextLine());

                LoanOptions.addLoanOption(loanType,interestRate,loanDur,maxLoan,minLoan);

            case 2:
                AccountType.showAccountTypes();
                break;

            case 3:
                showCustomersOfBank();
                break;
            case 4:
                TransactionHistory.viewTransactionHistoryOfAccount();
                break;
            case 5:
                TransactionHistory.viewLoanPaymentHistoryOfAccount();
                break;
            case 6:
                TransactionHistory.generateAnnualTransaction();
                break;
        }
    }

    public static void showCustomersOfBank(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );
            String query = "SELECT * FROM customer ORDER BY customer_last_name, customer_first_name DESC;";

            try(PreparedStatement statement = connection.prepareStatement(query)){
                ResultSet res = statement.executeQuery();

                while(res.next()){
                    System.out.println("\tCustomer Name: " + res.getString("customer_first_name") +
                            " " + res.getString("customer_last_name"));
                    System.out.println("\tAccounts of customer");
                    Account.showAccounts(res.getInt("customer_id"));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}



