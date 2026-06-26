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
        System.out.println("🧠 2. Entrando al Service. Validando datos...");

        if (turno.getFecha() == null || turno.getHora() == null) {
            throw new Exception("Falta la fecha o la hora del turno en el objeto Java.");
        }

        Paciente pacWeb = turno.getPaciente();
        if (pacWeb == null || pacWeb.getDni() == null) {
            throw new Exception("El paciente o su DNI vinieron nulos desde el Frontend.");
        }

        System.out.println("🔍 3. Buscando si el paciente con DNI " + pacWeb.getDni() + " existe en la DB...");
        Paciente pacienteExistente = pacienteDao.buscarPorDni(pacWeb.getDni());

        if (pacienteExistente == null) {
            System.out.println("🆕 4. El paciente no existe. Insertando en la DB...");
            pacienteDao.insertar(pacWeb);
            // Volvemos a buscarlo para asegurarnos de capturar el ID que le asignó PostgreSQL
            pacienteExistente = pacienteDao.buscarPorDni(pacWeb.getDni());
        }

        // Le asociamos al turno el paciente definitivo que ya tiene ID de la base de datos
        turno.setPaciente(pacienteExistente);

        if (turno.getPaciente() == null || turno.getPaciente().getId() == 0) {
            System.out.println("❌ No se puede registrar el turno porque el paciente no es válido o falló su inserción.");
            // Podés lanzar una excepción o retornar una lista/booleano indicando el fallo
            throw new Exception("El paciente no pudo ser registrado correctamente.");
        }

        System.out.println("✍️ 5. Mandando el turno definitivo al TurnoDao...");
        turnoDao.insertar(turno);
    }

    public List<String> calcularHorariosDisponibles(int idProfesional, String fechaStr) {
        List<String> horariosDisponibles = new ArrayList<>();

        // 1. Parseamos la fecha que mandó la web y calculamos el día de la semana (Lunes=1 ... Domingo=7)
        LocalDate fecha = LocalDate.parse(fechaStr);
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        int idDia = diaSemana.getValue(); // Convierte automáticamente a: Lunes=1, Jueves=4, etc.

        // 2. Buscamos la regla de atención en la base de datos
        AgendaDao agendaDao = new AgendaDao();
        AgendaMedica agenda = agendaDao.buscarPorProfesionalYDia(idProfesional, idDia);

        // Si el médico no atiende este día de la semana, devolvemos la lista vacía
        if (agenda == null) {
            return horariosDisponibles;
        }

        // 3. Convertimos los tiempos de SQL a LocalTime para operar matemáticamente
        LocalTime horaActual = agenda.getHoraInicio().toLocalTime();
        LocalTime horaFin = agenda.getHoraFin().toLocalTime();
        int intervalo = agenda.getDuracionMinutos();

        // 4. Armamos la grilla base en memoria sumando los intervalos correspondientes
        List<String> grillaCalculada = new ArrayList<>();
        while (horaActual.isBefore(horaFin)) {
            grillaCalculada.add(horaActual.toString().substring(0, 5)); // Guarda "08:00", "08:30", etc.
            horaActual = horaActual.plusMinutes(intervalo);
        }

        // 5. Buscamos qué horarios ya están tomados en la tabla 'turnos'
        TurnoDao turnoDao = new TurnoDao();
        List<String> horariosOcupados = turnoDao.obtenerHorariosOcupados(idProfesional, fechaStr);

        // 6. Cruzamos los datos: Pasamos a la lista final solo los que estén LIBRES
        for (String horaGrilla : grillaCalculada) {
            if (!horariosOcupados.contains(horaGrilla)) {
                horariosDisponibles.add(horaGrilla);
            }
        }

        return horariosDisponibles; // Devolvemos la lista limpia de horarios reales disponibles
    }

    // --- AGREGÁ ESTO ADENTRO DE TU TURNOSERVICE.JAVA ---

    public List<Turno> obtenerTodos() {
        TurnoDao turnoDao = new TurnoDao();
        return turnoDao.listarTurnosCompletos();
    }
}