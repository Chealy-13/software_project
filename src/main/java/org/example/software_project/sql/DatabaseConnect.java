package org.example.software_project.sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
    private static String url = "jdbc:mysql://localhost:3306/car_marketplace";
    private static String user = "root";
    private static String password = "";

    private DatabaseConnect(){

    }
    public static Connection getConnection() throws SQLException{
        Connection connection = null;
        connection = DriverManager.getConnection(url, user, password);

        return connection;
    }
    public static void main(String [] args){
        try {
            Connection conn = getConnection();
            if(conn != null){
                System.out.println("Database is connected successfull");
            }
        } catch (SQLException e) {

            System.out.println("Error Occured   " + e.getMessage());
        }
    }
}

