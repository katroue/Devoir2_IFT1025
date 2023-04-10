package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class main(String [] args) {
    try {
        Socket clientSocket = new Socket("127.0.0.1", 1337);

        OutputStreamWriter os = null;
        try {
            os = new OutputStreamWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter writer = new BufferedWriter(os);
    
         Scanner scanner = new Scanner(System.in);
    
         while(scanner.hasNext()) {
             String line = scanner.nextLine();
             System.out.println(line);

             writer.flush();
         }
         
         writer.close();
     } catch (IOException ex) {
            ex.printStackTrace();
     }
  }
}
