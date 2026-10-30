package Funcionamiento;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Entidad transaccional que gestiona plazos, retrasos y bloqueo de usuarios.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class Prestamo {
    private String codigoPrestamo;
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaPrestamo;
    private String estado;

    public Prestamo(String codigoPrestamo, Libro libro, Usuario usuario) {
        this.codigoPrestamo = codigoPrestamo;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = LocalDate.now();
        this.estado = "Activo";
        this.libro.prestarEjemplar();
    }

    public void finalizarPrestamo() {
        LocalDate fechaDevolucion = LocalDate.now();
        long diasTranscurridos = ChronoUnit.DAYS.between(fechaPrestamo, fechaDevolucion);
        
        if (diasTranscurridos > 7) {
            long diasTarde = diasTranscurridos - 7;
            this.estado = "Devuelto con Retraso (" + diasTarde + " días tarde)";
            // El usuario queda bloqueado automáticamente si se pasó de los 7 días
            this.usuario.setBloqueado(true);
        } else {
            this.estado = "Devuelto a Tiempo";
        }
        this.libro.devolverEjemplar();
    }

    public String getCodigoPrestamo() { return codigoPrestamo; }
    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}