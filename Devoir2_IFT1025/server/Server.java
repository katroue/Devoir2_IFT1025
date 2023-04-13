package server;

import java.io.*;
import server.models.Course;
import server.models.RegistrationForm;
import javafx.util;
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
     * A constructor that sets the connection to the client to the port given in argument, that initiate a list of
     * event, and when the event is added to the list, we throw it the to handleEvents method to run it.
     * @param port
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * A method that add the current event to the list of events.
     * @param h
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * For every event in the list of events, the functional interface EventHandler is called with the parameters given
     * in argument.
     * @param cmd
     * @param arg
     * @throws IOException
     */
    private void alertHandlers(String cmd, String arg) throws IOException, ClassNotFoundException {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * The method waits for a client to get connected, then send a message to confirm the connection. It then initiates
     * the input and the output stream. It then listen to the input the client sends, when the client is done, it
     * disconnects. The method with sending a disconnection confirmation message.
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
     * The method waits for a command to be received from the client. It then reads it and converts it to a String. This
     * command is then sent to the method processCommandLine.
     * The command is ten separated the first word of the command to be the action to take and the second word being the
     * session the client wants to see the available courses of. It then sends those two separate arguments to the method
     * alertHandlers.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * The command sent to the method is separated into parts, the separator being a space. It then creates a Pair with
     * the
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
