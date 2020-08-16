package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class ServerTCPSocketClientHandler {

    private ServerSocket serverSocket;

    @Autowired
    private ReportServiceImpl reportService;

    private long numberOfConnectedClients = 0;

    public void start() {


        try {
            serverSocket = new ServerSocket(12000);
            log.info("TCPServer Waiting for client on port 12000");
            while (true) {
                new EchoClientHandler(serverSocket.accept(), reportService).start();
                numberOfConnectedClients++;
                log.info("Clients connected: " + numberOfConnectedClients);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }


    private class EchoClientHandler extends Thread {

        // fields

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;


        private ReportServiceImpl reportService;


        // constructors

        public EchoClientHandler(Socket socket, ReportServiceImpl reportService) {
            this.clientSocket = socket;
            this.reportService = reportService;
        }

        public void run() {
            System.out.println("Client connected:");
            System.out.println("IP: " + clientSocket.getInetAddress());
            System.out.println("port: " + clientSocket.getPort());
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

                    System.out.println("RECIEVED:");
                    System.out.println(inputLine);
                    System.out.println("------------------------");

                    // save it to db if it contains proper report
                    if (inputLine.contains("GTFRI")) {
                        reportService.writeToDb(inputLine);
                    }
                    reportService.writeToFileWholeReport(inputLine);
                }
                numberOfConnectedClients--;

                in.close();
                out.close();
                clientSocket.close();
                log.info("connection closed due to connection close by remote client");
                numberOfConnectedClients--;


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        // private methods


    }

}