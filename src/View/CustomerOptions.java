package View;

import HelperClass.UserInput;

public class CustomerOptions {
    public static void showOptions(){
        System.out.println("1 - Sign Up\n2 - Login");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());
        while (option < 1 || option > 2) {
            System.out.println("\nInvalid option Choose again");
            System.out.println("1 - Customer Window\n2- Employee Window\n3 - Exit");
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

    public static login(){

    }

    public static signUp(){

    }

}
