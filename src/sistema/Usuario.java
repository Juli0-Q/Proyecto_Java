package sistema;

import java.io.Serializable;

/** Representa a un lector de la biblioteca (el que pide los libros prestados). */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Número que identifica al usuario (no se repite). */
    private int codigo;
    /** Nombre del lector. */
    private String nombre;

    /**
     * @param codigo código único del usuario
     * @param nombre nombre del lector
     */
    public Usuario(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Getters y setter: permiten leer y cambiar los atributos privados.
    public int getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return una línea con el código y el nombre del usuario
     */
    @Override
    public String toString() {
        return "Código: " + codigo + " | Nombre: " + nombre;
    }
}
