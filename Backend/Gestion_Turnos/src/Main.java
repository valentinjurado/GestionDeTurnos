import modelo.dao.*;
import modelo.entidades.*; // Asegurate de que el path sea correcto
import java.util.Scanner;
import java.util.List;

public class Main {
   /* // Definimos los DAOs y el Scanner como estáticos para usarlos en los métodos
    private static Scanner sc = new Scanner(System.in);
    private static PacienteDao pacDao = new PacienteDao();
    private static TurnoDao turDao = new TurnoDao();
    private static ProfesionalDao profDao = new ProfesionalDao();

    public static void main(String[] args) {
        int opcion = 0;

        do {
            System.out.println("\n========= SISTEMA DE TURNOS - UNICEN =========");
            System.out.println("1. Registrar nuevo Paciente");
            System.out.println("2. Ver lista de Pacientes");
            System.out.println("3. Agendar un Turno");
            System.out.println("4. Ver Agenda de Turnos");
            System.out.println("5. Eliminar un Paciente (Borrado en Cascada)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextInt();
            sc.nextLine(); // Limpieza de buffer

            switch (opcion) {
                case 1: registrarPaciente(); break;
                case 2: listarPacientes(); break;
                case 3: agendarTurno(); break;
                case 4: listarTurnos(); break;
                case 5: eliminarPaciente(); break;
                case 0: System.out.println("Saliendo del sistema..."); break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // --- Métodos de Acción ---

    private static void registrarPaciente() {
        System.out.println("\n--- Nuevo Paciente ---");
        System.out.print("Nombre: "); String nom = sc.nextLine();
        System.out.print("Apellido: "); String ape = sc.nextLine();
        System.out.print("DNI: "); String dni = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();

        Paciente p = new Paciente(nom, dni, ape, "2494...", email, new java.util.Date());
        pacDao.insertar(p);
    }

    private static void listarPacientes() {
        System.out.println("\n--- Lista de Pacientes en DB ---");
        List<Paciente> lista = pacDao.listarTodos();
        for (Paciente p : lista) {
            System.out.println("ID: " + p.getId() + " | " + p.getApellido() + ", " + p.getNombre() + " (DNI: " + p.getDni() + ")");
        }
    }}

    /*private static void agendarTurno() {
        System.out.println("\n--- Agendar Turno ---");
        System.out.print("ID del Paciente: "); int idPac = sc.nextInt();
        System.out.println("Profesionales Disponibles:");
        List<Profesional> staff = profDao.listarTodos();
        for(Profesional p : staff) {
            System.out.println(p.getId() + ": " + p.getApellido() + " (" + p.getEspecialidad().getNombreEspecialidad() + ")");
        }

        System.out.print("\nIngrese el ID del Profesional elegido: ");
        int idProf = sc.nextInt();
        System.out.println("Prioridad (0: Rutina, 1: Urgente, 2: Emergencia): ");
        int prio = sc.nextInt();
        sc.nextLine(); // Limpiar buffer
        System.out.print("Observaciones: "); String obs = sc.nextLine();

        // Recuperamos los objetos mínimos para el turno
        Paciente p = new Paciente(); p.setId(idPac);
        Profesional prof = new Profesional(); prof.setId(idProf);

        Turno t = new Turno(0, p, prof,
                new java.sql.Date(System.currentTimeMillis()),
                new java.sql.Time(System.currentTimeMillis()),
                PrioridadTurno.values()[prio], obs);

        turDao.insertar(t);
    }

    private static void listarTurnos() {
        System.out.println("\n--- Agenda Completa ---");
        List<Turno> lista = turDao.listarTurnosCompletos();
        for (Turno t : lista) {
            System.out.println("Fecha: " + t.getFecha() + " | Paciente: " + t.getPaciente().getApellido() +
                    " | Médico: " + t.getProfesional().getApellido() +
                    " | PRIORIDAD: " + t.getPrioridad());
        }
    }

    private static void eliminarPaciente() {
        System.out.print("\nIngrese el ID del paciente a eliminar: ");
        int id = sc.nextInt();
        pacDao.Delete(id);
    }*/
}