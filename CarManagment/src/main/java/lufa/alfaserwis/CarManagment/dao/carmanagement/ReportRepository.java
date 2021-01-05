package lufa.alfaserwis.CarManagment.dao.carmanagement;

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

    @Query("SELECT DISTINCT a.regNumber FROM Report a")
    List<String> findDistinctRegNumber();


}
