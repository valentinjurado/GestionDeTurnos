package modelo.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Turno {
    private int idTurno;
    private Paciente paciente;
    private Profesional profesional; // Conservalo si tu DAO lo usa

    // Agregamos este entero que es el que viene directo de la Web
    @JsonProperty("id_profesional")
    private int idProfesional;

    // Cambiamos a String para que Jackson reciba el JSON sin romperse
    private String fecha;
    private String hora;

    // Cambiamos temporalmente a String o int para destrabar el mapeo del Enum
    private String prioridad;
    private String observaciones;

    // --- CONSTRUCTOR VACÍO (Obligatorio para Spring) ---
    public Turno() {
    }

    // --- CONSTRUCTORES VIEJOS (Los dejamos por si tus DAOs los llaman) ---
    public Turno(Paciente paciente, Profesional profesional, String fecha, String hora, String prioridad) {
        this.paciente = paciente;
        this.profesional = profesional;
        this.fecha = fecha;
        this.hora = hora;
        this.prioridad = prioridad;
    }

    // --- GETTERS Y SETTERS REALES QUE USARÁ SPRING ---
    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Profesional getProfesional() { return profesional; }
    public void setProfesional(Profesional profesional) { this.profesional = profesional; }

    public int getIdProfesional() { return idProfesional; }
    public void setIdProfesional(int idProfesional) { this.idProfesional = idProfesional; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
