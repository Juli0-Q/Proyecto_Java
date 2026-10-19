package excepciones;

/** Error: se buscó un libro, un usuario o un préstamo que no existe. */
public class NoEncontradoException extends Exception {

    /**
     * @param mensaje texto que se mostrará al usuario en la ventana de error
     */
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
