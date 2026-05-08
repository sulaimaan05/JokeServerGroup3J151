package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    //Instance variables:
    private static DBConfig instance;
    private Connection con;

    //Constructor:
    private DBConfig(){
        try {
            //Loads driver onto the heap.
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Define where the database is.
            String url = "jdbc:mysql://localhost:3306/joke_server";
            //Create the actual connection to the database.
            this.con = DriverManager.getConnection(url, "root", "root");
            System.out.println("Connected to database successfully");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Getter:
    public Connection getConnection(){
        return this.con;
    }

    //Global access point:
    public static synchronized DBConfig getInstance(){
        if(instance == null){
            instance = new DBConfig();
        }
        return instance;
    }

    //Close method:
    public void close() throws SQLException{
        if(this.con != null){
            this.con.close();
        }
    }
}
