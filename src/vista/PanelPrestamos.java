package vista;

import controlador.BibliotecaControlador;
import controlador.Validador;
import excepciones.BibliotecaException;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Módulo de Préstamos y Devoluciones (RF14 - RF23). */
public class PanelPrestamos extends JPanel implements Actualizable {
    private final BibliotecaControlador ctrl;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Préstamo", "Cód. libro", "Libro", "Cód. usuario", "Usuario", "Fecha", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int col) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JRadioButton rbTodos = new JRadioButton("Todos los préstamos", true);
    private final JRadioButton rbActivos = new JRadioButton("Solo activos");
    private final JTextField txtConsultaLibro = new JTextField(7);
    private final JTextField txtConsultaUsuario = new JTextField(7);

    private final JTextField txtCodPrestamo = new JTextField(7);
    private final JTextField txtCodLibro = new JTextField(7);
    private final JTextField txtCodUsuario = new JTextField(7);

    public PanelPrestamos(BibliotecaControlador ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        FiltroNumerico.aplicar(txtConsultaLibro, 9);
        FiltroNumerico.aplicar(txtConsultaUsuario, 9);
        FiltroNumerico.aplicar(txtCodPrestamo, 9);
        FiltroNumerico.aplicar(txtCodLibro, 9);
        FiltroNumerico.aplicar(txtCodUsuario, 9);

        // ---- Filtros y consultas
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbTodos);
        grupo.add(rbActivos);
        JButton btnQuienTiene = new JButton("¿Quién lo tiene?");
        JButton btnHistorial = new JButton("Ver historial");

        JPanel pFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pFiltros.setBorder(BorderFactory.createTitledBorder("Filtros y consultas"));
        pFiltros.add(rbTodos);
        pFiltros.add(rbActivos);
        pFiltros.add(new JLabel("    Cód. libro:"));
        pFiltros.add(txtConsultaLibro);
        pFiltros.add(btnQuienTiene);
        pFiltros.add(new JLabel("    Cód. usuario:"));
        pFiltros.add(txtConsultaUsuario);
        pFiltros.add(btnHistorial);
        add(pFiltros, BorderLayout.NORTH);

        // ---- Tabla
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtCodLibro.setText(modelo.getValueAt(fila, 1).toString());
                txtCodUsuario.setText(modelo.getValueAt(fila, 3).toString());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ---- Formulario
        JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pForm.setBorder(BorderFactory.createTitledBorder("Registrar préstamo / devolución"));
        pForm.add(new JLabel("Cód. préstamo:"));
        pForm.add(txtCodPrestamo);
        pForm.add(new JLabel("Cód. libro:"));
        pForm.add(txtCodLibro);
        pForm.add(new JLabel("Cód. usuario:"));
        pForm.add(txtCodUsuario);

        JButton btnPrestar = new JButton("Registrar préstamo");
        JButton btnDevolver = new JButton("Registrar devolución");
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        pBotones.add(btnPrestar);
        pBotones.add(btnDevolver);

        JPanel pSur = new JPanel(new BorderLayout());
        pSur.add(pForm, BorderLayout.CENTER);
        pSur.add(pBotones, BorderLayout.SOUTH);
        add(pSur, BorderLayout.SOUTH);

        // ---- Acciones
        rbTodos.addActionListener(e -> actualizar());
        rbActivos.addActionListener(e -> actualizar());
        btnQuienTiene.addActionListener(e -> quienTiene());
        btnHistorial.addActionListener(e -> historial());
        btnPrestar.addActionListener(e -> prestar());
        btnDevolver.addActionListener(e -> devolver());
    }

    @Override
    public void actualizar() {
        mostrar(rbActivos.isSelected() ? ctrl.listarPrestamosActivos() : ctrl.listarPrestamos());
        txtCodPrestamo.setText(String.valueOf(ctrl.siguienteCodigoPrestamo()));
    }

    private void mostrar(List<Prestamo> prestamos) {
        modelo.setRowCount(0);
        for (Prestamo p : prestamos) {
            modelo.addRow(new Object[]{
                    p.getCodigo(),
                    p.getLibro().getCodigo(), p.getLibro().getTitulo(),
                    p.getUsuario().getCodigo(), p.getUsuario().getNombre(),
                    p.getFecha(), p.getEstado()});
        }
    }

    private void prestar() {
        try {
            int codPrestamo = Validador.codigo("Código de préstamo", txtCodPrestamo.getText());
            int codLibro = Validador.codigo("Código de libro", txtCodLibro.getText());
            int codUsuario = Validador.codigo("Código de usuario", txtCodUsuario.getText());
            ctrl.realizarPrestamo(codPrestamo, codLibro, codUsuario);
            txtCodLibro.setText("");
            txtCodUsuario.setText("");
            tabla.clearSelection();
            actualizar();
            Mensajes.info(this, "Préstamo realizado correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void devolver() {
        try {
            int codLibro = Validador.codigo("Código de libro", txtCodLibro.getText());
            ctrl.prestamoActivoDeLibro(codLibro); // verifica que exista y esté prestado
            if (!Mensajes.confirmar(this, "¿Registrar la devolución del libro con código " + codLibro + "?")) {
                return;
            }
            ctrl.devolverLibro(codLibro);
            txtCodLibro.setText("");
            txtCodUsuario.setText("");
            tabla.clearSelection();
            actualizar();
            Mensajes.info(this, "Libro devuelto correctamente.");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void quienTiene() {
        try {
            int codLibro = Validador.codigo("Código de libro", txtConsultaLibro.getText());
            Prestamo p = ctrl.prestamoActivoDeLibro(codLibro);
            Mensajes.info(this, "El libro \"" + p.getLibro().getTitulo() + "\" lo tiene "
                    + p.getUsuario().getNombre() + " desde el " + p.getFecha() + ".");
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }

    private void historial() {
        try {
            int codUsuario = Validador.codigo("Código de usuario", txtConsultaUsuario.getText());
            List<Prestamo> historial = ctrl.historialDeUsuario(codUsuario);
            if (historial.isEmpty()) {
                Mensajes.info(this, "El usuario no tiene préstamos registrados.");
            }
            mostrar(historial);
        } catch (BibliotecaException ex) {
            Mensajes.error(this, ex.getMessage());
        }
    }
}
