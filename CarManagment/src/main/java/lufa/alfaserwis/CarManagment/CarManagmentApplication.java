package lufa.alfaserwis.CarManagment;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.service.ServerTCPSocketClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class CarManagmentApplication {

    private static ServerTCPSocketClientHandler serverTCPSocketClientHandler;

    @Autowired
    public CarManagmentApplication(ServerTCPSocketClientHandler serverTCPSocketClientHandler) {
        this.serverTCPSocketClientHandler = serverTCPSocketClientHandler;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CarManagmentApplication.class, args);

        startTCPServer();


    }

    private static void startTCPServer() {
        try {
            serverTCPSocketClientHandler.start();
        } catch (Exception e) {
            log.error("THERE WAS AN ERROR WITH STARTING TCP SERVER");
            e.printStackTrace();
        }

    }


}

