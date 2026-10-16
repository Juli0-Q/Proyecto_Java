package excepciones;

public class LibroNoDisponibleException extends BibliotecaException {
    private static final long serialVersionUID = 1L;

    public LibroNoDisponibleException(String titulo) {
        super("El libro \"" + titulo + "\" no está disponible (ya se encuentra prestado).");
    }
}
