package modelo.entidades;

import java.sql.Time;

public class AgendaMedica {
    private Time horaInicio;
    private Time horaFin;
    private int duracionMinutos; // Guardamos el intervalo directamente como minutos limpios

    public AgendaMedica(Time horaInicio, Time horaFin, int duracionMinutos) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.duracionMinutos = duracionMinutos;
    }

    public Time getHoraInicio() { return horaInicio; }
    public Time getHoraFin() { return horaFin; }
    public int getDuracionMinutos() { return duracionMinutos; }
}