package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.entity.carmanagement.GPSElementBinaryCoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
                socket.setSoTimeout(25000);
                socket = serverSocket.accept();
                new EchoClientHandler(socket, reportService).start();
                System.out.println("Binary Client connected");
                numberOfConnectedClients++;
                System.out.println("Clients connected to binary coding socket: " + numberOfConnectedClients);

            }


        } catch (IOException e) {
            log.error("Error handling TCP connection");
            log.error(e.getMessage(), e);

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
        private DataInputStream in;
        private DataOutputStream out;
        private ReportServiceImpl reportService;
        private int numberOfDataForResponce;
        private int numberOfDataForResponce2;
        private long imei = 0;
        private GPSElementBinaryCoding gps = new GPSElementBinaryCoding();
        private long timestamp = 0;


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
                this.imei = Long.parseLong(imei);

                // read data only if data reception accepted, otherwise close socket
                if (respondDueToAcceptation(imei)) {
                    // now performing reading data
                    readData();
                    acknowledgeDataReception();
                }


                reportService.writeToDb(this.gps, this.timestamp, this.imei);


            } catch (IOException e) {
                log.error(e.getMessage(), e);
                numberOfConnectedClients--;

            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    numberOfConnectedClients--;
                } catch (IOException e) {
                    log.error("Error with closing streams form binary socket: ");
                    log.error(e.getMessage());
                }
            }
        }

        // private methods


        private String readIMEI() {
            String imei;
            int imeiLength = readIMEILength();
            byte[] imeiBytes = new byte[imeiLength];
            try {
                for (int i = 0; i < imeiLength; i++) {
                    imeiBytes[i] = in.readByte();
                }

            } catch (IOException e) {
                log.error("Error reading imei value from device.");
                log.error(e.getMessage(), e);

            }
            imei = new String(imeiBytes, StandardCharsets.UTF_8);
            return imei;

        }


        private short readIMEILength() {
            try {
                return in.readShort();

            } catch (IOException e) {
                log.error("Error reading imei length of connected device.");
                log.error(e.getMessage(), e);
                return -1;
            }

        }


        private boolean respondDueToAcceptation(String imei) {
            try {
                // check if gpsDevice imei is in DB, if true, accept data from device

                boolean isGpsPresentOnList = reportService.isGpsInDB(Long.parseLong(imei));
                System.out.println(imei);
                if (isGpsPresentOnList) {
                    out.writeByte(1);



                } else {
                    out.writeByte(0);

                }
                out.flush();
                TimeUnit.MILLISECONDS.sleep(200);
                return isGpsPresentOnList;

            } catch (Exception e) {
                log.error("Error sending positive acceptance response to device");
                log.error(e.getMessage(), e);
                return false;

            }
        }


        private void readData() throws SocketException {
            // every AVL packet has 4 zeros bytes at beginning
            try {
                if (read4ZeroBytes()) {
                    int dataLength = readDataLength();
                    readCodecID();
                    numberOfDataForResponce = numberOfData();
                    for (int i = 0; i < numberOfDataForResponce; i++) {
                        this.timestamp = readTimeStamp();
                        int priority = readPriority();
                        this.gps = readGPSElement();

                        readIOElemets();


                    }
                    numberOfDataForResponce2 = numberOfData();
                    readCRC();
                } else {
                    in.close();
                }
            } catch (Exception e) {
                log.error("Error receiving AVL data from device");
                log.error(e.getMessage(), e);
                e.printStackTrace();

            }


        }

        private boolean acknowledgeDataReception() {
            try {
                if (numberOfDataForResponce == numberOfDataForResponce2) {
                    out.writeInt(numberOfDataForResponce);
                    out.flush();


                }
                return true;
            } catch (IOException e) {
                log.error("Error sending data reception");
                log.error(e.getMessage(), e);
                return false;
            }
        }

        private boolean read4ZeroBytes() {
            try {
                if (in.readInt() == 0) {
                    return true;
                }
                return false;


            } catch (IOException e) {
                log.error("Error reading 4 zero bytes");
                log.error(e.getMessage(), e);
                return false;
            }

        }


        private int readDataLength() {
            try {

                return in.readInt();

            } catch (IOException e) {
                log.error("Error reading data length AVL packet");
                log.error(e.getMessage(), e);
                return -1;
            }

        }

        private byte readCodecID() {
            try {

                return in.readByte();

            } catch (IOException e) {
                log.error("Error reading Codec ID");
                log.error(e.getMessage(), e);
                return -1;
            }
        }

        private int numberOfData() {
            try {

                return in.readByte();

            } catch (IOException e) {
                log.error("Error reading Number fo Data");
                log.error(e.getMessage(), e);
                return -1;
            }

        }

        private long readTimeStamp() {
            try {
                return in.readLong();

            } catch (IOException e) {
                log.error("Error during reading timestamp");
                log.error(e.getMessage(), e);
                return -1;
            }
        }

        private int readPriority() {
            try {

                return in.readByte();

            } catch (IOException e) {
                log.error("Error during reading priority");
                log.error(e.getMessage(), e);
                return -1;
            }
        }

        private GPSElementBinaryCoding readGPSElement() {

            try {
                gps = new GPSElementBinaryCoding();

                // read longitude and latitude
                gps.setLongitude(readLongitudeOrLatitude());
                gps.setLatitude(readLongitudeOrLatitude());

                // read altitude
                gps.setAltitude(in.readShort());

                // read angle
                gps.setAngle(in.readShort());

                //read satellites
                gps.setSatellites(in.readByte());

                // read speed
                gps.setSpeed(in.readShort());

                return gps;
            } catch (IOException e) {
                log.error("Error during reading GPS Element data");
                log.error(e.getMessage(), e);
                return null;
            }
        }

        private float readLongitudeOrLatitude() {
            try {

                float valueToReturn = in.readInt();
                valueToReturn /= 10000000;

                return valueToReturn;

            } catch (IOException e) {
                log.error("Error during reading longitude/latitude");
                log.error(e.getMessage(), e);
                return -1;
            }
        }


        private void readIOElemets() {
            try {
                // here are IO Elements, for now im only skipping buffer, since i dont need those values for now.

                byte IOElementID = in.readByte();
                byte numberOfIOElements = in.readByte();

                read1ByteIOElements();
                read2ByteIOElements();
                read4ByteIOElements();
                read8ByteIOElements();


            } catch (IOException e) {
                log.error("Error reading IO elements");
                log.error(e.getMessage(), e);

            }


        }

        private HashMap<Integer, Integer> read1ByteIOElements() {
            try {
                byte numberOfIOElementsByte1 = in.readByte();
                HashMap<Integer, Integer> byte1IOElements = new HashMap<>();

                for (int i = 0; i < numberOfIOElementsByte1; i++) {

                    int id = in.readUnsignedByte();
                    int value = in.readByte();
                    byte1IOElements.put(id, value);
                }

                return byte1IOElements;
            } catch (IOException e) {
                log.error("Error reading 1Byte IO Elements");
                log.error(e.getMessage(), e);
                return null;
            }

        }

        private HashMap<Integer, Integer> read2ByteIOElements() {
            try {
                byte numberOfIOElementsByte2 = in.readByte();
                HashMap<Integer, Integer> byte2IOElements = new HashMap<>();

                for (int i = 0; i < numberOfIOElementsByte2; i++) {

                    int id = in.readByte();
                    int value = in.readShort();
                    byte2IOElements.put(id, value);

                }
                return byte2IOElements;
            } catch (IOException e) {
                log.error("Error reading 2Byte IO Elements");
                log.error(e.getMessage(), e);
                return null;
            }

        }

        private HashMap<Integer, Integer> read4ByteIOElements() {
            try {
                byte numberOfIOElementsByte4 = in.readByte();
                HashMap<Integer, Integer> byte4IOElements = new HashMap<>();

                for (int i = 0; i < numberOfIOElementsByte4; i++) {
                    int id = in.readByte();
                    int value = in.readInt();
                    byte4IOElements.put(id, value);

                }
                return byte4IOElements;
            } catch (IOException e) {
                log.error("Error reading 4Byte IO Elements");
                log.error(e.getMessage(), e);
                return null;
            }

        }

        private HashMap<Integer, Integer> read8ByteIOElements() {
            try {
                int numberOfIOElementsByte8 = Byte.toUnsignedInt(in.readByte());
                HashMap<Integer, Integer> byte8IOElements = new HashMap<>();

                for (int i = 0; i < numberOfIOElementsByte8; i++) {
                    int id = in.readByte();
                    int value = (int) in.readLong();
                    byte8IOElements.put(id, value);
                }
                return byte8IOElements;

            } catch (IOException e) {
                log.error("Error reading 8Byte IO Elements");
                log.error(e.getMessage(), e);
                return null;
            }

        }

        private int readCRC() {
            // crc16  4bytes;
            try {
                return in.readInt();

            } catch (IOException e) {
                log.error("Error reading CRC");
                log.error(e.getMessage(), e);
                return -1;
            }
        }


    }


}
