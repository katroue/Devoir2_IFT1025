package server;

import java.io.*;
import server.models.RegistrationForm;
import server.models.Course;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractMap;
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

    /**
     * A constructor that sets the connection to the client to the port given in argument, that initiate a list of
     * event, and when the event is added to the list, we throw it the to handleEvents method to run it.
     * @param port the port used to communicate with the client
     * @throws IOException throws an exception if the input of the port is different from the client's port
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * A method that add the current event to the list of events.
     * @param h the event to be added to the list of events
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * For every event in the list of events, the functional interface EventHandler is called with the parameters given
     * in argument.
     * @param cmd the action to be taken between charging the courses available for a certain session or to register
     *            the student in a course
     * @param arg the session the client wishes to view
     * @throws IOException throws an exception if there is nothing for the method handle to handle
     * @throws ClassNotFoundException
     */
    private void alertHandlers(String cmd, String arg) throws ClassNotFoundException, IOException {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * The method waits for a client to get connected, then send a message to confirm the connection. It then initiates
     * the input and the output stream. It then listens to the input the client sends, and when the client is done, it
     * disconnects. The method then sends a disconnection confirmation message.
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
            AbstractMap.SimpleEntry<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * The command sent to the method is separated into parts, the separator being a space. It then creates a Pair with
     * the
     * @param line the command to be processed
     * @return return a pair of the command and the argument
     */
    public AbstractMap.SimpleEntry<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new AbstractMap.SimpleEntry<>(cmd, args);
    }

    /**
     * The receiver and the sender of information are close and the connection with the client is cut.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     *
     * @param cmd the action that determines the event
     * @param arg the session the client wishes to view if the action is the load the courses
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
     @throws IOException
     */
    public void handleLoadCourses(String arg) throws IOException {

        FileReader fileCourse = null;
        try {
            File newFile = new File("Devoir2_IFT1025/server/data/cour.txt");
            fileCourse = new FileReader(newFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }

        BufferedReader courses = new BufferedReader(fileCourse);

        ArrayList<Course> listAllCourses = new ArrayList<>();

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
        this.objectOutputStream.writeObject(listAllCourses);
        this.objectOutputStream.flush();
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gère les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     @throws IOException
     @throws ClassNotFoundException
     */
    public void handleRegistration() throws IOException, ClassNotFoundException {
        String newRegistration = (String) this.objectInputStream.readObject();

        String[] partsInfoStudent = newRegistration.split(" ");
        String surnameStudent = partsInfoStudent[0];
        String lastNameStudent = partsInfoStudent[1];
        String emailStudent = partsInfoStudent[2];
        String matriculeStudent = partsInfoStudent[3];

        File inscriptionFile = new File("/Users/katherinedemers/Downloads/GitHub/Devoir2_IFT1025/server/data/inscription.txt");
        FileWriter inscriptionList = new FileWriter(inscriptionFile);

        BufferedWriter inscriptionsUpdated = new BufferedWriter(inscriptionList);

        String nouvelleLigneInscription = partsInfoStudent[5] + "\t" + partsInfoStudent[6] + "\t" +
                matriculeStudent + "\t" + lastNameStudent + "\t" + surnameStudent + "\t" + emailStudent;

        inscriptionsUpdated.append(nouvelleLigneInscription);

        inscriptionsUpdated.close();
    }
}
