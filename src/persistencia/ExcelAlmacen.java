package persistencia;

import excepciones.BibliotecaException;
import modelo.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.zip.*;

/**
 * Guarda y carga toda la biblioteca en UN solo archivo Excel (.xlsx) con tres hojas:
 * Libros, Usuarios y Préstamos. No usa librerías externas (un .xlsx es un ZIP de XML).
 * Si el usuario edita el archivo en Excel, el programa también puede leerlo.
 */
public final class ExcelAlmacen {
    private static final String NS_MAIN = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String NS_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
    private static final String NS_PKG_REL = "http://schemas.openxmlformats.org/package/2006/relationships";
    private static final String DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    private ExcelAlmacen() { }

    // ================================================================ GUARDAR

    public static void guardar(Biblioteca b, File archivo) throws IOException {
        List<Object[]> libros = new ArrayList<>();
        libros.add(new Object[]{"Código", "Título", "Autor", "Estado"});
        for (Libro l : b.listarLibros()) {
            libros.add(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getEstado()});
        }
        List<Object[]> usuarios = new ArrayList<>();
        usuarios.add(new Object[]{"Código", "Nombre"});
        for (Usuario u : b.listarUsuarios()) {
            usuarios.add(new Object[]{u.getCodigo(), u.getNombre()});
        }
        List<Object[]> prestamos = new ArrayList<>();
        prestamos.add(new Object[]{"Préstamo", "Cód. libro", "Libro", "Cód. usuario", "Usuario", "Fecha", "Estado"});
        for (Prestamo p : b.listarPrestamos()) {
            prestamos.add(new Object[]{p.getCodigo(), p.getLibro().getCodigo(), p.getLibro().getTitulo(),
                    p.getUsuario().getCodigo(), p.getUsuario().getNombre(), p.getFecha().toString(), p.getEstado()});
        }

        // Se escribe primero en un temporal para no dañar el archivo existente si algo falla
        Path destino = archivo.getAbsoluteFile().toPath();
        Path temporal = Paths.get(destino + ".tmp");
        try (ZipOutputStream z = new ZipOutputStream(Files.newOutputStream(temporal), StandardCharsets.UTF_8)) {
            entrada(z, "[Content_Types].xml", DECL
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                    + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                    + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                    + "<Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                    + "<Override PartName=\"/xl/worksheets/sheet3.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                    + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                    + "</Types>");
            entrada(z, "_rels/.rels", DECL + "<Relationships xmlns=\"" + NS_PKG_REL + "\">"
                    + "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/officeDocument\" Target=\"xl/workbook.xml\"/>"
                    + "</Relationships>");
            entrada(z, "xl/workbook.xml", DECL + "<workbook xmlns=\"" + NS_MAIN + "\" xmlns:r=\"" + NS_REL + "\"><sheets>"
                    + "<sheet name=\"Libros\" sheetId=\"1\" r:id=\"rId1\"/>"
                    + "<sheet name=\"Usuarios\" sheetId=\"2\" r:id=\"rId2\"/>"
                    + "<sheet name=\"Préstamos\" sheetId=\"3\" r:id=\"rId3\"/>"
                    + "</sheets></workbook>");
            entrada(z, "xl/_rels/workbook.xml.rels", DECL + "<Relationships xmlns=\"" + NS_PKG_REL + "\">"
                    + "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                    + "<Relationship Id=\"rId2\" Type=\"" + NS_REL + "/worksheet\" Target=\"worksheets/sheet2.xml\"/>"
                    + "<Relationship Id=\"rId3\" Type=\"" + NS_REL + "/worksheet\" Target=\"worksheets/sheet3.xml\"/>"
                    + "<Relationship Id=\"rId4\" Type=\"" + NS_REL + "/styles\" Target=\"styles.xml\"/>"
                    + "</Relationships>");
            entrada(z, "xl/styles.xml", DECL + "<styleSheet xmlns=\"" + NS_MAIN + "\">"
                    + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                    + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
                    + "<fills count=\"2\"><fill><patternFill patternType=\"none\"/></fill>"
                    + "<fill><patternFill patternType=\"gray125\"/></fill></fills>"
                    + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                    + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                    + "<cellXfs count=\"2\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                    + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/></cellXfs>"
                    + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                    + "</styleSheet>");
            entrada(z, "xl/worksheets/sheet1.xml", hoja(libros, new int[]{10, 36, 30, 14}));
            entrada(z, "xl/worksheets/sheet2.xml", hoja(usuarios, new int[]{10, 30}));
            entrada(z, "xl/worksheets/sheet3.xml", hoja(prestamos, new int[]{11, 11, 32, 13, 24, 13, 12}));
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(temporal);
            throw e;
        }
        try {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Files.deleteIfExists(temporal);
            throw new IOException("No se pudo escribir " + destino.getFileName()
                    + ". Si lo tiene abierto en Excel, ciérrelo e inténtelo de nuevo.", e);
        }
    }

