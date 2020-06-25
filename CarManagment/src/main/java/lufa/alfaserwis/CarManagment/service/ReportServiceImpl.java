package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.dao.carmanagement.ReportRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Day;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    @Async
    public void deleteRecordsOlderThan() {
        // deletes rows in reports table older than 14 days, runs every day at 3am
        int days = 14;
        long timestamp = System.currentTimeMillis() - days * 86400000;
        Query q = entityManager.createQuery("DELETE FROM Report s WHERE s.timestamp < :timestamp");
        q.setParameter("timestamp", timestamp);
        int deletedRows = q.executeUpdate();
        log.info("Deleted rows on scheduled task: " + deletedRows);
        entityManager.close();
    }


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
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            Date parsedDate = dateFormat.parse(date);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());

            return timestamp.getTime();
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println(day.getMinTimestamp());
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


}
