package excepciones;

/**
 * Error: se intentó registrar un libro o un usuario con un código que ya existe.
 *
 * Todas las excepciones de este paquete heredan de Exception, por eso Java obliga a
 * capturarlas (try-catch) o a declararlas (throws).
 */
public class CodigoDuplicadoException extends Exception {

    /**
     * @param mensaje texto que se mostrará al usuario en la ventana de error
     */
    public CodigoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
