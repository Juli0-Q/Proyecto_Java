package excepciones;

/** Error: se intentó eliminar un libro que está prestado o un usuario con préstamos activos. */
public class OperacionNoPermitidaException extends Exception {

    /**
     * @param mensaje texto que se mostrará al usuario en la ventana de error
     */
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
