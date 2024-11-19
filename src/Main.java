//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import View.BankWindow;
import View.CustomerOptions;
import View.EmployeeOptions;

import java.io.IOException;
import java.util.Date;

public class Main {
    // Test the connection
    public static void main(String[] args) {
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
                    stop = true;
                    System.out.println("Exited the app");
                    break;
            }
        }
    }
}