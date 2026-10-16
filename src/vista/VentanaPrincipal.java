package vista;

import controlador.BibliotecaControlador;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/** Ventana principal con navegación por módulos (RF24). */
public class VentanaPrincipal extends JFrame {
    private final BibliotecaControlador ctrl;
    private final JTabbedPane pestanas = new JTabbedPane();

    public VentanaPrincipal(BibliotecaControlador ctrl) {
        super("Sistema de Gestión y Control de Préstamos de una Biblioteca");
        this.ctrl = ctrl;

        pestanas.addTab("Libros", new PanelLibros(ctrl));
        pestanas.addTab("Usuarios", new PanelUsuarios(ctrl));
        pestanas.addTab("Préstamos y Devoluciones", new PanelPrestamos(ctrl));
        pestanas.addChangeListener(e -> actualizarPestanaActual());
        add(pestanas);

        setJMenuBar(crearMenu());
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });
        actualizarPestanaActual();
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu mArchivo = new JMenu("Archivo");
        JMenuItem miGuardar = new JMenuItem("Guardar datos");
        JMenuItem miExportar = new JMenuItem("Exportar copia a Excel...");
        JMenuItem miSalir = new JMenuItem("Salir");
        miGuardar.addActionListener(e -> guardar());
        miExportar.addActionListener(e -> exportar());
        miSalir.addActionListener(e -> salir());
        mArchivo.add(miGuardar);
        mArchivo.add(miExportar);
        mArchivo.addSeparator();
        mArchivo.add(miSalir);

        JMenu mModulos = new JMenu("Módulos");
        String[] nombres = {"Libros", "Usuarios", "Préstamos y Devoluciones"};
        for (int i = 0; i < nombres.length; i++) {
            final int indice = i;
            JMenuItem item = new JMenuItem(nombres[i]);
            item.addActionListener(e -> pestanas.setSelectedIndex(indice));
            mModulos.add(item);
        }

        JMenu mAyuda = new JMenu("Ayuda");
        JMenuItem miAcerca = new JMenuItem("Acerca de");
        miAcerca.addActionListener(e -> Mensajes.info(this,
                "Sistema de Gestión y Control de Préstamos de una Biblioteca\n"
                + "Técnicas de programación orientada a objetos - UPN"));
        mAyuda.add(miAcerca);

        barra.add(mArchivo);
        barra.add(mModulos);
        barra.add(mAyuda);
        return barra;
    }

    private void actualizarPestanaActual() {
        java.awt.Component c = pestanas.getSelectedComponent();
        if (c instanceof Actualizable) {
            ((Actualizable) c).actualizar();
        }
    }

    private void guardar() {
        try {
            ctrl.guardar();
            Mensajes.info(this, "Datos guardados en:\n" + ctrl.getArchivo());
        } catch (IOException ex) {
            Mensajes.error(this, "No se pudieron guardar los datos: " + ex.getMessage());
        }
    }

    private void exportar() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar copia en Excel");
        selector.setSelectedFile(new File("biblioteca.xlsx"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = selector.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".xlsx")) {
            destino = new File(destino.getPath() + ".xlsx");
        }
        if (destino.exists() && !Mensajes.confirmar(this, "El archivo ya existe. ¿Desea reemplazarlo?")) {
            return;
        }
        try {
            ctrl.guardarCopia(destino);
            Mensajes.info(this, "Copia guardada en:\n" + destino.getAbsolutePath());
        } catch (IOException ex) {
            Mensajes.error(this, "No se pudo exportar: " + ex.getMessage());
        }
    }

    private void salir() {
        if (!Mensajes.confirmar(this, "¿Desea salir de la aplicación? Los datos se guardarán automáticamente.")) {
            return;
        }
        try {
            ctrl.guardar();
        } catch (IOException ex) {
            Mensajes.error(this, "No se pudieron guardar los datos: " + ex.getMessage());
        }
        dispose();
        System.exit(0);
    }
}
