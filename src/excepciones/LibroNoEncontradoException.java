package excepciones;

public class LibroNoEncontradoException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public LibroNoEncontradoException(int codigo) {
        super("No existe un libro con el código " + codigo + ".");
    }

    public LibroNoEncontradoException(String titulo) {
        super("No se encontraron libros con el título \"" + titulo + "\".");
    }
}
