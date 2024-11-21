//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import Model.AvailedLoans;
import Model.LoanOptions;
import View.BankWindow;
import View.CustomerOptions;
import View.EmployeeOptions;

import java.io.IOException;
import java.util.Date;

public class Main {
    // Test the connection
    public static void main(String[] args) {
        LoanOptions loanOptions = new LoanOptions();
        loanOptions.defaultLoan("jdbc:mysql://localhost:3306/bankdb","java", "password");
        boolean stop = false;
        while(!stop){
            switch (BankWindow.chooseWindow()){
                case 1:
//                    System.out.print("\033\143");
                    CustomerOptions.showOptions();
                    break;
                case 2:
//                    System.out.print("\033\143");
                    EmployeeOptions.showOptions();
                    break;
                case 3:
                    AvailedLoans applyLoan = new AvailedLoans();
                    //applyLoan.loanAppli(1);
                    //applyLoan.showAvailedLoans(1);
                    applyLoan.loanPayment(1);
                    stop = true;
                    System.out.println("Exited the app");
                    break;
            }
        }
    }
}