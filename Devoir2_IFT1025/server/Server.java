package server;

import java.io.*;
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

    /**
     *
     * @param port
     * @throws IOException
     */

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     *
     * @param h
     */

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     *
     * @param cmd
     * @param arg
     * @throws IOException
     */

    private void alertHandlers(String cmd, String arg) throws IOException {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     *
     */

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

    /**
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     *
     * @param line
     * @return
     */

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     *
     * @param cmd
     * @param arg
     * @throws IOException
     */

    public void handleEvents(String cmd, String arg) throws IOException, ClassNotFoundException {
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
            fileCourse = new FileReader("data/cour.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }

        BufferedReader courses = new BufferedReader(fileCourse);

        ArrayList<Course> listAllCourses = new ArrayList<>();
        
        FileOutputStream fos = new FileOutputStream("coursesData");
        ObjectOutputStream listCoursesAsked = new ObjectOutputStream(fos);

        String line;
        while ((line = courses.readLine()) != null) {
            String codeCourse = line.split("\t")[0];
            String nameCourse = Arrays.toString(line.split("\t")[1].split("\t"));
            String sessionCourse = line.split("\t")[2];
            
            Course newCourse = new Course(nameCourse, codeCourse, sessionCourse);

            if (Objects.equals(sessionCourse, arg)) {
                listAllCourses.add(newCourse);
            }
        }
        listCoursesAsked.writeObject(listAllCourses);
        
        listCoursesAsked.close();
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gère les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() throws IOException, ClassNotFoundException {
        ObjectInputStream obForm = new ObjectInputStream(client.getInputStream());
        RegistrationForm newRegistration = (RegistrationForm) obForm.readObject();

        String surnameStudent = newRegistration.getPrenom();
        String lastNameStudent = newRegistration.getNom();
        String emailStudent = newRegistration.getEmail();
        String matriculeStudent = newRegistration.getMatricule();
        String courseWantedName = newRegistration.getCourse().getName();
        String courseWantedCode = newRegistration.getCourse().getCode();

        FileWriter inscriptionList = new FileWriter("data/inscription.txt");

        BufferedWriter inscriptionsUpdated = new BufferedWriter(inscriptionList);

        String nouvelleLigneInscription = courseWantedName + "\t" + courseWantedCode + "\t" +
                matriculeStudent + "\t" + lastNameStudent + "\t" + surnameStudent + "\t" + emailStudent;

        inscriptionsUpdated.append(nouvelleLigneInscription);

        inscriptionsUpdated.close();
    }
}
