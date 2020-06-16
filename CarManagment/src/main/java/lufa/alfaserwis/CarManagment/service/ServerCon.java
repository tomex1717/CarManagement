package lufa.alfaserwis.CarManagment.service;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ServerCon {

    private ServerSocket serverSocket;

    public void start() {

        try {
            serverSocket = new ServerSocket(12000);
            System.out.println("TCPServer Waiting for client on port 12000");
            while (true) {
                new EchoClientHandler(serverSocket.accept()).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {

        // fields

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // constructors

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("quit".equals(inputLine)) {
                        System.out.println("connection closed due to quit command");
                        break;
                    }
                    if (inputLine == null) {
                        System.out.println("connection closed due to connetion lost");
                        break;
                    }
                    System.out.println("RECIEVED:");
                    System.out.println(inputLine);


                    writeToFile(inputLine);
                }
                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        // private methods

        private void writeToFile(String text) {
            File file = new File("/usr/local/tomcat/webapps/images/file.txt");
            try (FileWriter fileWriter = new FileWriter(file, true)) {

                LocalDateTime now = LocalDateTime.now();

                fileWriter.write(dtf.format(now) + " ::: " + text);
                fileWriter.write("\n");


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}