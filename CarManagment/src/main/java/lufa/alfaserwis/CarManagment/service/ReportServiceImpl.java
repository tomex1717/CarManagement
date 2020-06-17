package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.dao.carmanagement.ReportRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl {

    private ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public void writeToDb(String report) {
        // convert String report to List first, then make entity object according to fields
        // and corresponding list index

        Report reportEntity = makeEntity(getReportAsList(report));

        reportRepository.save(reportEntity);


    }

    // change it to private later
    public List<String> getReportAsList(String report) {

        String[] reportArray = new String[50];
        reportArray = report.split(",");
        return Arrays.asList(reportArray);
    }


    private Report makeEntity(List<String> reportAsList) {
        Report report = new Report();


        report.setTimestamp(covertStringToTimeStampMillis(reportAsList.get(13)));
        report.setAltitude(Double.valueOf(reportAsList.get(10)));
        report.setImei(Long.valueOf(reportAsList.get(2)));
        report.setLatitude(Double.valueOf(reportAsList.get(12)));
        report.setLongitude(Double.valueOf(reportAsList.get(11)));

        report.setRegNumber(reportAsList.get(3));
        report.setSpeed(Double.valueOf(reportAsList.get(8)));

        return report;
    }


    private long covertStringToTimeStampMillis(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            Date parsedDate = dateFormat.parse(date);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

            return timestamp.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


}
