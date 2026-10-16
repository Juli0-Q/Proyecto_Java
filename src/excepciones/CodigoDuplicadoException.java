package excepciones;

public class CodigoDuplicadoException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public CodigoDuplicadoException(String entidad, int codigo) {
        super("Ya existe " + entidad + " con el código " + codigo + ".");
    }
}
