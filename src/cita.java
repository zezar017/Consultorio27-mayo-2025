/**public class cita {
}*/


import java.io.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class cita {
}
class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;
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

class Patient implements Serializable {
    private static final long serialVersionUID = 1L;
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

class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private static final String DB_DIR = "db";
    private static final String ADMINS_FILE = DB_DIR + "/admins.dat";
    private static final String DOCTORS_FILE = DB_DIR + "/doctors.dat";
    private static final String PATIENTS_FILE = DB_DIR + "/patients.dat";
    private static final String APPOINTMENTS_FILE = DB_DIR + "/appointments.dat";

    private List<Admin> admins;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    public Consultorio() {
        createDbDirectory();
        admins = loadData(ADMINS_FILE);
        doctors = loadData(DOCTORS_FILE);
        patients = loadData(PATIENTS_FILE);
        appointments = loadData(APPOINTMENTS_FILE);

        if (admins.isEmpty()) {
            admins.add(new Admin("admin", "1234"));
            saveData(ADMINS_FILE, admins);
        }
        updateNextIds();
    }

    private void createDbDirectory() {
        File dbDir = new File(DB_DIR);
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<T>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error cargando datos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void saveData(String filename, List<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Error guardando datos: " + e.getMessage());
        }
    }

    private void updateNextIds() {
        Doctor.nextId = doctors.stream().mapToInt(Doctor::getId).max().orElse(0) + 1;
        Patient.nextId = patients.stream().mapToInt(Patient::getId).max().orElse(0) + 1;
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
            scanner.nextLine();

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
        return admins.stream()
                .anyMatch(admin -> admin.getUsername().equals(username) &&
                        admin.getPassword().equals(password));
    }

    private void addDoctor(Scanner scanner) {
        System.out.print("Ingrese el nombre del doctor: ");
        String name = scanner.nextLine();
        System.out.print("Ingrese la especialidad: ");
        String specialty = scanner.nextLine();

        Doctor doctor = new Doctor(name, specialty);
        doctors.add(doctor);
        saveData(DOCTORS_FILE, doctors);
        System.out.println("Doctor registrado con éxito. ID: " + doctor.getId());
    }

    private void addPatient(Scanner scanner) {
        System.out.print("Ingrese el nombre del paciente: ");
        String name = scanner.nextLine();

        Patient patient = new Patient(name);
        patients.add(patient);
        saveData(PATIENTS_FILE, patients);
        System.out.println("Paciente registrado con éxito. ID: " + patient.getId());
    }

    private void createAppointment(Scanner scanner) {
        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("Debe registrar al menos un doctor y un paciente.");
            return;
        }

        System.out.println("\nDoctores disponibles:");
        doctors.forEach(doctor ->
                System.out.println("ID: " + doctor.getId() + " | Dr. " +
                        doctor.getName() + " (" + doctor.getSpecialty() + ")"));

        Doctor doctor = selectDoctor(scanner);
        Patient patient = selectPatient(scanner);

        System.out.print("Ingrese fecha (dd/mm/yyyy): ");
        String date = scanner.nextLine();
        System.out.print("Ingrese hora (HH:mm): ");
        String time = scanner.nextLine();

        appointments.add(new Appointment(date, time, doctor, patient));
        saveData(APPOINTMENTS_FILE, appointments);
        System.out.println("Cita programada con éxito.");
    }

    private Doctor selectDoctor(Scanner scanner) {
        Doctor doctor;
        do {
            System.out.print("Seleccione el ID del doctor: ");
            int doctorId = scanner.nextInt();
            scanner.nextLine();
            doctor = doctors.stream()
                    .filter(d -> d.getId() == doctorId)
                    .findFirst()
                    .orElse(null);
            if (doctor == null) {
                System.out.println("Doctor no encontrado. Intente nuevamente.");
            }
        } while (doctor == null);
        return doctor;
    }

    private Patient selectPatient(Scanner scanner) {
        Patient patient;
        System.out.println("\nPacientes disponibles:");
        patients.forEach(p ->
                System.out.println("ID: " + p.getId() + " | " + p.getName()));

        do {
            System.out.print("Seleccione el ID del paciente: ");
            int patientId = scanner.nextInt();
            scanner.nextLine();
            patient = patients.stream()
                    .filter(p -> p.getId() == patientId)
                    .findFirst()
                    .orElse(null);
            if (patient == null) {
                System.out.println("Paciente no encontrado. Intente nuevamente.");
            }
        } while (patient == null);
        return patient;
    }

    private void listDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("No hay doctores registrados.");
            return;
        }
        System.out.println("\nLista de Doctores:");
        doctors.forEach(doctor ->
                System.out.println("ID: " + doctor.getId() + " | Nombre: " +
                        doctor.getName() + " | Especialidad: " + doctor.getSpecialty()));
    }

    private void listPatients() {
        if (patients.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        System.out.println("\nLista de Pacientes:");
        patients.forEach(patient ->
                System.out.println("ID: " + patient.getId() + " | Nombre: " + patient.getName()));
    }

    private void listAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No hay citas programadas.");
            return;
        }
        System.out.println("\nLista de Citas:");
        appointments.forEach(app ->
                System.out.println("Fecha: " + app.getDate() + " | Hora: " + app.getTime() +
                        " | Doctor: " + app.getDoctor().getName() +
                        " | Paciente: " + app.getPatient().getName()));
    }
}