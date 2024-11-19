package View;

import HelperClass.UserInput;
import Model.LoanOptions;

public class EmployeeOptions {
    public static void showOptions(){
        LoanOptions loanOptions = new LoanOptions();
        System.out.println("1 - Add Loan Options");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());
        while (option < 1 || option > 2) {
            System.out.println("\nInvalid option Choose again");
            System.out.println("1 - Add Loan Options");
            System.out.print("Choose Window: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        }

        switch (option){
            case 1:
                System.out.println("Input the necessary Information for the new loan type: ");
                System.out.println("Loan Type: ");
                String loanType = UserInput.getScanner().nextLine();
                System.out.println("Interest Rate (decimal): ");
                double interestRate = Double.parseDouble(UserInput.getScanner().nextLine());
                System.out.println("Loan Duration (months): ");
                int loanDur = Integer.parseInt(UserInput.getScanner().nextLine());
                System.out.println("Max Loan Price: ");
                double maxLoan = Double.parseDouble(UserInput.getScanner().nextLine());
                System.out.println("Minimum Loan Price: ");
                double minLoan = Double.parseDouble(UserInput.getScanner().nextLine());

                loanOptions.addLoanOption(loanType,interestRate,loanDur,maxLoan,minLoan);

            case 2:
                break;
        }
    }
}
