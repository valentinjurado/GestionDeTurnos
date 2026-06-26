package modelo.controller;

import modelo.dao.ProfesionalDao;
import modelo.entidades.Turno;
import modelo.service.TurnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*") // Para que tu VS Code pueda hablar con IntelliJ
public class TurnoController {

    // El controlador NO llama directo al DAO, llama al Service
    private TurnoService turnoService = new TurnoService();

    @PostMapping
    public ResponseEntity<String> guardarTurnoDesdeWeb(@RequestBody Turno nuevoTurno) {
        try {
            // Recibe el objeto 'Turno' que Spring armó automáticamente desde el JSON
            turnoService.registrarTurno(nuevoTurno);

            return ResponseEntity.ok("{\"mensaje\": \"¡Turno guardado con éxito!\"}");
        } catch (Exception e) {
            System.out.println("❌ ERROR ADENTRO DEL CONTROLLER:");
            e.printStackTrace(); // <--- ESTO VA A PINTAR EL ERROR EN ROJO SI O SI
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Importá tu ProfesionalDao arriba si es necesario


    @GetMapping("/profesionales")
    public ResponseEntity<?> listarProfesionales() {
        try {
            ProfesionalDao profDao = new ProfesionalDao();
            // Llamamos al método que lee tu base de datos real
            return ResponseEntity.ok(profDao.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    // Dentro de TurnoController.java

    @GetMapping("/especialidades")
    public ResponseEntity<?> listarEspecialidades() {
        try {
            // Instanciás tu DAO de especialidades
            modelo.dao.EspecialidadDao espDao = new modelo.dao.EspecialidadDao();
            return ResponseEntity.ok(espDao.listarTodas()); // Devuelve la lista en JSON
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/horarios-ocupados") // Mantenemos la URL para no romper tu fetch de app.js
    public ResponseEntity<?> obtenerHorariosDisponibles(
            @RequestParam("id_profesional") int idProfesional,
            @RequestParam("fecha") String fecha) {
        try {
            // El controller le delega la inteligencia al Service
            List<String> libres = turnoService.calcularHorariosDisponibles(idProfesional, fecha);
            return ResponseEntity.ok(libres); // Devuelve el array JSON, ej: ["08:30", "10:00"]
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pacientes/buscar")
    public ResponseEntity<?> buscarPacientePorDni(@RequestParam("dni") String dni) {
        try {
            // Usamos el PacienteDao para ver si existe en PostgreSQL
            modelo.dao.PacienteDao pacienteDao = new modelo.dao.PacienteDao();
            modelo.entidades.Paciente paciente = pacienteDao.buscarPorDni(dni); // Asegurá que devuelva el objeto completo

            if (paciente != null) {
                return ResponseEntity.ok(paciente); // Devuelve el JSON del paciente encontrado
            } else {
                return ResponseEntity.status(404).body("{\"message\": \"Paciente no registrado\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping // Esto mapea a GET http://localhost:8080/api/turnos
    public ResponseEntity<?> obtenerTodosLosTurnos() {
        try {
            // Le pedimos la lista completa de turnos al Service
            List<modelo.entidades.Turno> lista = turnoService.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}

