package modelo;

import excepciones.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Administra las colecciones de libros, usuarios y préstamos y sus reglas de negocio. */
public class Biblioteca {
    private final ArrayList<Libro> libros = new ArrayList<>();
    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private final ArrayList<Prestamo> prestamos = new ArrayList<>();

    // ---------------------------------------------------------------- LIBROS

    public void agregarLibro(Libro libro) throws CodigoDuplicadoException {
        for (Libro l : libros) {
            if (l.getCodigo() == libro.getCodigo()) {
                throw new CodigoDuplicadoException("un libro", libro.getCodigo());
            }
        }
        libros.add(libro);
    }

    /** Búsqueda por código exacto. */
    public Libro buscarLibro(int codigo) throws LibroNoEncontradoException {
        for (Libro l : libros) {
            if (l.getCodigo() == codigo) {
                return l;
            }
        }
        throw new LibroNoEncontradoException(codigo);
    }

    /** Sobrecarga: búsqueda por título, sin distinguir mayúsculas/minúsculas (coincidencia parcial). */
    public List<Libro> buscarLibro(String titulo) {
        List<Libro> resultado = new ArrayList<>();
        String buscado = titulo.trim().toLowerCase();
        for (Libro l : libros) {
            if (l.getTitulo().toLowerCase().contains(buscado)) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    public void modificarLibro(int codigo, String titulo, String autor) throws LibroNoEncontradoException {
        Libro l = buscarLibro(codigo);
        l.setTitulo(titulo);
        l.setAutor(autor);
    }

    public void eliminarLibro(int codigo) throws BibliotecaException {
        Libro l = buscarLibro(codigo);
        if (!l.isDisponible()) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar el libro \"" + l.getTitulo() + "\" porque tiene un préstamo activo.");
        }
        libros.remove(l);
    }

    public List<Libro> listarLibros() {
        return Collections.unmodifiableList(libros);
    }

    // -------------------------------------------------------------- USUARIOS

    public void agregarUsuario(Usuario usuario) throws CodigoDuplicadoException {
        for (Usuario u : usuarios) {
            if (u.getCodigo() == usuario.getCodigo()) {
                throw new CodigoDuplicadoException("un usuario", usuario.getCodigo());
            }
        }
        usuarios.add(usuario);
    }

    public Usuario buscarUsuario(int codigo) throws UsuarioNoEncontradoException {
        for (Usuario u : usuarios) {
            if (u.getCodigo() == codigo) {
                return u;
            }
        }
        throw new UsuarioNoEncontradoException(codigo);
    }

    public void modificarUsuario(int codigo, String nombre) throws UsuarioNoEncontradoException {
        buscarUsuario(codigo).setNombre(nombre);
    }

    public void eliminarUsuario(int codigo) throws BibliotecaException {
        Usuario u = buscarUsuario(codigo);
        for (Prestamo p : prestamos) {
            if (p.isActivo() && p.getUsuario().getCodigo() == codigo) {
                throw new OperacionNoPermitidaException(
                        "No se puede eliminar al usuario \"" + u.getNombre() + "\" porque tiene préstamos activos.");
            }
        }
        usuarios.remove(u);
    }

    public List<Usuario> listarUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    // ------------------------------------------------------------- PRÉSTAMOS

    public Prestamo realizarPrestamo(int codPrestamo, int codLibro, int codUsuario) throws BibliotecaException {
        Libro libro = buscarLibro(codLibro);          // RF16: verifica que el libro exista
        Usuario usuario = buscarUsuario(codUsuario);  // RF16: verifica que el usuario exista
        for (Prestamo p : prestamos) {
            if (p.getCodigo() == codPrestamo) {
                throw new CodigoDuplicadoException("un préstamo", codPrestamo);
            }
        }
        libro.prestar();                              // RF15/RF17: lanza excepción si no está disponible
        Prestamo prestamo = new Prestamo(codPrestamo, libro, usuario);
        prestamos.add(prestamo);
        return prestamo;
    }

    /** Agrega un préstamo ya existente (al cargar datos); si está activo, el libro queda "Prestado". */
    public void agregarPrestamo(Prestamo p) throws BibliotecaException {
        for (Prestamo x : prestamos) {
            if (x.getCodigo() == p.getCodigo()) {
                throw new CodigoDuplicadoException("un préstamo", p.getCodigo());
            }
        }
        if (p.isActivo()) {
            p.getLibro().prestar();
        }
        prestamos.add(p);
    }

    public Prestamo devolverLibro(int codLibro) throws BibliotecaException {
        Prestamo p = prestamoActivoDeLibro(codLibro);
        p.finalizarPrestamo();                        // RF18/RF19
        return p;
    }

    /** RF22: quién tiene un libro prestado y desde cuándo. */
    public Prestamo prestamoActivoDeLibro(int codLibro) throws BibliotecaException {
        Libro libro = buscarLibro(codLibro);
        for (Prestamo p : prestamos) {
            if (p.isActivo() && p.getLibro() == libro) {
                return p;
            }
        }
        throw new PrestamoNoEncontradoException(
                "El libro \"" + libro.getTitulo() + "\" no tiene un préstamo activo.");
    }

    public List<Prestamo> listarPrestamos() {
        return Collections.unmodifiableList(prestamos);
    }

    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.isActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }

    /** RF23: historial (activos y devueltos) de un usuario. */
    public List<Prestamo> historialDeUsuario(int codUsuario) throws UsuarioNoEncontradoException {
        buscarUsuario(codUsuario);
        List<Prestamo> historial = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.getUsuario().getCodigo() == codUsuario) {
                historial.add(p);
            }
        }
        return historial;
    }

    // ------------------------------------------------- CÓDIGOS SUGERIDOS

    public int siguienteCodigoLibro() {
        int max = 0;
        for (Libro l : libros) max = Math.max(max, l.getCodigo());
        return max + 1;
    }

    public int siguienteCodigoUsuario() {
        int max = 0;
        for (Usuario u : usuarios) max = Math.max(max, u.getCodigo());
        return max + 1;
    }

    public int siguienteCodigoPrestamo() {
        int max = 0;
        for (Prestamo p : prestamos) max = Math.max(max, p.getCodigo());
        return max + 1;
    }
}
