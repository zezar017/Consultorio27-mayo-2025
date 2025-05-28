import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Admin {
    private String username;
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

class Doctor {
    private int id;
    private String name;
    private String specialty;
    private static int nextId = 1;

    public Doctor(String name, String specialty) {
        this.id = nextId++;
        this.name = name;
        this.specialty = specialty;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
}

class Patient {
    private int id;
    private String name;
    private static int nextId = 1;

    public Patient(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}

class Appointment {
    private String date;
    private String time;
    private Doctor doctor;
    private Patient patient;

    public Appointment(String date, String time, Doctor doctor, Patient patient) {
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
}

public class Consultorio {
    private List<Admin> admins;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    public Consultorio() {
        admins = new ArrayList<>();
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new ArrayList<>();
        admins.add(new Admin("admin", "1234")); // Admin predeterminado
    }

    public static void main(String[] args) {
        Consultorio consultorio = new Consultorio();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bienvenido al programa de Administración de Citas");
        System.out.println("*****Iniciar sesión*****");

        boolean isLoggedIn = false;
        while (!isLoggedIn) {
            System.out.print("Usuario: ");
            String usuario = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            isLoggedIn = consultorio.login(usuario, password);
            if (!isLoggedIn) {
                System.out.println("Credenciales incorrectas. Intente nuevamente.");
            }
        }

        boolean exit = false;
        while (!exit) {
            System.out.println("\nMenú Principal:");
            System.out.println("1. Dar de alta doctor");
            System.out.println("2. Dar de alta paciente");
            System.out.println("3. Crear cita");
            System.out.println("4. Listar doctores");
            System.out.println("5. Listar pacientes");
            System.out.println("6. Listar citas");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    consultorio.addDoctor(scanner);
                    break;
                case 2:
                    consultorio.addPatient(scanner);
                    break;
                case 3:
                    consultorio.createAppointment(scanner);
                    break;
                case 4:
                    consultorio.listDoctors();
                    break;
                case 5:
                    consultorio.listPatients();
                    break;
                case 6:
                    consultorio.listAppointments();
                    break;
                case 7:
                    exit = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
    }

    private boolean login(String username, String password) {
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void addDoctor(Scanner scanner) {
        System.out.print("Ingrese el nombre del doctor: ");
        String name = scanner.nextLine();
        System.out.print("Ingrese la especialidad: ");
        String specialty = scanner.nextLine();

        doctors.add(new Doctor(name, specialty));
        System.out.println("Doctor registrado con éxito. ID: " + doctors.get(doctors.size()-1).getId());
    }

    private void addPatient(Scanner scanner) {
        System.out.print("Ingrese el nombre del paciente: ");
        String name = scanner.nextLine();

        patients.add(new Patient(name));
        System.out.println("Paciente registrado con éxito. ID: " + patients.get(patients.size()-1).getId());
    }

    private void createAppointment(Scanner scanner) {
        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("Debe registrar al menos un doctor y un paciente.");
            return;
        }

        System.out.println("\nDoctores disponibles:");
        for (Doctor doctor : doctors) {
            System.out.println("ID: " + doctor.getId() + " | Dr. " + doctor.getName() + " (" + doctor.getSpecialty() + ")");
        }

        int doctorId;
        do {
            System.out.print("Seleccione el ID del doctor: ");
            doctorId = scanner.nextInt();
            if (getDoctorById(doctorId) == null) {
                System.out.println("Doctor no encontrado. Intente nuevamente.");
            }
        } while (getDoctorById(doctorId) == null);

        System.out.println("\nPacientes disponibles:");
        for (Patient patient : patients) {
            System.out.println("ID: " + patient.getId() + " | " + patient.getName());
        }

        int patientId;
        do {
            System.out.print("Seleccione el ID del paciente: ");
            patientId = scanner.nextInt();
            if (getPatientById(patientId) == null) {
                System.out.println("Paciente no encontrado. Intente nuevamente.");
            }
        } while (getPatientById(patientId) == null);

        scanner.nextLine(); // Limpiar buffer

        System.out.print("Ingrese fecha (dd/mm/yyyy): ");
        String date = scanner.nextLine();
        System.out.print("Ingrese hora (HH:mm): ");
        String time = scanner.nextLine();

        Doctor selectedDoctor = getDoctorById(doctorId);
        Patient selectedPatient = getPatientById(patientId);

        appointments.add(new Appointment(date, time, selectedDoctor, selectedPatient));
        System.out.println("Cita programada con éxito.");
    }

    private Doctor getDoctorById(int id) {
        for (Doctor doctor : doctors) {
            if (doctor.getId() == id) return doctor;
        }
        return null;
    }

    private Patient getPatientById(int id) {
        for (Patient patient : patients) {
            if (patient.getId() == id) return patient;
        }
        return null;
    }

    private void listDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("No hay doctores registrados.");
            return;
        }
        System.out.println("\nLista de Doctores:");
        for (Doctor doctor : doctors) {
            System.out.println("ID: " + doctor.getId() + " | Nombre: " + doctor.getName() + " | Especialidad: " + doctor.getSpecialty());
        }
    }

    private void listPatients() {
        if (patients.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        System.out.println("\nLista de Pacientes:");
        for (Patient patient : patients) {
            System.out.println("ID: " + patient.getId() + " | Nombre: " + patient.getName());
        }
    }

    private void listAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No hay citas programadas.");
            return;
        }
        System.out.println("\nLista de Citas:");
        int count = 1;
        for (Appointment appointment : appointments) {
            System.out.println(count + ". Fecha: " + appointment.getDate() + " | Hora: " + appointment.getTime() +
                    " | Doctor: " + appointment.getDoctor().getName() +
                    " | Paciente: " + appointment.getPatient().getName());
            count++;
        }
    }
}
