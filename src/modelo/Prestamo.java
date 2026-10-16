package modelo;

import java.time.LocalDate;

/** Asocia un libro y un usuario con una fecha automática y un estado Activo/Devuelto. */
public class Prestamo {
    private final int codigo;
    private final Libro libro;
    private final Usuario usuario;
    private final LocalDate fecha;
    private boolean activo;

    public Prestamo(int codigo, Libro libro, Usuario usuario) {
        this(codigo, libro, usuario, LocalDate.now(), true); // fecha automática
    }

    /** Usado al cargar préstamos ya guardados. */
    public Prestamo(int codigo, Libro libro, Usuario usuario, LocalDate fecha, boolean activo) {
        this.codigo = codigo;
        this.libro = libro;
        this.usuario = usuario;
        this.fecha = fecha;
        this.activo = activo;
    }

    /** Cierra el préstamo y deja el libro nuevamente disponible. */
    public void finalizarPrestamo() {
        activo = false;
        libro.devolver();
    }

    public int getCodigo() { return codigo; }
    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFecha() { return fecha; }
    public boolean isActivo() { return activo; }
    public String getEstado() { return activo ? "Activo" : "Devuelto"; }

    @Override
    public String toString() {
        return "Préstamo: " + codigo + " | Libro: " + libro.getTitulo()
                + " | Usuario: " + usuario.getNombre()
                + " | Fecha: " + fecha + " | Estado: " + getEstado();
    }
}
