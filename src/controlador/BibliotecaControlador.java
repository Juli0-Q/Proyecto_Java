package controlador;

import excepciones.BibliotecaException;
import modelo.*;
import persistencia.ExcelAlmacen;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controlador del patrón MVC: separa la vista (Swing) del modelo.
 * Guarda automáticamente en disco después de cada operación que modifica datos.
 */
public class BibliotecaControlador {
    private static final String ARCHIVO = "biblioteca.xlsx";

    private final File archivo = new File(ARCHIVO);
    private Biblioteca biblioteca;

    public BibliotecaControlador() {
        // RF28/RF29: carga datos guardados; si no existen, usa datos de ejemplo
        if (archivo.exists()) {
            try {
                biblioteca = ExcelAlmacen.cargar(archivo);
            } catch (IOException e) {
                // No se sobrescribe un archivo ilegible: se aparta para que el usuario pueda revisarlo
                System.err.println("No se pudo leer " + ARCHIVO + ": " + e.getMessage());
                File apartado = new File("biblioteca_con_error.xlsx");
                apartado.delete();
                archivo.renameTo(apartado);
            }
        }
        if (biblioteca == null) {
            biblioteca = new Biblioteca();
            cargarDatosIniciales();
        }
    }

    private void cargarDatosIniciales() {
        try {
            biblioteca.agregarLibro(new Libro(1, "El Principito", "Antoine de Saint-Exupéry"));
            biblioteca.agregarLibro(new Libro(2, "Don Quijote", "Miguel de Cervantes"));
            biblioteca.agregarLibro(new Libro(3, "Cien años de soledad", "Gabriel García Márquez"));
            biblioteca.agregarUsuario(new Usuario(1, "Julio"));
            biblioteca.agregarUsuario(new Usuario(2, "Carlos"));
        } catch (BibliotecaException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------ persistencia

    public void guardar() throws IOException {
        ExcelAlmacen.guardar(biblioteca, archivo);
    }

    /** Guarda una copia del Excel en otra ubicación (por ejemplo, para entregar o compartir). */
    public void guardarCopia(File destino) throws IOException {
        ExcelAlmacen.guardar(biblioteca, destino);
    }

    public File getArchivo() {
        return archivo.getAbsoluteFile();
    }

    private void autoguardar() {
        try {
            guardar();
        } catch (IOException e) {
            System.err.println("No se pudo guardar automáticamente: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ libros

    public List<Libro> listarLibros() { return biblioteca.listarLibros(); }

    public Libro buscarLibro(int codigo) throws BibliotecaException { return biblioteca.buscarLibro(codigo); }

    public List<Libro> buscarLibro(String titulo) { return biblioteca.buscarLibro(titulo); }

    public void registrarLibro(int codigo, String titulo, String autor) throws BibliotecaException {
        biblioteca.agregarLibro(new Libro(codigo, titulo, autor));
        autoguardar();
    }

    public void modificarLibro(int codigo, String titulo, String autor) throws BibliotecaException {
        biblioteca.modificarLibro(codigo, titulo, autor);
        autoguardar();
    }

    public void eliminarLibro(int codigo) throws BibliotecaException {
        biblioteca.eliminarLibro(codigo);
        autoguardar();
    }

    public int siguienteCodigoLibro() { return biblioteca.siguienteCodigoLibro(); }

    // ---------------------------------------------------------------- usuarios

    public List<Usuario> listarUsuarios() { return biblioteca.listarUsuarios(); }

    public Usuario buscarUsuario(int codigo) throws BibliotecaException { return biblioteca.buscarUsuario(codigo); }

    public void registrarUsuario(int codigo, String nombre) throws BibliotecaException {
        biblioteca.agregarUsuario(new Usuario(codigo, nombre));
        autoguardar();
    }

    public void modificarUsuario(int codigo, String nombre) throws BibliotecaException {
        biblioteca.modificarUsuario(codigo, nombre);
        autoguardar();
    }

    public void eliminarUsuario(int codigo) throws BibliotecaException {
        biblioteca.eliminarUsuario(codigo);
        autoguardar();
    }

    public int siguienteCodigoUsuario() { return biblioteca.siguienteCodigoUsuario(); }

    // --------------------------------------------------------------- préstamos

    public List<Prestamo> listarPrestamos() { return biblioteca.listarPrestamos(); }

    public List<Prestamo> listarPrestamosActivos() { return biblioteca.listarPrestamosActivos(); }

    public List<Prestamo> historialDeUsuario(int codUsuario) throws BibliotecaException {
        return biblioteca.historialDeUsuario(codUsuario);
    }

    public Prestamo prestamoActivoDeLibro(int codLibro) throws BibliotecaException {
        return biblioteca.prestamoActivoDeLibro(codLibro);
    }

    public void realizarPrestamo(int codPrestamo, int codLibro, int codUsuario) throws BibliotecaException {
        biblioteca.realizarPrestamo(codPrestamo, codLibro, codUsuario);
        autoguardar();
    }

    public void devolverLibro(int codLibro) throws BibliotecaException {
        biblioteca.devolverLibro(codLibro);
        autoguardar();
    }

    public int siguienteCodigoPrestamo() { return biblioteca.siguienteCodigoPrestamo(); }
}
