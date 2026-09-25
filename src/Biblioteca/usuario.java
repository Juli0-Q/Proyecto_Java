package Biblioteca;

public class usuario extends persona {
    private String correo;

    public usuario(int id, String nombre, String correo) {
        super(id, nombre);  
        this.correo = correo;
    }

    public String getCorreo() {
    return correo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", correo='" + correo + '\'' +
            '}';
    }
}
