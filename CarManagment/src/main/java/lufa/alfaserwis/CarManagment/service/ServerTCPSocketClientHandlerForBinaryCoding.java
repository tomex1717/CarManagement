package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.entity.carmanagement.GPSElementBinaryCoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ServerTCPSocketClientHandlerForBinaryCoding implements Runnable {

    private ServerSocket serverSocket;
    private long numberOfConnectedClients = 0;
    private ReportServiceImpl reportService;


    @Autowired
    public ServerTCPSocketClientHandlerForBinaryCoding(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }

    @Override
    public void run() {
        Socket socket = new Socket();

        try {
            serverSocket = new ServerSocket(12001);
            while (true) {
                socket.setSoTimeout(5000);
                socket = serverSocket.accept();
                new EchoClientHandler(socket, reportService).start();
                numberOfConnectedClients++;
                log.info("Clients connected to binary coding socket: " + numberOfConnectedClients);

            }


        } catch (IOException e) {
            log.error("Error handling TCP connection");
            log.error(String.valueOf(e.getStackTrace()));

        }


    }


    //
//    2. SENDING DATA OVER TCP/IP
//    First when module connects to server, module sends its IMEI. First comes short identifying number of bytes
//    written and then goes IMEI as text (bytes).
//    For example IMEI 356307042441013 would be sent as 000f333536333037303432343431303133
//    First two bytes denote IMEI length. In this case 000F means, that imei is 15 bytes long.
//    After receiving IMEI, server should determine if it would accept data from this module. If yes server will reply
//    to module 01 if not 00. Note that confirmation should be sent as binary packet. I.e. 1 byte 0x01 or 0x00.
//    Then module starts to send first AVL data packet. After server receives packet and parses it, server must
//    report to module number of data received as integer (four bytes).
//    If sent data number and reported by server doesn’t match module resends sent data.

    private class EchoClientHandler extends Thread {

        // fields

        private Socket clientSocket;
        DataInputStream in;
        DataOutputStream out;
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
                in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

                String imei = readIMEI();

                // TODO: now need to check if IMEI is in db, then send acceptance/
                respondDueToAcceptation();

                // now performing reading data
                readData();


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


        private String readIMEI() {
            String imei;
            int imeiLength = readIMEILength();
            System.out.println("IMEI LENGHT" + imeiLength);
            byte[] imeiBytes = new byte[imeiLength];
            try {
                for (int i = 0; i < imeiLength; i++) {
                    imeiBytes[i] = in.readByte();
                }

            } catch (IOException e) {
                log.error("Error reading imei value from device.");
                log.error(e.getMessage());

            }

            imei = new String(imeiBytes, StandardCharsets.UTF_8);
            System.out.println(imei);

            return imei;


        }


        private short readIMEILength() {
            try {
                byte[] imeiLenghtBytes = new byte[2];
                imeiLenghtBytes[0] = in.readByte();
                imeiLenghtBytes[1] = in.readByte();

                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.put(imeiLenghtBytes[0]);
                bb.put(imeiLenghtBytes[1]);
                bb.flip();

                return ByteBuffer.wrap(imeiLenghtBytes).getShort();


            } catch (IOException e) {
                log.error("Error reading imei length of connected device.");
                log.error(e.getMessage());
                return -1;
            }

        }


        private void respondDueToAcceptation() {
            try {
                out.writeByte(1);
                out.flush();
            } catch (IOException e) {
                log.error("Error sending positive acceptance response to device");
                log.error(e.getMessage());

            }
        }


        private void readData() throws SocketException {
            // every AVL packet has 4 zeros bytes at beginning
            try {
                read4ZeroBytes();
                int dataLength = readDataLength();
                readCodecID();
                int numberOfData = numberOfData();
                long timestamp = readTimeStamp();
                int priority = readPriority();
                GPSElementBinaryCoding gps = readGPSElement();
                readIOElemets();


            } catch (Exception e) {
                log.error("Error receiving AVL data from device");
                log.error(e.getMessage());
                e.printStackTrace();

            }


        }

        private void read4ZeroBytes() {
            try {
                in.readByte();
                in.readByte();
                in.readByte();
                in.readByte();

            } catch (IOException e) {
                log.error("Error reading 4 zero bytes");
                log.error(e.getMessage());
            }

        }


        private int readDataLength() {
            try {
                ByteBuffer bb = ByteBuffer.allocate(4);
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.flip();
                return bb.getInt();


            } catch (IOException e) {
                log.error("Error reading data length AVL packet");
                log.error(e.getMessage());
                return -1;
            }

        }

        private void readCodecID() {
            try {
                in.readByte();

            } catch (IOException e) {
                log.error("Error reading Codec ID");
                log.error(e.getMessage());

            }
        }

        private int numberOfData() {
            try {
                int numberOfData = in.readByte();
                return numberOfData;
            } catch (IOException e) {
                log.error("Error reading Number fo Data");
                log.error(e.getMessage());
                return -1;
            }

        }

        private long readTimeStamp() {
            try {
                //timestamp is 8 Bytes
                ByteBuffer bb = ByteBuffer.allocate(8);
                for (int i = 0; i < 8; i++) {
                    bb.put(in.readByte());
                }
                bb.flip();
                return bb.getLong();

            } catch (IOException e) {
                log.error("Error during reading timestamp");
                log.error(e.getMessage());
                return -1;
            }
        }

        private int readPriority() {
            try {
                int priority = in.readByte();
                return priority;

            } catch (IOException e) {
                log.error("Error during reading priority");
                log.error(e.getMessage());
                return -1;
            }
        }

        private GPSElementBinaryCoding readGPSElement() {
            //                Longitude      Latitude    Altitude   Angle       Satellites   Speed
//                             4 Bytes        4 Bytes     2 Bytes    2 Bytes     1 Byte      2 Bytes//
//                GPS Element
//                0f0ea850 – Longitude 252618832 = 25,2618832º N
//                209a6900 – Latitude 546990336 = 54,6990336 º E
//                0094 – Altitude 148 meters
//                0000 – Angle 214º
//                12 – 12 Visible sattelites
//                0000 – 0 km/h speed

            try {
                GPSElementBinaryCoding gps = new GPSElementBinaryCoding();

                // read longitude and latitude

                gps.setLongitude(readLongitudeOrLatitude());
                gps.setLatitude(readLongitudeOrLatitude());

                // read altitude
                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.flip();
                gps.setAltitude(bb.getShort());

                // read angle
                bb = ByteBuffer.allocate(2);
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.flip();
                gps.setAngle(bb.getShort());

                //read satellites
                gps.setSatellites(in.readByte());

                // read speed
                bb = ByteBuffer.allocate(2);
                bb.put(in.readByte());
                bb.put(in.readByte());
                bb.flip();
                gps.setSpeed(bb.getShort());


                System.out.println(gps);
                return gps;
            } catch (IOException e) {
                log.error("Error during reading GPS Element data");
                log.error(e.getMessage());
                return null;
            }
        }

        private float readLongitudeOrLatitude() {
            try {
                ByteBuffer bb = ByteBuffer.allocate(4);
                for (int i = 0; i < 4; i++) {
                    bb.put(in.readByte());
                }
                bb.flip();
                int intValue = bb.getInt();
                float valueToReturn = intValue;
                valueToReturn /= 10000000;

                return valueToReturn;

            } catch (IOException e) {
                log.error("Error during reading longitude/latitude");
                log.error(e.getMessage());
                return -1;
            }
        }


        private void readIOElemets() {
            try {
                // here are IO Elements, for now im only skipping buffer, since i dont need those values for now.

                int IOElementID = in.readByte();

                System.out.println("IOELEMENTID:  " + IOElementID);
                int numberOfIOElements = in.readByte();
                System.out.println("NUMBER OF ALL: " + numberOfIOElements);

                int numberOfIOElementsByte1 = Byte.toUnsignedInt(in.readByte());
                System.out.println();
                System.out.print("Number of 1bytes: " + numberOfIOElementsByte1);
                for (int i = 0; i < numberOfIOElementsByte1; i++) {

                    System.out.println();
                    int id = Byte.toUnsignedInt(in.readByte());
                    System.out.print(" ID: " + id);
                    int value = Byte.toUnsignedInt(in.readByte());
                    System.out.print(" value: " + value);

                }

                int numberOfIOElementsByte2 = Byte.toUnsignedInt(in.readByte());
                System.out.println();
                System.out.print("Number of 2bytes: " + numberOfIOElementsByte2);
                for (int i = 0; i < numberOfIOElementsByte2; i++) {

                    System.out.println();
                    System.out.print("IO ID: " + Byte.toUnsignedInt(in.readByte()));
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.flip();
                    System.out.print(" value: " + bb.getShort());

                }

                int numberOfIOElementsByte4 = Byte.toUnsignedInt(in.readByte());
                System.out.println();
                System.out.print("Number of 4bytes: " + numberOfIOElementsByte4);
                for (int i = 0; i < numberOfIOElementsByte4; i++) {
                    System.out.println();
                    System.out.print("IO ID: " + Byte.toUnsignedInt(in.readByte()));
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.flip();
                    System.out.print(" value: " + bb.getInt());
                }

                int numberOfIOElementsByte8 = Byte.toUnsignedInt(in.readByte());
                System.out.println();
                System.out.print("Number of 8bytes: " + numberOfIOElementsByte8);
                for (int i = 0; i < numberOfIOElementsByte8; i++) {
                    System.out.println();
                    System.out.print("IO ID: " + Byte.toUnsignedInt(in.readByte()));
                    ByteBuffer bb = ByteBuffer.allocate(8);
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.put(in.readByte());
                    bb.flip();
                    System.out.print(" value: " + bb.getLong());
                }

                // numberOfData
                int numberOfData = in.readByte();

                // crc16  4bytes;

                in.readByte();
                in.readByte();
                in.readByte();
                in.readByte();


                // Server acknowledges data reception numberOfData:
                out.write(numberOfData);
                out.flush();


            } catch (IOException e) {
                log.error("Error reading IO elements");
                log.error(e.getMessage());
                e.printStackTrace();
            }


        }


    }


}
