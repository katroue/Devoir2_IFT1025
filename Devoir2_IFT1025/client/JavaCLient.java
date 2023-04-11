package client;

import java.io.*;

import server.models.Course;
import server.models.RegistrationForm;

import java.util.ArrayList;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */

public class JavaCLient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 1337);

        // OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
        // BufferedWriter writer = new BufferedWriter(os);

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        String cmd = parts[0];

        if (cmd == "CHARGER") {
            String sessionWanted = String.join(" ", Arrays.asList(parts).subList(1, parts.length));

            ObjectInputStream listAvailableCoursesSession = new ObjectInputStream(clientSocket.getInputStream());
            ArrayList<Course> coursesSessionWanted = (ArrayList<Course>) listAvailableCoursesSession.readObject();

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            System.out.println(("Voici les cours disponibles pour la session d'" + sessionWanted));
            coursesSessionWanted.forEach((courses) -> System.out.println("- " + courses.getCode() + "/t" +
                    courses.getName() + "/t"));
        } else if (cmd == "INSCRIRE") {
            FileReader fileCourse = null;
            try {
                fileCourse = new FileReader("data/cour.txt");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);

            }

            BufferedReader courses = new BufferedReader(fileCourse);

            String lineCoursesAvailable;
            while ((lineCoursesAvailable = courses.readLine()) != null) {
                String codeCourse = lineCoursesAvailable.split("\t")[0];
                String nameCourse = Arrays.toString(lineCoursesAvailable.split("\t")[1].split("\t"));
                String sessionCourse = lineCoursesAvailable.split("\t")[2];

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            System.out.println("1. Automne");
            System.out.println("2. Hiver");
            System.out.println("3. Ete");
            int choiceSession = scanner.nextInt();

            OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter writer = new BufferedWriter(os);

            if (choiceSession == 1) {
                String commande = "CHARGER Automne";
                writer.append(commande);
                writer.flush();

            } else if (choiceSession == 2) {
                String commande = "CHARGER Hiver";
                writer.append(commande);
                writer.flush();

            } else if (choiceSession == 3) {
                String commande = "CHARGER Ete";
                writer.append(commande);
                writer.flush();
            }

            ObjectInputStream listCoursesAsked = new ObjectInputStream(clientSocket.getInputStream());
            ArrayList<Course> coursesSessionWanted = (ArrayList<Course>) listCoursesAsked.readObject();

            System.out.println("Les cours offerts pendant la session d'" + choiceSession + "sont:");
            coursesSessionWanted.forEach((course) -> System.out.println("- " + course.getCode() + "/t" +
                    course.getName()));
            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cour");
            int choiceEvent = scanner.nextInt();

            if (choiceEvent == 1) {
                // TO-DO
            } else if (choiceEvent == 2) {
                System.out.println("Veuillez saisir votre prénom: ");
                String surnameStudent = scanner.nextLine();
                System.out.println("Veuillez saisir votre nom: ");
                String nameStudent = scanner.nextLine();
                System.out.println("Veuillez saisir votre email: ");
                String emailStudent = scanner.nextLine();
                System.out.println("Veuillez saisir votre matricule: ");
                String matriculeStudent = scanner.nextLine();
                System.out.println("Veuillez saisir le code du cour: ");
                int codeCourseRegistered = scanner.nextInt();

                for (Course coursesListttt  : coursesSessionWanted) {
                    if (coursesListttt.getCode() == codeCourseRegistered) {
                        RegistrationForm newRegistrationForm = new RegistrationForm(surnameStudent, nameStudent,
                                emailStudent, matriculeStudent, new Course(coursesListttt.getName(),
                                coursesListttt.getCode(), coursesListttt.getSession()));
                    }
                }

                String commande = "INSCRIRE";
                writer.append(commande);
                writer.flush();

                ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
                os.writeObject(newRegistrationForm);
            }
            }
        }
    }
}


