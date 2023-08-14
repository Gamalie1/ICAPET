package vista;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import modelo.LogicaConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import static modelo.LogicaConexion.ejecutaQuery;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import java.sql.SQLException;


public class principal extends javax.swing.JFrame {
    DefaultTableModel TablaGENERAL = new DefaultTableModel();
    DefaultTableModel TablaGRUPO = new DefaultTableModel();
    String usuario;
    String pwd;
    Connection con;
    static Connection conexion = null;
    static ResultSet rs = null;
    String sexo; 
    
    public principal() {
        initComponents();
        
        //Tablas 
        TablaGENERAL = (DefaultTableModel) jTablaGENERAL.getModel();
        TablaGRUPO   = (DefaultTableModel) jTablaGrupo.getModel();
        //this.setSize(new Dimension(770, 345));
        this.setLocationRelativeTo(null);
        
        login.setSize(new Dimension(800, 400));
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        
        RepresentanteGNERAL.setSize(new Dimension(920, 500));
        RepresentanteGNERAL.setLocationRelativeTo(null);
        
        RepresentanteGPO.setSize(new Dimension(920, 500));
        RepresentanteGPO.setLocationRelativeTo(null);
        
        Lista.setSize(new Dimension(440, 460));
        Lista.setLocationRelativeTo(null);
        
        //Metodos para que los datos de la tabla se pasen alos text field
         jTablaGENERAL.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                JTable table=(JTable) e.getSource();
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                if(e.getClickCount()==1){
                    txtNR.setText(jTablaGENERAL.getValueAt(jTablaGENERAL.getSelectedRow(),1).toString());
                    txtUsuario.setText(jTablaGENERAL.getValueAt(jTablaGENERAL.getSelectedRow(),2).toString());
                   
                }
            }
         });
         
         jTablaGrupo.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                JTable table=(JTable) e.getSource();
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                if(e.getClickCount()==1){
                    txtNR1.setText(jTablaGrupo.getValueAt(jTablaGrupo.getSelectedRow(),1).toString());
                    txtTel.setText(jTablaGrupo.getValueAt(jTablaGrupo.getSelectedRow(),2).toString());
                    txtClav.setText(jTablaGrupo.getValueAt(jTablaGrupo.getSelectedRow(),3).toString());
                    txtSeccio.setText(jTablaGrupo.getValueAt(jTablaGrupo.getSelectedRow(),4).toString());
                }
            }
         });
    
    }
    
    public void salir(){//Metodo para cerrar la aplicacion
        int op = JOptionPane.showConfirmDialog(null, "¿Desea salir?", "Advertencia!", JOptionPane.YES_NO_OPTION);
        if(op==0){
            System.exit(0);
        }
    }
    
    public void obtieneSexo(){
        if(jrbHombre.isSelected()==true){
            sexo ="H";
        }else if(jrbMujer.isSelected()==true){
            sexo="M";
        }else{
            sexo="H";
        }
    }
    
    public static String generarClave(String municipio) {
        StringBuilder clave = new StringBuilder();
        
        // Dividir el nombre en palabras
        String[] palabras = municipio.split(" ");
        
        // Obtener la inicial de cada palabra y agregarla a la clave
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                clave.append(Character.toUpperCase(palabra.charAt(0)));
            }
        }
        
        return clave.toString();
    }
    
    public static boolean validarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
     public void InsertarReprensentanteGNERAL(){
        try {
            String rol = jcRol.getSelectedItem().toString(); // Agregar "ROLE " al nombre del rol
             System.out.println(rol);
            //Se comprueba que los campos no esten vacios
            if (txtNR.getText().isEmpty() || txtUsuario.getText().isEmpty() || txtPwd.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Hace falta datos.");

            } else {
              Connection con = LogicaConexion.getConecta();
              PreparedStatement ps;
                //Se insertan los datos digitados por el empleado hacia la base de datos  
                String sql = "INSERT INTO representantes_gral(representante_gral,usuario,pwd,current_rol) values( ?, ?, ?, ?::rol);";
                 ps = con.prepareStatement(sql);
                //Se prepara la sentencia sql 
                ps.setString(1, txtNR.getText());
                ps.setString(2, txtUsuario.getText());
                ps.setString(3, txtPwd.getText());
                ps.setString(4, rol);

                ps.executeUpdate();//Se insertan los datos en la base de datos
                //Se muestra un mensaje de que los usuarios fueron insertados
                JOptionPane.showMessageDialog(null, "Datos insertados correctamente");
                JOptionPane.showMessageDialog(null, "Usuario ingresado con exito");
                limpiarReprensentanteGNERAL();
                limpiaTabla(TablaGENERAL);
                consultarReprensentanteGNERAL(TablaGENERAL);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
     
    public void InsertarReprensentanteGRUPO(String Usuario){
        String representante = "";
        int idRepresentante = 0;

        try (Connection conexion = LogicaConexion.getConecta()) {
            String consulta = "SELECT id_representante_gral, representante_gral FROM representantes_gral WHERE usuario=?;";
            System.out.println(consulta);
            System.out.println(Usuario);
            try (PreparedStatement consultaPreparada = conexion.prepareStatement(consulta)) {
                consultaPreparada.setString(1, usuario);

                try (ResultSet resultado = consultaPreparada.executeQuery()) {
                    if (resultado.next()) {
                        idRepresentante = resultado.getInt("id_representante_gral");
                        representante = resultado.getString("representante_gral");
                    }
                }
            }
              // Inserción en la tabla representantes_gpo
        String sql = "INSERT INTO representantes_gpo (representante_gpo, telefono, clave_elector, seccion_electoral, representante_gral, id_representante_gral) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement insercionPreparada = conexion.prepareStatement(sql)) {
            // Setear los valores para la inserción
            insercionPreparada.setString(1, txtNR1.getText());
            insercionPreparada.setString(2, txtTel.getText());
            insercionPreparada.setString(3, txtClav.getText());
            insercionPreparada.setString(4, txtSeccio.getText());
            insercionPreparada.setString(5, representante);
            insercionPreparada.setInt(6, idRepresentante);

            // Ejecutar la inserción
            insercionPreparada.executeUpdate();
             JOptionPane.showMessageDialog(null, "Datos insertados correctamente");
            
        }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
     
     
     public void consultarReprensentanteGNERAL(DefaultTableModel x) { //Metodo que ayuda a realizar la consulta del producto
        Object[] ob = new Object[4];//Se define un arreglo de objetos
        try {
            rs = ejecutaQuery("select * from representantes_gral");//Se reaalizar una consulta de producto
            while (rs.next()) { //Se le asignan los valor a un arreglo de objetos 
                ob[0] = rs.getObject("id_representante_gral");
                ob[1] = rs.getObject("representante_gral"); 
                ob[2] = rs.getObject("usuario");
                ob[3] = rs.getObject("current_rol");
                x.addRow(ob);//Se agrega los campos en un JTable
            }
            conexion.close();//Se cierra conexion
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Hay algo mal");
        }
       jTablaGENERAL.setModel(x);
    }
     
      public void consultarReprensentanteGRUPAL(DefaultTableModel x) { //Metodo que ayuda a realizar la consulta del producto
        Object[] ob = new Object[6];//Se define un arreglo de objetos
        try {
            rs = ejecutaQuery("select * from representantes_gpo");//Se reaalizar una consulta de producto
            while (rs.next()) { //Se le asignan los valor a un arreglo de objetos 
                ob[0] = rs.getObject("id_representante_gpo");
                ob[1] = rs.getObject("representante_gpo"); 
                ob[2] = rs.getObject("telefono");
                ob[3] = rs.getObject("clave_elector");
                ob[4] = rs.getObject("seccion_electoral");
                ob[5] = rs.getObject("representante_gral");
                x.addRow(ob);//Se agrega los campos en un JTable
            }
            conexion.close();//Se cierra conexion
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Hay algo mal");
        }
       jTablaGrupo.setModel(x);
    }
     
      public void eliminarRegistroReprensentanteGNERAL(DefaultTableModel dtm) { //Metodo que ayuda a elimanr productos
        try {
            if (jTablaGENERAL.getSelectedRow() < 0) { //Se verifica si el usuario eligio alguna fila para moficar          
                JOptionPane.showMessageDialog(null, "Selecione algun registro para eliminar");//Mensaje para notificar que tiene que seleccionar un prodcuto
            } else {
                int fila = jTablaGENERAL.getSelectedRow(); //Se asigna la fila seleccionada
                int id = Integer.parseInt(jTablaGENERAL.getValueAt(fila, 0).toString());
               rs = ejecutaQuery("delete from  representantes_gral where id_representante_gral=" + id + ";");
                    JOptionPane.showMessageDialog(null, "Registro eliminado");
                    conexion.close();
                    dtm.removeRow(jTablaGENERAL.getSelectedRow());
                    
               
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
      }
      
      public void CambiarDatosRepresentanteGNERAL() {
    int fila = jTablaGENERAL.getSelectedRow();
    
    if (fila >= 0) { // Verificar si se ha seleccionado una fila
        int id = Integer.parseInt(jTablaGENERAL.getValueAt(fila, 0).toString());
        String rol =  jcRol.getSelectedItem().toString(); // Agregar "ROLE " al nombre del rol
          PreparedStatement ps;
        try {
            String actualizacion = "UPDATE representantes_gral SET representante_gral= ?, usuario= ?, pwd= ?, current_rol= ?::rol where id_representante_gral=" + id + ";";
            System.out.println(actualizacion);
            // Usando try-with-resources para manejar el cierre automático de recursos
            
            ps = con.prepareStatement(actualizacion);
                ps.setString(1, txtNR.getText());
                ps.setString(2, txtUsuario.getText());
                ps.setString(3, txtPwd.getText());
                ps.setString(4, rol);
              
                ps.executeUpdate();
            // Se cierra automáticamente el PreparedStatement aquí
            
            JOptionPane.showMessageDialog(null, "Datos actualizados correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar datos: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(null, "Selecciona una fila para actualizar");
    }
}
       
     
     public void limpiaTabla(DefaultTableModel x) { //Metod que ayuda a limpiar las tablas
        int a = x.getRowCount();

        while (a != 0) {
            if (a != 0) {
                x.removeRow(0);
            }
            a = x.getRowCount();
        }
    }
     
     public void limpiarReprensentanteGNERAL(){
         txtNR.setText(null);
         txtUsuario.setText(null);
         txtPwd.setText(null);
     }
     
     public void limpiarReprensentanteGRUPAL(){
         txtNR1.setText(null);
         txtTel.setText(null);
        txtClav.setText(null);
        txtSeccio.setText(null);
     }

    
    

   
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RepresentanteGNERAL = new javax.swing.JDialog();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jcRol = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        txtNR = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtPwd = new javax.swing.JTextField();
        txtUsuario = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        Listar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaGENERAL = new javax.swing.JTable();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        login = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtNUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        txtCUsuario = new javax.swing.JPasswordField();
        btnIniciarSesion1 = new javax.swing.JButton();
        jMenuItem3 = new javax.swing.JMenuItem();
        participantes = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtNombreP = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtClaveElectorP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTelefonoP = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jrbHombre = new javax.swing.JRadioButton();
        jrbMujer = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDomi = new javax.swing.JTextField();
        txtLocalidad = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtObservaciones = new javax.swing.JTextField();
        btnRegistrarP = new javax.swing.JButton();
        btnModificarP = new javax.swing.JButton();
        btnEliminarP = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtSeccionE = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtEda = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        RepresentanteGPO = new javax.swing.JDialog();
        jPanel12 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtNR1 = new javax.swing.JTextField();
        btnRegistrar1 = new javax.swing.JButton();
        btnModificar1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtClav = new javax.swing.JTextField();
        txtTel = new javax.swing.JTextField();
        txtSeccio = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        Listar1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablaGrupo = new javax.swing.JTable();
        jMenuBar4 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        Lista = new javax.swing.JDialog();
        jPanel15 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtMunicipio = new javax.swing.JTextField();
        btnRegistrar2 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtSeccElect = new javax.swing.JTextField();
        txtUsuario1 = new javax.swing.JTextField();
        txtNomenclatura = new javax.swing.JTextField();
        jMenuBar5 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        rbgSexo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        RepresentanteGNERAL.setUndecorated(true);

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setText("REPRESENTANTES ");
        jPanel9.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingresar datos*"));

        jLabel22.setText("Rol:");

        jcRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "normal", "privilegiado" }));

        jLabel24.setText("Nombre del representante:");

        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/create.png"))); // NOI18N
        btnRegistrar.setText("Agregar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/delete.png"))); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel25.setText("Contraseña:");

        jLabel26.setText("Usuario:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(99, 99, 99)
                                .addComponent(jLabel22)
                                .addGap(0, 41, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jcRol, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(88, 88, 88))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(txtPwd, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnModificar)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(207, 207, 207))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24))
                    .addComponent(txtNR, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel26)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addGap(13, 13, 13)
                .addComponent(txtNR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(btnRegistrar)
                .addGap(18, 18, 18)
                .addComponent(btnModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addGap(20, 20, 20)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel9.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 410, 400));

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/clear.png"))); // NOI18N
        jButton1.setText("Limpiar");

        Listar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/read.png"))); // NOI18N
        Listar.setText("Listar");

        jTablaGENERAL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Usuario", "Rol"
            }
        ));
        jScrollPane1.setViewportView(jTablaGENERAL);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(Listar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 132, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Listar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 60, 410, 400));

        jMenu2.setText("Menú");

        jMenuItem4.setText("Regresar a inicio");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Salir");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar2.add(jMenu2);

        RepresentanteGNERAL.setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout RepresentanteGNERALLayout = new javax.swing.GroupLayout(RepresentanteGNERAL.getContentPane());
        RepresentanteGNERAL.getContentPane().setLayout(RepresentanteGNERALLayout);
        RepresentanteGNERALLayout.setHorizontalGroup(
            RepresentanteGNERALLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        RepresentanteGNERALLayout.setVerticalGroup(
            RepresentanteGNERALLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        login.setUndecorated(true);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(32, 222, 110));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 420));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Source Code Pro Light", 0, 14)); // NOI18N
        jLabel2.setText("Contraseña:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, -1, -1));

        jLabel1.setFont(new java.awt.Font("Source Code Pro Light", 0, 14)); // NOI18N
        jLabel1.setText("Usuario:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, -1, -1));

        txtNUsuario.setFont(new java.awt.Font("Source Code Pro Light", 0, 14)); // NOI18N
        jPanel2.add(txtNUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 179, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/login2.png"))); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 96, 96));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pwd.png"))); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 252, 32, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/usr.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 182, 32, 30));

        btnSalir.setBackground(new java.awt.Color(0, 153, 204));
        btnSalir.setFont(new java.awt.Font("Source Code Pro Semibold", 0, 14)); // NOI18N
        btnSalir.setText("Iniciar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jPanel2.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 330, 130, -1));

        txtCUsuario.setFont(new java.awt.Font("Source Code Pro Light", 0, 14)); // NOI18N
        jPanel2.add(txtCUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 260, 180, -1));

        btnIniciarSesion1.setBackground(new java.awt.Color(0, 153, 204));
        btnIniciarSesion1.setFont(new java.awt.Font("Source Code Pro Semibold", 0, 14)); // NOI18N
        btnIniciarSesion1.setText("Salir");
        btnIniciarSesion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarSesion1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnIniciarSesion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 330, 130, -1));

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 400, 420));

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login.getContentPane());
        login.getContentPane().setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jMenuItem3.setText("jMenuItem3");

        participantes.setUndecorated(true);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingresar datos*"));

        jLabel6.setText("Nombre del participante:");

        jLabel7.setText("Clave de elector:");

        jLabel8.setText("Teléfono:");

        jLabel9.setText("Sexo:");

        jrbHombre.setText("Hombre");

        jrbMujer.setText("Mujer");

        jLabel10.setText("Domicilio:");

        jLabel11.setText("Localidad:");

        jLabel12.setText("EE:");

        jLabel13.setText("Observaciones:");

        btnRegistrarP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/create.png"))); // NOI18N
        btnRegistrarP.setText("Agregar");
        btnRegistrarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarPActionPerformed(evt);
            }
        });

        btnModificarP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update.png"))); // NOI18N
        btnModificarP.setText("Modificar");

        btnEliminarP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/delete.png"))); // NOI18N
        btnEliminarP.setText("Eliminar");

        jLabel14.setText("Seccion electoral");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGG", "RG", "RC" }));

        jLabel15.setText("Edad");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(55, 55, 55))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(92, 262, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel9)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreP)
                            .addComponent(txtSeccionE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTelefonoP)
                            .addComponent(txtClaveElectorP, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addGap(56, 56, 56))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRegistrarP, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btnModificarP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEliminarP, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(80, 80, 80))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(txtDomi, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(jrbHombre, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jrbMujer, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(36, 36, 36)))
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtLocalidad, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                                    .addComponent(txtEda))
                                .addContainerGap())))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel11))
                .addGap(74, 74, 74))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClaveElectorP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel14))
                .addGap(5, 5, 5)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefonoP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeccionE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbMujer)
                    .addComponent(jrbHombre)
                    .addComponent(txtEda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDomi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModificarP)
                    .addComponent(btnRegistrarP)
                    .addComponent(btnEliminarP))
                .addGap(18, 18, 18))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Clave", "Telefono", "Sexo", "Edad", "Domicilio", "Localidad", "EE", "Observaciones", "Representante"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(286, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jMenu3.setText("Menu");

        jMenuItem8.setText("Regresar al inicio");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuItem9.setText("Salir");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuBar3.add(jMenu3);

        participantes.setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout participantesLayout = new javax.swing.GroupLayout(participantes.getContentPane());
        participantes.getContentPane().setLayout(participantesLayout);
        participantesLayout.setHorizontalGroup(
            participantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        participantesLayout.setVerticalGroup(
            participantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        RepresentanteGPO.setUndecorated(true);

        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setText("REPRESENTANTES ");
        jPanel12.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingresar datos*"));

        jLabel23.setText("Seccion electoral");

        jLabel27.setText("Nombre del representante:");

        btnRegistrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/create.png"))); // NOI18N
        btnRegistrar1.setText("Agregar");
        btnRegistrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrar1ActionPerformed(evt);
            }
        });

        btnModificar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update.png"))); // NOI18N
        btnModificar1.setText("Modificar");

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/delete.png"))); // NOI18N
        jButton4.setText("Eliminar");

        jLabel28.setText("Clave electoral");

        jLabel29.setText("Telefono");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(txtClav, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel28)
                                .addGap(69, 69, 69)))
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSeccio))
                        .addGap(159, 159, 159))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel13Layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(btnModificar1))
                                .addComponent(btnRegistrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(txtNR1)
                .addGap(159, 159, 159))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel27))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel29)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addGap(13, 13, 13)
                .addComponent(txtNR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel28)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeccio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(btnRegistrar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar1)
                .addGap(20, 20, 20)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 410, 400));

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/clear.png"))); // NOI18N
        jButton2.setText("Limpiar");

        Listar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/read.png"))); // NOI18N
        Listar1.setText("Listar");

        jTablaGrupo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Telefono", "Clave electoral", "Seccion electoral", "Representante"
            }
        ));
        jScrollPane3.setViewportView(jTablaGrupo);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(Listar1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 146, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Listar1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 60, 410, 400));

        jMenu4.setText("Menú");

        jMenuItem6.setText("Regresar a inicio");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuItem7.setText("Salir");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuBar4.add(jMenu4);

        RepresentanteGPO.setJMenuBar(jMenuBar4);

        javax.swing.GroupLayout RepresentanteGPOLayout = new javax.swing.GroupLayout(RepresentanteGPO.getContentPane());
        RepresentanteGPO.getContentPane().setLayout(RepresentanteGPOLayout);
        RepresentanteGPOLayout.setHorizontalGroup(
            RepresentanteGPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        RepresentanteGPOLayout.setVerticalGroup(
            RepresentanteGPOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        Lista.setUndecorated(true);

        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setText("Lista");
        jPanel15.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, -1, -1));

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingresar datos*"));

        jLabel30.setText("Nomenclatura");

        jLabel31.setText("Municipio");

        btnRegistrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/create.png"))); // NOI18N
        btnRegistrar2.setText("Agregar");

        jLabel32.setText("Seccion electoral");

        jLabel33.setText("Localidad");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(txtUsuario1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRegistrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtSeccElect))
                                .addGap(50, 50, 50)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel30)
                                    .addComponent(txtNomenclatura, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel31))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel33))
                    .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addGap(13, 13, 13)
                .addComponent(txtMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel32)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuario1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSeccElect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomenclatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(84, 84, 84)
                .addComponent(btnRegistrar2)
                .addContainerGap())
        );

        jPanel15.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 410, 400));

        jMenu5.setText("Menú");

        jMenuItem10.setText("Regresar a inicio");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuItem11.setText("Salir");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem11);

        jMenuBar5.add(jMenu5);

        Lista.setJMenuBar(jMenuBar5);

        javax.swing.GroupLayout ListaLayout = new javax.swing.GroupLayout(Lista.getContentPane());
        Lista.getContentPane().setLayout(ListaLayout);
        ListaLayout.setHorizontalGroup(
            ListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListaLayout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ListaLayout.setVerticalGroup(
            ListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jTextField1.setText("Bienvenido al sistema");
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, -1, 37));

        jMenu1.setText("Menú");

        jMenuItem1.setText("Nuevo Representante General");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem12.setText("Nuevo Representante De Grupo");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        jMenuItem13.setText("Nueva Lista");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem13);

        jMenuItem2.setText("Salir");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        usuario = txtNUsuario.getText();
        char contrasenia [] = txtCUsuario.getPassword();
        String contra=String.valueOf(contrasenia);
        
        String consulta = "Select usuario, pwd, current_rol "+
                "from representantes_gral where usuario='"+usuario+"'";     
        try{
            con = LogicaConexion.getConecta();
            PreparedStatement ps = con.prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String  u = rs.getString("usuario");
                
                String p = rs.getString("pwd");
                String r = rs.getString("current_rol");
                if(contra.equals(p)){
                    if(r.equals("privilegiado")){
                        login.dispose();
                        this.setVisible(true);
                    }else if(r.equals("normal")){
                        login.dispose();
                        this.setVisible(true);
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null, "La contraseña es incorrecta");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
        if(txtNUsuario.getText().isEmpty() || contra.isEmpty()){
            JOptionPane.showMessageDialog(null, "Rellene todos los campos");
        }else{
            //int prueba = LogicaConexion.probarUsuario(txtNUsuario.getText(), contra);
        }

    }//GEN-LAST:event_btnSalirActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        RepresentanteGNERAL.dispose();
        RepresentanteGNERAL.setVisible(true);
        this.setVisible(false);
        
        limpiaTabla(TablaGENERAL);
        limpiarReprensentanteGNERAL();
        consultarReprensentanteGNERAL(TablaGENERAL);
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        RepresentanteGNERAL.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        salir();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void btnIniciarSesion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarSesion1ActionPerformed
        salir();
    }//GEN-LAST:event_btnIniciarSesion1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
      RepresentanteGPO.setVisible(false);
     this.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        salir();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
     Lista.setVisible(false);
     this.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        salir();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
         Lista.dispose();
         Lista.setVisible(true);
         this.setVisible(false);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
         RepresentanteGPO.dispose();
         RepresentanteGPO.setVisible(true);
         this.setVisible(false);
         consultarReprensentanteGRUPAL(TablaGRUPO);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
     participantes.setVisible(false);
     this.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        salir();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
         salir();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void btnRegistrarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarPActionPerformed
       
    }//GEN-LAST:event_btnRegistrarPActionPerformed

    private void btnRegistrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrar1ActionPerformed
        InsertarReprensentanteGRUPO(usuario);
    }//GEN-LAST:event_btnRegistrar1ActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
      InsertarReprensentanteGNERAL();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
       if (jTablaGENERAL.getSelectedRow() < 0) { //Se verifica si el usuario eligio alguna fila para moficar          
            JOptionPane.showMessageDialog(null, "Selecione el dato a editar");//En caso de no haber seleccionada alguna se le manda este mensage
        } else if (txtNR.getText().isEmpty() ||txtUsuario.getText().isEmpty() ||txtPwd.getText().isEmpty()){
          JOptionPane.showMessageDialog(null, "Ingrese los datos solicitados", "Error", JOptionPane.ERROR_MESSAGE);
        }else{

            
           CambiarDatosRepresentanteGNERAL();
           limpiaTabla(TablaGENERAL);
           limpiarReprensentanteGNERAL();
           consultarReprensentanteGNERAL(TablaGENERAL);
          
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       eliminarRegistroReprensentanteGNERAL(TablaGENERAL);
       limpiaTabla(TablaGENERAL);
       limpiarReprensentanteGNERAL();
       consultarReprensentanteGNERAL(TablaGENERAL);
    }//GEN-LAST:event_jButton3ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                } catch (Exception ex) {
                    Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
                }
                new principal().setVisible(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Lista;
    private javax.swing.JButton Listar;
    private javax.swing.JButton Listar1;
    private javax.swing.JDialog RepresentanteGNERAL;
    private javax.swing.JDialog RepresentanteGPO;
    private javax.swing.JButton btnEliminarP;
    private javax.swing.JButton btnIniciarSesion1;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnModificar1;
    private javax.swing.JButton btnModificarP;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnRegistrar1;
    private javax.swing.JButton btnRegistrar2;
    private javax.swing.JButton btnRegistrarP;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuBar jMenuBar4;
    private javax.swing.JMenuBar jMenuBar5;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTablaGENERAL;
    private javax.swing.JTable jTablaGrupo;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JComboBox<String> jcRol;
    private javax.swing.JRadioButton jrbHombre;
    private javax.swing.JRadioButton jrbMujer;
    private static javax.swing.JDialog login;
    private javax.swing.JDialog participantes;
    private javax.swing.ButtonGroup rbgSexo;
    private javax.swing.JPasswordField txtCUsuario;
    private javax.swing.JTextField txtClav;
    private javax.swing.JTextField txtClaveElectorP;
    private javax.swing.JTextField txtDomi;
    private javax.swing.JTextField txtEda;
    private javax.swing.JTextField txtLocalidad;
    private javax.swing.JTextField txtMunicipio;
    private javax.swing.JTextField txtNR;
    private javax.swing.JTextField txtNR1;
    private javax.swing.JTextField txtNUsuario;
    private javax.swing.JTextField txtNombreP;
    private javax.swing.JTextField txtNomenclatura;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtPwd;
    private javax.swing.JTextField txtSeccElect;
    private javax.swing.JTextField txtSeccio;
    private javax.swing.JTextField txtSeccionE;
    private javax.swing.JTextField txtTel;
    private javax.swing.JTextField txtTelefonoP;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtUsuario1;
    // End of variables declaration//GEN-END:variables
}
