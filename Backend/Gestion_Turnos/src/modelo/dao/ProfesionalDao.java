package modelo.dao;
import database.ConexionDB;
import modelo.entidades.Profesional;
import modelo.entidades.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ProfesionalDao {
    public List<Profesional> listarTodos() {
        List<Profesional> lista = new ArrayList<>();
        String sql = "SELECT p.*, e.nombre_especialidad " +
                "FROM profesionales p " +
                "JOIN especialidades e ON p.id_especialidad = e.id_especialidad";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProfesional(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    //  Buscar por ID específico
    public Profesional buscarPorId(int id) {
        String sql = "SELECT p.*, e.nombre_especialidad " +
                "FROM profesionales p " +
                "JOIN especialidades e ON p.id_especialidad = e.id_especialidad " +
                "WHERE p.id_profesional = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProfesional(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //  Filtrar por Especialidad
    public List<Profesional> listarPorEspecialidad(int idEspecialidad) {
        List<Profesional> lista = new ArrayList<>();
        String sql = "SELECT p.*, e.nombre_especialidad " +
                "FROM profesionales p " +
                "JOIN especialidades e ON p.id_especialidad = e.id_especialidad " +
                "WHERE p.id_especialidad = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProfesional(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    private Profesional mapearProfesional(ResultSet rs) throws SQLException {
        Especialidad esp = new Especialidad(
                rs.getInt("id_especialidad"),
                rs.getString("nombre_especialidad")
        );

        Profesional prof = new Profesional();
        prof.setId(rs.getInt("id_profesional"));
        prof.setNombre(rs.getString("nombre"));
        prof.setApellido(rs.getString("apellido"));
        prof.setEspecialidad(esp); 
        return prof;
    }
}
