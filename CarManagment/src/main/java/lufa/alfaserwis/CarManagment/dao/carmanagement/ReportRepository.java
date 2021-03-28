package lufa.alfaserwis.CarManagment.dao.carmanagement;

import lufa.alfaserwis.CarManagment.entity.carmanagement.CarAssignmentToGpsDevice;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByRegNumber(String regNumber);

    Report findFirstByRegNumberOrderByTimestamp(String regNumber);


    @Query("FROM CarAssignmentToGpsDevice")
    List<CarAssignmentToGpsDevice> getAllGpsDevices();

    @Query("SELECT DISTINCT a.regNumber FROM Report a")
    List<String> findDistinctRegNumber();

    @Query("SELECT r FROM Report r where r.timestamp = (SELECT max(timestamp) FROM Report where imei =?1) and  r.imei = ?1")
    Report findLatestReportForImei(Long imei);


}
