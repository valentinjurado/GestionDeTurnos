package modelo.entidades;

public class Especialidad {
    private int id;
    private String nombreEspecialidad;

    // Constructor completo (para cuando traes datos de la DB)
    public Especialidad(int id, String nombreEspecialidad) {
        this.id = id;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // Constructor para crear una nueva (sin ID, Postgres lo genera)
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
