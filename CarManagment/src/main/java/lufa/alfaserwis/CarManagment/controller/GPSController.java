package lufa.alfaserwis.CarManagment.controller;


import lufa.alfaserwis.CarManagment.entity.carmanagement.Day;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import lufa.alfaserwis.CarManagment.service.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/gps")
@Controller
public class GPSController {

    private ReportServiceImpl reportService;

    @Autowired
    public GPSController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/showmaplatest")
    public String showMap(Model model, @RequestParam("regNumber") String regNumber) {
        List<Day> dayList = reportService.last14DaysList();
        model.addAttribute("dayList", dayList);
        model.addAttribute("regnumber", regNumber);
        List<Report> reportList = reportService.getReportsOfDay(dayList.get(0), regNumber);
        model.addAttribute("reportlist", reportList);


        return "show-map";
    }

    @GetMapping("/showmap")
    public String showMap(Model model, @RequestParam("regNumber") String regNumber, @RequestParam("timestamp") long timestamp) {
        List<Day> dayList = reportService.last14DaysList();
        model.addAttribute("dayList", dayList);
        model.addAttribute("regnumber", regNumber);
        List<Report> reportList = reportService.getReportsOfDay(timestamp, regNumber);
        model.addAttribute("reportlist", reportList);


        return "show-map";
    }


}
