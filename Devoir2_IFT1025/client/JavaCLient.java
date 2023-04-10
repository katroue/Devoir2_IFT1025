package client;

import java.io.IOException;
import java.net.Socket;

public class main(String [] args) {
    try {Socket clientSocket = new Socket("127.0.0.1", 1337);
    
         OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
    
         BufferedWirter writer = new BufferedWriter(os);
    
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