    private static void entrada(ZipOutputStream z, String nombre, String contenido) throws IOException {
        z.putNextEntry(new ZipEntry(nombre));
        z.write(contenido.getBytes(StandardCharsets.UTF_8));
        z.closeEntry();
    }

    private static String hoja(List<Object[]> filas, int[] anchos) {
        StringBuilder sb = new StringBuilder(DECL);
        sb.append("<worksheet xmlns=\"").append(NS_MAIN).append("\"><cols>");
        for (int i = 0; i < anchos.length; i++) {
            sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
              .append("\" width=\"").append(anchos[i]).append("\" customWidth=\"1\"/>");
        }
        sb.append("</cols><sheetData>");
        for (int r = 0; r < filas.size(); r++) {
            sb.append("<row r=\"").append(r + 1).append("\">");
            Object[] fila = filas.get(r);
            for (int c = 0; c < fila.length; c++) {
                String ref = "" + (char) ('A' + c) + (r + 1);
                if (fila[c] instanceof Number) {
                    sb.append("<c r=\"").append(ref).append("\"><v>").append(fila[c]).append("</v></c>");
                } else {
                    sb.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"").append(r == 0 ? " s=\"1\"" : "")
                      .append("><is><t xml:space=\"preserve\">").append(esc(String.valueOf(fila[c])))
                      .append("</t></is></c>");
                }
            }
            sb.append("</row>");
        }
        return sb.append("</sheetData></worksheet>").toString();
    }

    private static String esc(String s) {
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                default:
                    if (ch >= 0x20 || ch == '\t' || ch == '\n' || ch == '\r') sb.append(ch);
            }
        }
        return sb.toString();
    }

    // ================================================================ CARGAR

    private static final class Fila {
        final int numero;
        final String[] celdas;
        Fila(int numero, String[] celdas) { this.numero = numero; this.celdas = celdas; }
        String celda(int i) { return i < celdas.length && celdas[i] != null ? celdas[i].trim() : ""; }
        boolean vacia() { for (int i = 0; i < celdas.length; i++) if (!celda(i).isEmpty()) return false; return true; }
    }

    public static Biblioteca cargar(File archivo) throws IOException {
        Map<String, byte[]> partes = leerZip(archivo);
        Map<String, String> rutas = rutasDeHojas(partes);
        List<String> compartidas = cadenasCompartidas(partes);

        List<Fila> libros = filas(partes, rutas, compartidas, "Libros");
        List<Fila> usuarios = filas(partes, rutas, compartidas, "Usuarios");
        List<Fila> prestamos = filas(partes, rutas, compartidas, "Préstamos", "Prestamos");

        Biblioteca b = new Biblioteca();
        try {
            for (Fila f : libros) {
                if (f.vacia()) continue;
                b.agregarLibro(new Libro(codigo(f, 0, "Libros"), texto(f, 1, "Libros"), texto(f, 2, "Libros")));
            }
            for (Fila f : usuarios) {
                if (f.vacia()) continue;
                b.agregarUsuario(new Usuario(codigo(f, 0, "Usuarios"), texto(f, 1, "Usuarios")));
            }
            for (Fila f : prestamos) {
                if (f.vacia()) continue;
                Libro libro = b.buscarLibro(codigo(f, 1, "Préstamos"));
                Usuario usuario = b.buscarUsuario(codigo(f, 3, "Préstamos"));
                boolean activo = f.celda(6).equalsIgnoreCase("Activo");
                b.agregarPrestamo(new Prestamo(codigo(f, 0, "Préstamos"), libro, usuario, fecha(f, 5), activo));
            }
        } catch (BibliotecaException e) {
            throw new IOException("Datos inconsistentes en el Excel: " + e.getMessage(), e);
        }
        return b;
    }

    private static int codigo(Fila f, int col, String hoja) throws IOException {
        String s = f.celda(col);
        try {
            double d = Double.parseDouble(s);
            if (d > 0 && d == Math.floor(d) && d <= Integer.MAX_VALUE) return (int) d;
        } catch (NumberFormatException ignorada) { }
        throw new IOException("Hoja " + hoja + ", fila " + f.numero + ": \"" + s + "\" no es un código válido.");
    }

    private static String texto(Fila f, int col, String hoja) throws IOException {
        String s = f.celda(col);
        if (s.isEmpty()) throw new IOException("Hoja " + hoja + ", fila " + f.numero + ": falta un dato obligatorio.");
        return s;
    }

    private static LocalDate fecha(Fila f, int col) throws IOException {
        String s = f.celda(col);
        try {
            if (s.matches("\\d+(\\.\\d+)?")) { // Excel guarda las fechas como número de serie
                return LocalDate.of(1899, 12, 30).plusDays((long) Double.parseDouble(s));
            }
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new IOException("Hoja Préstamos, fila " + f.numero + ": fecha inválida \"" + s + "\".");
        }
    }

    private static Map<String, byte[]> leerZip(File archivo) throws IOException {
        Map<String, byte[]> partes = new HashMap<>();
        try (ZipInputStream z = new ZipInputStream(new BufferedInputStream(new FileInputStream(archivo)))) {
            ZipEntry e;
            while ((e = z.getNextEntry()) != null) {
                if (!e.isDirectory()) partes.put(e.getName(), z.readAllBytes());
            }
        } catch (ZipException e) {
            throw new IOException("El archivo no es un Excel (.xlsx) válido.", e);
        }
        if (!partes.containsKey("xl/workbook.xml")) {
            throw new IOException("El archivo no es un Excel (.xlsx) válido.");
        }
        return partes;
    }

    private static Document xml(byte[] datos) throws IOException {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return f.newDocumentBuilder().parse(new ByteArrayInputStream(datos));
        } catch (Exception e) {
            throw new IOException("Archivo Excel dañado: " + e.getMessage(), e);
        }
    }

    /** Relaciona el nombre de cada hoja con la ruta de su XML dentro del ZIP. */
    private static Map<String, String> rutasDeHojas(Map<String, byte[]> partes) throws IOException {
        Map<String, String> destinos = new HashMap<>();
        byte[] rels = partes.get("xl/_rels/workbook.xml.rels");
        if (rels == null) throw new IOException("El archivo no es un Excel (.xlsx) válido.");
        NodeList rs = xml(rels).getElementsByTagName("Relationship");
        for (int i = 0; i < rs.getLength(); i++) {
            Element r = (Element) rs.item(i);
            String t = r.getAttribute("Target");
            destinos.put(r.getAttribute("Id"), t.startsWith("/") ? t.substring(1) : "xl/" + t);
        }
        Map<String, String> rutas = new HashMap<>();
        NodeList hs = xml(partes.get("xl/workbook.xml")).getElementsByTagName("sheet");
        for (int i = 0; i < hs.getLength(); i++) {
            Element h = (Element) hs.item(i);
            rutas.put(h.getAttribute("name"), destinos.get(h.getAttribute("r:id")));
        }
        return rutas;
    }

    private static List<String> cadenasCompartidas(Map<String, byte[]> partes) throws IOException {
        List<String> lista = new ArrayList<>();
        byte[] datos = partes.get("xl/sharedStrings.xml");
        if (datos == null) return lista;
        NodeList sis = xml(datos).getElementsByTagName("si");
        for (int i = 0; i < sis.getLength(); i++) {
            lista.add(textoDe((Element) sis.item(i)));
        }
        return lista;
    }

    private static String textoDe(Element e) {
        StringBuilder sb = new StringBuilder();
        NodeList ts = e.getElementsByTagName("t");
        for (int i = 0; i < ts.getLength(); i++) {
            if (!"rPh".equals(ts.item(i).getParentNode().getNodeName())) sb.append(ts.item(i).getTextContent());
        }
        return sb.toString();
    }

    private static List<Fila> filas(Map<String, byte[]> partes, Map<String, String> rutas,
                                    List<String> compartidas, String... nombres) throws IOException {
        String ruta = null;
        for (String n : nombres) {
            if (rutas.get(n) != null) { ruta = rutas.get(n); break; }
        }
        if (ruta == null || partes.get(ruta) == null) {
            throw new IOException("Falta la hoja \"" + nombres[0] + "\" en el archivo Excel.");
        }
        List<Fila> filas = new ArrayList<>();
        NodeList rows = xml(partes.get(ruta)).getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            Element row = (Element) rows.item(i);
            List<String> celdas = new ArrayList<>();
            NodeList cs = row.getElementsByTagName("c");
            for (int j = 0; j < cs.getLength(); j++) {
                Element c = (Element) cs.item(j);
                int col = columna(c.getAttribute("r"), j);
                while (celdas.size() <= col) celdas.add("");
                celdas.set(col, valor(c, compartidas));
            }
            int numero = row.hasAttribute("r") ? Integer.parseInt(row.getAttribute("r")) : i + 1;
            if (numero > 1) { // la fila 1 es el encabezado
                filas.add(new Fila(numero, celdas.toArray(new String[0])));
            }
        }
        return filas;
    }

    private static int columna(String ref, int respaldo) {
        int col = 0, n = 0;
        for (char ch : ref.toCharArray()) {
            if (ch < 'A' || ch > 'Z') break;
            col = col * 26 + (ch - 'A' + 1);
            n++;
        }
        return n == 0 ? respaldo : col - 1;
    }

    private static String valor(Element c, List<String> compartidas) {
        String tipo = c.getAttribute("t");
        if ("inlineStr".equals(tipo)) return textoDe(c);
        NodeList vs = c.getElementsByTagName("v");
        if (vs.getLength() == 0) return "";
        String v = vs.item(0).getTextContent();
        if ("s".equals(tipo)) {
            int idx = Integer.parseInt(v.trim());
            return idx >= 0 && idx < compartidas.size() ? compartidas.get(idx) : "";
        }
        return v;
    }
}
