package modelo.entidades;

public class Profesional {
    private int id;
    private String nombre;
    private String apellido;
    private Especialidad especialidad;


    public Profesional() {
    }

    // Constructor completo
    public Profesional(int id, String nombre, String apellido, Especialidad especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }


    public Profesional(String nombre, String apellido, Especialidad especialidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    // Un buen toString ayuda mucho al debugear en la consola del Main
    @Override
    public String toString() {
        return "Profesional: " + apellido + ", " + nombre + " [" + especialidad.getNombreEspecialidad() + "]";
    }
}
