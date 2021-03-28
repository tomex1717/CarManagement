package lufa.alfaserwis.CarManagment.controller;


import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import lufa.alfaserwis.CarManagment.service.ReportServiceImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@PropertySource("classpath:application.properties")
@RequestMapping("/gps")
@Controller
@Slf4j
public class GPSController {

    private ReportServiceImpl reportService;
    @Value("${googlemapsapi.key}")
    private String googleMapsAPIKey;

    @Autowired
    public GPSController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }


    @GetMapping("/showmap")
    public String showMap(Model model, @RequestParam("imei") long imei, @RequestParam("timestamp") long timestamp) {


        model.addAttribute("imei", imei);
        List<Report> reportList = reportService.getReportsOfDay(timestamp, imei);
        model.addAttribute("apikey", googleMapsAPIKey);
        model.addAttribute("reportArray",
                reportService.makeDirectionsAsJsonArray(reportList));


        return "show-map";
    }

    @GetMapping("/")
    public String latestPositionAllCars(Model model) {
        model.addAttribute("apikey", googleMapsAPIKey);
        List<Report> reportList = reportService.findLatestReportForAllRegNumbers();
        JSONArray array = new JSONArray(reportList);
        model.addAttribute("reportArray", array.toString());
        return "show-latest";
    }

    @GetMapping("/showall")
    public String showAllCarsOnMap(Model model) {
        model.addAttribute("apikey", googleMapsAPIKey);
        model.addAttribute("latestReportsList", reportService.makeJsonStringFromLatestReportsSet());
        return "show-map-all";
    }


}
