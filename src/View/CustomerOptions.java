package View;

import HelperClass.UserInput;
import Model.UserInfo;

public class CustomerOptions {
    public static void showOptions() {
        System.out.println("1 - Sign Up\n2 - Login");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());
        while (option < 1 || option > 2) {
            System.out.println("\nInvalid option. Choose again.");
            System.out.println("1 - Sign Up\n2 - Login");
            System.out.print("Choose Window: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        }

        switch (option) {
            case 1:
                UserInfo.signUp();
                break;
            case 2:
                login();
                break;
        }
    }

    public static void login() {
        System.out.println("Login functionality is not yet implemented.");
    }
}
