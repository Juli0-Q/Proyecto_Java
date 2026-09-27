package Biblioteca;

public class usuario {

    private int codigo;
    private String nombre;

    public usuario(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "Código: " + codigo
                + " | Nombre: " + nombre;
    }
}
