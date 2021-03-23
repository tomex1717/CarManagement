package lufa.alfaserwis.CarManagment;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.service.ServerTCPSocketClientHandlerForBinaryCoding;
import lufa.alfaserwis.CarManagment.service.ServerTCPSocketClientHandlerForCharacterCoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;


@SpringBootApplication
@Slf4j
public class CarManagmentApplication {

    private static ServerTCPSocketClientHandlerForCharacterCoding asciiTCPServer;
    private static ServerTCPSocketClientHandlerForBinaryCoding binnaryTCPServer;


    @PostConstruct
    private static void startTCPServer() {
        try {
            Thread thread = new Thread(asciiTCPServer);
            thread.start();

        } catch (Exception e) {
            log.error("THERE WAS AN ERROR WITH STARTING TCP ASCII SERVER");
            e.printStackTrace();
        }

        try {
            Thread thread = new Thread(binnaryTCPServer);
            thread.start();
            System.out.println("Binnery started");

        } catch (Exception e) {
            log.error("THERE WAS AN ERROR WITH STARTING TCP BINNARY SERVER");
            e.printStackTrace();
        }


    }

    @Autowired
    public CarManagmentApplication(ServerTCPSocketClientHandlerForCharacterCoding serverTCPSocketClientHandlerForCharacterCoding,
                                   ServerTCPSocketClientHandlerForBinaryCoding serverTCPSocketClientHandlerForBinaryCoding) {
        asciiTCPServer = serverTCPSocketClientHandlerForCharacterCoding;
        binnaryTCPServer = serverTCPSocketClientHandlerForBinaryCoding;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CarManagmentApplication.class, args);


    }




}

