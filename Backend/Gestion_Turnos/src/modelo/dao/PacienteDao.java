package modelo.dao; // Ajustá esto según tu carpeta real
import database.ConexionDB;
import modelo.entidades.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PacienteDao implements DAO<Paciente>{
    @Override
    public void insertar(Paciente p) {
        String sql = "INSERT INTO pacientes (dni, nombre, apellido,telefono,email,fecha_nacimiento) VALUES (?, ?, ?,?,?,?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEmail());
            if (p.getFecha_nacimiento() != null) {

                ps.setDate(6, new java.sql.Date(p.getFecha_nacimiento().getTime()));
            } else {

                ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            }
            ps.executeUpdate();

            System.out.println("Paciente " + p.getNombre() + " guardado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al insertar paciente: " + e.getMessage());
        }
    }

    @Override
    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM pacientes ORDER BY nombre ASC";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Paciente p = new Paciente(
                        rs.getInt("id_paciente"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getDate("fecha_nacimiento")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("ERROR DETALLADO: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }


    public Paciente buscarPorDni(String dniBuscado) {

        String sql = "SELECT * FROM pacientes WHERE CAST(dni AS TEXT) = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setString(1, dniBuscado.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    return new Paciente(
                            rs.getInt("id_paciente"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getDate("fecha_nacimiento")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(" ERROR CRÍTICO EN BUSCAR POR DNI: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void Delete(int idPaciente) {
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            ps.executeUpdate();

            System.out.println("Paciente eliminado. Se borraron sus turnos asociados automáticamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar paciente: " + e.getMessage());
        }
    }
    }



