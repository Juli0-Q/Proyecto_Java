package Funcionamiento;

import ExcepcionesPersonalizadas.CodigoDuplicadoException;
import ExcepcionesPersonalizadas.LibroNoDisponibleException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controlador principal que centraliza la lógica de negocio, reglas de préstamos, 
 * control de stock, bloqueo por mora y persistencia automática en archivos CSV.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class ControladorBiblioteca {
    private List<Libro> libros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Prestamo> prestamos = new ArrayList<>();
    
    private final String ARCHIVO_LIBROS = "Inventario_Biblioteca.csv";
    private final String ARCHIVO_USUARIOS = "Registro_Usuarios.csv";
    private final String ARCHIVO_PRESTAMOS = "Registro_Prestamos.csv";

    public ControladorBiblioteca() {
        cargarDatosIniciales();
        cargarUsuariosGuardados();
        
        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario("US-001", "Maria Gutierrez", "maria@mail.com"));
            usuarios.add(new Usuario("US-002", "Valentino Morales", "valentino@mail.com"));
        }
    }

    public List<Libro> getLibros() { return libros; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Prestamo> getPrestamos() { return prestamos; }

    public String generarNuevoCodigoLibro() {
        int mayor = 0;
        for (Libro l : libros) {
            try {
                String[] partes = l.getCodigo().split("-");
                int num = Integer.parseInt(partes[1]);
                if (num > mayor) mayor = num;
            } catch (Exception e) {}
        }
        return String.format("LIB-%03d", mayor + 1);
    }

    public String generarNuevoCodigoUsuario() {
        int mayor = 0;
        for (Usuario u : usuarios) {
            try {
                String[] partes = u.getCodigo().split("-");
                int num = Integer.parseInt(partes[1]);
                if (num > mayor) mayor = num;
            } catch (Exception e) {}
        }
        return String.format("US-%03d", mayor + 1);
    }

    public String generarNuevoCodigoPrestamo() {
        return String.format("PRES-%03d", prestamos.size() + 1);
    }

    /**
     * Registra un nuevo libro en el catálogo con stock inicial en 0.
     */
    public void registrarLibro(String codigo, String titulo, String autor) throws CodigoDuplicadoException {
        for (Libro l : libros) {
            if (l.getCodigo().equalsIgnoreCase(codigo)) {
                throw new CodigoDuplicadoException("El código " + codigo + " ya existe.");
            }
        }
        libros.add(new Libro(codigo, titulo, autor, 0));
    }

    public boolean modificarLibro(String codigo, String nuevoTitulo, String nuevoAutor, int stockActual) {
        for (Libro l : libros) {
            if (l.getCodigo().equalsIgnoreCase(codigo)) {
                l.setTitulo(nuevoTitulo);
                l.setAutor(nuevoAutor);
                l.setStock(stockActual);
                return true;
            }
        }
        return false;
    }

    public boolean ajustarStockLibro(String codigo, int cantidadAñadir) {
        for (Libro l : libros) {
            if (l.getCodigo().equalsIgnoreCase(codigo)) {
                int nuevoStock = l.getStock() + cantidadAñadir;
                if (nuevoStock < 0) nuevoStock = 0;
                l.setStock(nuevoStock);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarLibro(String codigo) {
        return libros.removeIf(l -> l.getCodigo().equalsIgnoreCase(codigo));
    }

    public List<Libro> buscarLibros(String filtro) {
        List<Libro> resultados = new ArrayList<>();
        String query = filtro.toLowerCase().trim();
        for (Libro l : libros) {
            if (l.getCodigo().toLowerCase().contains(query) ||
                l.getTitulo().toLowerCase().contains(query) ||
                l.getAutor().toLowerCase().contains(query)) {
                resultados.add(l);
            }
        }
        return resultados;
    }

    public void registrarUsuario(String codigo, String nombre, String correo) throws CodigoDuplicadoException {
        for (Usuario u : usuarios) {
            if (u.getCodigo().equalsIgnoreCase(codigo)) {
                throw new CodigoDuplicadoException("El código de usuario " + codigo + " ya existe.");
            }
        }
        usuarios.add(new Usuario(codigo, nombre, correo));
    }

    public boolean modificarUsuario(String codigo, String nuevoNombre, String nuevoCorreo) {
        for (Usuario u : usuarios) {
            if (u.getCodigo().equalsIgnoreCase(codigo)) {
                u.setNombre(nuevoNombre);
                u.setCorreo(nuevoCorreo);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String codigo) {
        return usuarios.removeIf(u -> u.getCodigo().equalsIgnoreCase(codigo));
    }

    public List<Usuario> buscarUsuarios(String filtro) {
        List<Usuario> resultados = new ArrayList<>();
        String query = filtro.toLowerCase().trim();
        for (Usuario u : usuarios) {
            if (u.getCodigo().toLowerCase().contains(query) ||
                u.getNombre().toLowerCase().contains(query)) {
                resultados.add(u);
            }
        }
        return resultados;
    }

    /**
     * Valida disponibilidad y que el usuario no esté bloqueado antes de realizar el préstamo.
     */
    public void registrarPrestamo(String codP, String codLibro, String codUsuario) throws LibroNoDisponibleException, Exception {
        Libro libroEncontrado = null;
        Usuario usuarioEncontrado = null;

        for (Libro l : libros) { if (l.getCodigo().equalsIgnoreCase(codLibro)) { libroEncontrado = l; break; } }
        for (Usuario u : usuarios) { if (u.getCodigo().equalsIgnoreCase(codUsuario)) { usuarioEncontrado = u; break; } }

        if (libroEncontrado == null || usuarioEncontrado == null) {
            throw new Exception("El Libro o el Usuario especificado no existen.");
        }
        if (usuarioEncontrado.isBloqueado()) {
            throw new LibroNoDisponibleException("El usuario '" + usuarioEncontrado.getNombre() + "' está BLOQUEADO por multas o retrasos.");
        }
        if (!libroEncontrado.isDisponible()) {
            throw new LibroNoDisponibleException("No hay stock disponible del libro '" + libroEncontrado.getTitulo() + "'.");
        }

        prestamos.add(new Prestamo(codP, libroEncontrado, usuarioEncontrado));
    }

    public boolean registrarDevolucion(String codigoPrestamo) {
        for (Prestamo p : prestamos) {
            if (p.getCodigoPrestamo().equalsIgnoreCase(codigoPrestamo) && p.getEstado().startsWith("Activo")) {
                LocalDate hoy = LocalDate.now();
                long diasPasados = ChronoUnit.DAYS.between(p.getFechaPrestamo(), hoy);
                
                p.finalizarPrestamo();
                
                if (diasPasados > 7) {
                    p.getUsuario().setBloqueado(true);
                } else {
                    verificarYDesbloquearUsuario(p.getUsuario());
                }
                return true;
            }
        }
        return false;
    }

    public boolean registrarBajaPorPerdida(String codigoPrestamo) {
        for (Prestamo p : prestamos) {
            if (p.getCodigoPrestamo().equalsIgnoreCase(codigoPrestamo) && p.getEstado().startsWith("Activo")) {
                p.setEstado("Dado de Baja (Pérdida / Daño)");
                p.getUsuario().setBloqueado(true);
                return true;
            }
        }
        return false;
    }

    public boolean revertirBajaPorPerdida(String codigoPrestamo) {
        for (Prestamo p : prestamos) {
            if (p.getCodigoPrestamo().equalsIgnoreCase(codigoPrestamo) && p.getEstado().startsWith("Dado de Baja")) {
                p.setEstado("Devuelto tras Baja (Repuesto)");
                p.getLibro().devolverEjemplar();
                verificarYDesbloquearUsuario(p.getUsuario());
                return true;
            }
        }
        return false;
    }

    private void verificarYDesbloquearUsuario(Usuario u) {
        boolean tieneRetrasosPendientes = false;
        for (Prestamo p : prestamos) {
            if (p.getUsuario().getCodigo().equalsIgnoreCase(u.getCodigo()) && p.getEstado().contains("Retraso")) {
                tieneRetrasosPendientes = true;
                break;
            }
        }
        if (!tieneRetrasosPendientes) {
            u.setBloqueado(false);
        }
    }

    public void exportarTodoACSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_LIBROS))) {
            writer.println("Código;Título;Autor;Stock;Estado");
            for (Libro l : libros) { writer.println(l.getCodigo() + ";" + l.getTitulo() + ";" + l.getAutor() + ";" + l.getStock() + ";" + l.getEstadoTexto()); }
        } catch (IOException e) {}

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            writer.println("Código;Nombre;Correo;EstadoBloqueo");
            for (Usuario u : usuarios) { writer.println(u.getCodigo() + ";" + u.getNombre() + ";" + u.getCorreo() + ";" + u.getEstadoTexto()); }
        } catch (IOException e) {}

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_PRESTAMOS))) {
            writer.println("CodigoPrestamo;Libro;Usuario;FechaPrestamo;Estado");
            for (Prestamo p : prestamos) { writer.println(p.getCodigoPrestamo() + ";" + p.getLibro().getTitulo() + ";" + p.getUsuario().getNombre() + ";" + p.getFechaPrestamo() + ";" + p.getEstado()); }
        } catch (IOException e) {}
    }

    private void cargarDatosIniciales() {
        File archivo = new File(ARCHIVO_LIBROS);
        if (!archivo.exists()) {
            libros.add(new Libro("LIB-001", "El Señor de los Anillos", "John Ronald Reuel Tolkien", 3));
            libros.add(new Libro("LIB-002", "Harry Potter", "Joanne Rowling", 2));
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            libros.clear();
            boolean primera = true;
            while ((linea = reader.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] p = linea.split(";");
                if (p.length >= 4) {
                    libros.add(new Libro(p[0], p[1], p[2], Integer.parseInt(p[3])));
                }
            }
        } catch (IOException e) {}
    }

    private void cargarUsuariosGuardados() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = reader.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] p = linea.split(";");
                if (p.length >= 3) {
                    Usuario u = new Usuario(p[0], p[1], p[2]);
                    if (p.length >= 4 && p[3].contains("Bloqueado")) {
                        u.setBloqueado(true);
                    }
                    usuarios.add(u);
                }
            }
        } catch (IOException e) {}
    }
}