package vista;

import controlador.BibliotecaControlador;
import controlador.Validador;
import excepciones.BibliotecaException;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/** Módulo de Gestión de Libros (RF01 - RF07). */
public class PanelLibros extends JPanel implements Actualizable {
    private final BibliotecaControlador ctrl;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Título", "Autor", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int col) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtBuscarCodigo = new JTextField(8);
    private final JTextField txtBuscarTitulo = new JTextField(16);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtAutor = new JTextField(20);

    public PanelLibros(BibliotecaControlador ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        FiltroNumerico.aplicar(txtBuscarCodigo, 9);
        FiltroNumerico.aplicar(txtCodigo, 9);

        // ---- Búsqueda
        JPanel pBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        JButton btnPorCodigo = new JButton("Buscar por código");
        JButton btnPorTitulo = new JButton("Buscar por título");
        JButton btnTodos = new JButton("Mostrar todos");
        pBusqueda.add(new JLabel("Código:"));
        pBusqueda.add(txtBuscarCodigo);
        pBusqueda.add(btnPorCodigo);
        pBusqueda.add(new JLabel("   Título:"));
        pBusqueda.add(txtBuscarTitulo);
        pBusqueda.add(btnPorTitulo);
        pBusqueda.add(btnTodos);
        add(pBusqueda, BorderLayout.NORTH);

        // ---- Tabla
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
                txtTitulo.setText(modelo.getValueAt(fila, 1).toString());
                txtAutor.setText(modelo.getValueAt(fila, 2).toString());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ---- Formulario
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Datos del libro"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; pForm.add(new JLabel("Código:"), g);
        g.gridx = 1; pForm.add(txtCodigo, g);
        g.gridx = 2; pForm.add(new JLabel("Título:"), g);
        g.gridx = 3; pForm.add(txtTitulo, g);
        g.gridx = 4; pForm.add(new JLabel("Autor:"), g);
        g.gridx = 5; pForm.add(txtAutor, g);

        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        pBotones.add(btnRegistrar);
        pBotones.add(btnModificar);
        pBotones.add(btnEliminar);
        pBotones.add(btnLimpiar);

        JPanel pSur = new JPanel(new BorderLayout());
        pSur.add(pForm, BorderLayout.CENTER);
        pSur.add(pBotones, BorderLayout.SOUTH);
        add(pSur, BorderLayout.SOUTH);

        // ---- Acciones
        btnPorCodigo.addActionListener(e -> buscarPorCodigo());
        btnPorTitulo.addActionListener(e -> buscarPorTitulo());
        btnTodos.addActionListener(e -> actualizar());
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    @Override
    public void actualizar() {
        mostrar(ctrl.listarLibros());
        if (txtCodigo.getText().isEmpty()) {
            txtCodigo.setText(String.valueOf(ctrl.siguienteCodigoLibro()));
        }
    }

    private void mostrar(List<Libro> libros) {
        modelo.setRowCount(0);
        for (Libro l : libros) {
            modelo.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getEstado()});
        }
    }

    private void buscarPorCodigo() {
        try {
            int codigo = Validador.codigo("Código de búsqueda", txtBuscarCodigo.getText());
            mostrar(Collections.singletonList(ctrl.buscarLibro(codigo)));
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void buscarPorTitulo() {
        try {
            String titulo = Validador.texto("Título de búsqueda", txtBuscarTitulo.getText());
            List<Libro> resultado = ctrl.buscarLibro(titulo);
            if (resultado.isEmpty()) {
                Mensajes.info(this, "No se encontraron libros con el título \"" + titulo + "\".");
            }
            mostrar(resultado);
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void registrar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            String titulo = Validador.texto("Título", txtTitulo.getText());
            String autor = Validador.texto("Autor", txtAutor.getText());
            ctrl.registrarLibro(codigo, titulo, autor);
            limpiar();
            Mensajes.info(this, "Libro registrado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void modificar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            String titulo = Validador.texto("Título", txtTitulo.getText());
            String autor = Validador.texto("Autor", txtAutor.getText());
            ctrl.modificarLibro(codigo, titulo, autor);
            limpiar();
            Mensajes.info(this, "Libro modificado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void eliminar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            ctrl.buscarLibro(codigo); // verifica que exista antes de confirmar
            if (!Mensajes.confirmar(this, "¿Seguro que desea eliminar el libro con código " + codigo + "?")) {
                return;
            }
            ctrl.eliminarLibro(codigo);
            limpiar();
            Mensajes.info(this, "Libro eliminado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtBuscarCodigo.setText("");
        txtBuscarTitulo.setText("");
        tabla.clearSelection();
        actualizar();
    }
}
