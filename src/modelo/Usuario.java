package modelo;


/** Representa a un lector registrado en la biblioteca. */
public class Usuario {
    private final int codigo;
    private String nombre;

    public Usuario(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Código: " + codigo + " | Nombre: " + nombre;
    }
}
