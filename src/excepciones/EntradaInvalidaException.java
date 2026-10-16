package excepciones;

public class EntradaInvalidaException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public EntradaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
