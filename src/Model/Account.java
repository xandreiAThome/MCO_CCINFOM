package Model;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Date;

public class Account {
    private int account_id;
    private String account_name;
    private String account_type;
    private double current_balance;
    private java.util.Date date_opened;
    private double interest_rate;
    private String account_status;
    private int customer_id;

    public static void getAccount(int customer_id){
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "java",
                "password")) {
            String checkQuery = "SELECT * FROM account WHERE customer_id = ?";

            try(PreparedStatement statement = con.prepareStatement(checkQuery)){
                statement.setInt(1, customer_id);

                try(ResultSet res = statement.executeQuery()){
                    System.out.println("Customer Accounts:");
                    while(res.next()){
                        System.out.print("Account ID: " + res.getInt("account_id"));
                        System.out.println("\tAccount Type:" + res.getString("account_type"));
                    }
                }
            }



        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void viewAccountInfo (){
        System.out.println("");
    }

}
