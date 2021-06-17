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
                checkNumberOfConnectedClients();
            }

        } catch (IOException e) {
            log.warn(e.getMessage());

        }

    }

    private void checkNumberOfConnectedClients() {
        if (numberOfConnectedClients > 100) {
            log.warn("Number of connected clients over 100!");
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
        @Autowired
        public EchoClientHandler(Socket socket, ReportServiceImpl reportService) {
            this.clientSocket = socket;
            this.reportService = reportService;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {


                    // save it to db if it contains proper report
                    if (inputLine.contains("GTFRI")) {
                        reportService.writeToDb(inputLine);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                numberOfConnectedClients--;
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    numberOfConnectedClients--;
                } catch (IOException e) {
                    log.error("Error with closing streams form ascii socket: ");
                    log.error(e.getMessage());
                }
            }
        }

        // private methods
    }

}