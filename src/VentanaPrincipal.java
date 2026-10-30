import Funcionamiento.ControladorBiblioteca;
import Funcionamiento.Libro;
import Funcionamiento.Usuario;
import Funcionamiento.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Vista principal de la interfaz gráfica en Swing (MVC).
 * Incorpora 4 pestañas profesionales, búsquedas dinámicas, limpieza general 
 * y guardado automático al cerrar la aplicación.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class VentanaPrincipal extends JFrame {
    private ControladorBiblioteca controlador;
    
    // Componentes Libros
    private JTextField txtCodLibro, txtTitulo, txtAutor, txtBuscarLibro;
    private JTable tablaLibros;
    private DefaultTableModel modeloLibros;

    // Componentes Stock
    private JTextField txtCodLibroStock, txtCantidadAñadir, txtBuscarStock;
    private JTable tablaStock;
    private DefaultTableModel modeloStock;

    // Componentes Usuarios
    private JTextField txtCodUsuario, txtNombreUsuario, txtCorreoUsuario, txtBuscarUsuario;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;

    // Componentes Préstamos
    private JTextField txtCodPrestamo, txtCodLibroPrestamo, txtCodUsrPrestamo, txtCodDevolucion, txtBuscarPrestamo;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloPrestamos;

    private JButton btnExportar, bPerdida, bRevertirBaja;

    public VentanaPrincipal() {
        controlador = new ControladorBiblioteca();

        setTitle("Sistema Integral de Gestión de Biblioteca - MVC");
        setSize(1150, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Guardado automático al cerrar la aplicación con la 'X'
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                controlador.exportarTodoACSV();
            }
        });

        JTabbedPane pestanas = new JTabbedPane();

        // ==========================================
        // PESTAÑA 1: GESTIÓN DE LIBROS
        // ==========================================
        JPanel panelLibros = new JPanel(new BorderLayout(10, 10));
        JPanel panelNorteLibros = new JPanel(new BorderLayout(5, 5));
        JPanel formLibros = new JPanel(new GridBagLayout());
        formLibros.setBorder(BorderFactory.createTitledBorder("Catálogo de Libros (Registro de Títulos y Autores)"));
        formLibros.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formLibros.add(new JLabel("Código (Auto):"), gbc);
        gbc.gridx = 1; txtCodLibro = new JTextField(15); txtCodLibro.setEditable(false); txtCodLibro.setBackground(new Color(230, 230, 230)); formLibros.add(txtCodLibro, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formLibros.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; txtTitulo = new JTextField(25); formLibros.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formLibros.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; txtAutor = new JTextField(25); formLibros.add(txtAutor, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formLibros.add(new JLabel("🔍 Buscar Libro:"), gbc);
        gbc.gridx = 1; txtBuscarLibro = new JTextField(25); formLibros.add(txtBuscarLibro, gbc);

        JPanel btnLibros = new JPanel(new FlowLayout());
        JButton bRegL = crearBoton("Registrar Libro", new Color(40, 167, 69));
        JButton bModL = crearBoton("Modificar", new Color(23, 162, 184));
        JButton bDelL = crearBoton("Eliminar", new Color(220, 53, 69));
        JButton bLimL = crearBoton("Limpiar", new Color(100, 100, 100));
        btnExportar = crearBoton("📁 Exportar Todo a CSV", new Color(108, 117, 125));
        btnLibros.add(bRegL); btnLibros.add(bModL); btnLibros.add(bDelL); btnLibros.add(bLimL); btnLibros.add(btnExportar);

        panelNorteLibros.add(formLibros, BorderLayout.CENTER);
        panelNorteLibros.add(btnLibros, BorderLayout.SOUTH);
        panelLibros.add(panelNorteLibros, BorderLayout.NORTH);

        modeloLibros = new DefaultTableModel(new String[]{"Código", "Título", "Autor", "Stock", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaLibros = new JTable(modeloLibros);
        panelLibros.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);


        // ==========================================
        // PESTAÑA 2: GESTIÓN DE STOCK E INVENTARIO
        // ==========================================
        JPanel panelStock = new JPanel(new BorderLayout(10, 10));
        JPanel panelNorteStock = new JPanel(new BorderLayout(5, 5));
        JPanel formStock = new JPanel(new GridBagLayout());
        formStock.setBorder(BorderFactory.createTitledBorder("Auditoría y Aumento de Stock Físico"));
        formStock.setBackground(new Color(245, 247, 250));

        gbc.gridx = 0; gbc.gridy = 0; formStock.add(new JLabel("Cod. Libro seleccionado:"), gbc);
        gbc.gridx = 1; txtCodLibroStock = new JTextField(15); txtCodLibroStock.setEditable(false); txtCodLibroStock.setBackground(new Color(230, 230, 230)); formStock.add(txtCodLibroStock, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formStock.add(new JLabel("Cantidad a Sumar (+ / -):"), gbc);
        gbc.gridx = 1; txtCantidadAñadir = new JTextField(15); txtCantidadAñadir.setText("1"); formStock.add(txtCantidadAñadir, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formStock.add(new JLabel("🔍 Buscar por Título/Autor:"), gbc);
        gbc.gridx = 1; txtBuscarStock = new JTextField(25); formStock.add(txtBuscarStock, gbc);

        JPanel btnPanelStock = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bSumarStock = crearBoton("Actualizar / Sumar Stock", new Color(40, 167, 69));
        JButton bLimS = crearBoton("Limpiar", new Color(100, 100, 100));
        btnPanelStock.add(bSumarStock);
        btnPanelStock.add(bLimS);

        panelNorteStock.add(formStock, BorderLayout.CENTER);
        panelNorteStock.add(btnPanelStock, BorderLayout.SOUTH);
        panelStock.add(panelNorteStock, BorderLayout.NORTH);

        modeloStock = new DefaultTableModel(new String[]{"Código", "Título", "Autor", "Stock Actual", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaStock = new JTable(modeloStock);
        panelStock.add(new JScrollPane(tablaStock), BorderLayout.CENTER);


        // ==========================================
        // PESTAÑA 3: GESTIÓN DE USUARIOS
        // ==========================================
        JPanel panelUsuarios = new JPanel(new BorderLayout(10, 10));
        JPanel panelNorteUsr = new JPanel(new BorderLayout(5, 5));
        JPanel formUsr = new JPanel(new GridBagLayout());
        formUsr.setBorder(BorderFactory.createTitledBorder("Gestión de Usuarios / Lectores (Control de Bloqueo por Mora)"));
        formUsr.setBackground(new Color(245, 247, 250));

        gbc.gridx = 0; gbc.gridy = 0; formUsr.add(new JLabel("Cod. Usuario (Auto):"), gbc);
        gbc.gridx = 1; txtCodUsuario = new JTextField(15); txtCodUsuario.setEditable(false); txtCodUsuario.setBackground(new Color(230, 230, 230)); formUsr.add(txtCodUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formUsr.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; txtNombreUsuario = new JTextField(25); formUsr.add(txtNombreUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formUsr.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1; txtCorreoUsuario = new JTextField(25); formUsr.add(txtCorreoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formUsr.add(new JLabel("🔍 Buscar Usuario:"), gbc);
        gbc.gridx = 1; txtBuscarUsuario = new JTextField(25); formUsr.add(txtBuscarUsuario, gbc);

        JPanel btnUsr = new JPanel(new FlowLayout());
        JButton bRegU = crearBoton("Registrar Usuario", new Color(40, 167, 69));
        JButton bModU = crearBoton("Modificar", new Color(23, 162, 184));
        JButton bDelU = crearBoton("Eliminar", new Color(220, 53, 69));
        JButton bLimU = crearBoton("Limpiar", new Color(100, 100, 100));
        btnUsr.add(bRegU); btnUsr.add(bModU); btnUsr.add(bDelU); btnUsr.add(bLimU);

        panelNorteUsr.add(formUsr, BorderLayout.CENTER);
        panelNorteUsr.add(btnUsr, BorderLayout.SOUTH);
        panelUsuarios.add(panelNorteUsr, BorderLayout.NORTH);

        modeloUsuarios = new DefaultTableModel(new String[]{"Código", "Nombre", "Correo", "Estado / Bloqueo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        panelUsuarios.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);


        // ==========================================
        // PESTAÑA 4: PRÉSTAMOS
        // ==========================================
        JPanel panelPrestamos = new JPanel(new BorderLayout(10, 10));
        JPanel panelNortePres = new JPanel(new BorderLayout(5, 5));
        JPanel formPres = new JPanel(new GridBagLayout());
        formPres.setBorder(BorderFactory.createTitledBorder("Control de Préstamos, Devoluciones, Pérdidas y Reposiciones"));
        formPres.setBackground(new Color(245, 247, 250));

        gbc.gridx = 0; gbc.gridy = 0; formPres.add(new JLabel("Cod. Préstamo (Auto):"), gbc);
        gbc.gridx = 1; txtCodPrestamo = new JTextField(15); txtCodPrestamo.setEditable(false); txtCodPrestamo.setBackground(new Color(230, 230, 230)); formPres.add(txtCodPrestamo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPres.add(new JLabel("Cod. Libro a Prestar:"), gbc);
        gbc.gridx = 1; txtCodLibroPrestamo = new JTextField(15); formPres.add(txtCodLibroPrestamo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPres.add(new JLabel("Cod. Usuario Solicitante:"), gbc);
        gbc.gridx = 1; txtCodUsrPrestamo = new JTextField(15); formPres.add(txtCodUsrPrestamo, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton bPrestar = crearBoton("Realizar Préstamo", new Color(70, 130, 180));
        formPres.add(bPrestar, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPres.add(new JLabel("Cod. Préstamo a Devolver/Revertir:"), gbc);
        gbc.gridx = 1; txtCodDevolucion = new JTextField(15); formPres.add(txtCodDevolucion, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPres.add(new JLabel("🔍 Buscar Préstamo (Usuario/Libro):"), gbc);
        gbc.gridx = 1; txtBuscarPrestamo = new JTextField(25); formPres.add(txtBuscarPrestamo, gbc);

        JPanel panelBotonesPres = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton bDevolver = crearBoton("Registrar Devolución", new Color(255, 140, 0));
        bPerdida = crearBoton("Registrar Pérdida (Baja)", new Color(220, 53, 69));
        bRevertirBaja = crearBoton("Revertir Baja / Reponer", new Color(40, 167, 69));
        JButton bLimP = crearBoton("Limpiar", new Color(100, 100, 100));

        panelBotonesPres.add(bDevolver);
        panelBotonesPres.add(bPerdida);
        panelBotonesPres.add(bRevertirBaja);
        panelBotonesPres.add(bLimP);

        gbc.gridx = 1; gbc.gridy = 6;
        formPres.add(panelBotonesPres, gbc);

        panelNortePres.add(formPres, BorderLayout.CENTER);
        panelPrestamos.add(panelNortePres, BorderLayout.NORTH);

        modeloPrestamos = new DefaultTableModel(new String[]{"Préstamo", "Libro", "Usuario", "Fecha", "Estado / Retraso"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPrestamos = new JTable(modeloPrestamos);
        panelPrestamos.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        // Añadir Pestañas
        pestanas.addTab("📖 Catálogo Libros", panelLibros);
        pestanas.addTab("📦 Gestión de Stock", panelStock);
        pestanas.addTab("👥 Usuarios", panelUsuarios);
        pestanas.addTab("🔄 Préstamos", panelPrestamos);
        add(pestanas);

        actualizarTablas();
        limpiarFormularios();

        // ==========================================
        // EVENTOS DE SELECCIÓN Y BÚSQUEDA
        // ==========================================
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaLibros.getSelectedRow() != -1) {
                int f = tablaLibros.getSelectedRow();
                txtCodLibro.setText(modeloLibros.getValueAt(f, 0).toString());
                txtTitulo.setText(modeloLibros.getValueAt(f, 1).toString());
                txtAutor.setText(modeloLibros.getValueAt(f, 2).toString());
            }
        });

        tablaStock.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaStock.getSelectedRow() != -1) {
                int f = tablaStock.getSelectedRow();
                txtCodLibroStock.setText(modeloStock.getValueAt(f, 0).toString());
            }
        });

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                int f = tablaUsuarios.getSelectedRow();
                txtCodUsuario.setText(modeloUsuarios.getValueAt(f, 0).toString());
                txtNombreUsuario.setText(modeloUsuarios.getValueAt(f, 1).toString());
                txtCorreoUsuario.setText(modeloUsuarios.getValueAt(f, 2).toString());
            }
        });

        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrestamos.getSelectedRow() != -1) {
                int f = tablaPrestamos.getSelectedRow();
                txtCodDevolucion.setText(modeloPrestamos.getValueAt(f, 0).toString());
            }
        });

        // Buscadores
        txtBuscarLibro.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                List<Libro> filtrados = controlador.buscarLibros(txtBuscarLibro.getText());
                modeloLibros.setRowCount(0);
                for (Libro l : filtrados) modeloLibros.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getStock(), l.getEstadoTexto()});
            }
        });

        txtBuscarStock.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                List<Libro> filtrados = controlador.buscarLibros(txtBuscarStock.getText());
                modeloStock.setRowCount(0);
                for (Libro l : filtrados) modeloStock.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getStock(), l.getEstadoTexto()});
            }
        });

        txtBuscarUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                List<Usuario> filtrados = controlador.buscarUsuarios(txtBuscarUsuario.getText());
                modeloUsuarios.setRowCount(0);
                for (Usuario u : filtrados) modeloUsuarios.addRow(new Object[]{u.getCodigo(), u.getNombre(), u.getCorreo(), u.getEstadoTexto()});
            }
        });

        txtBuscarPrestamo.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String query = txtBuscarPrestamo.getText().toLowerCase().trim();
                modeloPrestamos.setRowCount(0);
                for (Prestamo p : controlador.getPrestamos()) {
                    if (p.getUsuario().getNombre().toLowerCase().contains(query) ||
                        p.getLibro().getTitulo().toLowerCase().contains(query) ||
                        p.getCodigoPrestamo().toLowerCase().contains(query)) {
                        modeloPrestamos.addRow(new Object[]{p.getCodigoPrestamo(), p.getLibro().getTitulo(), p.getUsuario().getNombre(), p.getFechaPrestamo(), p.getEstado()});
                    }
                }
            }
        });

        // ACCIONES DE BOTONES
        bRegL.addActionListener(e -> {
            try {
                String cod = txtCodLibro.getText().trim();
                String tit = txtTitulo.getText().trim();
                String aut = txtAutor.getText().trim();
                if(tit.isEmpty() || aut.isEmpty()){ JOptionPane.showMessageDialog(this, "Rellene campos obligatorios."); return; }
                controlador.registrarLibro(cod, tit, aut);
                JOptionPane.showMessageDialog(this, "¡Libro registrado exitosamente!");
                limpiarFormularios();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        bModL.addActionListener(e -> {
            try {
                String cod = txtCodLibro.getText().trim();
                int stockActual = 0;
                for(Libro l : controlador.getLibros()) { if(l.getCodigo().equalsIgnoreCase(cod)) stockActual = l.getStock(); }
                controlador.modificarLibro(cod, txtTitulo.getText().trim(), txtAutor.getText().trim(), stockActual);
                JOptionPane.showMessageDialog(this, "¡Libro modificado!");
                limpiarFormularios();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Seleccione un libro."); }
        });

        bDelL.addActionListener(e -> {
            try {
                String cod = txtCodLibro.getText().trim();
                if(controlador.eliminarLibro(cod)){ JOptionPane.showMessageDialog(this, "Eliminado."); limpiarFormularios(); }
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Seleccione un libro."); }
        });

        bSumarStock.addActionListener(e -> {
            try {
                String cod = txtCodLibroStock.getText().trim();
                int cantidad = Integer.parseInt(txtCantidadAñadir.getText().trim());
                if(cod.isEmpty()){ 
                    JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla de stock."); 
                    return; 
                }
                if(controlador.ajustarStockLibro(cod, cantidad)){
                    JOptionPane.showMessageDialog(this, "¡Stock actualizado correctamente!");
                    limpiarFormularios();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el libro especificado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(NumberFormatException ex){ 
                JOptionPane.showMessageDialog(this, "Ingrese un número entero válido para sumar o restar stock.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        });

        bRegU.addActionListener(e -> {
            try {
                String cod = txtCodUsuario.getText().trim();
                String nom = txtNombreUsuario.getText().trim();
                String cor = txtCorreoUsuario.getText().trim();
                if(nom.isEmpty()){ JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
                controlador.registrarUsuario(cod, nom, cor);
                JOptionPane.showMessageDialog(this, "¡Usuario registrado!");
                limpiarFormularios();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        bModU.addActionListener(e -> {
            try {
                String cod = txtCodUsuario.getText().trim();
                controlador.modificarUsuario(cod, txtNombreUsuario.getText().trim(), txtCorreoUsuario.getText().trim());
                JOptionPane.showMessageDialog(this, "¡Usuario modificado!");
                limpiarFormularios();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Seleccione un usuario."); }
        });

        bDelU.addActionListener(e -> {
            try {
                String cod = txtCodUsuario.getText().trim();
                if(controlador.eliminarUsuario(cod)){ JOptionPane.showMessageDialog(this, "Usuario eliminado."); limpiarFormularios(); }
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Seleccione un usuario."); }
        });

        // Botones Limpiar
        bLimL.addActionListener(e -> limpiarFormularios());
        bLimS.addActionListener(e -> limpiarFormularios());
        bLimU.addActionListener(e -> limpiarFormularios());
        bLimP.addActionListener(e -> limpiarFormularios());

        btnExportar.addActionListener(e -> {
            controlador.exportarTodoACSV();
            JOptionPane.showMessageDialog(this, "¡Archivos exportados a CSV con éxito!");
        });

        bPrestar.addActionListener(e -> {
            try {
                String codP = txtCodPrestamo.getText();
                String codL = txtCodLibroPrestamo.getText().trim();
                String codU = txtCodUsrPrestamo.getText().trim();
                
                if(codL.isEmpty() || codU.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Debe ingresar el código del libro y del usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                controlador.registrarPrestamo(codP, codL, codU);
                JOptionPane.showMessageDialog(this, "¡Préstamo registrado exitosamente!");
                limpiarFormularios();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso de Préstamo", JOptionPane.WARNING_MESSAGE); }
        });

        bDevolver.addActionListener(e -> {
            String codP = txtCodDevolucion.getText().trim();
            if(codP.isEmpty()){
                JOptionPane.showMessageDialog(this, "Ingrese el código del préstamo a devolver.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(controlador.registrarDevolucion(codP)){
                JOptionPane.showMessageDialog(this, "¡Devolución procesada! Se verificó retraso, se restituyó el stock y se actualizó el estado del usuario.");
                limpiarFormularios();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró préstamo activo con ese código.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bPerdida.addActionListener(e -> {
            String codP = txtCodDevolucion.getText().trim();
            if(codP.isEmpty()){
                JOptionPane.showMessageDialog(this, "Ingrese el código del préstamo a dar de baja.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(controlador.registrarBajaPorPerdida(codP)){
                JOptionPane.showMessageDialog(this, "¡Pérdida registrada! El stock se descontó permanentemente y el usuario fue bloqueado.");
                limpiarFormularios();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró préstamo activo con ese código.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bRevertirBaja.addActionListener(e -> {
            String codP = txtCodDevolucion.getText().trim();
            if(codP.isEmpty()){
                JOptionPane.showMessageDialog(this, "Ingrese el código del préstamo dado de baja a reponer.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(controlador.revertirBajaPorPerdida(codP)){
                JOptionPane.showMessageDialog(this, "¡Baja revertida con éxito! Se reintegró el libro al stock y el usuario fue desbloqueado.");
                limpiarFormularios();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un préstamo 'Dado de Baja' con ese código.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private void actualizarTablas() {
        modeloLibros.setRowCount(0);
        for (Libro l : controlador.getLibros()) modeloLibros.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getStock(), l.getEstadoTexto()});

        modeloStock.setRowCount(0);
        for (Libro l : controlador.getLibros()) modeloStock.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getStock(), l.getEstadoTexto()});

        modeloUsuarios.setRowCount(0);
        for (Usuario u : controlador.getUsuarios()) modeloUsuarios.addRow(new Object[]{u.getCodigo(), u.getNombre(), u.getCorreo(), u.getEstadoTexto()});

        modeloPrestamos.setRowCount(0);
        for (Prestamo p : controlador.getPrestamos()) modeloPrestamos.addRow(new Object[]{p.getCodigoPrestamo(), p.getLibro().getTitulo(), p.getUsuario().getNombre(), p.getFechaPrestamo(), p.getEstado()});
    }

    private void limpiarFormularios() {
        txtCodLibro.setText(controlador.generarNuevoCodigoLibro());
        txtTitulo.setText(""); txtAutor.setText(""); txtBuscarLibro.setText(""); tablaLibros.clearSelection();

        txtCodLibroStock.setText(""); txtCantidadAñadir.setText("1"); txtBuscarStock.setText(""); tablaStock.clearSelection();

        txtCodUsuario.setText(controlador.generarNuevoCodigoUsuario());
        txtNombreUsuario.setText(""); txtCorreoUsuario.setText(""); txtBuscarUsuario.setText(""); tablaUsuarios.clearSelection();

        txtCodPrestamo.setText(controlador.generarNuevoCodigoPrestamo());
        txtCodLibroPrestamo.setText(""); txtCodUsrPrestamo.setText(""); txtCodDevolucion.setText(""); txtBuscarPrestamo.setText(""); tablaPrestamos.clearSelection();

        actualizarTablas();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}