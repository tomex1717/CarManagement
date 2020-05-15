package lufa.alfaserwis.CarManagment.dao;

import lufa.alfaserwis.CarManagment.entity.CarRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepairRepository extends JpaRepository<CarRepair,Integer> {
    List<CarRepair> findByOrderByDateAsc();
    List<CarRepair> findByRegNumber(String regNumber);

}
