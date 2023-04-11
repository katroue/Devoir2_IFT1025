package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import server.models.Course;
import java.util.ArrayList;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */

public class JavaCLient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 1337);

        //OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
        //BufferedWriter writer = new BufferedWriter(os);

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String sessionWanted = String.join(" ", Arrays.asList(parts).subList(1, parts.length));

        if (cmd == "CHARGER") {
            ObjectInputStream listAvailableCoursesSession = new ObjectInputStream(clientSocket.getInputStream());
            ArrayList<Course> coursesSessionWanted = (ArrayList<Course>) listAvailableCoursesSession.readObject();
            for (int i=0; i<coursesSessionWanted.length; i++) {
                courseName[i] = coursesSessionWanted[i].getName();
            }
            //Course coursesSessionWanted = (Course) listAvailableCoursesSession.readObject();
            String courseName = coursesSessionWanted.getName();

            System.out.println("*** Bienvenue au portail d'inscription de cours de l<UDEM ***");
            System.out.println(("Voici les cours disponibles pour la session d'" + sessionWanted));
            coursesSessionWanted.forEach((courses) -> System.out.println("- " + courseName));
        }
    }
}


