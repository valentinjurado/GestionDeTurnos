package modelo.dao;
import database.ConexionDB;
import modelo.entidades.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class EspecialidadDao {
    public List<Especialidad> listarTodas() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM especialidades ORDER BY nombre_especialidad ASC";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Especialidad(
                        rs.getInt("id_especialidad"),
                        rs.getString("nombre_especialidad")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e.getMessage());
        }
        return lista;
    }
}
