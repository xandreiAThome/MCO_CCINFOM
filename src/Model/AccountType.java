package Model;

import HelperClass.UserInput;

import java.sql.*;

public class AccountType {

    public static void showAccountTypes(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            String getInfoQuery = "SELECT * FROM account_type";
            PreparedStatement preparedStatementInfo = connection.prepareStatement(getInfoQuery);
            ResultSet res = preparedStatementInfo.executeQuery();

            int option = 1;
            String[] types = new String[3];
            while(res.next()){
                System.out.println(option + " - " + res.getString("account_name"));
                types[option - 1] = res.getString("account_name");
                option++;
            }

            int chosenOption;
            do {
                System.out.print("Choose account type to view information: ");
                chosenOption = Integer.parseInt(UserInput.getScanner().nextLine());
            } while( chosenOption < 1 || chosenOption > 3);


            String viewQuery = "SELECT * FROM account_type WHERE account_name = ?";

            try(PreparedStatement statement = connection.prepareStatement(viewQuery)){
                statement.setString(1, types[chosenOption - 1]);

                ResultSet typeRes = statement.executeQuery();

                if(typeRes.next()){
                    System.out.println("Account Type: " + typeRes.getString("account_name"));
                    System.out.println("Interest rate: " + typeRes.getDouble("interest_rate"));
                    System.out.println("Minimum balance: " + typeRes.getDouble("minimum_balance"));

                    int typeAction;
                    do {
                        System.out.println("\n1 - Change interest rate\n2 - Change minimum balance");
                        System.out.print("Choose option: ");
                        typeAction = Integer.parseInt(UserInput.getScanner().nextLine());
                    } while(typeAction < 1 || typeAction > 2);

                    switch (typeAction) {
                        case 1:
                            changeInterestRate(types[chosenOption - 1]);
                            break;
                        case 2:
                            changeMinimumBalance(types[chosenOption - 1]);
                            break;
                    }

                } else {
                    System.out.println("Account type doesn't exist");
                }
            }


        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void changeInterestRate(String accType){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            System.out.print("Enter new interest rate: ");
            double rate = Double.parseDouble(UserInput.getScanner().nextLine());

            String updateQuery = "UPDATE account_type SET interest_rate = ? WHERE account_name = ?;";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)){
                statement.setDouble(1, rate);
                statement.setString(2, accType);
                int rowsAffected =  statement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.println("Updated interest rate");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeMinimumBalance(String accType){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/bankdb",
                    "java",
                    "password"
            );

            System.out.print("Enter new  minimum balance: ");
            double min = Double.parseDouble(UserInput.getScanner().nextLine());

            String updateQuery = "UPDATE account_type SET minimum_balance = ? WHERE account_name = ?;";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)){
                statement.setDouble(1, min);
                statement.setString(2, accType);
                int rowsAffected =  statement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.println("Updated minimum balance");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
