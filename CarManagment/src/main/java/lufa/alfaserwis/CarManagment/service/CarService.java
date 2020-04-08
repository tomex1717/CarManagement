package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.entity.Car;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CarService {

    List<Car> getAll();
    Car getByRegNumber(String regNumber);
    void save(Car car);
    void deleteById(String regNumber);

}
