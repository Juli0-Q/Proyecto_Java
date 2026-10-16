package excepciones;

public class UsuarioNoEncontradoException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public UsuarioNoEncontradoException(int codigo) {
        super("No existe un usuario con el código " + codigo + ".");
    }
}
