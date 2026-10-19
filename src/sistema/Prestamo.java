package sistema;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa un préstamo: qué usuario se llevó qué libro, en qué fecha, y si ya lo devolvió.
 *
 * Guarda un objeto Libro y un objeto Usuario (composición).
 */
public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Número que identifica al préstamo. */
    private int codigo;
    /** Libro que se prestó. */
    private Libro libro;
    /** Usuario que se llevó el libro. */
    private Usuario usuario;
    /** Fecha en que se hizo el préstamo. */
    private LocalDate fecha;
    /** true mientras el libro no se haya devuelto. */
    private boolean activo; // true = Activo, false = Devuelto

    /**
     * Crea un préstamo nuevo. La fecha es la de hoy y queda activo.
     *
     * @param codigo código del préstamo
     * @param libro libro que se presta
     * @param usuario usuario que lo recibe
     */
    public Prestamo(int codigo, Libro libro, Usuario usuario) {
        this.codigo = codigo;
        this.libro = libro;
        this.usuario = usuario;
        this.fecha = LocalDate.now(); // la fecha de hoy, automática
        this.activo = true;
    }

    /** Cierra el préstamo (pasa a Devuelto) y deja el libro disponible. */
    public void finalizarPrestamo() {
        activo = false;
        libro.devolver();
    }

    // Getters: permiten leer los atributos privados.
    public int getCodigo() { return codigo; }
    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFecha() { return fecha; }
    public boolean isActivo() { return activo; }
    /**
     * @return el texto "Activo" o "Devuelto"
     */
    public String getEstado() { return activo ? "Activo" : "Devuelto"; }

    /**
     * @return una línea con el código, libro, usuario, fecha y estado del préstamo
     */
    @Override
    public String toString() {
        return "Préstamo: " + codigo + " | Libro: " + libro.getTitulo() + " | Usuario: " + usuario.getNombre()
                + " | Fecha: " + fecha + " | Estado: " + getEstado();
    }
}
