package lufa.alfaserwis.CarManagment.dao.carmanagement;

import lufa.alfaserwis.CarManagment.entity.carmanagement.CarRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Repository
public interface CarRepairRepository extends JpaRepository<CarRepair,Integer> {
    List<CarRepair> findByOrderByDateAsc();
    List<CarRepair> findByRegNumber(String regNumber);

}
