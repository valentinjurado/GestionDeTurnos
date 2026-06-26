package modelo.dao;

import database.ConexionDB;
import modelo.entidades.Paciente;
import modelo.entidades.PrioridadTurno;
import modelo.entidades.Turno;
import modelo.entidades.Profesional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TurnoDao {

    public void insertar(Turno t) {
        String sql = "INSERT INTO turnos (id_paciente, id_profesional, fecha, hora, prioridad, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getPaciente().getId());
            ps.setInt(2, t.getIdProfesional());
            ps.setDate(3, java.sql.Date.valueOf(t.getFecha()));

            String horaWeb = t.getHora();
            if (horaWeb.length() == 5) {
                horaWeb += ":00";
            }
            ps.setTime(4, java.sql.Time.valueOf(horaWeb));
            ps.setInt(5, Integer.parseInt(t.getPrioridad()));
            ps.setString(6, t.getObservaciones());

            ps.executeUpdate();
            System.out.println("✅ Turno guardado con éxito en PostgreSQL.");

        } catch (SQLException e) {
            System.out.println("❌ ERROR REAL DE POSTGRESQL AL INSERTAR TURNO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Turno> listarTurnosCompletos() {
        List<Turno> lista = new ArrayList<>();
        // Ajustá los nombres de las columnas (id_paciente, id_profesional) según tus tablas reales
        String sql = "SELECT t.*, p.nombre as pac_nom, p.apellido as pac_ape, " +
                "prof.nombre as prof_nom, prof.apellido as prof_ape " +
                "FROM turnos t " +
                "JOIN pacientes p ON t.id_paciente = p.id " +
                "JOIN profesionales prof ON t.id_profesional = prof.id";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Mapeo de Paciente (Usando el constructor que recibe DNI, Nombre, Apellido)
                Paciente pac = new Paciente();
                pac.setId(rs.getInt("id_paciente"));
                pac.setNombre(rs.getString("pac_nom"));
                pac.setApellido(rs.getString("pac_ape"));

                // Mapeo de Profesional
                Profesional prof = new Profesional();
                prof.setId(rs.getInt("id_profesional"));
                prof.setNombre(rs.getString("prof_nom"));
                prof.setApellido(rs.getString("prof_ape"));

                // Creamos el turno seteando las propiedades individualmente para evitar choques de constructores
                Turno t = new Turno();
                t.setIdTurno(rs.getInt("id_turno"));
                t.setPaciente(pac);
                t.setProfesional(prof);
                t.setIdProfesional(rs.getInt("id_profesional"));

                // Convertimos el Date y el Time de SQL a String de manera segura
                t.setFecha(rs.getDate("fecha") != null ? rs.getDate("fecha").toString() : null);
                t.setHora(rs.getTime("hora") != null ? rs.getTime("hora").toString() : null);

                // Mapeamos la prioridad convirtiendo el ordinal del ENUM a String
                int prioridadOrdinal = rs.getInt("prioridad");
                t.setPrioridad(String.valueOf(prioridadOrdinal));

                t.setObservaciones(rs.getString("observaciones"));

                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en listarTurnosCompletos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public void eliminar(int idTurno) {
        String sql = "DELETE FROM turnos WHERE id_turno = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTurno);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Turno ID " + idTurno + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún turno con el ID " + idTurno);
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar turno: " + e.getMessage());
        }
    }
    public List<String> obtenerHorariosOcupados(int idProfesional, String fecha) {
        List<String> ocupados = new ArrayList<>();
        String sql = "SELECT hora FROM turnos WHERE id_profesional = ? AND fecha = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProfesional);
            ps.setDate(2, java.sql.Date.valueOf(fecha)); // Convierte el String "YYYY-MM-DD"

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Time horaSql = rs.getTime("hora");
                    if (horaSql != null) {
                        // Tomamos las primeras 5 letras para que devuelva "09:30" en vez de "09:30:00"
                        String horaFormateada = horaSql.toString().substring(0, 5);
                        ocupados.add(horaFormateada);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener horarios ocupados: " + e.getMessage());
            e.printStackTrace();
        }
        return ocupados; // Devuelve algo como: ["08:00", "11:00"]
    }
}
