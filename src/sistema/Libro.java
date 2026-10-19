package sistema;

import excepciones.LibroNoDisponibleException;
import java.io.Serializable;

/**
 * Representa un libro del catálogo.
 *
 * Todos los atributos son privados (encapsulamiento): solo se leen o cambian con métodos.
 * Implementa Serializable para que Java pueda guardar el libro en un archivo.
 */
public class Libro implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Número que identifica al libro (no se repite). */
    private int codigo;
    /** Título del libro. */
    private String titulo;
    /** Autor del libro. */
    private String autor;
    /** true si está en la biblioteca, false si está prestado. */
    private boolean disponible; // true = Disponible, false = Prestado

    /**
     * Crea un libro nuevo. Siempre empieza como disponible.
     *
     * @param codigo código único del libro
     * @param titulo título del libro
     * @param autor autor del libro
     */
    public Libro(int codigo, String titulo, String autor) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true; // un libro nuevo siempre está disponible
    }

    /**
     * Marca el libro como prestado.
     *
     * @throws LibroNoDisponibleException si el libro ya estaba prestado
     */
    public void prestar() throws LibroNoDisponibleException {
        if (!disponible) {
            throw new LibroNoDisponibleException("El libro \"" + titulo + "\" no está disponible (ya está prestado).");
        }
        disponible = false;
    }

    /** Marca el libro como disponible otra vez. */
    public void devolver() {
        disponible = true;
    }

    // Getters y setters: permiten leer y cambiar los atributos privados.
    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponible() { return disponible; }
    /**
     * @return el texto "Disponible" o "Prestado"
     */
    public String getEstado() { return disponible ? "Disponible" : "Prestado"; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }

    /**
     * @return una línea con el código, título, autor y estado del libro
     */
    @Override
    public String toString() {
        return "Código: " + codigo + " | Título: " + titulo + " | Autor: " + autor + " | Estado: " + getEstado();
    }
}
