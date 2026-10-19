package sistema;

import excepciones.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase principal del sistema. Tiene tres partes:
 * 1) OPERACIONES: las listas de libros, usuarios y préstamos, y lo que se puede hacer con ellas.
 * 2) ARCHIVO: guarda los datos en un archivo y los exporta a Excel.
 * 3) VENTANA: la interfaz gráfica con la que se registran los datos (por eso extiende JFrame).
 */
public class Biblioteca extends JFrame {

    // ===================================================================== 1) OPERACIONES

    /** Todos los libros del catálogo. */
    private ArrayList<Libro> libros = new ArrayList<>();
    /** Todos los usuarios registrados. */
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    /** Todos los préstamos, activos y devueltos. */
    private ArrayList<Prestamo> prestamos = new ArrayList<>();

    // ---- Libros

    /**
     * Registra un libro nuevo.
     *
     * @param codigo código único del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @throws CodigoDuplicadoException si ya existe un libro con ese código
     */
    public void agregarLibro(int codigo, String titulo, String autor) throws CodigoDuplicadoException {
        for (Libro l : libros) {
            if (l.getCodigo() == codigo) {
                throw new CodigoDuplicadoException("Ya existe un libro con el código " + codigo + ".");
            }
        }
        libros.add(new Libro(codigo, titulo, autor));
    }

    /**
     * Busca un libro por su código exacto.
     *
     * @param codigo código a buscar
     * @return el libro encontrado
     * @throws NoEncontradoException si no existe ningún libro con ese código
     */
    public Libro buscarLibro(int codigo) throws NoEncontradoException {
        for (Libro l : libros) {
            if (l.getCodigo() == codigo) {
                return l;
            }
        }
        throw new NoEncontradoException("No existe un libro con el código " + codigo + ".");
    }

    /**
     * Sobrecarga de buscarLibro: busca por título, sin distinguir mayúsculas de minúsculas.
     * Basta con escribir una parte del título ("quijote" encuentra "Don Quijote").
     *
     * @param titulo texto a buscar dentro de los títulos
     * @return los libros que coinciden (lista vacía si no hay ninguno)
     */
    public List<Libro> buscarLibro(String titulo) {
        List<Libro> encontrados = new ArrayList<>();
        for (Libro l : libros) {
            if (l.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                encontrados.add(l);
            }
        }
        return encontrados;
    }

    /**
     * Cambia el título y el autor de un libro. El código no se puede cambiar.
     *
     * @throws NoEncontradoException si no existe un libro con ese código
     */
    public void modificarLibro(int codigo, String titulo, String autor) throws NoEncontradoException {
        Libro libro = buscarLibro(codigo);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
    }

    /**
     * Elimina un libro del catálogo.
     *
     * @throws NoEncontradoException si no existe un libro con ese código
     * @throws OperacionNoPermitidaException si el libro está prestado
     */
    public void eliminarLibro(int codigo) throws NoEncontradoException, OperacionNoPermitidaException {
        Libro libro = buscarLibro(codigo);
        if (!libro.isDisponible()) {
            throw new OperacionNoPermitidaException("No se puede eliminar un libro que está prestado.");
        }
        libros.remove(libro);
    }

    // ---- Usuarios

    /**
     * Registra un usuario nuevo.
     *
     * @param codigo código único del usuario
     * @param nombre nombre del lector
     * @throws CodigoDuplicadoException si ya existe un usuario con ese código
     */
    public void agregarUsuario(int codigo, String nombre) throws CodigoDuplicadoException {
        for (Usuario u : usuarios) {
            if (u.getCodigo() == codigo) {
                throw new CodigoDuplicadoException("Ya existe un usuario con el código " + codigo + ".");
            }
        }
        usuarios.add(new Usuario(codigo, nombre));
    }

    /**
     * Busca un usuario por su código.
     *
     * @throws NoEncontradoException si no existe
     */
    public Usuario buscarUsuario(int codigo) throws NoEncontradoException {
        for (Usuario u : usuarios) {
            if (u.getCodigo() == codigo) {
                return u;
            }
        }
        throw new NoEncontradoException("No existe un usuario con el código " + codigo + ".");
    }

    /**
     * Cambia el nombre de un usuario. El código no se puede cambiar.
     *
     * @throws NoEncontradoException si no existe
     */
    public void modificarUsuario(int codigo, String nombre) throws NoEncontradoException {
        buscarUsuario(codigo).setNombre(nombre);
    }

