package modelo.entidades;

import java.util.Date;

public class Paciente {
    private int id; // <--- Agregamos el ID para las relaciones
    private String nombre;
    private String dni;
    private String apellido;
    private String telefono;
    private String email;
    private Date fecha_nacimiento;

    // Constructor completo (el que usarás al listar desde la DB)
    public Paciente(int id, String nombre, String dni, String apellido, String telefono, String email, Date fecha_nacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    // Constructor para cuando creas un paciente nuevo (sin ID, porque Postgres lo genera solo)
    public Paciente(String nombre, String dni, String apellido, String telefono, String email, Date fecha_nacimiento) {
        this.nombre = nombre;
        this.dni = dni;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Paciente() {
    }

    // Getter y Setter para el ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Los demás Getters y Setters que ya tenías están perfectos...
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(Date fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }
}
