import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class ClientConnect implements Runnable{

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean condition = true;

    ClientConnect(ServerSocket serverSocket, int sessionID){
        try {
            Socket clientSocket;
            clientSocket = serverSocket.accept();

            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Client with ID " + sessionID + " connected.");

            sendPacket("Y","R", sessionID,0.0,0.0); // initialization packet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that generates a packet in server - text-based coding
    private String generatePacket(String operation, String status, int sessionID, double number_1, double number_2)
    {
        //            EXAMPLE OF PACKET SEND BY SERVER
        // #1:O=A+#2:S=R+#3:I=1+#4:T=1544626488+#5:K=8.0+#6:L=0.0+

        //                 DESCRIPTION OF PACKET
        // O - operation field
        // S - status field
        // I - session ID field
        // T - time stamp field
        // K - first number field
        // L - second number field

        int number = 1;

        String packet = "";
        packet += "#" + number + ":O=" + operation + "+";
        number++;
        packet += "#" + number + ":S=" + status + "+";
        number++;
        packet += "#" + number + ":I=" + sessionID + "+";
        number++;
        packet += "#" + number + ":T=" + (System.currentTimeMillis() / 1000) + "+";
        number++;
        packet += "#" + number + ":K=" + number_1 + "+";
        number++;
        packet += "#" + number + ":L=" + number_2 + "+";

        return packet;
    }

    // method that sends packet form server to client
    private void sendPacket(String operation, String status, int sessionID, double number_1, double number_2)
    {
        printWriter.println(generatePacket(operation, status, sessionID, number_1, number_2));
    }

    // method that reads received packet
    private void readPacket() throws IOException
    {
        char[] packet1 = new char[1024];
        int length = bufferedReader.read(packet1);

        String packet2 = new String(packet1);
        String finalPacket = packet2.substring(0,length);

        decodePacket(finalPacket);
    }

    // method that calculates factorial
    private int factorial(int n) {
        if (n == 0) return 1;
        else return n * factorial(n - 1);
    }

    // method that decode packet received from client - text-based decoding
    // thanks to split method we get all important data like: operation field, session ID etc.,
    // from received string
    private void decodePacket(String packet){

        double number_1;
        double number_2;
        int sessionID;

        ArrayList<String> arrayList = new ArrayList<>();

        Hashtable<String,String> hashTable = new Hashtable<>();

        String[] hashArray = packet.split("#");

        for(String w : hashArray){

            String[] equalSignArray = w.split("=");

            String[] colonArray = equalSignArray[0].split(":");

            if(colonArray.length == 2){
                String argument = equalSignArray[1].replace("+","");

                hashTable.put(colonArray[1],argument);

                arrayList.add(colonArray[1]);
                arrayList.add(argument);
            }
        }

        number_1 = Double.parseDouble(hashTable.get("K"));
        number_2 = Double.parseDouble(hashTable.get("L"));
        sessionID = Integer.parseInt(hashTable.get("I"));

        //////////////////////////////////////////////////////////////////////////////

        // here server execute proper calculations
        // for example:
        // - server received in operation field 'A' and in status field C,
        // - than server adds two received numbers
        // - at the end server send the result to client

        double result;

        // addition
        if(arrayList.get(1).equals("A") && arrayList.get(3).equals("C"))
        {
            result = number_1 + number_2;
            if(result>179769e+303)
            {
                sendPacket("W","W",sessionID,0,0);
            }
            sendPacket("A","R", sessionID, result,0);
        }

        // subtraction
        else if(arrayList.get(1).equals("S") && arrayList.get(3).equals("C"))
        {
            result = number_1 - number_2;
            sendPacket("S","R", sessionID, result,0);
        }

        // multiplication
        else if(arrayList.get(1).equals("M") && arrayList.get(3).equals("C"))
        {
            result = number_1 * number_2;
            sendPacket("M","R", sessionID, result,0);
        }

        // division
        else if(arrayList.get(1).equals("D") && arrayList.get(3).equals("C"))
        {
            result = number_1 / number_2;
            sendPacket("D","R", sessionID, result,0);
        }

        // exponentiation
        else if(arrayList.get(1).equals("P") && arrayList.get(3).equals("C"))
        {
            result = Math.pow(number_1, number_2);
            sendPacket("P","R", sessionID, result,0);
        }

        // modulo
        else if(arrayList.get(1).equals("R") && arrayList.get(3).equals("C"))
        {
            result = number_1 % number_2;
            sendPacket("R","R", sessionID, result,0);
        }

        // factorial
        else if(arrayList.get(1).equals("F") && arrayList.get(3).equals("C"))
        {
            result = factorial((int)number_1);
            sendPacket("F","R", sessionID, result,0);
        }

        // logarithm
        else if(arrayList.get(1).equals("Z") && arrayList.get(3).equals("C"))
        {
            result = Math.log(number_1);
            sendPacket("Z","R",sessionID,result,0);
        }

        // comparison
        else if(arrayList.get(1).equals("X") && arrayList.get(3).equals("C"))
        {
            if(number_1 > number_2) {
                result = number_1;
            }
            else {
                result = number_2;
            }
            sendPacket("G","R",sessionID,result,0);
        }

        // end of the connection
        else if(arrayList.get(1).equals("E")&&arrayList.get(3).equals("E")) {
            condition = false;
        }
    }

    public void run() {
        try{
            while(condition) {
                readPacket();
            }
        }catch (IOException e) {
            e.getMessage();
        }
        System.out.println("End of the connection.");
    }
}