    /**
     * Elimina un usuario.
     *
     * @throws NoEncontradoException si no existe
     * @throws OperacionNoPermitidaException si el usuario tiene libros sin devolver
     */
    public void eliminarUsuario(int codigo) throws NoEncontradoException, OperacionNoPermitidaException {
        Usuario usuario = buscarUsuario(codigo);
        for (Prestamo p : prestamos) {
            if (p.isActivo() && p.getUsuario() == usuario) {
                throw new OperacionNoPermitidaException("No se puede eliminar un usuario que tiene préstamos activos.");
            }
        }
        usuarios.remove(usuario);
    }

    // ---- Préstamos

    /**
     * Registra un préstamo. El código del préstamo se asigna solo y la fecha es la de hoy.
     *
     * @param codLibro código del libro que se presta
     * @param codUsuario código del usuario que lo recibe
     * @throws NoEncontradoException si el libro o el usuario no existen
     * @throws LibroNoDisponibleException si el libro ya está prestado
     */
    public void realizarPrestamo(int codLibro, int codUsuario) throws NoEncontradoException, LibroNoDisponibleException {
        Libro libro = buscarLibro(codLibro);         // falla si el libro no existe
        Usuario usuario = buscarUsuario(codUsuario); // falla si el usuario no existe
        libro.prestar();                              // falla si el libro ya está prestado
        prestamos.add(new Prestamo(prestamos.size() + 1, libro, usuario));
    }

    /**
     * Registra la devolución de un libro: cierra su préstamo y el libro vuelve a estar disponible.
     *
     * @param codLibro código del libro que se devuelve
     * @throws NoEncontradoException si el libro no existe o no está prestado
     */
    public void devolverLibro(int codLibro) throws NoEncontradoException {
        prestamoActivoDeLibro(codLibro).finalizarPrestamo();
    }

    /**
     * Busca el préstamo activo de un libro. Sirve para saber quién lo tiene y desde cuándo.
     *
     * @param codLibro código del libro
     * @return el préstamo activo de ese libro
     * @throws NoEncontradoException si el libro no existe o no está prestado
     */
    public Prestamo prestamoActivoDeLibro(int codLibro) throws NoEncontradoException {
        Libro libro = buscarLibro(codLibro);
        for (Prestamo p : prestamos) {
            if (p.isActivo() && p.getLibro() == libro) {
                return p;
            }
        }
        throw new NoEncontradoException("El libro \"" + libro.getTitulo() + "\" no está prestado.");
    }

