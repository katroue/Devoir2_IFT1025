package server;

import javafx.util.Pair;
import jdk.internal.icu.text.UnicodeSet;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Server<Pair> {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) throws IOException {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) throws IOException {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transforme en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) throws IOException {
        FileReader fileCourse = null;
        try {
            fileCourse = new FileReader("data/cour.txt"); // je suis pas sur comment appelé le fichier cour
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }

        BufferedReader courses = new BufferedReader(fileCourse);

        ArrayList<Course> listAllCourses = new ArrayList<>();

        ObjectOutputStream listCoursesAsked = new ObjectOutputStream(listAllCourses);

        String line;
        while ((line = courses.readLine()) != null) {
            String codeCourse = line.split("\t")[0];
            String nameCourse = Arrays.toString(line.split("\t")[1].split("\t"));
            String sessionCourse = line.split("\t")[2];
            Course newCourse = new Course(nameCourse, codeCourse, sessionCourse);

            if (Objects.equals(sessionCourse, arg)) {
                listCoursesAsked.writeObject(newCourse);
            }
        }
        listCoursesAsked.close();
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gère les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() throws IOException {
            InputStream RegistrationForm = null;
            ObjectInputStream newRegistration = new ObjectInputStream(RegistrationForm);

        String surnameStudent = newRegistration.getPrenom();
        String lastNameStudent = newRegistration.getName();
        String emailStudent = newRegistration.getEmail();
        String matriculeStudent = newRegistration.getMatricule();
        String courseWantedName = newRegistration.getCourse();

        FileReader fileCourse = null;
        try {
            fileCourse = new FileReader("data/cour.txt"); // je suis pas sur comment appelé le fichier cour
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader courses = new BufferedReader(fileCourse);

        String lineCourses;
        String courseWantedCode = null;
        String courseWantedSession = null;

        while ((lineCourses = courses.readLine()) != null) {
            String codeCourseAvailable = lineCourses.split("\t")[0];
            String nameCourseAvailable = Arrays.toString(lineCourses.split("\t")[1].split("\t"));
            String sessionCourseAvailable = lineCourses.split("\t")[2];
            if (Objects.equals(courseWantedName, nameCourseAvailable)) {
                courseWantedCode = codeCourseAvailable;
                courseWantedSession = sessionCourseAvailable;
            }
        }

        FileWriter inscriptions = new FileWriter("data/inscription.txt");

        BufferedWriter inscriptionsUpdated = new BufferedWriter(inscriptions);

        String nouvelleLigneInscription = courseWantedSession + "\t" + courseWantedCode + "\t" +
                matriculeStudent + "\t" + lastNameStudent + "\t" + surnameStudent + "\t" + emailStudent;

        inscriptionsUpdated.append(nouvelleLigneInscription);

        inscriptionsUpdated.close();
    }
}
