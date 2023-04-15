package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class JavaCLient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //clientFx cfx = new clientFx();
        //cfx.launch(args);
        task2Launcher();
}


public static void task2Launcher ()throws IOException, ClassNotFoundException {
    Scanner scanner = new Scanner(System.in);

    // On se connecte au serveur
    Socket clientSocket = new Socket("127.0.0.1", 1337);
    System.out.println("Ca roule!");

    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

    // Pour pouvoir se déplacer facilement dans les étapes d'inscription
    String stepEvent = "Choix de session";
    ArrayList<String> infoStudent = new ArrayList<>();
    //boolean caRoule = true;
    //while (caRoule) {
    switch (stepEvent) {
        // Choix de la session
        case "Choix de session":
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            System.out.println("1. Automne\n2. Hiver\n3. Ete");

            String choiceSession = scanner.nextLine();

            if (Objects.equals(choiceSession, "1")) {
                String commande = "CHARGER Automne";
                oos.writeObject(commande);
                oos.flush();
                stepEvent = "Choix d'action";
            } else if (Objects.equals(choiceSession, "2")) {
                String commande = "CHARGER Hiver";
                oos.writeObject(commande);
                oos.flush();
                stepEvent = "Choix d'action";
            } else if (Objects.equals(choiceSession, "3")) {
                String commande = "CHARGER Ete";
                oos.writeObject(commande);
                oos.flush();
                stepEvent = "Choix d'action";
            } else {
                System.out.println("Cette option n'est pas disponible");
            }

            // On montre les cours pour la session choisie et on demande de choisir la prochaine action
        case "Choix d'action":
            ArrayList<server.models.Course> coursesObject = (ArrayList<server.models.Course>) ois.readObject();

            System.out.println("Les cours offerts pendant pour cette session sont:");
            coursesObject.forEach((course) -> System.out.println("- " + course.getName() + "\t" +
                    course.getCode()));
            System.out.println("1. Consulter les cours offerts pour une autre session\n2. Inscription à un cour");

            int choiceEvent = scanner.nextInt();

            if (choiceEvent == 1) {
                stepEvent = "Choix de session";

            } else if (choiceEvent == 2) {
                stepEvent = "Inscription";
            } else {
                System.out.println("Veuillez entrer un option valide.");
                stepEvent = "Choix d'action";
            }
            // On obtient les informations de l'étudiant pour l'inscription au cours
        case "Inscription":

            int questions =1;
            switch (questions) {
                case 1:
                    System.out.println("Veuillez saisir votre prénom: ");
                    String surnameStudent = scanner.nextLine();
                    if (surnameStudent.matches(".*\\d.*")) {
                        System.out.println("Le prénom ne peut pas contenir un nombre");
                        questions = 1;
                        break;
                    } else {
                        infoStudent.add(surnameStudent);
                        questions = 2;
                        break;
                    }
                case 2:
                    System.out.println("Veuillez saisir votre nom: ");
                    String nameStudent = scanner.nextLine();
                    if (nameStudent.matches(".*\\d.*")) {
                        System.out.println("Le nom ne peut pas contenir un nombre");
                        questions = 2;
                        break;
                    } else {
                        infoStudent.add(nameStudent);
                        questions = 3;
                        break;
                    }
                case 3:
                    System.out.println("Veuillez saisir votre email: ");
                    String emailStudent = scanner.nextLine();
                    if (emailStudent.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
                        infoStudent.add(emailStudent);
                        questions = 4;
                    } else {
                        System.out.println("Veuillez rentrer un email valide");
                        questions = 3;
                        break;
                    }
                case 4:
                    System.out.println("Veuillez saisir votre matricule: ");
                    String matriculeStudent = scanner.nextLine();
                case 5:
                    System.out.println("Veuillez saisir le code du cour: ");
                    String codeCourseRegistered = scanner.nextLine();
            }
            String chargement = "CHARGER";
            oos.writeObject(chargement);
            oos.flush();

            ArrayList<server.models.Course> coursesInscription = (ArrayList<server.models.Course>) ois.readObject();

            for (int i = 0; i < coursesInscription.size(); i++) {
                if (infoStudent.get(4) == coursesInscription.get(i).getCode()) {
                    String courseRegistrationName = coursesInscription.get(i).getName();
                    String courseRegistrationCode = coursesInscription.get(i).getCode();
                    String courseRegistrationSession = coursesInscription.get(i).getSession();
                    RegistrationForm newRegistrationForm = new RegistrationForm(infoStudent.get(0), infoStudent.get(1),
                            infoStudent.get(2), infoStudent.get(3), new Course(courseRegistrationName,
                            courseRegistrationCode, courseRegistrationSession));

                    String commande = "INSCRIRE";
                    oos.writeObject(commande);
                    oos.flush();

                    oos.writeObject(newRegistrationForm);
                    oos.flush();
                    System.out.println("Félicitation! Inscription réussie de " + infoStudent.get(0) + " au cours " +
                            courseRegistrationCode);

                } else {
                    System.out.println("Ce cour ne se trouve pas dans la liste de cour offert.");
                }
            }
            break;
        default:
            throw new IllegalStateException("Unexpected value: " + stepEvent);
    }
}
}
