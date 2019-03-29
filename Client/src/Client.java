import java.io.*;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;

public class Client implements Runnable{

    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    static private int sessionID;
    private static boolean threadCondition = true;

    private Client(String IP, int port){
        try {
            System.out.println("Waiting for connection...");

            clientSocket = new Socket(IP, port);

            bufferedReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that generates a packet in server - text-based coding
    private String generatePacket(String operation, String status, int sessionID, double number_1, double number_2)
    {
        //            EXAMPLE OF PACKET SEND BY CLIENT
        // #1:O=A+#2:S=C+#3:I=1+#4:T=1544626488+#5:K=3.0+#6:L=5.0+

        //                 DESCRIPTION OF PACKET
        // O - operation field
        // S - status field
        // I - session ID field
        // T - time stamp field
        // K - first number field
        // L - second number field

        int number = 1;

        String packet ="";
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
    private void sendPacket(String opeartion, String status, int sessionID, double number_1, double number_2)
    {
        printWriter.println(generatePacket(opeartion, status, sessionID, number_1, number_2));
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

    // method that decodes packet received from client - text-based decoding
    // thanks to split method we get all important data like: operation field, session ID etc.,
    // from received string
    private void decodePacket(String packet)
    {
        double number_1;

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
        sessionID = Integer.parseInt(hashTable.get("I"));

        if((arrayList.get(1).equals("A") | arrayList.get(1).equals("S") | arrayList.get(1).equals("M") |
            arrayList.get(1).equals("D") | arrayList.get(1).equals("P") | arrayList.get(1).equals("R") |
            arrayList.get(1).equals("F") | arrayList.get(1).equals("Z") | arrayList.get(1).equals("G"))
            && arrayList.get(3).equals("R"))
        {
            System.out.println("Result = " + number_1);
        }
        else if(arrayList.get(1).equals("W") &&arrayList.get(3).equals("W"))
        {
            System.out.println("Result is to big.");
        }
    }

    private void help()
    {
        System.out.println("\nadd - addition");
        System.out.println("sub - subtraction");
        System.out.println("mul - multiplication");
        System.out.println("div - division");
        System.out.println("pow - exponentiation");
        System.out.println("mod - modulo");
        System.out.println("fac - factorial");
        System.out.println("log - logarithm");
        System.out.println("com - comparison");
        System.out.println("end - end the connection with server");
    }

    public static void main(String[] args){

        Client client = new Client("127.0.0.1", 1234);

        boolean condition = true;

        double number_1, number_2;

        String message;

        System.setProperty("line.separator", "");

        if(client.clientSocket != null)
        {
            System.out.println("Connected with server.");
            System.out.println("Type in 'help' to display list of the commands.");

            new Thread(client).start();

            // condition is true to the moment when we type in end command
            while(condition)
            {
                System.out.print("\nType in operation you would like to execute: ");

                Scanner scanner = new Scanner(System.in);
                message = scanner.nextLine();

                // here client app executes proper actions after we type in some command
                switch(message)
                {
                    // addition
                    case "add":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if (number_1 > 179769e+303)
                        {
                            System.out.println("Number is too big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if (number_2 > 179769e+303)
                        {
                            System.out.println("Number is too big.");
                            break;
                        }

                        client.sendPacket("A","C", sessionID, number_1, number_2);
                        break;
                    }

                    // subtraction
                    case "sub":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("S","C", sessionID, number_1, number_2);
                        break;
                    }

                    // multiplication
                    case "mul":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("M","C", sessionID, number_1, number_2);
                        break;
                    }

                    // division
                    case "div":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }
                        if(number_2 == 0)
                        {
                            System.out.print("Division by zero is not allowed.");
                            break;
                        }

                        client.sendPacket("D","C", sessionID, number_1, number_2);
                        break;
                    }

                    // exponentiation
                    case "pow":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("P","C", sessionID, number_1, number_2);
                        break;
                    }

                    // modulo
                    case "mod":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("R","C", sessionID, number_1, number_2);
                        break;
                    }

                    // logarithm
                    case "log":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("Z","C",sessionID, number_1,0);
                        break;

                    }

                    // comparison
                    case "com":
                    {
                        System.out.print("Type in first number: ");
                        number_1 = scanner.nextDouble();
                        if(number_1 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        System.out.print("Type in second number: ");
                        number_2 = scanner.nextDouble();
                        if(number_2 > 179769e+303)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }

                        client.sendPacket("X","C", sessionID, number_1, number_2);
                        break;
                    }

                    // factorial
                    case "fac":
                    {
                        System.out.print("Type in number: ");
                        number_1 = scanner.nextDouble();

                        if(number_1 > 13)
                        {
                            System.out.println("Number is to big.");
                            break;
                        }
                        client.sendPacket("F","C", sessionID, number_1,0);
                        break;
                    }

                    // end of the connection
                    case "end":
                    {
                        client.sendPacket("E","E", sessionID,0,0);
                        threadCondition = false;
                        condition = false;
                        try {
                            client.clientSocket.close();
                        }catch (IOException e) {
                            e.getMessage();
                        }
                        System.out.println("End of the connection.");
                        break;
                    }

                    // help list
                    case "help":
                    {
                        client.help();
                        break;
                    }
                    default:
                    {
                        System.out.println("You typed in wrong command. Try once again.");
                        break;
                    }
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.out.println("Unable to connect to the server.");
            threadCondition = false;
        }
    }

    public void run()
    {
        while(threadCondition) {
            try {
                readPacket();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}