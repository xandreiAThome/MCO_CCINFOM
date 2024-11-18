//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.sql.*;

public class Main {
    // Test the connection
    public static void main(String[] args) {
        try{
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",
                    "java",
                    "password"
            );

            Statement statement = con.createStatement();
            ResultSet res = statement.executeQuery("Select * From customer");

            while(res.next()){
                System.out.println(res.getString("customer_first_name"));
                System.out.println(res.getString("customer_last_name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}