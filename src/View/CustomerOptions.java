package View;

import HelperClass.UserInput;
import Model.Account;
import Model.Customer;

import java.sql.*;

public class CustomerOptions {
    public static void showOptions() {
        System.out.println("1 - Sign Up\n2 - Login");
        System.out.print("Choose option: ");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());
        while (option < 1 || option > 2) {
            System.out.println("\nInvalid option. Choose again.");
            System.out.println("1 - Sign Up\n2 - Login");
            System.out.print("Choose Window: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        }

        switch (option) {
            case 1:
                Customer.signUp();
                break;
            case 2:
                Customer.login();
                break;
        }
    }
}
