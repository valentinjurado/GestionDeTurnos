package modelo.service;
import modelo.dao.PacienteDao;
import modelo.entidades.Paciente;
import java.util.List;
public class PacienteService {
    private PacienteDao pacDao = new PacienteDao();

    public void registrarPaciente(Paciente p) throws Exception {
        // REGLA DE NEGOCIO 1: Validar que el DNI no esté vacío
        if (p.getDni() == null || p.getDni().trim().isEmpty()) {
            throw new Exception("El DNI es obligatorio para registrar un paciente.");
        }

        // REGLA DE NEGOCIO 2: No permitir duplicados (Evita errores de SQL feos)
        if (existeDni(p.getDni())) {
            throw new Exception("Ya existe un paciente registrado con el DNI: " + p.getDni());
        }

        // Si pasa todas las reglas, recién ahí llamamos al DAO
        pacDao.insertar(p);
    }

    public List<Paciente> obtenerTodosLosPacientes() {
        // Aquí podrías agregar lógica de filtrado o auditoría si quisieras
        return pacDao.listarTodos();
    }

    // Método de soporte para validaciones
    private boolean existeDni(String dni) {
        List<Paciente> lista = pacDao.listarTodos();
        return lista.stream().anyMatch(pac -> pac.getDni().equals(dni));
    }
}
