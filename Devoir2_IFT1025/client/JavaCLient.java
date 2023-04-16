package client;

import java.io.*;
import java.net.Socket;
import server.models.Course;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class JavaCLient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        // On se connecte au serveur
        Socket clientSocket = new Socket("127.0.0.1", 1337);
        System.out.println("Ca roule!");

        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

        // Pour pouvoir se déplacer facilement dans les étapes d'inscription
        String stepEvent = null;
        ArrayList<String> infoStudent = new ArrayList<>();
        //boolean caRoule = true;
        //while (caRoule) {

        // Message d'ouverture du portail d'inscription
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1. Automne\n2. Hiver\n3. Ete");

        String choiceSession = scanner.nextLine();

        // Choix de la session et envoi de la commande d'action de chargement de la liste des cours
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
        ArrayList<server.models.Course> coursesObject = (ArrayList<server.models.Course>) ois.readObject();
        coursesObject.forEach((courses)->System.out.println(courses.getCode()));
        switch (Objects.requireNonNull(stepEvent)) {
                // On montre les cours pour la session choisie et on demande de choisir la prochaine action
            case "Choix d'action":
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
                System.out.println("Veuillez saisir votre prénom: ");
                scanner.nextLine();
                String surnameStudent = scanner.nextLine();
                while (surnameStudent.matches(".*\\d.*")) { // On attend que l'entrée soit valide
                    System.out.println("Le prénom ne peut pas contenir un nombre");
                }
                infoStudent.add(surnameStudent);
         
                System.out.println("Veuillez saisir votre nom: ");
                String nameStudent = scanner.nextLine();
                while (nameStudent.matches(".*\\d.*")) { // On attend que l'entrée soit valide
                    System.out.println("Le nom ne peut pas contenir un nombre");
                }
                infoStudent.add(nameStudent);
                
                System.out.println("Veuillez saisir votre email: ");
                String emailStudent = scanner.nextLine();
                while (emailStudent.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") == false) {
                    System.out.println("Veuillez rentrer un email valide");
                }
                infoStudent.add(emailStudent);
                
                System.out.println("Veuillez saisir votre matricule: ");
                String matriculeStudent = scanner.nextLine();
                while (matriculeStudent.matches("[0-9]{2,}") == false) {
                    System.out.println("Veuillez rentrer un matricule valide");
                   
                }
                infoStudent.add(matriculeStudent);
                
                System.out.println("Veuillez saisir le code du cour: ");
                String codeCourseRegistered = scanner.nextLine();
                while (codeCourseRegistered.matches("[A-Z]{3}[0-9]{4}") == false) {
                    System.out.println("Veuillez rentrer un code de cour valide");
                }
                infoStudent.add(codeCourseRegistered);
        }
        for (int i = 0; i < coursesObject.size(); i++) {
            if (Objects.equals(infoStudent.get(4), coursesObject.get(i).getCode())) {
                String courseRegistrationName = coursesObject.get(i).getName();
                String courseRegistrationCode = coursesObject.get(i).getCode();
                String courseRegistrationSession = coursesObject.get(i).getSession();
                String newRegistrationForm = infoStudent.get(0) + " " + infoStudent.get(1) + " " + infoStudent.get(2) +
                " " + infoStudent.get(3) + " " + courseRegistrationName + " " + courseRegistrationCode + " " +
                        courseRegistrationSession;

                // On envoie la commande qu'on veut l'action d'inscrire
                String commande = "INSCRIRE arg";
                oos.writeObject(commande);
                oos.flush();

                // On envoit le formulaire d'inscription rempli
                oos.writeObject(newRegistrationForm);
                oos.flush();
                System.out.println("Félicitation! Inscription réussie de " + infoStudent.get(0) + " au cours " +
                                    courseRegistrationCode);

            }
        }
    }
}



