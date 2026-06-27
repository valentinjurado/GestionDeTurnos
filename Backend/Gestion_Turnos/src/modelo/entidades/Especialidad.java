package modelo.entidades;

public class Especialidad {
    private int id;
    private String nombreEspecialidad;


    public Especialidad(int id, String nombreEspecialidad) {
        this.id = id;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public Especialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // Para que en los combos de la interfaz o en la consola se vea bien
    @Override
    public String toString() {
        return nombreEspecialidad;
    }
}
