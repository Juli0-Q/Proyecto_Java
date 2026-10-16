package vista;

import controlador.BibliotecaControlador;
import controlador.Validador;
import excepciones.BibliotecaException;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/** Módulo de Usuarios (RF08 - RF13). */
public class PanelUsuarios extends JPanel implements Actualizable {
    private final BibliotecaControlador ctrl;

    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Código", "Nombre"}, 0) {
        @Override public boolean isCellEditable(int fila, int col) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtBuscarCodigo = new JTextField(8);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombre = new JTextField(24);

    public PanelUsuarios(BibliotecaControlador ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        FiltroNumerico.aplicar(txtBuscarCodigo, 9);
        FiltroNumerico.aplicar(txtCodigo, 9);

        JPanel pBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        JButton btnBuscar = new JButton("Buscar por código");
        JButton btnTodos = new JButton("Mostrar todos");
        pBusqueda.add(new JLabel("Código:"));
        pBusqueda.add(txtBuscarCodigo);
        pBusqueda.add(btnBuscar);
        pBusqueda.add(btnTodos);
        add(pBusqueda, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
                txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pForm.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));
        pForm.add(new JLabel("Código:"));
        pForm.add(txtCodigo);
        pForm.add(new JLabel("Nombre:"));
        pForm.add(txtNombre);

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

        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> actualizar());
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    @Override
    public void actualizar() {
        mostrar(ctrl.listarUsuarios());
        if (txtCodigo.getText().isEmpty()) {
            txtCodigo.setText(String.valueOf(ctrl.siguienteCodigoUsuario()));
        }
    }

    private void mostrar(List<Usuario> usuarios) {
        modelo.setRowCount(0);
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{u.getCodigo(), u.getNombre()});
        }
    }

    private void buscar() {
        try {
            int codigo = Validador.codigo("Código de búsqueda", txtBuscarCodigo.getText());
            mostrar(Collections.singletonList(ctrl.buscarUsuario(codigo)));
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void registrar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            String nombre = Validador.texto("Nombre", txtNombre.getText());
            ctrl.registrarUsuario(codigo, nombre);
            limpiar();
            Mensajes.info(this, "Usuario registrado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void modificar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            String nombre = Validador.texto("Nombre", txtNombre.getText());
            ctrl.modificarUsuario(codigo, nombre);
            limpiar();
            Mensajes.info(this, "Usuario modificado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void eliminar() {
        try {
            int codigo = Validador.codigo("Código", txtCodigo.getText());
            ctrl.buscarUsuario(codigo);
            if (!Mensajes.confirmar(this, "¿Seguro que desea eliminar al usuario con código " + codigo + "?")) {
                return;
            }
            ctrl.eliminarUsuario(codigo);
            limpiar();
            Mensajes.info(this, "Usuario eliminado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtBuscarCodigo.setText("");
        tabla.clearSelection();
        actualizar();
    }
}
