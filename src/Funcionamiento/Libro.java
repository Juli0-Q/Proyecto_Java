package Funcionamiento;

/**
 * Entidad que representa un libro dentro del inventario de la biblioteca.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class Libro {
    private String codigo;
    private String titulo;
    private String autor;
    private int stock;

    public Libro(String codigo, String titulo, String autor, int stock) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.stock = stock;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getStock() { return stock; }
    
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isDisponible() {
        return stock > 0;
    }

    public void prestarEjemplar() {
        if (stock > 0) stock--;
    }

    public void devolverEjemplar() {
        stock++;
    }

    public String getEstadoTexto() {
        if (stock > 0) {
            String palabra = (stock == 1) ? "libro" : "libros";
            return "Disponible (" + stock + " " + palabra + ")";
        } else {
            return "Agotado";
        }
    }
}