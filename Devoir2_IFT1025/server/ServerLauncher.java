package server;

public class ServerLauncher {
    public final static int PORT = 1337;

    /**
     * The server connects itself to the port, sends a message to confirm the connection and then go to the class server
     * to run.
     * @param args gets the args from the command line
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
