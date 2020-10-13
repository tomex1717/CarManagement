package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.config.YAMLConfig;
import lufa.alfaserwis.CarManagment.dao.carmanagement.ReportRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Service
@EnableAsync
@Slf4j
public class ReportServiceImpl {

    private ReportRepository reportRepository;
    private EntityManager entityManager;
    private YAMLConfig config;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, @Qualifier("carManagementEM") EntityManager entityManager,
                             YAMLConfig config) {
        this.reportRepository = reportRepository;
        this.entityManager = entityManager;
        this.config = config;

    }


    public void writeToDb(String report) {
        // convert String report to List first, then make entity object according to fields
        // and corresponding list index

        Report reportEntity = makeEntity(getReportAsList(report));

        reportRepository.save(reportEntity);


    }

    public void writeToFileWholeReport(String report) {
        File file = new File(config.getReportFilePath());

        try (FileWriter fileWriter = new FileWriter(file, true)) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            fileWriter.write(dtf.format(now) + " ::: " + report);
            fileWriter.write("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


//    @Transactional
//    @Scheduled(cron = "0 0 3 * * *")
//    @Async
//    public void deleteRecordsOlderThan() {
//        // deletes rows in reports table older than 90 days, runs every day at 3am
//        int days = 90;
//        long timestamp = System.currentTimeMillis() - days * 86400000;
//        Query q = entityManager.createQuery("DELETE FROM Report s WHERE s.timestamp < :timestamp");
//        q.setParameter("timestamp", timestamp);
//        int deletedRows = q.executeUpdate();
//        log.info("Deleted rows on scheduled task: " + deletedRows);
//        entityManager.close();
//    }


    private List<String> getReportAsList(String report) {

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
        // Converts DateAndTime string in report to timestamp in polish TimeZone
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

//            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = dateFormat.parse(date);
            System.out.println(date);
            LocalDateTime localDateTime = parsedDate.toInstant().atZone(ZoneId.of("Poland")).toLocalDateTime();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            System.out.println(timestamp);

            return timestamp.getTime();
        } catch (Exception e) {
            log.error("Error converting date to timestamp:");
            log.error(e.getMessage());
            return 0;
        }

    }

    public List<Report> getAllReportsByRegNumber(String regNumber) {
        return reportRepository.findByRegNumber(regNumber);

    }






    public List<Report> getReportsOfDay(long timestamp, String regNumber) {
        List<Report> reportList;

        Query q = entityManager.createQuery(
                "SELECT r FROM Report r WHERE r.regNumber = :regnumber AND r.timestamp between :mintimestamp AND :maxtimestamp  ORDER BY r.timestamp ASC");


        q.setParameter("maxtimestamp", timestamp + 86399999);
        q.setParameter("mintimestamp", timestamp);
        q.setParameter("regnumber", regNumber);

        reportList = q.getResultList();

        Report previousReport = new Report();
        long timeCount = 0;
        int route = 1;

        boolean routeIncremented = true;


        ListIterator<Report> listIterator = reportList.listIterator();
        if (!reportList.isEmpty()) {
            previousReport = reportList.get(0);
        }
        while (listIterator.hasNext()) {
            Report report = listIterator.next();


            if (report.getLatitude().equals(previousReport.getLatitude()) &&
                    report.getLongitude().equals(previousReport.getLongitude())) {


                if (timeCount == 0) {
                    timeCount = previousReport.getTimestamp();
                }


                if (report.getTimestamp() > timeCount + 600000) {


                    if (!routeIncremented) {
                        route++;
                        routeIncremented = true;

                    }


                } else {
                    report.setRouteNumber(route);
                }


            } else {
                if (routeIncremented) {
                    listIterator.previous();
                    Report report1 = listIterator.previous();
                    report1.setRouteNumber(route);

                    listIterator.next();
                    listIterator.next();

                }

                routeIncremented = false;
                timeCount = 0;
                report.setRouteNumber(route);


            }
            previousReport = report;

        }


        return reportList;
    }

    public String makeDirectionsAsJsonArray(List<Report> reportList) {

        JSONArray allRoutes = new JSONArray();
        JSONArray routeArray = new JSONArray();
        Report cacheReport = new Report();


        ListIterator<Report> listIterator = reportList.listIterator();
        if (listIterator.hasNext()) {
            cacheReport = listIterator.next();
        }
        while (listIterator.hasNext()) {
            Report report = listIterator.next();

            if ((report.getRouteNumber().equals(cacheReport.getRouteNumber())) && (report.getRouteNumber() != 0)) {
                routeArray.put(makeJsonObject(
                        cacheReport.getLongitude(), cacheReport.getLatitude(), cacheReport.getTimestamp()));

            } else {
                routeArray.put(makeJsonObject(
                        report.getLongitude(), report.getLatitude(), report.getTimestamp()));

                if (routeArray.length() > 1) {
                    allRoutes.put(routeArray);
                }

                routeArray = new JSONArray();
            }

            cacheReport = report;
        }

        return allRoutes.toString();
    }

    private JSONObject makeJsonObject(double lng, double lat, long timestamp) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("lng", lng);
        jsonObject.put("lat", lat);
        jsonObject.put("timestamp", timestamp);
        return jsonObject;
    }

}

