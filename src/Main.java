//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import View.BankWindow;

import java.io.IOException;

public class Main {
    // Test the connection
    public static void main(String[] args) {
        boolean stop = false;
        while(!stop){
            switch (BankWindow.chooseWindow()){
                case 1:
                    System.out.println("test1");
                    break;
                case 2:
                    System.out.println("test2");
                    break;
                case 3:
                    stop = true;
                    System.out.println("Exited the app");
                    break;
            }
        }
    }
}