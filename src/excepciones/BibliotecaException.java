package excepciones;

/** Excepción base de todos los errores de negocio del sistema. */
public class BibliotecaException extends Exception {
    private static final long serialVersionUID = 1L;

    public BibliotecaException(String mensaje) {
        super(mensaje);
    }
}
