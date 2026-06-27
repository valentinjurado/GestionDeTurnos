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
@CrossOrigin(origins = "*")

public class TurnoController {


    private TurnoService turnoService = new TurnoService();

    @PostMapping
    public ResponseEntity<String> guardarTurnoDesdeWeb(@RequestBody Turno nuevoTurno) {
        try {

            turnoService.registrarTurno(nuevoTurno);

            return ResponseEntity.ok("{\"mensaje\": \"¡Turno guardado con éxito!\"}");
        } catch (Exception e) {
            System.out.println("❌ ERROR ADENTRO DEL CONTROLLER:");
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }




    @GetMapping("/profesionales")
    public ResponseEntity<?> listarProfesionales() {
        try {
            ProfesionalDao profDao = new ProfesionalDao();

            return ResponseEntity.ok(profDao.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/especialidades")
    public ResponseEntity<?> listarEspecialidades() {
        try {

            modelo.dao.EspecialidadDao espDao = new modelo.dao.EspecialidadDao();
            return ResponseEntity.ok(espDao.listarTodas());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/horarios-ocupados")
    public ResponseEntity<?> obtenerHorariosDisponibles(
            @RequestParam("id_profesional") int idProfesional,
            @RequestParam("fecha") String fecha) {
        try {

            List<String> libres = turnoService.calcularHorariosDisponibles(idProfesional, fecha);
            return ResponseEntity.ok(libres);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pacientes/buscar")
    public ResponseEntity<?> buscarPacientePorDni(@RequestParam("dni") String dni) {
        try {

            modelo.dao.PacienteDao pacienteDao = new modelo.dao.PacienteDao();
            modelo.entidades.Paciente paciente = pacienteDao.buscarPorDni(dni);

            if (paciente != null) {
                return ResponseEntity.ok(paciente);
            } else {
                return ResponseEntity.status(404).body("{\"message\": \"Paciente no registrado\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosTurnos() {
        try {

            List<modelo.entidades.Turno> lista = turnoService.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}

