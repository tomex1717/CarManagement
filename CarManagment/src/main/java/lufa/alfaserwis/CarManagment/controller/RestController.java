package lufa.alfaserwis.CarManagment.controller;


import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import lufa.alfaserwis.CarManagment.service.ReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    // fields

    private ReportServiceImpl reportService;


    // constructors

    @Autowired
    public RestController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }


    // public methods


    @GetMapping("/gps/latestReports")
    public List<Report> latestReportsOfAllRegNumbers() {
        return reportService.findLatestReportForAllRegNumbers();

    }


    // private methods


}
