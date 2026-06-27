package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

        private static final String URL = "jdbc:postgresql://dpg-d8oticsvikkc7391ehlg-a.oregon-postgres.render.com:5432/gestion_turnos_md8a";
    private static final String USER = "gestion_turnos_md8a_user";
    private static final String PASS = "3RutP6JaYlrHjS5cQO6lVHbrFYEGaRBB";

        private static Connection conexion = null;

        private ConexionDB() {}

    public static Connection obtenerConexion() {
        try {

            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }
    }