package lufa.alfaserwis.CarManagment.controller;


import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;
import lufa.alfaserwis.CarManagment.service.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GPSRestController {

    private ReportServiceImpl reportService;

    @Autowired
    public GPSRestController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/gps/latestpositionallcars")
    public String latestPositionsAllCars() {

        return reportService.makeJsonStringFromLatestReportsSet();
    }

    @GetMapping("/gps/findcarbyassociatedimei")
    public String findCarByAssociatedImei(@RequestParam long imei) {
        Car car = reportService.findGpsByImei(imei).getCar();

        return reportService.makeJsonForInfoWindowFromCarObject(car);
    }


}
