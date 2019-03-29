import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class Server {

    private ServerSocket serverSocket;
    private int sessionID;

    private Server(int port){
        try {
            System.out.println("Waiting for connection...");

            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that generates session IDs in server (range from 1 to 7)
    private void generate_sessionID(){

        Random random = new Random();
        sessionID = random.nextInt(7) + 1;
    }


    private void start(){

        generate_sessionID();

        Client client1 = new Client(serverSocket, sessionID);

        Thread t1 = new Thread(client1);

        t1.start();

        t1.interrupt();

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.setProperty("line.separator","");
        Server server = new Server(1234);
        server.start();
    }
}
