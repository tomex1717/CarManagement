package lufa.alfaserwis.CarManagment.service;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ServerCon {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public void start() {
        String fromclient;

        try{
            ServerSocket Server = new ServerSocket(12000);
            System.out.println("TCPServer Waiting for client on port 12000");

            while (true) {
                Socket connected = Server.accept();
                System.out.println(" THE CLIENT" + " " + connected.getInetAddress()
                        + ":" + connected.getPort() + " IS CONNECTED ");



                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(connected.getInputStream()));


                while (true) {

                    fromclient = inFromClient.readLine();
                    if (fromclient == null) {
                        connected.close();
                        System.out.println("Connetcion close due conettion lost");
                        break;
                    }
                    if (fromclient.equals("q")) {
                        connected.close();
                        System.out.println("Connetcion close due server teminate connection command");
                        break;
                    }


                    System.out.println("RECIEVED:");
                    System.out.println(fromclient);
                    writeToFile(fromclient);





                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void writeToFile(String text){
        File file = new File("/usr/local/tomcat/webapps/images/file.txt");
        try(FileWriter fileWriter = new FileWriter(file,true)){

            LocalDateTime now = LocalDateTime.now();

            fileWriter.write(dtf.format(now)+" ::: " + text);
            fileWriter.write("\n");


        }catch (IOException e){
            e.printStackTrace();
        }

    }

}

