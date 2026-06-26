package modelo.dao;

import database.ConexionDB;// Asegurate de usar tu clase real de conexión
import modelo.entidades.AgendaMedica;
import java.sql.*;

public class AgendaDao {

    public AgendaMedica buscarPorProfesionalYDia(int idProfesional, int idDia) {
        String sql = "SELECT hora_inicio, hora_fin, EXTRACT(MINUTE FROM duracion_turno) AS minutos " +
                "FROM agendas_medicas WHERE id_profesional = ? AND id_dia = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProfesional);
            ps.setInt(2, idDia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Time inicio = rs.getTime("hora_inicio");
                    Time fin = rs.getTime("hora_fin");
                    int minutos = rs.getInt("minutos");
                    return new AgendaMedica(inicio, fin, minutos);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en AgendaDao: " + e.getMessage());
        }
        return null; // Si no atiende ese día, devuelve null
    }
}