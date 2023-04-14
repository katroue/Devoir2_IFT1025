package client;

import java.io.*;
import client.Course;
import client.RegistrationForm;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaCLient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 1337);

        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

        Scanner scanner = new Scanner(System.in);

        // Pour pouvoir revenir en arrière facilement
        int stepEvent = 1;

        switch (stepEvent) {
            case 1:
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            System.out.println("1. Automne/n2. Hiver/n3. Ete");

            int choiceSession = scanner.nextInt();

            if (choiceSession == 1) {
                String commande = "CHARGER Automne";
                oos.writeObject(commande);
                oos.flush();

            } else if (choiceSession == 2) {
                String commande = "CHARGER Hiver";
                oos.writeObject(commande);
                oos.flush();

            } else if (choiceSession == 3) {
                String commande = "CHARGER Ete";
                oos.writeObject(commande);
                oos.flush();
            }
            stepEvent = 2;
            break;

            case 2:
            Object coursesObject = (Object) ois.readObject();
            ArrayList<ArrayList<String>> coursesSessionWanted = (ArrayList<ArrayList<String>>) coursesObject;

            System.out.println("Les cours offerts pendant pour cette session sont:");
            coursesSessionWanted.forEach((course) -> System.out.println("- " + course.get(0) + "/t" +
                    course.get(1)));

            System.out.println("1. Consulter les cours offerts pour une autre session/n2. Inscription à un cour");

            int choiceEvent = scanner.nextInt();

            if (choiceEvent == 1) {
                stepEvent = 1;

            } else if (choiceEvent == 2) {
                stepEvent = 3;
            }
            case 3:
                System.out.println("/nVeuillez saisir votre prénom: ");
                String surnameStudent = scanner.nextLine();
                System.out.println("/nVeuillez saisir votre nom: ");
                String nameStudent = scanner.nextLine();
                System.out.println("/nVeuillez saisir votre email: ");
                String emailStudent = scanner.nextLine();
                System.out.println("/nVeuillez saisir votre matricule: ");
                String matriculeStudent = scanner.nextLine();
                System.out.println("/nVeuillez saisir le code du cour: ");
                String codeCourseRegistered = String.valueOf(scanner.nextInt());

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
                        
                    } else {
                        System.out.println("Ce cour ne se trouve pas dans la liste de cour offert.");
                    }
                }
        }
    }
}


