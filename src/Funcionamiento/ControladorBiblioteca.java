package Funcionamiento;

import ExcepcionesPersonalizadas.CodigoDuplicadoException;
import ExcepcionesPersonalizadas.LibroNoDisponibleException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    // Si algún CSV existe pero no se pudo leer, no se exporta para no sobrescribirlo con datos vacíos.
    private boolean errorDeCarga = false;

    public ControladorBiblioteca() {
        cargarDatosIniciales();
        cargarUsuariosGuardados();
        cargarPrestamosGuardados();
        
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
        int mayor = 0;
        for (Prestamo p : prestamos) {
            try {
                int num = Integer.parseInt(p.getCodigoPrestamo().split("-")[1]);
                if (num > mayor) mayor = num;
            } catch (Exception e) {}
        }
        return String.format("PRES-%03d", mayor + 1);
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

    // =====================================================================
    //  PERSISTENCIA CSV
    //  - UTF-8 con BOM (Excel muestra bien tildes y ñ)
    //  - Campos con ';' o '"' se entrecomillan, así no se corrompen las columnas
    //  - Escritura en archivo temporal + reemplazo, para no dejar archivos a medias
    // =====================================================================
    private static final char SEP = ';';
    private static final String BOM = "\uFEFF";
    private static final String FIN_LINEA = "\r\n";

    private static String esc(String s) {
        if (s == null) return "";
        s = s.replace("\r", " ").replace("\n", " ");
        if (s.indexOf(SEP) >= 0 || s.indexOf('"') >= 0) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String fila(String... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) sb.append(SEP);
            sb.append(esc(campos[i]));
        }
        return sb.toString();
    }

    private static List<String> separar(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (enComillas) {
                if (c == '"') {
                    if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') { actual.append('"'); i++; }
                    else enComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == '"') {
                enComillas = true;
            } else if (c == SEP) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString());
        return campos;
    }

    private void escribirCSV(String nombre, String cabecera, List<String> filas) throws IOException {
        Path destino = Paths.get(nombre).toAbsolutePath();
        Path temporal = destino.resolveSibling(nombre + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
            w.write(BOM);
            w.write(cabecera);
            w.write(FIN_LINEA);
            for (String f : filas) {
                w.write(f);
                w.write(FIN_LINEA);
            }
        }
        try {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Lee un CSV (sin la cabecera). Acepta UTF-8 con/sin BOM y, si no es UTF-8 válido, Windows-1252. */
    private List<List<String>> leerCSV(String nombre) throws IOException {
        List<List<String>> filas = new ArrayList<>();
        File archivo = new File(nombre);
        if (!archivo.exists()) return filas;

        byte[] bytes = Files.readAllBytes(archivo.toPath());
        String texto;
        try {
            texto = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            texto = new String(bytes, Charset.forName("windows-1252"));
        }
        if (texto.startsWith(BOM)) texto = texto.substring(1);

        boolean cabecera = true;
        for (String linea : texto.split("\\r?\\n")) {
            if (cabecera) { cabecera = false; continue; }
            if (linea.trim().isEmpty()) continue;
            filas.add(separar(linea));
        }
        return filas;
    }

    /**
     * Guarda libros, usuarios y préstamos en sus archivos CSV.
     * @throws IOException si algún archivo no se pudo escribir (p. ej. está abierto en Excel).
     */
    public void exportarTodoACSV() throws IOException {
        if (errorDeCarga) {
            throw new IOException("No se exportó para no sobrescribir archivos CSV que no pudieron leerse al iniciar. "
                    + "Revisa Inventario_Biblioteca.csv, Registro_Usuarios.csv y Registro_Prestamos.csv.");
        }

        List<String> filas = new ArrayList<>();
        for (Libro l : libros) {
            filas.add(fila(l.getCodigo(), l.getTitulo(), l.getAutor(), String.valueOf(l.getStock()), l.getEstadoTexto()));
        }
        escribirCSV(ARCHIVO_LIBROS, "Código;Título;Autor;Stock;Estado", filas);

        filas = new ArrayList<>();
        for (Usuario u : usuarios) {
            filas.add(fila(u.getCodigo(), u.getNombre(), u.getCorreo(), u.getEstadoTexto()));
        }
        escribirCSV(ARCHIVO_USUARIOS, "Código;Nombre;Correo;EstadoBloqueo", filas);

        filas = new ArrayList<>();
        for (Prestamo p : prestamos) {
            filas.add(fila(p.getCodigoPrestamo(),
                    p.getLibro().getCodigo(), p.getLibro().getTitulo(),
                    p.getUsuario().getCodigo(), p.getUsuario().getNombre(),
                    p.getFechaPrestamo().toString(), p.getEstado()));
        }
        escribirCSV(ARCHIVO_PRESTAMOS, "CodigoPrestamo;CodigoLibro;Libro;CodigoUsuario;Usuario;FechaPrestamo;Estado", filas);
    }

    /** Ruta absoluta de la carpeta donde se guardan los CSV (para mostrarla al usuario). */
    public String getRutaExportacion() {
        return new File(ARCHIVO_LIBROS).getAbsoluteFile().getParent();
    }

    private void cargarDatosIniciales() {
        if (!new File(ARCHIVO_LIBROS).exists()) {
            libros.add(new Libro("LIB-001", "El Señor de los Anillos", "John Ronald Reuel Tolkien", 3));
            libros.add(new Libro("LIB-002", "Harry Potter", "Joanne Rowling", 2));
            return;
        }
        try {
            for (List<String> p : leerCSV(ARCHIVO_LIBROS)) {
                if (p.size() >= 4) {
                    try {
                        libros.add(new Libro(p.get(0), p.get(1), p.get(2), Integer.parseInt(p.get(3).trim())));
                    } catch (NumberFormatException e) {
                        System.err.println("Fila de inventario ignorada (stock inválido): " + p);
                    }
                }
            }
        } catch (IOException e) {
            errorDeCarga = true;
            System.err.println("No se pudo leer " + ARCHIVO_LIBROS + ": " + e.getMessage());
        }
    }

    private void cargarUsuariosGuardados() {
        try {
            for (List<String> p : leerCSV(ARCHIVO_USUARIOS)) {
                if (p.size() >= 3) {
                    Usuario u = new Usuario(p.get(0), p.get(1), p.get(2));
                    if (p.size() >= 4 && p.get(3).contains("Bloqueado")) {
                        u.setBloqueado(true);
                    }
                    usuarios.add(u);
                }
            }
        } catch (IOException e) {
            errorDeCarga = true;
            System.err.println("No se pudo leer " + ARCHIVO_USUARIOS + ": " + e.getMessage());
        }
    }

    /**
     * Restaura los préstamos guardados. Antes se exportaban pero nunca se volvían a cargar,
     * por lo que tras reiniciar el stock seguía descontado y no se podía registrar la devolución.
     * Acepta el formato nuevo (7 columnas) y el antiguo (5 columnas, por título y nombre).
     */
    private void cargarPrestamosGuardados() {
        try {
            for (List<String> p : leerCSV(ARCHIVO_PRESTAMOS)) {
                Libro libro; Usuario usuario; String fecha, estado;
                if (p.size() >= 7) {
                    libro = buscarLibroPorCodigo(p.get(1));
                    usuario = buscarUsuarioPorCodigo(p.get(3));
                    fecha = p.get(5); estado = p.get(6);
                } else if (p.size() >= 5) {
                    libro = buscarLibroPorTitulo(p.get(1));
                    usuario = buscarUsuarioPorNombre(p.get(2));
                    fecha = p.get(3); estado = p.get(4);
                } else {
                    continue;
                }
                if (libro == null || usuario == null) {
                    System.err.println("Préstamo ignorado (libro o usuario no existe): " + p);
                    continue;
                }
                try {
                    prestamos.add(new Prestamo(p.get(0), libro, usuario, LocalDate.parse(fecha.trim()), estado));
                } catch (DateTimeParseException e) {
                    System.err.println("Préstamo ignorado (fecha inválida): " + p);
                }
            }
        } catch (IOException e) {
            errorDeCarga = true;
            System.err.println("No se pudo leer " + ARCHIVO_PRESTAMOS + ": " + e.getMessage());
        }
    }

    private Libro buscarLibroPorCodigo(String codigo) {
        for (Libro l : libros) if (l.getCodigo().equalsIgnoreCase(codigo.trim())) return l;
        return null;
    }

    private Libro buscarLibroPorTitulo(String titulo) {
        for (Libro l : libros) if (l.getTitulo().equalsIgnoreCase(titulo.trim())) return l;
        return null;
    }

    private Usuario buscarUsuarioPorCodigo(String codigo) {
        for (Usuario u : usuarios) if (u.getCodigo().equalsIgnoreCase(codigo.trim())) return u;
        return null;
    }

    private Usuario buscarUsuarioPorNombre(String nombre) {
        for (Usuario u : usuarios) if (u.getNombre().equalsIgnoreCase(nombre.trim())) return u;
        return null;
    }
}