package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // 1. Atributos para la configuración

    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_turnos_db";
    private static final String USER = "postgres";
    private static final String PASS = "admin123";

    // 2. La única instancia de la conexión
    private static Connection conexion = null;

    // 3. Constructor privado: NADIE puede hacer 'new ConexionDB()' desde fuera
    private ConexionDB() {}

    // 4. El método que entrega la conexión
    public static Connection obtenerConexion() {
        try {
            // Si la conexión no existe o se cerró, la creamos
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("--- Nueva conexión establecida con PostgreSQL ---");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
}