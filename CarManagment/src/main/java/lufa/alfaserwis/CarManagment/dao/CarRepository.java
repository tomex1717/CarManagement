package lufa.alfaserwis.CarManagment.dao;

import lufa.alfaserwis.CarManagment.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,String> {

}
