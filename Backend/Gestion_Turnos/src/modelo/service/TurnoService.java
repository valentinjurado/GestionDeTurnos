package modelo.service;

import modelo.dao.AgendaDao;
import modelo.dao.TurnoDao;
import modelo.dao.PacienteDao;
import modelo.entidades.AgendaMedica;
import modelo.entidades.Turno;
import modelo.entidades.Paciente;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TurnoService {

    private TurnoDao turnoDao = new TurnoDao();
    private PacienteDao pacienteDao = new PacienteDao();

    public void registrarTurno(Turno turno) throws Exception {
        System.out.println(" Entrando al Service. Validando datos...");

        if (turno.getFecha() == null || turno.getHora() == null) {
            throw new Exception("Falta la fecha o la hora del turno");
        }

        Paciente pacWeb = turno.getPaciente();
        if (pacWeb == null || pacWeb.getDni() == null) {
            throw new Exception("El paciente o su DNI vinieron nulos desde el Frontend.");
        }

        System.out.println("Buscando si el paciente con DNI " + pacWeb.getDni() + " existe en la DB.");
        Paciente pacienteExistente = pacienteDao.buscarPorDni(pacWeb.getDni());

        if (pacienteExistente == null) {
            System.out.println("El paciente no existe. Insertando en la DB.");
            pacienteDao.insertar(pacWeb);

            pacienteExistente = pacienteDao.buscarPorDni(pacWeb.getDni());
        }


        turno.setPaciente(pacienteExistente);

        if (turno.getPaciente() == null || turno.getPaciente().getId() == 0) {
            System.out.println("No se puede registrar el turno porque el paciente no es válido o falló su inserción.");
            throw new Exception("El paciente no pudo ser registrado correctamente.");
        }

        System.out.println("Mandando el turno definitivo al TurnoDao.");
        turnoDao.insertar(turno);
    }

    public List<String> calcularHorariosDisponibles(int idProfesional, String fechaStr) {
        List<String> horariosDisponibles = new ArrayList<>();


        LocalDate fecha = LocalDate.parse(fechaStr);
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        int idDia = diaSemana.getValue();


        AgendaDao agendaDao = new AgendaDao();
        AgendaMedica agenda = agendaDao.buscarPorProfesionalYDia(idProfesional, idDia);

        if (agenda == null) {
            return horariosDisponibles;
        }


        LocalTime horaActual = agenda.getHoraInicio().toLocalTime();
        LocalTime horaFin = agenda.getHoraFin().toLocalTime();
        int intervalo = agenda.getDuracionMinutos();


        List<String> grillaCalculada = new ArrayList<>();
        while (horaActual.isBefore(horaFin)) {
            grillaCalculada.add(horaActual.toString().substring(0, 5));
            horaActual = horaActual.plusMinutes(intervalo);
        }


        TurnoDao turnoDao = new TurnoDao();
        List<String> horariosOcupados = turnoDao.obtenerHorariosOcupados(idProfesional, fechaStr);


        for (String horaGrilla : grillaCalculada) {
            if (!horariosOcupados.contains(horaGrilla)) {
                horariosDisponibles.add(horaGrilla);
            }
        }

        return horariosDisponibles;
    }


    public List<Turno> obtenerTodos() {
        TurnoDao turnoDao = new TurnoDao();
        return turnoDao.listarTurnosCompletos();
    }
}