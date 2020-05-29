package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;

import java.util.List;


public interface CarService {

    List<Car> getAll();
    Car getByRegNumber(String regNumber);

    void save(Car car);
    void deleteById(String regNumber);

}
