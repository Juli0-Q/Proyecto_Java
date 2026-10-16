package modelo;

import excepciones.LibroNoDisponibleException;

/** Representa un libro del catálogo. El código es inmutable. */
public class Libro {
    private final int codigo;
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro(int codigo, String titulo, String autor) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true; // RF01: al registrarse queda "Disponible"
    }

    public void prestar() throws LibroNoDisponibleException {
        if (!disponible) {
            throw new LibroNoDisponibleException(titulo);
        }
        disponible = false;
    }

    public void devolver() {
        disponible = true;
    }

    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponible() { return disponible; }
    public String getEstado() { return disponible ? "Disponible" : "Prestado"; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }

    @Override
    public String toString() {
        return "Código: " + codigo + " | Título: " + titulo
                + " | Autor: " + autor + " | Estado: " + getEstado();
    }
}
