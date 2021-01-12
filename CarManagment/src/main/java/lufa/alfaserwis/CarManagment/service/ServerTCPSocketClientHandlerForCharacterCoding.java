package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class ServerTCPSocketClientHandlerForCharacterCoding implements Runnable {

    private ServerSocket serverSocket;

    @Autowired
    private ReportServiceImpl reportService;

    private long numberOfConnectedClients = 0;


    public void run() {

        Socket socket = new Socket();
        try {
            serverSocket = new ServerSocket(12000);

            log.info("TCPServer Waiting for client on port 12000");
            while (true) {
                socket.setSoTimeout(5000);
                socket = serverSocket.accept();
                new EchoClientHandler(socket, reportService).start();
                numberOfConnectedClients++;
                log.info("Clients connected: " + numberOfConnectedClients);
            }

        } catch (IOException e) {
            log.warn(e.getMessage());

        }
//        try{
//
//            ServerSocket newSocket = new ServerSocket(11000);
//            Socket socket1 = newSocket.accept();
//
//
//
//        } catch (IOException e){
//
//        }
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
        @Autowired
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


                    // save it to db if it contains proper report
                    if (inputLine.contains("GTFRI")) {
                        reportService.writeToDb(inputLine);
                    }
                    reportService.writeToFileWholeReport(inputLine);
                }

                in.close();
                out.close();
                clientSocket.close();
                log.info("connection closed due to connection close by remote client");
                numberOfConnectedClients--;


            } catch (IOException e) {
                e.printStackTrace();
                numberOfConnectedClients--;

            }
        }

        // private methods


    }

//    private class BinaryDataClientHandler extends Thread{
//
//        private Socket clientSocket;
//        private BufferedReader bufferedReader;
//        private ReportServiceImpl reportService;
//
//
//        @Autowired
//        public BinaryDataClientHandler(Socket clientSocket,  ReportServiceImpl reportService) {
//            this.clientSocket = clientSocket;
//            this.reportService = reportService;
//        }
//
//        @Override
//        public void run() {
//            try{
//                DataInputStream dataInputStream= new DataInputStream());
//
//
//
//
//
//
//
//
//
//            } catch (IOException e){
//                log.error(e.getMessage());
//            }
//        }
//
//
//
//
//
//
//
//    }

}