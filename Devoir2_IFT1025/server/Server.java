package server;

import javafx.util.Pair;
import jdk.internal.icu.text.UnicodeSet;
import server.models.Course;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Server {

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

    private void alertHandlers(String cmd, String arg) {
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

    public void handleEvents(String cmd, String arg) {
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
            String codeCourse = line.split("   ")[0];
            String nameCourse = Arrays.toString(line.split("   ")[1].split("   "));
            String sessionCourse = line.split("   ")[2];
            if (sessionCourse == arg) {
                Course newCourse = new Course(nameCourse, codeCourse, sessionCourse);
                listCoursesAsked.writeObject(newCourse);
            }
        }
        listCoursesAsked.close();
    }
        
        /*String line;
        ArrayList<Course> listCoursesFall = new ArrayList<Course>();
        ArrayList<Course> listCoursesWinter = new ArrayList<Course>();
        ArrayList<Course> listCoursesSummer = new ArrayList<Course>();
        
        while (true) {
            try {
                if ((line = courses.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String codeCourse = line.split("   ")[0];
            String nameCourse = Arrays.toString(line.split("   ")[1].split("   "));
            String sessionCourse = line.split("   ")[2];
            
            Course newCourse = new Course(nameCourse, codeCourse, sessionCourse);
            
            if (Objects.equals(sessionCourse, "Automne")) {
                listCoursesFall.add(newCourse);
            }
            else if (Objects.equals(sessionCourse, "Hiver")) {
                listCoursesWinter.add(newCourse);
            }
            else if (Objects.equals(sessionCourse, "Ete")) {
                listCoursesSummer.add(newCourse);
            }
            
        }*/

        /*if (arg == "Fall") {
            listCoursesFall.forEach((cours) -> finalFileGiven.writeObject(cours));
        }
        else if (arg == "Winter") {
            listCoursesWinter.forEach((cours) -> finalFileGiven.writeObject(cours));
        }
        else if (arg == "Summer") {
            listCoursesSummer.forEach((cours) -> finalFileGiven.writeObject(cours));
        }*/



    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gère les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode
    }
}
