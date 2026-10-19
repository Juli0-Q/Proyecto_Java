package excepciones;

/** Error: se intentó prestar un libro que ya está prestado. */
public class LibroNoDisponibleException extends Exception {

    /**
     * @param mensaje texto que se mostrará al usuario en la ventana de error
     */
    public LibroNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
