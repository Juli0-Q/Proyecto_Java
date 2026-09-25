package Biblioteca;

public class libro {

    private int codigo;
    private String titulo;
    private String autor;
    private boolean disponible;

    public libro(int codigo, String titulo, String autor) {
    this.codigo = codigo;
    this. titulo = titulo;
    this.autor = autor;
    this.disponible = true;
    }

    public int getCodigo() {
    return codigo;
    }

    public String getTitulo() {
    return titulo;
    }

    public String getAutor() {
    return autor;
    }   
    public boolean isDisponible() {
    return disponible;
    }

    public void prestar() {
    disponible = false;
    }


    public void devolver() {
    disponible = true;
    }

    @Override
    public String toString() {
    return "Libro{" +
    "codigo=" + codigo +
    ", titulo='" + titulo + '\'' +
    ", autor='" + autor + '\'' +
    ", disponible=" + (disponible ? "S" : "No") + '}';

    }
}       

