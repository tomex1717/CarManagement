package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.dao.carmanagement.ReportRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Day;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import lufa.alfaserwis.utils.Paths;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@EnableAsync
@Slf4j
public class ReportServiceImpl {

    private ReportRepository reportRepository;
    private EntityManager entityManager;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, @Qualifier("carManagementEM") EntityManager entityManager) {
        this.reportRepository = reportRepository;
        this.entityManager = entityManager;

    }


    public void writeToDb(String report) {
        // convert String report to List first, then make entity object according to fields
        // and corresponding list index

        Report reportEntity = makeEntity(getReportAsList(report));

        reportRepository.save(reportEntity);


    }

    public void writeToFileWholeReport(String report) {
        File file = new File(Paths.REPORT_FILE_PATH);

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


    public List<Day> last14DaysList() {
        List<Day> dayList = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        while (dayList.size() < 15) {
            Day day = new Day();
            day.setDate(localDate.toString());
            day.setMinTimestamp(Timestamp.valueOf(day.getDate() + " " + "00:00:00").getTime());
            day.setMaxTimestamp(day.getMinTimestamp() + 86399999);
            dayList.add(day);
            localDate = localDate.minusDays(1);

        }
        return dayList;
    }

    public List<Report> getReportsOfDay(Day day, String regNumber) {
        List reportList;

        Query q = entityManager.createQuery(
                "SELECT r FROM Report r WHERE r.regNumber = :regnumber AND r.timestamp between :mintimestamp AND :maxtimestamp  ORDER BY r.timestamp");


        q.setParameter("maxtimestamp", day.getMaxTimestamp());
        q.setParameter("mintimestamp", day.getMinTimestamp());
        q.setParameter("regnumber", regNumber);

        reportList = q.getResultList();

        return reportList;
    }

    public List<Report> getReportsOfDay(long timestamp, String regNumber) {
        List reportList;

        Query q = entityManager.createQuery(
                "SELECT r FROM Report r WHERE r.regNumber = :regnumber AND r.timestamp between :mintimestamp AND :maxtimestamp  ORDER BY r.timestamp");


        q.setParameter("maxtimestamp", timestamp + 86399999);
        q.setParameter("mintimestamp", timestamp);
        q.setParameter("regnumber", regNumber);

        reportList = q.getResultList();
        return reportList;
    }

    public String makeDirectionsAsJsonArray(List<Report> reportList) {
        JSONArray ja = new JSONArray();

        for (Report report : reportList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt("lng", report.getLongitude());
            jsonObject.put("lat", report.getLatitude());
            jsonObject.put("timestamp", report.getTimestamp());
            ja.put(jsonObject);

        }

        return ja.toString();
    }


}
