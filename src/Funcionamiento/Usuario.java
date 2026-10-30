package Funcionamiento;

/**
 * Entidad que representa a un usuario, incluyendo su estado de bloqueo por retrasos.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class Usuario {
    private String codigo;
    private String nombre;
    private String correo;
    private boolean bloqueado; // NUEVO: Estado de bloqueo por mora

    public Usuario(String codigo, String nombre, String correo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.correo = correo;
        this.bloqueado = false;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public boolean isBloqueado() { return bloqueado; }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public String getEstadoTexto() {
        return bloqueado ? "Bloqueado (Mora)" : "Activo";
    }
}