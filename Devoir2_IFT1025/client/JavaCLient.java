package client;

import java.io.*;
import java.net.Socket;
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
        String stepEvent = "Choix de session";

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
                    }
                case "Inscription":
                    System.out.println("\nVeuillez saisir votre prénom: ");
                    String surnameStudent = scanner.nextLine();
                    System.out.println("\nVeuillez saisir votre nom: ");
                    String nameStudent = scanner.nextLine();
                    System.out.println("\nVeuillez saisir votre email: ");
                    String emailStudent = scanner.nextLine();
                    System.out.println("\nVeuillez saisir votre matricule: ");
                    String matriculeStudent = scanner.nextLine();
                    System.out.println("\nVeuillez saisir le code du cour: ");
                    String codeCourseRegistered = scanner.nextLine();

                    String chargement = "CHARGER";
                    oos.writeObject(chargement);
                    oos.flush();

                    Object coursesObjectInscription = (Object) ois.readObject();
                    ArrayList<ArrayList<String>> coursesInscription = (ArrayList<ArrayList<String>>) coursesObjectInscription;

                    for (int i = 0; i < coursesInscription.size(); i++) {
                        if (codeCourseRegistered == coursesInscription.get(i).get(1)) {
                            String courseRegistrationName = coursesInscription.get(i).get(0);
                            String courseRegistrationCode = coursesInscription.get(i).get(1);
                            String courseRegistrationSession = coursesInscription.get(i).get(2);
                            RegistrationForm newRegistrationForm = new RegistrationForm(surnameStudent, nameStudent,
                                    emailStudent, matriculeStudent, new Course(courseRegistrationName,
                                    courseRegistrationCode, courseRegistrationSession));
                            String commande = "INSCRIRE";
                            oos.writeObject(commande);
                            oos.flush();

                            oos.writeObject(newRegistrationForm);
                            oos.flush();
                            System.out.println("Félicitation! Inscription réussie de " + surnameStudent + " au cours " +
                                    courseRegistrationCode);

                            // Fermeture de la connection
                            //caRoule = false;

                        } else {
                            System.out.println("Ce cour ne se trouve pas dans la liste de cour offert.");
                        }
                    }
            }
        }
}



