package Biblioteca;
import java.time.LocalDate;

public class prestamo {

    private int codigo;
    private Libro libro;
    private Usuario usuario;
    private LocalDate fecha;
    private boolean activo;

    public prestamo(int codigo, Libro libro, Usuario usuario) {
        this.codigo = codigo;
        this.libro = libro;
        this.usuario = usuario;
        this.fecha = LocalDate.now();
        this.activo = true;
    }

    public int getCodigo() {
        return codigo;
    }

    public Libro getLibro() {
        return libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void finalizarPrestamo() {
        activo = false;
    }

    @Override
    public String toString() {
        return "Préstamo: " + codigo
                + " | Libro: " + libro.getTitulo()
                + " | Usuario: " + usuario.getNombre()
                + " | Fecha: " + fecha
                + " | Estado: " + (activo ? "Activo" : "Devuelto");
    }
}
