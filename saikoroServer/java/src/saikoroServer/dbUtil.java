package saikoroServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbUtil {
	
	public static final String URL = "jdbc:mysql://localhost:3306/saikorogame";
	public static final String USER = "root";
    public static final String PASSWORD = "password";    
    
    private static Connection connDB = null;
    static{
        try {
            connDB = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return connDB;
    }

}
