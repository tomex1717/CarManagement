package lufa.alfaserwis.CarManagment.dao;

import lufa.alfaserwis.CarManagment.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,String> {

    List<Car> findByOrderByRegNumberAsc();

}
