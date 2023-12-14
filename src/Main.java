import java.sql.*;
import java.util.Scanner;

class Account{

    private static final String url="jdbc:mysql://localhost:3306/BalanceReporting";
    private static final String username="root";
    private static final String password="12345";

    public static void main(String[] args) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            String debitQuery = "update accounts set balance = balance - ? where accountNumber = ?";
            String creditQuery = "update accounts set balance = balance + ? where accountNumber = ?";
            PreparedStatement debitpreparedStatement = connection.prepareStatement(debitQuery);
            PreparedStatement creditpreparedStatement = connection.prepareStatement(creditQuery);
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter debit account number ");
            int account = sc.nextInt();
            System.out.print("Enter debit  amount ");
            double amount = sc.nextDouble();
            System.out.print("Enter the credit account number ");
            int account1 = sc.nextInt();

            debitpreparedStatement.setDouble(1,amount);
            debitpreparedStatement.setInt(2,account);
            creditpreparedStatement.setDouble(1,amount);
            creditpreparedStatement.setInt(2,account1);

            debitpreparedStatement.executeUpdate();
            creditpreparedStatement.executeUpdate();

            if(isSufficient(connection,account,account1,amount)){
                connection.commit();
                System.out.println("transaction successfull..");

            }else{
                connection.rollback();
                System.out.println("transaction failed....");
                System.out.println("not sufficient balance...");
            }
            debitpreparedStatement.close();
            creditpreparedStatement.close();
            sc.close();
            connection.close();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    static boolean isSufficient(Connection connection, int account, int account1, double amount) {
        try {
            String query = "select balance from accounts where accountNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account);
            preparedStatement.setInt(1, account1);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                if (amount > current_balance) {
                    return false;
                } else {
                    return true;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
      }
    }