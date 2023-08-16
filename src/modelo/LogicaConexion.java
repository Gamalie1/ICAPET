
package modelo;

import java.sql.*;
import javax.swing.JOptionPane;


public class LogicaConexion {
    static ResultSet rs = null;
    static Connection conexion = null;
    public static String database = "gpos_promovidos";
   // public static String url = "jdbc:postgresql://192.168.1.30/"+database;
   // public static String usuario="postgres" ,password="123";
     public static String url = "jdbc:postgresql://localhost/"+database;
     public static String usuario="postgres" ,password="HOLA";
    
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
    
       static public ResultSet ejecutaQuery(String sql) {

        try {
          Connection con = LogicaConexion.getConecta();
         
            //Se crea un statement para realizar la consulta
            Statement st = con.createStatement();
            rs = st.executeQuery(sql);

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return rs;

    }
}
