package modelo.service;
import modelo.dao.PacienteDao;
import modelo.entidades.Paciente;
import java.util.List;
public class PacienteService {
    private PacienteDao pacDao = new PacienteDao();

    public void registrarPaciente(Paciente p) throws Exception {

        if (p.getDni() == null || p.getDni().trim().isEmpty()) {
            throw new Exception("El DNI es obligatorio para registrar un paciente.");
        }


        if (existeDni(p.getDni())) {
            throw new Exception("Ya existe un paciente registrado con el DNI: " + p.getDni());
        }


        pacDao.insertar(p);
    }

    public List<Paciente> obtenerTodosLosPacientes() {
        return pacDao.listarTodos();
    }


    private boolean existeDni(String dni) {
        List<Paciente> lista = pacDao.listarTodos();
        return lista.stream().anyMatch(pac -> pac.getDni().equals(dni));
    }
}
