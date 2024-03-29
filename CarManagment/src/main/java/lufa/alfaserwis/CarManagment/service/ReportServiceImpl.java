package lufa.alfaserwis.CarManagment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.config.YAMLConfig;
import lufa.alfaserwis.CarManagment.dao.carmanagement.GpsDevicesRepository;
import lufa.alfaserwis.CarManagment.dao.carmanagement.ReportRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;
import lufa.alfaserwis.CarManagment.entity.carmanagement.CarAssignmentToGpsDevice;
import lufa.alfaserwis.CarManagment.entity.carmanagement.GPSElementBinaryCoding;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableAsync
@Slf4j
public class ReportServiceImpl {


    private ReportRepository reportRepository;
    private EntityManager entityManager;
    private YAMLConfig config;
    private GpsDevicesRepository gpsDevicesRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, @Qualifier("carManagementEM") EntityManager entityManager,
                             YAMLConfig config, GpsDevicesRepository gpsDevicesRepository) {
        this.reportRepository = reportRepository;
        this.entityManager = entityManager;
        this.config = config;
        this.gpsDevicesRepository = gpsDevicesRepository;

    }


    public void writeToDb(String report) {
        // convert String report to List first, then make entity object according to fields
        // and corresponding list index
        Report reportEntity = makeEntity(getReportAsList(report));

        // save in db when there is imei number on DB
        if (isGpsInDB(reportEntity.getImei())) {
            reportRepository.save(reportEntity);
        }
    }

    public void writeToDb(GPSElementBinaryCoding gps, long timestamp, long imei, HashMap<Integer, Integer> allIOElements) {

        reportRepository.save(makeEntity(gps, timestamp, imei, allIOElements));

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

    private Report makeEntity(GPSElementBinaryCoding gps, long timestamp, long imei,
                              HashMap<Integer, Integer> allIOElements) {
        Report report = new Report();
        report.setImei(imei);
        report.setTimestamp(timestamp);
        report.setLatitude((double) gps.getLatitude());
        report.setAltitude(gps.getAltitude());
        report.setLongitude((double) gps.getLongitude());
        report.setSpeed(gps.getSpeed());
        report.setRegNumber("N/A");
        report.setEngineRPM(allIOElements.getOrDefault(36, 0));


        return report;
    }


    private long covertStringToTimeStampMillis(String date) {
        // Converts DateAndTime string in report to timestamp in polish TimeZone
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date parsedDate = dateFormat.parse(date);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());

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


    // fetching report of the day of given regNumber and dividing into routes (setting route number to entity(Transient data))

    public List<Report> getReportsOfDay(long timestamp, long imei) {
        List<Report> reportList;

        Query q = entityManager.createQuery(
                "SELECT r FROM Report r WHERE r.imei = :imei AND r.timestamp between " +
                        ":mintimestamp AND :maxtimestamp  ORDER BY r.timestamp ASC");


        q.setParameter("maxtimestamp", timestamp + 86399999);
        q.setParameter("mintimestamp", timestamp);
        q.setParameter("imei", imei);

        reportList = q.getResultList();

        Report previousReport = new Report();
        long timeCount = 0;
        int route = 1;

        boolean routeIncremented = true;
        List<Integer> cacheReports = new ArrayList<>();
        ListIterator<Report> listIterator = reportList.listIterator();
        if (!reportList.isEmpty()) {
            previousReport = reportList.get(0);
        }
        while (listIterator.hasNext()) {
            Report report = listIterator.next();

            if (report.getLatitude().equals(previousReport.getLatitude()) &&
                    report.getLongitude().equals(previousReport.getLongitude())) {

                cacheReports.add(listIterator.nextIndex() - 1);
                if (timeCount == 0) {
                    timeCount = previousReport.getTimestamp();
                }


                if (report.getTimestamp() > timeCount + 600000) {
                    for (int index : cacheReports) {
                        reportList.get(index).setRouteNumber(0);
                    }

                    if (!routeIncremented) {

                        cacheReports = new ArrayList<>();
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
                    routeIncremented = false;
                }


                cacheReports = new ArrayList<>();
                timeCount = 0;
                report.setRouteNumber(route);
            }
            previousReport = report;
        }
        for (int index : cacheReports) {
            reportList.get(index).setRouteNumber(0);
        }
        return reportList;
    }


    // making jsonArray for js script. Dividing routes into smaller jsonArrays and adding them to allRoute jsonArray.
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
                        cacheReport.getLongitude(), cacheReport.getLatitude(), cacheReport.getTimestamp(),
                        cacheReport.getSpeed(), cacheReport.getEngineRPM()));

            } else {
                routeArray.put(makeJsonObject(
                        cacheReport.getLongitude(), cacheReport.getLatitude(), cacheReport.getTimestamp(),
                        cacheReport.getSpeed(), cacheReport.getEngineRPM()));

                if (routeArray.length() > 1) {
                    allRoutes.put(routeArray);
                }

                routeArray = new JSONArray();
            }


            cacheReport = report;
        }

        if (!reportList.isEmpty()) {
            routeArray.put(makeJsonObject(
                    cacheReport.getLongitude(), cacheReport.getLatitude(), cacheReport.getTimestamp(),
                    cacheReport.getSpeed(), cacheReport.getEngineRPM()));
            allRoutes.put(routeArray);
        }
        return allRoutes.toString();
    }


    public List<Report> findLatestReportForAllRegNumbers() {
        List<String> listOfRegNumber = findAllRegNumber();
        List<Report> reports = new ArrayList<>();
        for (String regNumber : listOfRegNumber) {
            Report report = getNewestReportByRegNumber(regNumber);
            if (report != null) {
                reports.add(report);
            }


        }
        return reports;

    }


    public boolean isGpsInDB(long imei) {
        List<CarAssignmentToGpsDevice> gpsList = getAllGpsDevices();
        boolean isPresent = false;
        for (CarAssignmentToGpsDevice gps : gpsList) {
            if (gps.getGPSImei() == null || gps.getGPSImei().isEmpty()) {
                continue;
            }
            if (Long.parseLong(gps.getGPSImei()) == imei) {
                isPresent = true;
            }

        }
        return isPresent;
    }

    public List<CarAssignmentToGpsDevice> getAllGpsDevices() {
        return gpsDevicesRepository.findAll();

    }


    private JSONObject makeJsonObject(double lng, double lat, long timestamp, double speed, int rpm) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("lng", lng);
        jsonObject.put("lat", lat);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("speed", speed);
        jsonObject.put("rpm", rpm);

        return jsonObject;
    }


    // for now all cars are meant to be placed in only one table, in future imm planning creating table for each car
    // it'll need to be changed

    private List<String> findAllRegNumber() {

        return reportRepository.findDistinctRegNumber();


    }


    private Report getNewestReportByRegNumber(String regNumber) {
        return reportRepository.findFirstByRegNumberOrderByTimestamp(regNumber);

    }

    private Report findLatestReportForImei(Long imei) {

        return reportRepository.findLatestReportForImei(imei);
    }


    private Set<Report> findLatestReportForEachGpsImei() {
        List<CarAssignmentToGpsDevice> gpsList = getAllGpsDevices();
        Set<Report> reportSet = new HashSet<>();
        for (CarAssignmentToGpsDevice gps : gpsList) {
            if (gps.getGPSImei().isEmpty()) {
                continue;
            } else {
                reportSet.add(findLatestReportForImei(Long.parseLong(gps.getGPSImei())));
            }

        }
        return reportSet;
    }


    public String makeJsonStringFromLatestReportsSet() {
        Set<Report> set = findLatestReportForEachGpsImei();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(set);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return jsonString;
    }

    public CarAssignmentToGpsDevice findGpsByImei(long imei) {
        List<CarAssignmentToGpsDevice> gpsList = gpsDevicesRepository.findAll();
        for (CarAssignmentToGpsDevice gps : gpsList) {
            if (!gps.getGPSImei().isEmpty()) {
                if (Long.parseLong(gps.getGPSImei()) == imei) {

                    return gps;
                }
            }

        }


        throw new NoSuchElementException("No gps associated with imei number " + imei);

    }

    public String makeJsonForInfoWindowFromCarObject(Car car) {
        JSONObject object = new JSONObject();
        object.put("regNumber", car.getRegNumber());
        object.put("user", car.getUser());
        object.put("mark", car.getMark());
        object.put("model", car.getModel());
        object.put("nickname", car.getNickname());


        return object.toString();

    }


}

