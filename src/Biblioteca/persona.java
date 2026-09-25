package Biblioteca;

public class persona {
    protected int id;
    protected String nombre;

    public persona(int id, String nombre) {
    this.id = id;
    this.nombre = nombre;
    }

    public int getId() {
        return id;  
    }

    public String getNombre() {
    return nombre;
    }

    @Override
    public String toString() {
    return "ID: " + id + ", Nombre: " + nombre;
    }

}
