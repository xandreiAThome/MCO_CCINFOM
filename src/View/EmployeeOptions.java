package View;

import HelperClass.UserInput;

public class EmployeeOptions {
    public static void showOptions(){
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
                break;
            case 2:
                break;
        }
    }
}
