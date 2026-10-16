package excepciones;

public class PrestamoNoEncontradoException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public PrestamoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