    /**
     * @return solo los préstamos que aún no se han devuelto
     */
    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.isActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }

    /**
     * Obtiene todos los préstamos (activos y devueltos) de un usuario.
     *
     * @param codUsuario código del usuario
     * @throws NoEncontradoException si el usuario no existe
     */
    public List<Prestamo> historialDeUsuario(int codUsuario) throws NoEncontradoException {
        Usuario usuario = buscarUsuario(codUsuario);
        List<Prestamo> historial = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.getUsuario() == usuario) {
                historial.add(p);
            }
        }
        return historial;
    }

    // ===================================================================== 2) ARCHIVO

    /** Nombre del archivo donde se guardan los datos. */
    private static final String ARCHIVO = "datos.dat";

    /**
     * Guarda las tres listas en el archivo. Se llama después de cada cambio, así no se pierde nada al cerrar.
     */
    private void guardar() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            out.writeObject(libros);
            out.writeObject(usuarios);
            out.writeObject(prestamos);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron guardar los datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lee las tres listas desde el archivo al iniciar el programa.
     * Si el archivo no existe, usa datos de ejemplo; si está dañado, lo aparta y usa datos de ejemplo.
     */
    @SuppressWarnings("unchecked")
    private void cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            datosEjemplo();
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            libros = (ArrayList<Libro>) in.readObject();
            usuarios = (ArrayList<Usuario>) in.readObject();
            prestamos = (ArrayList<Prestamo>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // El archivo está dañado: se aparta (no se pierde) y se empieza con datos de ejemplo.
            File apartado = new File("datos_con_error.dat");
            apartado.delete();
            archivo.renameTo(apartado);
            datosEjemplo();
            JOptionPane.showMessageDialog(null, "No se pudo leer " + ARCHIVO
                    + ".\nSe guardó como datos_con_error.dat y se usarán datos de ejemplo.");
        }
    }

    /** Crea 3 libros y 2 usuarios de ejemplo para poder usar el sistema la primera vez. */
    private void datosEjemplo() {
        libros = new ArrayList<>();
        usuarios = new ArrayList<>();
        prestamos = new ArrayList<>();
        libros.add(new Libro(1, "El Principito", "Antoine de Saint-Exupéry"));
        libros.add(new Libro(2, "Don Quijote", "Miguel de Cervantes"));
        libros.add(new Libro(3, "Cien años de soledad", "Gabriel García Márquez"));
        usuarios.add(new Usuario(1, "Julio"));
        usuarios.add(new Usuario(2, "Carlos"));
    }

    /**
     * Exporta todos los datos a un archivo .csv que se abre con Excel.
     * Tiene tres bloques: LIBROS, USUARIOS y PRÉSTAMOS.
     *
     * @param destino archivo que se va a crear
     * @throws IOException si no se puede escribir el archivo
     */
    private void exportarExcel(File destino) throws IOException {
        try (PrintWriter out = new PrintWriter(destino, "UTF-8")) {
            out.print('\uFEFF'); // marca para que Excel muestre bien las tildes y la ñ

            out.println("LIBROS");
            out.println(fila("Código", "Título", "Autor", "Estado"));
            for (Libro l : libros) {
                out.println(fila(l.getCodigo(), l.getTitulo(), l.getAutor(), l.getEstado()));
            }

            out.println();
            out.println("USUARIOS");
            out.println(fila("Código", "Nombre"));
            for (Usuario u : usuarios) {
                out.println(fila(u.getCodigo(), u.getNombre()));
            }

            out.println();
            out.println("PRÉSTAMOS");
            out.println(fila("Código", "Libro", "Usuario", "Fecha", "Estado"));
            for (Prestamo p : prestamos) {
                out.println(fila(p.getCodigo(), p.getLibro().getTitulo(), p.getUsuario().getNombre(),
                        p.getFecha(), p.getEstado()));
            }
        }
    }

    /**
     * Arma una fila del archivo CSV: cada dato entre comillas y separado por comas.
     *
     * @param datos los valores de la fila
     * @return la fila lista para escribir
     */
    private String fila(Object... datos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < datos.length; i++) {
            if (i > 0) {
                linea.append(",");
            }
            linea.append("\"").append(datos[i].toString().replace("\"", "\"\"")).append("\"");
        }
        return linea.toString();
    }

    // ===================================================================== 3) VENTANA

    // Tablas y campos de texto de cada pestaña
    private DefaultTableModel modeloLibros = crearModelo("Código", "Título", "Autor", "Estado");
    private JTable tablaLibros = new JTable(modeloLibros);
    private JTextField txtBuscarCodigo = new JTextField(6);
    private JTextField txtBuscarTitulo = new JTextField(12);
    private JTextField txtLibroCodigo = new JTextField(6);
    private JTextField txtLibroTitulo = new JTextField(16);
    private JTextField txtLibroAutor = new JTextField(16);

    private DefaultTableModel modeloUsuarios = crearModelo("Código", "Nombre");
    private JTable tablaUsuarios = new JTable(modeloUsuarios);
    private JTextField txtBuscarUsuario = new JTextField(6);
    private JTextField txtUsuarioCodigo = new JTextField(6);
    private JTextField txtUsuarioNombre = new JTextField(20);

    private DefaultTableModel modeloPrestamos =
            crearModelo("Préstamo", "Cód. libro", "Libro", "Cód. usuario", "Usuario", "Fecha", "Estado");
    private JTable tablaPrestamos = new JTable(modeloPrestamos);
    private JCheckBox chkActivos = new JCheckBox("Mostrar solo préstamos activos");
    private JTextField txtPrestLibro = new JTextField(6);
    private JTextField txtPrestUsuario = new JTextField(6);

    /**
     * Representa lo que hace un botón. Puede lanzar cualquiera de nuestras excepciones,
     * que el método boton() captura para mostrar el error en una ventana.
     */
    private interface Accion {
        void ejecutar() throws Exception;
    }

    /** Construye la ventana: carga los datos, crea las tres pestañas y el menú, y llena las tablas. */
    public Biblioteca() {
        super("Sistema de Gestión y Control de Préstamos de una Biblioteca");
        cargar();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros", crearPestanaLibros());
        pestanas.addTab("Usuarios", crearPestanaUsuarios());
        pestanas.addTab("Préstamos y devoluciones", crearPestanaPrestamos());
        add(pestanas);
        setJMenuBar(crearMenu());

        actualizarTablas();
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // ---- Menú: Archivo > Exportar a Excel / Salir

    /** Crea el menú Archivo, con las opciones Exportar a Excel y Salir. */
    private JMenuBar crearMenu() {
        JMenuItem exportar = new JMenuItem("Exportar a Excel...");
        exportar.addActionListener(e -> elegirDestinoYExportar());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> {
            if (confirmar("¿Desea salir del sistema?")) {
                System.exit(0);
            }
        });
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.add(exportar);
        menuArchivo.add(salir);
        JMenuBar barra = new JMenuBar();
        barra.add(menuArchivo);
        return barra;
    }

    /** Pregunta dónde guardar el archivo y lo exporta a Excel. Muestra un mensaje de éxito o de error. */
    private void elegirDestinoYExportar() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("biblioteca.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = selector.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".csv")) {
            destino = new File(destino.getPath() + ".csv");
        }
        try {
            exportarExcel(destino);
            JOptionPane.showMessageDialog(this, "Archivo exportado en:\n" + destino.getAbsolutePath()
                    + "\nÁbralo con Excel.");
        } catch (IOException e) {
            mostrarError("No se pudo exportar: " + e.getMessage()
                    + "\n(Si el archivo está abierto en Excel, ciérrelo.)");
        }
    }

    // ---- Pestaña Libros

    /** Crea la pestaña de libros: buscadores arriba, tabla en el centro y formulario con botones abajo. */
    private JPanel crearPestanaLibros() {
        // Arriba: búsquedas
        JPanel arriba = new JPanel();
        arriba.add(new JLabel("Código:"));
        arriba.add(txtBuscarCodigo);
        arriba.add(boton("Buscar por código", () -> {
            Libro libro = buscarLibro(numero(txtBuscarCodigo, "Código"));
            llenarLibros(Arrays.asList(libro));
        }));
        arriba.add(new JLabel("   Título:"));
        arriba.add(txtBuscarTitulo);
        arriba.add(boton("Buscar por título", () -> {
            List<Libro> encontrados = buscarLibro(texto(txtBuscarTitulo, "Título"));
            if (encontrados.isEmpty()) {
                throw new NoEncontradoException("No se encontraron libros con ese título.");
            }
            llenarLibros(encontrados);
        }));
        arriba.add(boton("Mostrar todos", () -> actualizarTablas()));

        // Al hacer clic en una fila, sus datos pasan al formulario
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaLibros.getSelectedRow();
            if (fila >= 0) {
                txtLibroCodigo.setText(modeloLibros.getValueAt(fila, 0).toString());
                txtLibroTitulo.setText(modeloLibros.getValueAt(fila, 1).toString());
                txtLibroAutor.setText(modeloLibros.getValueAt(fila, 2).toString());
            }
        });

        // Abajo: formulario y botones
        JPanel campos = new JPanel();
        campos.add(new JLabel("Código:"));
        campos.add(txtLibroCodigo);
        campos.add(new JLabel("Título:"));
        campos.add(txtLibroTitulo);
        campos.add(new JLabel("Autor:"));
        campos.add(txtLibroAutor);

        JPanel botones = new JPanel();
        botones.add(boton("Registrar", () -> {
            agregarLibro(numero(txtLibroCodigo, "Código"), texto(txtLibroTitulo, "Título"), texto(txtLibroAutor, "Autor"));
            limpiar(txtLibroCodigo, txtLibroTitulo, txtLibroAutor);
            cambioRealizado("Libro registrado correctamente.");
        }));
        botones.add(boton("Modificar", () -> {
            modificarLibro(numero(txtLibroCodigo, "Código"), texto(txtLibroTitulo, "Título"), texto(txtLibroAutor, "Autor"));
            limpiar(txtLibroCodigo, txtLibroTitulo, txtLibroAutor);
            cambioRealizado("Libro modificado correctamente.");
        }));
        botones.add(boton("Eliminar", () -> {
            int codigo = numero(txtLibroCodigo, "Código");
            buscarLibro(codigo); // verifica que exista
            if (!confirmar("¿Eliminar el libro con código " + codigo + "?")) {
                return;
            }
            eliminarLibro(codigo);
            limpiar(txtLibroCodigo, txtLibroTitulo, txtLibroAutor);
            cambioRealizado("Libro eliminado correctamente.");
        }));

        return armarPestana(arriba, tablaLibros, campos, botones);
    }

    // ---- Pestaña Usuarios

    /** Crea la pestaña de usuarios: buscador, tabla y formulario con botones. */
    private JPanel crearPestanaUsuarios() {
        JPanel arriba = new JPanel();
        arriba.add(new JLabel("Código:"));
        arriba.add(txtBuscarUsuario);
        arriba.add(boton("Buscar por código", () -> {
            Usuario usuario = buscarUsuario(numero(txtBuscarUsuario, "Código"));
            llenarUsuarios(Arrays.asList(usuario));
        }));
        arriba.add(boton("Mostrar todos", () -> actualizarTablas()));

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila >= 0) {
                txtUsuarioCodigo.setText(modeloUsuarios.getValueAt(fila, 0).toString());
                txtUsuarioNombre.setText(modeloUsuarios.getValueAt(fila, 1).toString());
            }
        });

        JPanel campos = new JPanel();
        campos.add(new JLabel("Código:"));
        campos.add(txtUsuarioCodigo);
        campos.add(new JLabel("Nombre:"));
        campos.add(txtUsuarioNombre);

        JPanel botones = new JPanel();
        botones.add(boton("Registrar", () -> {
            agregarUsuario(numero(txtUsuarioCodigo, "Código"), texto(txtUsuarioNombre, "Nombre"));
            limpiar(txtUsuarioCodigo, txtUsuarioNombre);
            cambioRealizado("Usuario registrado correctamente.");
        }));
        botones.add(boton("Modificar", () -> {
            modificarUsuario(numero(txtUsuarioCodigo, "Código"), texto(txtUsuarioNombre, "Nombre"));
            limpiar(txtUsuarioCodigo, txtUsuarioNombre);
            cambioRealizado("Usuario modificado correctamente.");
        }));
        botones.add(boton("Eliminar", () -> {
            int codigo = numero(txtUsuarioCodigo, "Código");
            buscarUsuario(codigo); // verifica que exista
            if (!confirmar("¿Eliminar al usuario con código " + codigo + "?")) {
                return;
            }
            eliminarUsuario(codigo);
            limpiar(txtUsuarioCodigo, txtUsuarioNombre);
            cambioRealizado("Usuario eliminado correctamente.");
        }));

        return armarPestana(arriba, tablaUsuarios, campos, botones);
    }

    // ---- Pestaña Préstamos

    /**
     * Crea la pestaña de préstamos: filtro, tabla y botones para prestar, devolver, ver quién tiene un libro y el historial.
     */
    private JPanel crearPestanaPrestamos() {
        JPanel arriba = new JPanel();
        chkActivos.addActionListener(e -> actualizarTablas());
        arriba.add(chkActivos);

        // Al hacer clic en un préstamo, sus códigos pasan al formulario (sirve para devolver)
        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPrestamos.getSelectedRow();
            if (fila >= 0) {
                txtPrestLibro.setText(modeloPrestamos.getValueAt(fila, 1).toString());
                txtPrestUsuario.setText(modeloPrestamos.getValueAt(fila, 3).toString());
            }
        });

        JPanel campos = new JPanel();
        campos.add(new JLabel("Código del libro:"));
        campos.add(txtPrestLibro);
        campos.add(new JLabel("Código del usuario:"));
        campos.add(txtPrestUsuario);

        JPanel botones = new JPanel();
        botones.add(boton("Registrar préstamo", () -> {
            realizarPrestamo(numero(txtPrestLibro, "Código del libro"), numero(txtPrestUsuario, "Código del usuario"));
            limpiar(txtPrestLibro, txtPrestUsuario);
            cambioRealizado("Préstamo realizado correctamente.");
        }));
        botones.add(boton("Registrar devolución", () -> {
            int codigo = numero(txtPrestLibro, "Código del libro");
            prestamoActivoDeLibro(codigo); // verifica que esté prestado
            if (!confirmar("¿Registrar la devolución del libro " + codigo + "?")) {
                return;
            }
            devolverLibro(codigo);
            limpiar(txtPrestLibro, txtPrestUsuario);
            cambioRealizado("Libro devuelto correctamente.");
        }));
        botones.add(boton("¿Quién tiene el libro?", () -> {
            Prestamo p = prestamoActivoDeLibro(numero(txtPrestLibro, "Código del libro"));
            JOptionPane.showMessageDialog(this, "El libro \"" + p.getLibro().getTitulo() + "\" lo tiene "
                    + p.getUsuario().getNombre() + " desde el " + p.getFecha() + ".");
        }));
        botones.add(boton("Historial del usuario", () ->
                llenarPrestamos(historialDeUsuario(numero(txtPrestUsuario, "Código del usuario")))));

        return armarPestana(arriba, tablaPrestamos, campos, botones);
    }

    // ---- Métodos de apoyo de la ventana

    /** Junta las partes de una pestaña: arriba, tabla en el centro, y abajo los campos y los botones. */
    private JPanel armarPestana(JPanel arriba, JTable tabla, JPanel campos, JPanel botones) {
        JPanel abajo = new JPanel(new GridLayout(2, 1));
        abajo.add(campos);
        abajo.add(botones);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        return panel;
    }

    /** Crea el modelo de una tabla con las columnas indicadas, sin permitir editar las celdas a mano. */
    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /**
     * Crea un botón. Si la acción lanza un error, se muestra en una ventana y el programa sigue funcionando.
     *
     * @param texto texto del botón
     * @param accion lo que hace el botón al pulsarlo
     */
    private JButton boton(String texto, Accion accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            try {
                accion.ejecutar();
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });
        return boton;
    }

    /**
     * Lee un número de un campo de texto (validación de entrada).
     *
     * @param campo campo de texto a leer
     * @param nombre nombre del campo, para el mensaje de error
     * @return el número escrito
     * @throws EntradaInvalidaException si no es un número válido o no es mayor que cero
     */
    private int numero(JTextField campo, String nombre) throws EntradaInvalidaException {
        try {
            int valor = Integer.parseInt(campo.getText().trim());
            if (valor <= 0) {
                throw new EntradaInvalidaException("El campo \"" + nombre + "\" debe ser mayor que cero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException("El campo \"" + nombre + "\" debe ser un número válido.");
        }
    }

    /**
     * Lee un texto obligatorio de un campo.
     *
     * @return el texto sin espacios sobrantes
     * @throws EntradaInvalidaException si el campo está vacío
     */
    private String texto(JTextField campo, String nombre) throws EntradaInvalidaException {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new EntradaInvalidaException("El campo \"" + nombre + "\" es obligatorio.");
        }
        return texto;
    }

    /** Deja vacíos los campos de texto indicados. */
    private void limpiar(JTextField... campos) {
        for (JTextField c : campos) {
            c.setText("");
        }
    }

    /** Muestra una ventana de error con el mensaje. */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pregunta Sí o No al usuario.
     *
     * @return true si eligió Sí
     */
    private boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Se llama después de registrar, modificar, eliminar, prestar o devolver:
     * refresca las tablas, guarda los datos en el archivo y avisa al usuario.
     */
    private void cambioRealizado(String mensaje) {
        actualizarTablas();
        guardar();
        JOptionPane.showMessageDialog(this, mensaje);
    }

    /** Vuelve a llenar las tres tablas con los datos actuales. */
    private void actualizarTablas() {
        llenarLibros(libros);
        llenarUsuarios(usuarios);
        llenarPrestamos(chkActivos.isSelected() ? listarPrestamosActivos() : prestamos);
    }

    /** Borra las filas de la tabla de libros y la llena con la lista recibida. */
    private void llenarLibros(List<Libro> lista) {
        modeloLibros.setRowCount(0);
        for (Libro l : lista) {
            modeloLibros.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getEstado()});
        }
    }

    /** Borra las filas de la tabla de usuarios y la llena con la lista recibida. */
    private void llenarUsuarios(List<Usuario> lista) {
        modeloUsuarios.setRowCount(0);
        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{u.getCodigo(), u.getNombre()});
        }
    }

    /** Borra las filas de la tabla de préstamos y la llena con la lista recibida. */
    private void llenarPrestamos(List<Prestamo> lista) {
        modeloPrestamos.setRowCount(0);
        for (Prestamo p : lista) {
            modeloPrestamos.addRow(new Object[]{p.getCodigo(), p.getLibro().getCodigo(), p.getLibro().getTitulo(),
                    p.getUsuario().getCodigo(), p.getUsuario().getNombre(), p.getFecha(), p.getEstado()});
        }
    }
}
