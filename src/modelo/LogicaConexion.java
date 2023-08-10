
package modelo;

import java.sql.*;
import javax.swing.JOptionPane;


public class LogicaConexion {
    public static String database = "gpos_promovidos";
    public static String url = "jdbc:postgresql://192.168.1.30:5432/"+database;
    public static String usuario="postgres" ,password="123";
    
    
    public static Connection getConecta(){
        try{
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, usuario, password);
            System.out.println("conexion establecida a la base de datos "+database);
            return con;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Ocurrio un error" +e.getMessage());
            return null;
        }  
    } 
}
